package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeFormationDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.*;
import fr.upem.hireanemployee.profildata.Formation.DegreeType;
import fr.upem.hireanemployee.validators.CollectionsSort;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private FormationControllerBuilder formationControllerBuilder;
    private SimpleDateFormat yearFormatter;
    private List<Visibility> visibilities;
    private List<Country> countries;


    @PostConstruct
    private void init() {
        Collection<Formation> originalFormations = employee.getFormations();
        log("init - formations " + originalFormations);
        if (originalFormations == null) {
            originalFormations = new ArrayList<>();
            log("The current formations are null. For employee : " + employee.getId());
        }
        // This manual sort is needed once. Because automatic entities merge does not support
        // Order by clauses. We want the formation to be sorted.
        // Moreover, Employee#getExperiences does not perform this sort. Since the method is
        // not called only once. And we don't want to perform it every time.
        CollectionsSort.sortFormation(originalFormations);

        yearFormatter = new SimpleDateFormat("yyyy");

        // Creating formationsControllers.
        formations = createFormationControllers(originalFormations);

        // Creating our builder.
        formationControllerBuilder = new FormationControllerBuilder();
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
            log("performUpdate - formation set " + " " + getStartDate() + " " + getEndDate() + " ");
            // TODO update the country for Formations.
            // Updates inner formation's fields.
            dao.updateFormation(formation, getName(), getDescription(), DegreeType.PHD, getSchool(),
                    getStartDate(), getEndDate(), getVisibility());
            // Calculating new field in cache from this new formation.
            updateWrappedFormation(formation);
            return Constants.CURRENT_PAGE;
        }

        @Override
        void updateAjaxRenders(AjaxBehaviorEvent event) {
            UIComponent target = event.getComponent().findComponent("formation-" + getId() + "-fields");
            UIComponent currentFormationTarget = event.getComponent().findComponent(":description_current_formation");
            Collection<String> renderIds = FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds();
            renderIds.add(target.getClientId());
            renderIds.add(currentFormationTarget.getClientId());
        }
    }

    public class FormationControllerBuilder extends FormationController {

        public FormationControllerBuilder() {
            super(new Formation(null, null, DegreeType.PHD, new School(null, Country.NONE), new Date(),
                    new Date(), Visibility.PUBLIC));
        }


        @Override
        protected String performUpdate() {
            // Creating the new newFormations. Updates the employee.
            dao.createFormation(getName(), getDescription(), getLevel(), getSchool(), getStartDate(), getEndDate(),
                    employee, getVisibility());

            Collection<Formation> newFormations = employee.getFormations();

            // Updating new values.
            formations = createFormationControllers(newFormations);
            formationControllerBuilder.updateWrappedFormation(emptyFormation());
            log("performUpdate - newFormations handled : " + Formation.printIds(newFormations));
            return Constants.CURRENT_PAGE;
        }

        @Override
        void updateAjaxRenders(AjaxBehaviorEvent event) {
            // Adding the re render of the form field and the re render of the list producer of formations.
            FacesContext currentInstance = FacesContext.getCurrentInstance();
            UIComponent target = event.getComponent().findComponent("formation-addForma-fields");
            UIComponent currentFormationTarget = event.getComponent().findComponent(":description_current_formation");
            UIComponent hintTarget = currentInstance.getViewRoot().findComponent("no-formation-hint");
            UIComponent formationsListTarget = currentInstance.getViewRoot().findComponent("formations-list");
            Collection<String> renderIds = currentInstance.getPartialViewContext().getRenderIds();
            renderIds.add(target.getClientId());
            renderIds.add(hintTarget.getClientId());
            renderIds.add(currentFormationTarget.getClientId());
            renderIds.add(formationsListTarget.getClientId());
            notificationBean.setSuccess("Nouvelle formation créée !");
        }

        private Formation emptyFormation() {
            return new Formation(null, null, DegreeType.PHD, new School(null, Country.NONE), new Date(),
                    new Date(), Visibility.PUBLIC);
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
        Formation formation;
        private long id;
        private String name;
        private String description;
        private DegreeType level;
        private School school;
        private String schoolName;
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
            log("update - can we update");
            // If fields not validated, aborting.
            if (!fieldValidated || removed) {
                log("update - update Impossible.");
                return Constants.CURRENT_PAGE;
            }
            log("update - Starting update");
            return performUpdate();
        }


        private boolean validateFields() {
            log("validateFields - " + id + " " + school.getName() + " " + schoolName + " " + name + " " + country +
                    " " + startYear + " " + endYear + " " + visibility + " " + description);
            notificationBean.clear();

            // Parsing required field.
            if (school == null || schoolName == null || startYear == null || endYear == null
                    || schoolName.trim().isEmpty() || startYear.trim().isEmpty() || endYear.trim().isEmpty()) {
                log("validateFields - empty field");
                notificationBean.setError("Veuillez remplir les champs requis.");
                return false;
            }

            school.setName(schoolName);
            // Parse variables with specific controls bound to this very instance class.
            try {

                int startYear = Integer.valueOf(this.startYear) - 1900;
                int endYear = Integer.valueOf(this.endYear) - 1900;

                log("validateFields - " + startYear + " " + endYear);
                if (startYear < 0 || endYear < 0) {
                    notificationBean.setError("Les deux années doivent être supérieures à 1899.");
                    return false;
                }
                if (startYear > 300 || endYear > 300) {
                    notificationBean.setError("Les deux années doivent être inférieure à 2100.");
                    return false;
                }

                log("validateFields - " + startYear + " " + endYear);
                // Setting dates to compare.
                startDate = new Date(startYear, 0, 1);
                endDate = new Date(endYear, 11, 30);
                log("validateFields - calculated dates : " + startDate + " end : " + endDate);
                if (startDate.compareTo(endDate) > 0) {
                    notificationBean.setError("La date de départ doit précéder la date de fin.");
                    return false;
                }
                school.setCountry(country);
            } catch (NumberFormatException e) {
                log("update - " + e.getMessage());
                notificationBean.setError(NotificationBean.DEFAULT_MSG);
                return false;
            }

            return true;
        }

        public void dynamicFields(AjaxBehaviorEvent event) {
            log("dynamicFields - removed : " + removed + " Before test of the fields :" + fieldValidated);
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
        public void setRemoved(AjaxBehaviorEvent event) {
            log("setRemoved - : " + id);
            // First deleting pending keys of removed formations.
            employee.removeFormationById(formation);
            // Merging it with the database.
            ddao.mergeEmployee(employee);
            removed = true;
            fieldValidated = false;
        }

        void updateWrappedFormation(Formation formation) {
            this.formation = formation;
            this.id = formation.getId();
            this.name = formation.getName();
            this.description = formation.getDescription();
            this.level = formation.getLevel();
            this.school = formation.getSchool() == null ? new School(null, Country.NONE) : formation.getSchool();
            schoolName = school.getName();
            this.country = school.getCountry();
            this.startDate = formation.getStartDate() == null ? new Date() : formation.getStartDate();
            this.endDate = formation.getEndDate() == null ? new Date() : formation.getEndDate();
            this.visibility = formation.getVisibility();
            toDate = DateTranslator.toDateYears(formation.getStartDate(), formation.getEndDate());
            startYear = yearFormatter.format(startDate);
            endYear = yearFormatter.format(endDate);
            // Done after toDate, which sets specific formatting when these dates are not set.
            startDate = startDate == null ? new Date() : startDate;
            endDate = endDate == null ? new Date() : endDate;

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

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
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

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
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

    public FormationControllerBuilder getFormationControllerBuilder() {
        return formationControllerBuilder;
    }

    /**
     * @return The number of currently shown/visible formation by this very bean.
     * This is needed since the life cycle of jsf needs to perform indexing multiple
     * times on the same number of forms, often. So we keep deleted formationController
     * until a new total AJAX refresh of the entire list (new indexing).
     */
    public int nbOfExperienceShown() {
        int size = 0;
        for (FormationController f : formations) {
            size += f.isRemoved() ? 0 : 1;
        }
        log("nbOfFormationShown - " + size);
        return size;
    }

    public int nbOfFormationPublic() {
        int size = 0;
        for (FormationController f : formations) {
            size += f.formation.getVisibility().equals(Visibility.PUBLIC) ? 1 : 0;
        }
        log("nbOfFormationPublic - " + size);
        return size;
    }

    /**
     * Factory list method.
     */
    private ArrayList<FormationController> createFormationControllers(Collection<Formation> originalFormations) {
        ArrayList<FormationController> controllers = new ArrayList<>();
        for (Formation f : originalFormations) {
            controllers.add(new FormationControllerUpdater(f));
        }
        return controllers;
    }
}
