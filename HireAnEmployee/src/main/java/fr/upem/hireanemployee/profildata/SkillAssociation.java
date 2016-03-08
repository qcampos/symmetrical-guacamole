package fr.upem.hireanemployee.profildata;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "EMPLOYEE_SKILL")
@IdClass(SkillAssociationId.class)
public class SkillAssociation {

    @Id
    private long employeeId;
    @Id
    private long skillId;
    @Column(name = "LEVEL")
    private int level;
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "EMPLOYEEID", referencedColumnName = "ID")
    private Employee employee;
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "SKILLID", referencedColumnName = "ID")
    private Skill skill;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Employee> voters;

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getSkillId() {
        return skillId;
    }

    public void setSkillId(long skillId) {
        this.skillId = skillId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill sill) {
        this.skill = sill;
    }

    public List<Employee> getVoters() {
        return voters;
    }

    public void setVoters(List<Employee> voters) {
        this.voters = voters;
    }

    public boolean increaseLevel(Employee voter) {
        if (voters.contains(voter)) {
            Logger.log("[BEAN] SkillAssociation#increaseLevel - voter id : " +
                    voter.getId() + "already exists in : " + employeeId, Logger.BEAN);
            return false;
        }
        level++;
        voters.add(voter);
        return true;
    }

    public boolean decreaseLevel(Employee voter) {
        if (!voters.remove(voter)) {
            Logger.log("[BEAN] SkillAssociation#increaseLevel - voter " +
                    voter.getId() + " not exists " + employeeId, Logger.BEAN);
            return false;
        }
        level--;
        if (level < 0) level = 0;
        return true;
    }

    public boolean hasVoted(Employee voter) {
        for (Employee employee : voters) {
            if (employee.getId() == voter.getId()) {
                Logger.log("[BEAN] SkillAssociation#hasVoted - " + voter.getId() + " has voted ",
                        Logger.BEAN);
                return true;
            }
        }
        Logger.log("[BEAN] SkillAssociation#hasVoted - has not voted ", Logger.BEAN);
        return false;
    }
}
