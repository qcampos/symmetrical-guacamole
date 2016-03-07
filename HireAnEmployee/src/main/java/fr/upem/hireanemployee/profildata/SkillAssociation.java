package fr.upem.hireanemployee.profildata;

import fr.upem.hireanemployee.Employee;

import javax.persistence.*;

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

    public void increaseLevel() {
        level++;
    }

    public void decreaseLevel() {
        level--;
        if (level < 0) level = 0;
    }
}
