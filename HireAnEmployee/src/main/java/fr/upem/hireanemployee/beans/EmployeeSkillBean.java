package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeSkillDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
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
    private Employee visitor;
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
        if (visitorConnected) visitor = ddao.getEmployeeByID(sessionBean.getId());
        // Initializing list of available skillsList.
        skillsList = ddao.getSkills();
        // Creating list of updaters.
        skillControllers = new ArrayList<>();
        // If we are a visitor, or a visitor connected, using Controllers updater.
        buildSkillControllers(employeeSkills);
    }

    /**
     * Builds a collection of SkillControllers wrapping the given employee's skills.
     *
     * @see BaseSkillController
     */
    private void buildSkillControllers(Collection<SkillAssociation> employeeSkills) {
        if (!visitorConnected || visitor.getId() != employee.getId()) {
            buildControllerUpdaters(employeeSkills);
        } else {
            // Otherwise, using a controller remover.
            buildControllerRemovers(employeeSkills);
        }
    }

    private void buildControllerRemovers(Collection<SkillAssociation> employeeSkills) {
        skillControllers.clear();
        for (SkillAssociation association : employeeSkills) {
            skillControllers.add(new SkillControllerRemover(association));
        }
    }

    private void buildControllerUpdaters(Collection<SkillAssociation> employeeSkills) {
        skillControllers.clear();
        for (SkillAssociation association : employeeSkills) {
            skillControllers.add(new SkillControllerUpdater(association));
        }
    }

    // There are !3 updater : One for a relation. One for visitor. One for the owner.

    /**
     * This controller skill allows the update of a skill by voting up or down values.
     */
    public class SkillControllerUpdater extends BaseSkillController {
        public SkillControllerUpdater(SkillAssociation skill) {
            super(skill);
        }

        @Override
        public String perform() {
            log("perform - Visitor " + visitor.getId() + " has voted : " + hasVoted);
            if (!visitorConnected) return Constants.CURRENT_PAGE;
            if (!hasVoted) {
                dao.increaseSkill(employee, name, visitor);
                // Updating cache values.
                skill.increaseLevel(visitor);
                level = skill.getLevel();
                hasVoted = !hasVoted;
                return Constants.CURRENT_PAGE;
            }
            // Otherwise decreasing.
            dao.decreaseSkill(employee, name, visitor);
            // Updating cache values.
            skill.decreaseLevel(visitor);
            level = skill.getLevel();
            hasVoted = !hasVoted;
            return Constants.CURRENT_PAGE;
        }
    }

    /**
     * This controller skill allows the suppression of a skill.
     */
    public class SkillControllerRemover extends BaseSkillController {
        public SkillControllerRemover(SkillAssociation skill) {
            super(skill);
        }

        @Override
        public String perform() {
            log("perform (Remover) - Visitor " + visitor.getId());
            dao.removeSkill(employee, skill.getSkill());
            buildControllerRemovers(employee.getSkills());
            return Constants.CURRENT_PAGE;
        }
    }

    // TODO make it abstract.
    // Here we are sure that visitor is not null. Otherwise we are in the SkillControllerObserver class.
    public abstract class BaseSkillController extends Logger {

        // Caches for JSF getters.
        final SkillAssociation skill;
        final String name;
        boolean hasVoted; // If the current visitor has voted for this very skill or not.
        long skillId;
        int level;

        public BaseSkillController(SkillAssociation skill) {
            this.skill = skill;
            skillId = skill.getSkillId();
            name = skill.getSkill().getName();
            level = skill.getLevel();
            hasVoted = visitorConnected && skill.hasVoted(visitor);
        }


        /**
         * Performs the click action on a skill.
         * If the visitor has already voted, it will be removed.
         * Otherwise it will add one recommendation to the given skill.
         */
        public abstract String perform();

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public boolean hasVoted() {
            return hasVoted;
        }

        public long getSkillId() {
            return skillId;
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

    public int getSize() {
        return skillControllers.size();
    }

}
