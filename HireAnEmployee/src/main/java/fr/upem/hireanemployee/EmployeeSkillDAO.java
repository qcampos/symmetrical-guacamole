package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.Skill;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Handles Skills'/Employee's database merges.
 */
@Stateless
public class EmployeeSkillDAO extends Logger {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    /**
     * Creates a skill. And persists it.
     *
     * @return the current list of skills into the database.
     */
    public void createSkill(String name) {
        Logger.log("Requested creation of skill : " + name, Logger.DATABASE);
        em.merge(new Skill(name));
        em.flush();
    }

    /**
     * Adds a new skill to the given employee.
     * And persists it.
     */
    public void addSkill(Employee employee, Skill skill) {
        employee.addSkill(skill);
        // Updates the given reference.
        employee.setSkills(merge(employee).getSkills());
    }

    /**
     * Removes the given skill of the given employee.
     * And persists it.
     */
    public void removeSkill(Employee employee, Skill skill) {
        employee.removeSkill(skill);
        // Normally no needs to update the current list,
        // But for sanity check purposes.
        employee.setSkills(merge(employee).getSkills());
    }


    public void increaseSkill(Employee voter, String skillName, Employee employee) {
        log("increaseSkill - skill increased : " + employee.increaseSkill(skillName, voter));
        employee.setSkills(merge(employee).getSkills());
    }

    public void decreaseSkill(Employee voter, String skillName, Employee employee) {
        log("decreaseSkill - skill decreased : " + employee.decreaseSkill(skillName, voter));
        employee.setSkills(merge(employee).getSkills());
    }

    private Employee merge(Employee employee) {
        Employee merge = em.merge(employee);
        em.flush();
        return merge;
    }
}
