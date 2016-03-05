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
        public String update() {
            EmployeeExperienceBean.this.log("update - id : " + id + " field validation : " + fieldValidated + " hashcode : " + hashCode());
            if (!fieldValidated) {
                return Constants.CURRENT_PAGE;
            }
            Experience experience = dao.updateExperience(id, companyName, jobName, "job abstract", jobDescription,
                    country, visibility, startDate, endDate);
            setExperience(experience);
            log("update - experience set.");
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

        private ExperienceControllerBuilder() {
            super();
            // TODO solve "France" with init values test (Countries...) !!
        }

        @Override
        public String update() {
            EmployeeExperienceBean.this.log("update - " + id + " " + fieldValidated);
            if (!fieldValidated) {
                return Constants.CURRENT_PAGE;
            }
            log("update - creation with fields : " + companyName + " " + jobName + " " + jobDescription + " " + country + " " +
                    startDate + "  " + endDate + " " + employee.getId() + " " + visibility);
            List<Experience> experience = dao.createExperience(companyName, jobName, "jobAbstract", jobDescription,
                    country, visibility, startDate, endDate, employee.getId());
            log("update - experience created ! ");
            // Updating the newly added values.
            originalExperiences = experience;
            experiences = createExperienceControllerUpdaterList();
            log("update - experiences : " + ids(experiences));
            log("update - originalExperiences : " + ids(originalExperiences));
            log("update - end of the update call.");

            experienceControllerBuilder.setEmptyExperience();
            return Constants.CURRENT_PAGE;
        }

        private String ids(Collection<? extends Experience> experiences) {
            StringBuilder n = new StringBuilder();
            for (Experience e : experiences) {
                n.append(e.getId());
            }
            return n.toString();
        }

        private String ids(ArrayList<? extends ExperienceController> experiences) {
            StringBuilder n = new StringBuilder();
            for (ExperienceController e : experiences) {
                n.append(e.id);
            }
            return n.toString();
        }

        @Override
        void updateAjaxRenders(AjaxBehaviorEvent event) {
            // Adding the re render of the form field and the re render of the list producer of experiences.
            FacesContext currentInstance = FacesContext.getCurrentInstance();
            UIComponent target = event.getComponent().findComponent("experience-addExp-fields");
            UIComponent producerTarget = currentInstance.getViewRoot().findComponent("experience-list"/*"experience_producer_list"*/);
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
        private Experience experience;


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
        }

        boolean fieldValidated = false;

        public abstract String update();

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
            log("dynamicFields - before test of the fields : " + fieldValidated);
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
}
