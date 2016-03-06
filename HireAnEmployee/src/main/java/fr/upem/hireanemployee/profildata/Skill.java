package fr.upem.hireanemployee.profildata;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @OneToMany(mappedBy = "skill")
    private Collection<SkillAssociation> employees;

    public Skill() {
        employees = new ArrayList<>();
    }

    public Skill(final String name) {
        this.name = name;
        employees = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Collection<SkillAssociation> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<SkillAssociation> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Skill)) {
            return false;
        }
        Skill skill = (Skill) obj;
        return skill.id == id;
    }


    // Care of infinite calls (calling with this then removeSkill call here with
    // The same employee.
    public boolean removeEmployee(Employee employee) { // TODO auto delete with an interceptor.
        Iterator<SkillAssociation> it = employees.iterator();
        while (it.hasNext()) {
            // Compares ids.
            SkillAssociation association = it.next();
            if (association.getEmployee().equals(employee)) {
                employee.removeAssociation(association);
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Only removes the association on this side without calling the other part of it.
     *
     * @return true if deleted, false otherwise if not found.
     */
    public boolean removeAssociation(SkillAssociation association) {
        Iterator<SkillAssociation> it = employees.iterator();
        while (it.hasNext()) {
            // Compares ids.
            if (it.next().getEmployeeId() == association.getEmployeeId()) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}
