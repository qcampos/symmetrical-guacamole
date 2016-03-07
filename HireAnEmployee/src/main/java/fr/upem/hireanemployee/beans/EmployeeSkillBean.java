package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeSkillDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.Skill;
import fr.upem.hireanemployee.profildata.SkillAssociation;
import fr.upem.hireanemployee.validators.CollectionsSort;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages skills control on jsf pages.
 */
@ManagedBean
@ViewScoped
public class EmployeeSkillBean extends Logger {

    @EJB
    private EmployeeSkillDAO dao;
    @EJB
    private DatabaseDAO ddao;

    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;
    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;

    // Caches for jsf gets & Data fields to edit.
    private List<Skill> skillsList;
    private List<BaseSkillController> skillControllers;
    // Visitor data.
    private long visitorId;
    private boolean visitorConnected;

    @PostConstruct
    private void init() {
        Collection<SkillAssociation> employeeSkills = employee.getSkills();
        log("init - Skills " + employeeSkills);
        if (employeeSkills == null) {
            employeeSkills = new ArrayList<>();
            log("The current employeeSkills are null. For employee : " + employee.getId());
        }
        // This manual sort is needed once. Because automatic entities merge does not support
        // Order by clauses. We want the employeeSkills to be sorted.
        // Moreover, Employee#getExperiences does not perform this sort. Since the method is
        // not called only once. And we don't want to perform it every time.
        CollectionsSort.sortSkillByLevel(employeeSkills);
        // Setting right now the visitor ids to determine skills' states.
        visitorConnected = sessionBean.isConnected();
        visitorId = sessionBean.getId();
        // Initializing list of available skillsList.
        skillsList = ddao.getSkills();
        // Creating list of updaters.
        skillControllers = new ArrayList<>();
        for (SkillAssociation association : employeeSkills) {
            skillControllers.add(new BaseSkillController(association));
        }
    }

    // There are !3 updater : One for a relation. One for visitor. One for the owner.


    // TODO make it abstract.
    public class BaseSkillController extends Logger {

        // Caches for JSF getters.
        private final SkillAssociation skill;
        private final String name;
        private boolean hasVoted; // If the current visitor has voted for this very skill or not.
        private int level;

        public BaseSkillController(SkillAssociation skill) {
            this.skill = skill;
            name = skill.getSkill().getName();
            level = skill.getLevel();
            hasVoted = visitorConnected && skill.hasVoted(visitorId);
        }


        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public boolean hasVoted() {
            return hasVoted;
        }

    }

    public List<BaseSkillController> getSkillControllers() {
        return skillControllers;
    }

    public List<Skill> getSkillsList() {
        return skillsList;
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

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}
