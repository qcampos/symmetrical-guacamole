package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeFormationDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Formation;
import fr.upem.hireanemployee.profildata.Formation.DegreeType;
import fr.upem.hireanemployee.profildata.School;
import fr.upem.hireanemployee.profildata.Visibility;
import fr.upem.hireanemployee.validators.DateTranslator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Baxtalou on 22/02/2016.
 * Manages the description section during the cv display.
 */
@ManagedBean
@ViewScoped
public class EmployeeFormationBean extends Logger {

    @EJB
    private EmployeeFormationDAO dao;
    @EJB
    private DatabaseDAO ddao;

    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;

    // Data fields to edit.

    private ArrayList<FormationController> formations;
    private SimpleDateFormat yearFormatter;
    private List<Visibility> visibilities;
    private List<Country> countries;


    @PostConstruct
    private void init() {
        Collection<Formation> originalFormations = employee.getFormations();
        log("init - formations " + originalFormations);
        if (originalFormations == null) {
            throw new NullPointerException("The current formations are null.");
        }

        yearFormatter = new SimpleDateFormat("yyyy");

        // Creating formationsControllers.
        formations = new ArrayList<>();
        for (Formation f : originalFormations) {
            formations.add(new FormationControllerUpdater(f));
        }

        // Setting visibilities in cache.
        visibilities = ddao.getVisibilities();
        // Setting countries in cache.
        countries = ddao.getCountries();
    }

    public List<Country> getCountries() {
        return countries;
    }

    public List<Visibility> getVisibilities() {
        return visibilities;
    }

    public class FormationControllerUpdater extends FormationController {

        public FormationControllerUpdater(Formation formation) {
            super(formation);
        }

        @Override
        protected String performUpdate() {
            return null;
        }

        @Override
        void updateAjaxRenders(AjaxBehaviorEvent event) {

        }
    }

    /**
     * Controls any data about formations on jsf pages. It is replaces Validator to allow us to use
     * custom notifications (replaced by message in Validators).
     * This abstract class can be specialize in the creation and update of a Formation.
     * Two implementations are currently inheriting : The FormationControllerUpdater and the FormationControllerBuilder.
     * The former updates database values of the Formation wrapped during its perform call.
     * The later creates a new Formation during its perform call.
     */
    public abstract class FormationController extends Logger {
        // Caching parameters.
        private Formation formation;
        private long id;
        private String name;
        private String description;
        private DegreeType level;
        private School school;
        private Date startDate;
        private Date endDate;
        private String toDate;
        private String startYear;
        private String endYear;
        private Country country;
        private Visibility visibility;

        // Flag indicating our fields validity
        boolean fieldValidated;
        boolean removed;

        public FormationController() {
        }

        public FormationController(Formation formation) {
            updateWrappedFormation(formation);
        }


        /**
         * Method called when an update form the current field values is requested and the enclosing
         * state is valid.
         * Sub-classes has to override it to specify the update operation.
         *
         * @return The page to move on.
         */
        protected abstract String performUpdate();

        abstract void updateAjaxRenders(AjaxBehaviorEvent event);


        public String update() {
            /*log("update - creation with fields : " + id + " " + fieldValidated + " " + companyName + " " + jobName + " " +
                    jobDescription + " " + country + " " + startDate + "  " + endDate + " " + employee.getId() + " " + visibility);
            // If fields not validated, aborting.
            if (!fieldValidated || removed) {
                return Constants.CURRENT_PAGE;
            }
            return performUpdate();*/
            return "";
        }


        private boolean validateFields() {
        /*    log("validateFields - " + id + " " + companyName + " " + jobName + " " + "job abstract" + " " +
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
*/
            return true;
        }

        public void dynamicFields(AjaxBehaviorEvent event) {
            log("dynamicFields - removed : " + removed + " before test of the fields:" + fieldValidated);
            if (removed) return;
            fieldValidated = false || validateFields();
            log("dynamicFields - Validation result :  " + fieldValidated);
            if (fieldValidated) {
                updateAjaxRenders(event);
            } else {
                // Resetting fields to the current formation.
                updateWrappedFormation(formation);
            }
        }

        /**
         * Removes the inner Formation from the database. But does not delete it from
         * the current Controller until a refresh of the list is done. Setting
         * a flag Removed to do so. (This is needed since JSF needs to have
         * the same number of formulaire to indexe them, until you refresh
         * the whole set. Yes, it's the biggest bug which has ever existed).
         */
        public void setRemoved() {
            log("setRemoved - : " + id);
            // First deleting pending keys of removed formations.
            employee.removeFormationById(formation);
            // Merging it with the database.
            ddao.mergeEmployee(employee);
            removed = true;
            fieldValidated = false;
        }

        private void updateWrappedFormation(Formation formation) {
            this.formation = formation;
            this.id = formation.getId();
            this.name = formation.getName();
            this.description = formation.getDescription();
            this.level = formation.getLevel();
            this.school = formation.getSchool();
            this.country = school.getCountry();
            this.startDate = formation.getStartDate();
            this.endDate = formation.getEndDate();
            this.visibility = formation.getVisibility();
            toDate = DateTranslator.toDateYears(formation.getStartDate(), formation.getEndDate());
            startYear = startYear == null ? null : yearFormatter.format(startDate);
            endYear = endYear == null ? null : yearFormatter.format(endDate);

            description = description != null ? (description.isEmpty() ? null : description) : null;
            toDate = toDate != null ? (toDate.isEmpty() ? null : toDate) : null;
            name = name != null ? (name.isEmpty() ? null : name) : null;
        }


        public boolean isRemoved() {
            return removed;
        }

        public boolean areFieldValidated() {
            return fieldValidated;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public DegreeType getLevel() {
            return level;
        }

        public School getSchool() {
            return school;
        }

        public String getToDate() {
            return toDate;
        }

        public String getStartYear() {
            return startYear;
        }

        public String getEndYear() {
            return endYear;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setLevel(DegreeType level) {
            this.level = level;
        }

        public void setSchool(School school) {
            this.school = school;
        }

        public void setStartYear(String startYear) {
            this.startYear = startYear;
        }

        public void setEndYear(String endYear) {
            this.endYear = endYear;
        }

        public void setCountry(Country country) {
            this.country = country;
        }

        public void setVisibility(Visibility visibility) {
            this.visibility = visibility;
        }

        public Country getCountry() {
            return country;
        }
    }

    public ArrayList<FormationController> getFormations() {
        return formations;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public NotificationBean getNotificationBean() {
        return notificationBean;
    }

    public void setNotificationBean(NotificationBean notificationBean) {
        this.notificationBean = notificationBean;
    }

    /**
     * @return The number of currently shown/visible experience by this very bean.
     * This is needed since the life cycle of jsf needs to perform indexing multiple
     * times on the same number of forms, often. So we keep deleted experienceController
     * until a new total AJAX refresh of the entire list (new indexing).
     */
    public int nbOfExperienceShown() {
        int size = 0;
        for (FormationController f : formations) {
            size += f.isRemoved() ? 0 : 1;
        }
        log("nbOfExperienceShown - " + size);
        return size;
    }
}
