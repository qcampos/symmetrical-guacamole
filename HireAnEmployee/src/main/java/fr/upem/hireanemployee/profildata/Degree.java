package fr.upem.hireanemployee.profildata;

import javax.persistence.*;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Degree {

    public enum DegreeType {
        // TODO add some degrees or switch to a entity to get a user-provided content.
        BACHELOR, MASTER, LICENCE, PHD;

        @Override
        public String toString() {
            String s = super.toString();
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }


    @Id
    @GeneratedValue
    private long id;
    private String name;
    private DegreeType level;
    @ManyToOne(cascade = CascadeType.ALL)
    private School school;

    public Degree() {
    }

    public Degree(final String name, final DegreeType level, final School school) {
        this.name = name;
        this.level = level;
        this.school = school;
    }
}
