package fr.upem.hireanemployee.profildata;

import fr.upem.hireanemployee.Employee;

import javax.persistence.*;
import java.util.Collection;
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
    }

    public Skill(final String name) {
        this.name = name;
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
}
