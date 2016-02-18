package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Employee {

    // User data.
    @Id
    @GeneratedValue
    private long id;
    private String firstName;
    private String lastName;
    private String currentJob;
    private Country country;
    @ManyToOne(cascade = CascadeType.ALL)
    private Domain domain;


    // Account data.
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "{invalid.email}")
    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    // Profile data.
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Language> skills;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Employee> relations;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Degree> degrees;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Experience> experiences;

    public Employee() {
    }

    public Employee(final String firstName, final String lastName, final String currentJob, final Country country, final Domain domain, final String email, final String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentJob = currentJob;
        this.country = country;
        this.domain = domain;
        this.email = email;
        this.password = password;
        skills = new ArrayList<>();
        relations = new ArrayList<>();
        degrees = new ArrayList<>();
        experiences = new ArrayList<>();
    }

    public Employee(final String firstName, final String lastName, final String currentJob, final Country country, final Domain domain, final String email, final String password, final Collection<Language> skills, final Collection<Employee> relations, final Collection<Degree> degrees, final Collection<Experience> experiences) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentJob = currentJob;
        this.country = country;
        this.domain = domain;
        this.email = email;
        this.password = password;
        this.skills = skills;
        this.relations = relations;
        this.degrees = degrees;
        this.experiences = experiences;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(final String currentJob) {
        this.currentJob = currentJob;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(final Domain domain) {
        this.domain = domain;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Collection<Language> getSkills() {
        return skills;
    }

    public void setSkills(final Collection<Language> skills) {
        this.skills = skills;
    }

    public Collection<Employee> getRelations() {
        return relations;
    }

    public void setRelations(final Collection<Employee> relations) {
        this.relations = relations;
    }

    public Collection<Degree> getDegrees() {
        return degrees;
    }

    public void setDegrees(final Collection<Degree> degrees) {
        this.degrees = degrees;
    }

    public Collection<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(final Collection<Experience> experience) {
        this.experiences = experience;
    }

    public void addSkill(Language skill) {
        skills.add(skill);
    }

    public void addRelation(Employee relation) {
        relations.add(relation);
        // Relations are always bidirectionnals
        if (!relation.relations.contains(this)) {
            relation.relations.add(this);
        }

    }

    public void addDegree(Degree degree) {
        degrees.add(degree);
    }

    public void addExperience(Experience experience) {
        experiences.add(experience);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", currentJob='" + currentJob + '\'' +
                ", country=" + country +
                ", domain=" + domain +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", skills=" + skills +
                ", relations=" + relations +
                ", degrees=" + degrees +
                ", experiences=" + experiences +
                '}';
    }
}
