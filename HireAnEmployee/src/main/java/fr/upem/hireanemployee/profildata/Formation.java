package fr.upem.hireanemployee.profildata;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

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
    private Visibility visibility;
    @Column(length = 2000)
    private String description;
    private DegreeType level;
    @ManyToOne(cascade = CascadeType.ALL)
    private School school;
    @Temporal(javax.persistence.TemporalType.DATE)
    // @Past
    private Date startDate;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;

    public Formation() {
    }

    public Formation(final String name, final String description, final DegreeType level, final School school,
                     final Date startDate, final Date endDate, final Visibility visibility) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.school = school;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visibility = visibility;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date from) {
        this.startDate = from;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date to) {
        this.endDate = to;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return String.format("%s : %s at %s", level, description, school);
    }

    @Override
    public boolean equals(Object obj) {
        // Careful, our equal is a handle with an id
        // (only thing needed inside EmployeeFormationeBean class).
        if (!(obj instanceof Formation)) {
            return false;
        }
        Formation formation = (Formation) obj;
        return id == formation.id;
    }

    /**
     * @return The concatenation of every ids in the given Formation Collection.
     */
    public static String printIds(Collection<Formation> formations) {
        StringBuilder n = new StringBuilder();
        for (Formation e : formations) {
            n.append(e.getId()).append(' ');
        }
        return n.toString();
    }
}
