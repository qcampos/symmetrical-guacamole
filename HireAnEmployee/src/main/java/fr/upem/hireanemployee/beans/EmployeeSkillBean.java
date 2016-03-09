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
    private DatabaseDAO dataDao;

    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;
    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;

    // Caches for jsf gets & Data fields to edit.
    private List<Skill> skillsList;
    private List<BaseSkillController> skillControllers;
    private SkillControllerBuilder builder;
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
        if (visitorConnected) visitor = dataDao.getEmployeeByID(sessionBean.getId());
        // Initializing list of available skillsList.
        skillsList = getSkillsAddable(employeeSkills);
        // Creating list of updaters.
        skillControllers = new ArrayList<>();
        // If we are a visitor, or a visitor connected, using Controllers updater.
        buildSkillControllers(employeeSkills);
        builder = new SkillControllerBuilder();
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

    /**
     * Returns the list of skills which can be added to the user.
     */
    private List<Skill> getSkillsAddable(Collection<SkillAssociation> employeeSkills) {
        List<Skill> skills = dataDao.getSkills();
        // Filtering this list.
        for (SkillAssociation a : employeeSkills) {
            skills.remove(a.getSkill());
        }
        return skills;
    }

    // There are 3 : One for a relation. One for visitor. One for the owner.

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
            Collection<SkillAssociation> employeeSkills = employee.getSkills();
            // New removers and addable skills.
            buildControllerRemovers(employeeSkills);
            skillsList = getSkillsAddable(employeeSkills);
            return Constants.CURRENT_PAGE;
        }
    }


    /**
     * This controller skill allows the build of a new skill.
     */
    public class SkillControllerBuilder extends BaseSkillController {
        // The handle on the new skill to build.
        private Skill skillBuilt;

        public SkillControllerBuilder() {
            // Null pattern for this very builder.
            super();
        }

        @Override
        public String perform() {
            log("perform (Builder) - Visitor " + visitor.getId() + " skill built : " + skillBuilt);
            // When the user has all the skills and the list is empty, guarding this case.
            if (skillBuilt == null) return Constants.CURRENT_PAGE;
            dao.addSkill(employee, skillBuilt);
            Collection<SkillAssociation> employeeSkills = employee.getSkills();
            // New removers and addable skills.
            buildControllerRemovers(employee.getSkills());
            skillsList = getSkillsAddable(employeeSkills);
            return Constants.CURRENT_PAGE;
        }

        public Skill getSkillBuilt() {
            return skillBuilt;
        }

        public void setSkillBuilt(Skill skillBuilt) {
            this.skillBuilt = skillBuilt;
        }
    }

    /**
     * This controller skill allows the update of a skill by voting up or down values.
     */
    public class SkillControllerUpdater extends BaseSkillController {
        public SkillControllerUpdater(SkillAssociation skill) {
            super(skill);
        }

        @Override
        public String perform() {
            log("perform - (Updater) Visitor " + visitor.getId() + " has voted : " + hasVoted);
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

    // Here we are sure that visitor is not null. Otherwise we are in the SkillControllerObserver class.
    public abstract class BaseSkillController extends Logger {

        // Caches for JSF getters.
        final SkillAssociation skill;
        final String name;
        final long skillId;
        boolean hasVoted; // If the current visitor has voted for this very skill or not.
        int level;

        /**
         * Constructor wrapping the given skill.
         */
        public BaseSkillController(SkillAssociation skill) {
            this.skill = skill;
            skillId = skill.getSkillId();
            name = skill.getSkill().getName();
            level = skill.getLevel();
            hasVoted = visitorConnected && (visitor != null && skill.hasVoted(visitor));
        }

        /**
         * Null pattern constructor.
         */
        public BaseSkillController() {
            this.skill = null;
            skillId = -1;
            name = "skill_name";
            level = 0;
            hasVoted = false;
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

    public SkillControllerBuilder getBuilder() {
        return builder;
    }
}
