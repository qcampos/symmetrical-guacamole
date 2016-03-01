package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.*;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Experience;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

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

        months = Arrays.asList(new String[]{"janvier", "février", "mars", "juin", "juillet",
                "aout", "septembre", "octobre", "septembre", "novembre", "décembre"});

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

        return new ExperienceController(id, jobName, place, companyName, jobDescription, startDate, endDate, toDate,
                startMonth, endMonth, startYear, endYear);
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


        private ExperienceController(long id, String jobName, String place, String companyName, String jobDescription,
                                     Date startDate, Date endDate, String toDate, String startMonth, String endMonth,
                                     String startYear, String endYear) {
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
        }

        void setExperience(Experience experience) {
            Objects.requireNonNull(experience);

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

        public String update() {
            startDate.setMonth(months.indexOf(startMonth));
            startDate.setYear(Integer.valueOf(startYear) - 1900);
            endDate.setMonth(months.indexOf(endMonth));
            endDate.setYear(Integer.valueOf(endYear) - 1900);

            log(startDate.toString());
            EmployeeExperienceBean.this.log("update - " + id + " " + companyName + " " + jobName + " " + "job abstract" + " " +
                    jobDescription + " " + startDate + " " + endDate);
            Experience experience = dao.updateExperience(id, companyName, jobName, "job abstract", jobDescription, startDate, endDate);
            setExperience(experience);
            log("update - experience save/set.");
            return Constants.CURRENT_PAGE;
        }
    }
}
