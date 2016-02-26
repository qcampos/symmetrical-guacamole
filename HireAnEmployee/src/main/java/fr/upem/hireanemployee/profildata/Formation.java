package fr.upem.hireanemployee.profildata;

import javax.persistence.*;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Formation {

    public enum DegreeType {
        // TODO add some degrees or switch to a entity to get a user-provided content.
        BACHELOR(2), MASTER(5), LICENCE(3), PHD(8), NONE(0);

        private final int years;

        private DegreeType(int years) {
            this.years = years;
        }

        public int getYears() {
            return years;
        }

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
    private String description;
    private DegreeType level;
    @ManyToOne(cascade = CascadeType.ALL)
    private School school;

    public Formation() {
    }

    public Formation(final String name, final String description, final DegreeType level, final School school) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.school = school;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public DegreeType getLevel() {
        return level;
    }

    public void setLevel(final DegreeType level) {
        this.level = level;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(final School school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return String.format("%s : %s at %s", level, description, school);
    }
}
