package fr.upem.hireanemployee;

import sun.reflect.LangReflectAccess;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Employee {

    @Id
    @GeneratedValue
    private long id;
    private String firstName;
    private String lastName;
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "{invalid.email}")
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Collection<Language> skills;

    public Employee() {
    }

    public Employee(final String firstName, final String lastName, final String email, final String password, final Collection<Language> skills) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.skills = skills;
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


    public void addSkill(Language skill) {
        skills.add(skill);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", skills=" + skills +
                '}';
    }
}
