package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.*;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Experience;

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

    @EJB
    private EmployeeExperienceDAO dao;

    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employeeExperiences}")
    private Collection<Experience> originalExperiences;
    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;

    // Data fields to show.
    private String professionalTitle;


    // Data fields to edit.
    private String newFirstName;
    private List<String> months;
    private ArrayList<ExperienceController> experiences;
    private SimpleDateFormat monthFormatter;
    private SimpleDateFormat yearFormatter;


    @PostConstruct
    private void init() {
        log("init - experiences " + originalExperiences);
        if (originalExperiences == null) {
            throw new NullPointerException("The current experiences are null.");
        }

        months = Arrays.asList(new String[]{"janvier", "février", "mars", "avril", "mai", "juin", "juillet",
                "aout", "septembre", "octobre", "novembre", "décembre"});

        experiences = new ArrayList<>();
        monthFormatter = new SimpleDateFormat("MMMM");
        yearFormatter = new SimpleDateFormat("yyyy");
        for (Experience exp : originalExperiences) {
            experiences.add(ExperienceControllerFactory(exp));
        }

        // Normalizing null values. We program with the option preventing the use of empty strings.
        // Null strings are handle ate the printing to place placeholder independent from this very model.
        // It allows us for example to translate placeholders outside the model.
        //professionalTitle = setNullIfEmpty(professionalTitle);s

        // Allowing default values by setting these forms.
//        newFirstName = firstName;

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

    public Collection<Experience> getOriginalExperiences() {
        return originalExperiences;
    }

    public ArrayList<ExperienceController> getExperiences() {
        return experiences;
    }

    public List<String> getMonths() {
        return months;
    }


    ExperienceController ExperienceControllerFactory(Experience experience) {
        Objects.requireNonNull(experience);

        // Saving fields to prevent jsf's get to indirect several times.
        long id = experience.getId();
        String place = "France"; // TODO solve this with the persistence BDD !!
        String jobName = experience.getJobName();
        String jobDescription = experience.getJobDescription();
        String companyName = experience.getCompanyName();
        Date startDate = experience.getStartDate();
        Date endDate = experience.getEndDate();
        String toDate = experience.toDate();
        String startMonth = monthFormatter.format(startDate);
        String endMonth = monthFormatter.format(endDate);
        String startYear = yearFormatter.format(startDate);
        String endYear = yearFormatter.format(endDate);
        jobDescription = jobDescription != null ? (jobDescription.isEmpty() ? null : jobDescription) : null;

        return new ExperienceController(id, jobName, place, companyName, jobDescription, startDate, endDate, toDate,
                startMonth, endMonth, startYear, endYear, experience);
    }

    /**
     * Controls any data about experiences on jsf pages. It is used in place of Validator to allow us using
     * custom notifications (replaced by message in Validators).
     */
    public class ExperienceController extends Logger {

        private long id;
        private String jobName;
        private String place;
        private String companyName;
        private String jobDescription;
        private Date startDate;
        private Date endDate;
        private String toDate;
        private String startMonth;
        private String endMonth;
        private String startYear;
        private String endYear;
        private Experience experience;


        private ExperienceController(long id, String jobName, String place, String companyName, String jobDescription,
                                     Date startDate, Date endDate, String toDate, String startMonth, String endMonth,
                                     String startYear, String endYear, Experience experience) {
            this.id = id;
            this.jobName = jobName;
            this.place = place;
            this.companyName = companyName;
            this.jobDescription = jobDescription;
            this.startDate = startDate;
            this.endDate = endDate;
            this.toDate = toDate;
            this.startMonth = startMonth;
            this.endMonth = endMonth;
            this.startYear = startYear;
            this.endYear = endYear;
            this.experience = experience;
        }

        void setExperience(Experience experience) {
            this.experience = Objects.requireNonNull(experience);

            id = experience.getId();
            place = "France"; // TODO solve this with the persistence BDD !!
            jobName = experience.getJobName();
            jobDescription = experience.getJobDescription();
            companyName = experience.getCompanyName();
            startDate = experience.getStartDate();
            endDate = experience.getEndDate();
            toDate = experience.toDate();
            startMonth = monthFormatter.format(startDate);
            endMonth = monthFormatter.format(endDate);
            startYear = yearFormatter.format(startDate);
            endYear = yearFormatter.format(endDate);

            jobDescription = jobDescription != null ? (jobDescription.isEmpty() ? null : jobDescription) : null;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
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

        public long getId() {
            return id;
        }

        public String getJobName() {
            return jobName;
        }

        public String getPlace() {
            return place;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
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

        private boolean fieldValidated = true;

        public String update() {
            EmployeeExperienceBean.this.log("update - " + id + " " + fieldValidated);
            if (!fieldValidated) {
                return Constants.CURRENT_PAGE;
            }
            //if (validateFields()) return Constants.CURRENT_PAGE;
            EmployeeExperienceBean.this.log("update - " + id + " " + companyName + " " + jobName + " " + "job abstract" + " " +
                    jobDescription + " " + startDate + " " + endDate);
            Experience experience = dao.updateExperience(id, companyName, jobName, "job abstract", jobDescription, startDate, endDate);
            setExperience(experience);
            log("update - experience set.");
            return Constants.CURRENT_PAGE;
        }

        private boolean validateFields() {
            notificationBean.clear();

            // Parsing required field.
            if (jobName == null) {
                log("validateFields - empty field");
                notificationBean.setError(NotificationBean.DEFAULT_MSG);
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
            log("dynamicFields - render fields after good state " + fieldValidated);
            fieldValidated = false || validateFields();
            log("dynamicFields - validation " + fieldValidated);
            if (fieldValidated) {
                UIComponent target = event.getComponent().findComponent("experience-" + id + "-fields");
                FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(target.getClientId());
            } else {
                // Reseting fields.
                setExperience(experience);
            }
        }

        public boolean getUpdateState() {
            return fieldValidated;
        }
    }
}
