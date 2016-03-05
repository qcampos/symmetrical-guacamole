package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeExperienceDAO;
import fr.upem.hireanemployee.EmployeeFormationDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Formation;
import fr.upem.hireanemployee.profildata.Formation.DegreeType;
import fr.upem.hireanemployee.profildata.School;
import fr.upem.hireanemployee.validators.DateTranslator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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

    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employeeFormations}")
    private Collection<Formation> originalFormations;
    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;

    // Data fields to edit.

    private ArrayList<FormationController> formations;
    private SimpleDateFormat yearFormatter;
    private List<String> months;


    @PostConstruct
    private void init() {
        log("init - formations " + originalFormations);
        if (originalFormations == null) {
            throw new NullPointerException("The current formations are null.");
        }

        yearFormatter = new SimpleDateFormat("yyyy");

        // Creating the list of months.
        months = DateTranslator.getMonths();

        formations = new ArrayList<>();
        for (Formation f : originalFormations) {
            formations.add(new FormationController(f));
        }
    }


    public class FormationController extends Logger {
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
        private String country;

        public FormationController() {
        }

        public FormationController(Formation formation) {
            this.formation = formation;
            this.id = formation.getId();
            this.name = formation.getName();
            this.description = formation.getDescription();
            this.level = formation.getLevel();
            this.school = formation.getSchool();
            this.startDate = formation.getStartDate();
            this.endDate = formation.getEndDate();
            toDate = DateTranslator.toDateYears(formation.getStartDate(), formation.getEndDate());
            startYear = startYear == null ? null : yearFormatter.format(startDate);
            endYear = endYear == null ? null : yearFormatter.format(endDate);
            country = (school.getCountry().equals(Country.NONE) ? Country.FRANCE : school.getCountry()).toString();
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

        public String getCountry() {
            return country;
        }
    }

    public ArrayList<FormationController> getFormations() {
        return formations;
    }

    public Collection<Formation> getOriginalFormations() {
        return originalFormations;
    }

    public void setOriginalFormations(Collection<Formation> originalFormations) {
        this.originalFormations = originalFormations;
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
}
