package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.*;

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
    @OneToOne(cascade = CascadeType.ALL)
    private EmployeeDescription description;

    // Account data.
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "{invalid.email}")
    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    // Profile data.
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Collection<SkillAssociation> skills;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Employee> relations;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Collection<Formation> formations;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Collection<Experience> experiences;

    public Employee() {
    }

    public Employee(final String firstName, final String lastName, final String professionalTitle, final Country country, final Sector sector, final String email, final String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = new EmployeeDescription(this, professionalTitle, country, sector);
        this.email = email;
        this.password = password;
        skills = new ArrayList<>();
        relations = new ArrayList<>();
        formations = new ArrayList<>();
        experiences = new ArrayList<>();
    }

    public Employee(final String firstName, final String lastName, final String professionalTitle, final Country country, final Sector sector, final String email, final String password,
                    final Collection<SkillAssociation> skills, final Collection<Employee> relations, final Collection<Formation> formations, final Collection<Experience> experiences) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = new EmployeeDescription(this, professionalTitle, country, sector);
        this.email = email;
        this.password = password;
        this.skills = skills;
        this.relations = relations;
        this.formations = formations;
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

    public EmployeeDescription getDescription() {
        return description;
    }

    public void setDescription(final EmployeeDescription description) {
        this.description = description;
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

    public Collection<SkillAssociation> getSkills() {
        return skills;
    }

    public void setSkills(final Collection<SkillAssociation> skills) {
        this.skills = skills;
    }

    public Collection<Employee> getRelations() {
        return relations;
    }

    public void setRelations(final Collection<Employee> relations) {
        this.relations = relations;
    }

    public Collection<Formation> getFormations() {
        return formations;
    }

    public void setFormations(final Collection<Formation> formations) {
        this.formations = formations;
    }

    public Collection<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(final Collection<Experience> experience) {
        this.experiences = experience;
    }

    public void addSkill(Skill skill) { // TODO auto delete with an interceptor.
        SkillAssociation association = new SkillAssociation();
        association.setEmployee(this);
        association.setEmployeeId(id);
        association.setSkill(skill);
        association.setSkillId(skill.getId());
        // Adding on skills side
        Collection<SkillAssociation> employees = skill.getEmployees();
        employees.add(association);
        skills.add(association);
    }

    public void addRelation(Employee relation) {
        relations.add(relation);
        // Relations are always bidirectional.
        if (!relation.relations.contains(this)) {
            relation.relations.add(this);
        }
    }

    public void addFormation(Formation formation) {
        formations.add(formation);
    }

    public void addExperience(Experience experience) {
        experiences.add(experience);
    }

    public void removeRelation(Employee relation) {
        relations.remove(relation);
        // Relations are always bidirectional.
        if (relation.relations.contains(this)) {
            relation.removeRelation(this);
        }
    }

    public void removeExperienceById(Experience experience) {
        Logger.log("Employee removeExperienceById experience ids to delete : " + experience.getId(), Logger.BEAN);
        Logger.log("Employee removeExperienceById Before the deletion : " + Experience.printIds(experiences), Logger.BEAN);
        experiences.remove(experience);
        Logger.log("Employee removeExperienceById After the deletion : " + Experience.printIds(experiences), Logger.BEAN);
    }

    public void removeFormationById(Formation formation) {
        Logger.log("Employee removeFormationById experience ids to delete : " + formation.getId(), Logger.BEAN);
        Logger.log("Employee removeFormationById Before the deletion : " + Formation.printIds(formations), Logger.BEAN);
        formations.remove(formation);
        Logger.log("Employee removeFormationById After the deletion : " + Formation.printIds(formations), Logger.BEAN);
    }

    public int getNbRelations() {
        return relations.size();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", description=" + description +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", skills=" + skills +
                ", relations=" + relations +
                ", formations=" + formations +
                ", experiences=" + experiences +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    // Care of infinite calls (calling with this then removeEmployee call here with
    // The same skill.
    public boolean removeSkill(Skill skill) { // TODO auto delete with an interceptor.
        Iterator<SkillAssociation> it = skills.iterator();
        while (it.hasNext()) {
            SkillAssociation association = it.next();
            // equals only compares ids.
            if (association.getSkillId() == (skill.getId())) {
                skill.removeAssociation(association);
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
        Iterator<SkillAssociation> it = skills.iterator();
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
