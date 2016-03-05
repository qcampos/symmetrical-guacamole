package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.*;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Visibility;
import fr.upem.hireanemployee.validators.DateTranslator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Baxtalou on 22/02/2016.
 * Manages the description section during the cv display.
 */
@ManagedBean
@ViewScoped
public class EmployeeExperienceBean extends Logger {

    // DAOs.
    @EJB
    private DatabaseDAO ddao;
    @EJB
    private EmployeeExperienceDAO dao;
    ;

    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employeeExperiences}")
    private Collection<Experience> originalExperiences;
    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;

    // Data fields to edit.
    private List<String> months;
    private List<Country> countries;
    private ArrayList<ExperienceControllerUpdater> experiences;
    private ExperienceController experienceControllerBuilder;
    private SimpleDateFormat monthFormatter;
    private SimpleDateFormat yearFormatter;
    private List<Visibility> visibilities;


    @PostConstruct
    private void init() {
        log("init - experiences " + originalExperiences);
        if (originalExperiences == null) {
            throw new NullPointerException("The current experiences are null.");
        }

        monthFormatter = new SimpleDateFormat("MMMM");
        yearFormatter = new SimpleDateFormat("yyyy");

        // Creating the lsit of experiences updater. They will wrap real experience inside the DB.
        experiences = createExperienceControllerUpdaterList();

        // Creating the experience factory.
        experienceControllerBuilder = new ExperienceControllerBuilder();

        // Creating the list of months.
        months = DateTranslator.getMonths();

        // Creating the list of countries.
        countries = ddao.getCountries();

        // Creating the list of visibilities.
        visibilities = ddao.getVisibilities();
    }

    public int size() {
        int size = 0;
        for (ExperienceController e : experiences) {
            log("size : " + e.id + " dead ? " + e.isRemoved());
            size += e.isRemoved() ? 0 : 1;
        }
        log("size - " + size);
        return size;
    }

    public boolean experienceToDelete() {
        for (ExperienceController e : experiences) {
            if (e.isRemoved()) return true;
        }
        return false;
    }

    /**
     * Performs updates on the database values of the experience wrapped(id) during its perform call.
     */
    public class ExperienceControllerUpdater extends ExperienceController {

        private ExperienceControllerUpdater(String jobDescription, String startYear, String startMonth, String endYear,
                                            String endMonth, Experience experience) {
            super(experience.getId(), experience.getJobName(), experience.getCountry(), experience.getCompanyName(), jobDescription,
                    experience.getStartDate(), experience.getEndDate(),
                    DateTranslator.toDate(experience.getStartDate(), experience.getEndDate()),
                    startMonth, endMonth, startYear, endYear, experience.getVisibility(), experience);
        }

        @Override
        public String performUpdate() {
            log("performUpdate - experience set.");
            // Updates inner experiences' fields. Set them to the global updater to update calculated fields.
            dao.updateExperience(experience, companyName, jobName, "job abstract", jobDescription, country, visibility, startDate, endDate);
            setExperience(experience);
            return Constants.CURRENT_PAGE;
        }

        @Override
        void updateAjaxRenders(AjaxBehaviorEvent event) {
            UIComponent target = event.getComponent().findComponent("experience-" + id + "-fields");
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(target.getClientId());
        }
    }

    /**
     * Performs updates on the database values of the experience wrapped(id) during its perform call.
     */
    public class ExperienceControllerBuilder extends ExperienceController {

        @Override
        public String performUpdate() {
            // Creating the new newExperiences. Updates the employee.
            List<Experience> newExperiences = dao.createExperience(companyName, jobName, "jobAbstract",
                    jobDescription, country, visibility, startDate, endDate, employee);

            // Updating new values.
            originalExperiences = newExperiences;
            experiences = createExperienceControllerUpdaterList();
            experienceControllerBuilder.setEmptyExperience();
            log("performUpdate - originalExperiences : " + Experience.printIds(originalExperiences));
            log("performUpdate - newExperiences created ! End of the update call.");
            return Constants.CURRENT_PAGE;
        }


        @Override
        void updateAjaxRenders(AjaxBehaviorEvent event) {
            // Adding the re render of the form field and the re render of the list producer of experiences.
            FacesContext currentInstance = FacesContext.getCurrentInstance();
            UIComponent target = event.getComponent().findComponent("experience-addExp-fields");
            UIComponent producerTarget = currentInstance.getViewRoot().findComponent("experience-list");
            Collection<String> renderIds = currentInstance.getPartialViewContext().getRenderIds();
            renderIds.add(target.getClientId());
            renderIds.add(producerTarget.getClientId());
            notificationBean.setSuccess("Nouvelle expérience ajoutée");
        }
    }

    /**
     * Controls any data about experiences on jsf pages. It is used in country of Validator to allow us to use
     * custom notifications (replaced by message in Validators).
     * This abstract class is used in the creation and update experiences'states.
     * Two implementations are derived : The ExperienceControllerUpdater and the ExperienceControllerBuilder.
     * The former updates the database values of the experience wrapped during the perform call.
     * The later creates a new one during the perform call.
     */
    public abstract class ExperienceController extends Logger {

        long id;
        String jobName;
        Country country;
        String companyName;
        String jobDescription;
        Date startDate;
        Date endDate;
        private String toDate;
        private String startMonth;
        private String endMonth;
        private String startYear;
        private String endYear;
        Visibility visibility;
        Experience experience;

        // Flag indicating our fields validity
        boolean fieldValidated;
        boolean removed;


        private ExperienceController() {
            setEmptyExperience();
        }

        void setEmptyExperience() {
            setExperience(new Experience(null, null, null, null, Country.NONE, new Date(), new Date(), Visibility.PUBLIC));
        }

        private ExperienceController(long id, String jobName, Country country, String companyName, String jobDescription,
                                     Date startDate, Date endDate, String toDate, String startMonth, String endMonth,
                                     String startYear, String endYear, Visibility visibility, Experience experience) {
            this.id = id;
            this.jobName = jobName;
            this.country = country;
            this.companyName = companyName;
            this.jobDescription = jobDescription;
            this.startDate = startDate;
            this.endDate = endDate;
            this.toDate = toDate;
            this.startMonth = startMonth;
            this.endMonth = endMonth;
            this.startYear = startYear;
            this.endYear = endYear;
            this.visibility = visibility;
            this.experience = experience;
            removed = false;
        }

        void setExperience(Experience experience) {
            this.experience = Objects.requireNonNull(experience);

            id = experience.getId();
            country = experience.getCountry();
            jobName = experience.getJobName();
            jobDescription = experience.getJobDescription();
            companyName = experience.getCompanyName();
            startDate = experience.getStartDate();
            endDate = experience.getEndDate();
            toDate = DateTranslator.toDate(experience.getStartDate(), experience.getEndDate());
            startMonth = monthFormatter.format(startDate);
            endMonth = monthFormatter.format(endDate);
            startYear = yearFormatter.format(startDate);
            endYear = yearFormatter.format(endDate);
            visibility = experience.getVisibility();
            jobDescription = jobDescription != null ? (jobDescription.isEmpty() ? null : jobDescription) : null;
            removed = false;
        }

        /**
         * Method called when an update form the current field values is requested and the enclosing
         * state is valid.
         * Sub-classes has to override it to specify the update operation.
         *
         * @return The page to move on.
         */
        protected abstract String performUpdate();


        public String update() {
            log("update - creation with fields : " + id + " " + fieldValidated + " " + companyName + " " + jobName + " " +
                    jobDescription + " " + country + " " + startDate + "  " + endDate + " " + employee.getId() + " " + visibility);
            // If fields not validated, aborting.
            if (!fieldValidated || removed) {
                return Constants.CURRENT_PAGE;
            }
            return performUpdate();
        }


        private boolean validateFields() {
            log("validateFields - " + id + " " + companyName + " " + jobName + " " + "job abstract" + " " +
                    jobDescription + " " + startYear + " " + startMonth + " " + endYear + " " + endMonth +
                    " " + visibility);
            notificationBean.clear();

            // Parsing required field.
            if (jobName == null || companyName == null || startYear == null || endYear == null) {
                log("validateFields - empty field");
                notificationBean.setError("Veuillez remplire les champs requis.");
                return false;
            }

            // Parse variables with specific controls bound to this very instance class.
            try {
                int startMonth = months.indexOf(this.startMonth);
                int endMonth = months.indexOf(this.endMonth);
                int startYear = Integer.valueOf(this.startYear) - 1900;
                int endYear = Integer.valueOf(this.endYear) - 1900;

                // Parsing dates logic.
                startDate.setMonth(startMonth);
                startDate.setYear(startYear);
                endDate.setMonth(endMonth);
                endDate.setYear(endYear);
                startDate.setDate(1); // To create a 1 month interval (caution 29 is for February).
                endDate.setDate(29);

                if (startDate.compareTo(endDate) > 0) {
                    notificationBean.setError("La date de départ doit précéder la date de fin.");
                    return false;
                }
            } catch (NumberFormatException e) {
                log("update - " + e.getMessage());
                notificationBean.setError(NotificationBean.DEFAULT_MSG);
                return false;
            }

            return true;
        }

        public void dynamicFields(AjaxBehaviorEvent event) {
            log("dynamicFields - removed : " + removed + " before test of the fields:" + fieldValidated);
            if (removed) return;
            fieldValidated = false || validateFields();
            log("dynamicFields - after validation result :  " + fieldValidated);
            if (fieldValidated) {
                updateAjaxRenders(event);
            } else {
                // Resetting fields to normal.
                setExperience(experience);
            }
        }

        abstract void updateAjaxRenders(AjaxBehaviorEvent event);


        public void setRemoved(long id) {
            log("setRemoved - receiving : " + id + " Effective id : " + id);
            // First deleting pending keys of removed experiences.
            employee.removeExperienceById(experience);
            // Merging it with the database.
            dao.deleteExperience(employee);
            // TODO rename the name of the deleteExperience function and its documentation to fit.

            removed = true;
            fieldValidated = false;

            size(); // TODO delete debugs calls like these.

            return;
        }

        public boolean isRemoved() {
            return removed;
        }

        public boolean getUpdateState() {
            return fieldValidated;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public void setCountry(Country country) {
            this.country = country;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public void setStartMonth(String startMonth) {
            this.startMonth = startMonth;
        }

        public void setEndMonth(String endMonth) {
            this.endMonth = endMonth;
        }

        public void setStartYear(String startYear) {
            this.startYear = startYear;
        }

        public void setEndYear(String endYear) {
            this.endYear = endYear;
        }

        public void setVisibility(Visibility visibility) {
            this.visibility = visibility;
        }

        public long getId() {
            return id;
        }

        public String getJobName() {
            return jobName;
        }

        public Country getCountry() {
            return country;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public String getToDate() {
            return toDate;
        }

        public String getStartMonth() {
            return startMonth;
        }

        public String getEndMonth() {
            return endMonth;
        }

        public String getStartYear() {
            return startYear;
        }

        public String getEndYear() {
            return endYear;
        }

        public Visibility getVisibility() {
            return visibility;
        }

    }

    private ArrayList<ExperienceControllerUpdater> createExperienceControllerUpdaterList() {
        ArrayList<ExperienceControllerUpdater> experiences = new ArrayList<>();
        for (Experience exp : originalExperiences) {
            experiences.add(ExperienceControllerFactory(exp));
        }
        return experiences;
    }

    private ExperienceControllerUpdater ExperienceControllerFactory(Experience experience) {
        Objects.requireNonNull(experience);

        // Saving fields to prevent jsf's get to indirect several times.
        String jobDescription = experience.getJobDescription();
        Date startDate = experience.getStartDate();
        Date endDate = experience.getEndDate();
        String startMonth = monthFormatter.format(startDate);
        String endMonth = monthFormatter.format(endDate);
        String startYear = yearFormatter.format(startDate);
        String endYear = yearFormatter.format(endDate);
        jobDescription = jobDescription != null ? (jobDescription.isEmpty() ? null : jobDescription) : null;

        return new ExperienceControllerUpdater(jobDescription, startYear, startMonth, endYear, endMonth, experience);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setNotificationBean(NotificationBean notificationBean) {
        this.notificationBean = notificationBean;
    }

    public void setOriginalExperiences(Collection<Experience> originalExperiences) {
        this.originalExperiences = originalExperiences;
    }

    public ExperienceController getExperienceControllerBuilder() {
        return experienceControllerBuilder;
    }

    public void setExperienceControllerBuilder(ExperienceController experienceControllerBuilder) {
        this.experienceControllerBuilder = experienceControllerBuilder;
    }

    public Collection<Experience> getOriginalExperiences() {
        return originalExperiences;
    }

    public ArrayList<ExperienceControllerUpdater> getExperiences() {
        return experiences;
    }

    public List<String> getMonths() {
        return months;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public List<Visibility> getVisibilities() {
        return visibilities;
    }
}
