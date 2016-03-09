package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Quentin on 18/02/2016.
 */
@Stateless
// TODO extend the custom Logger class to log
public class DatabaseDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;


    /**
     * Encrypt the password in order to store in with some security.
     *
     * @param password the password to be encrypted.
     * @return the String representation of the encryted password.
     */
    private static String encrypt(String password) {
        try {
            return new String(MessageDigest.getInstance("sha1").digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    /**
     * Check if the given credentials are bind to a user into the database and return the right one. Otherwise, return <code>null</code>
     *
     * @param email    the user's email used as a login.
     * @param password the user's password.
     * @return the corresponding Employee object, or null if the credential does not match.
     */
    public Employee connect(String email, String password) {
        try {
            Employee result = em.createQuery("SELECT e FROM Employee e WHERE e.email = :email AND e.password = :password", Employee.class)
                    .setParameter("email", email).setParameter("password", encrypt(password))
                    .getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Register a new user into the database.
     *
     * @param firstName
     * @param lastName
     * @param email     the user's email which will be used as login.
     * @param password  the use's password.
     * @return the newly created Employee object.
     */
    public Employee signup(String firstName, String lastName, String email, String password) {
        // Try to get the empty domain
        Sector emptySector;
        try {
            emptySector = em.createQuery("SELECT d FROM Sector d WHERE d.name = '" + Sector.DEFAULT_NAME + "'", Sector.class).getSingleResult();
        } catch (NoResultException e) {
            emptySector = new Sector(Sector.DEFAULT_NAME);
        }

        // Prevent SQL exception is these very code.
        if (emailExists(email)) {
            return null;
        }
        // Persist the new employee into the database.
        Employee e = new Employee(firstName, lastName, "", Country.NONE, emptySector, email, encrypt(password));
        em.persist(e);
        em.flush();
        return e;
    }

    /**
     * Check if the given email is already in use in the database.
     *
     * @param email the email to check.
     * @return <code>true</code> if the user is already in use, <code>false</code> otherwise.
     */
    public boolean emailExists(String email) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.email = :email").setParameter("email", email).getResultList().size() > 0;
    }

    /**
     * Update the given employee to get the last version stored in the database.
     *
     * @param employee the employee to update.
     * @return another Employee object with up-to-date values.
     */
    public Employee update(Employee employee) {
        return em.find(Employee.class, employee.getId());
    }

    public Employee getEmployeeByMail(final String email) {
        return em.createQuery("SELECT e from Employee e where e.email = :email", Employee.class).setParameter("email", email).getSingleResult();
    }

    public List<Employee> searchEmployeeByName(String name) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.firstName LIKE :name OR e.lastName LIKE :name").setParameter("name", name).getResultList();
    }

    public List<Employee> searchEmployeeBySkill(final String skillname) {
        List<Employee> employees = em.createQuery("SELECT e FROM Employee e").getResultList();
        List<Employee> skilledEmployees = new ArrayList<>();
/* TODO remove comments
        for (Employee e : employees) {
            for (Skill s : e.getSkills()) {
                if (s.getName().contains(skillname)) {
                    skilledEmployees.add(e);
                }
            }
        }*/
        return skilledEmployees;
    }

    public void register(Object o) {
        try {
            em.persist(o);
            em.flush();
        } catch (Exception e) {
            throw new IllegalStateException("There was an error while adding an item to the database", e);
        }
    }


    /**
     * Finds a skill by its name.
     * @param name has to be the exact skill's name.
     * @return the skill found, null otherwise.
     */
    public Skill getSkillByName(final String name) {
        return em.createQuery("SELECT s FROM Skill s WHERE s.name = :name", Skill.class).setParameter("name", name)
                .getSingleResult();
    }

    public void cleanAll() {
        em.clear();
    }

    /**
     * Retrieves the corresponding employee in the database.
     *
     * @param id primary key of the employee wanted.
     * @return The employee found, null otherwise.
     */
    public Employee getEmployeeByID(long id) {
        return em.find(Employee.class, id);
    }

    /**
     * Merges the given employee into the database.
     *
     * @param employee merge result.
     */
    public Employee mergeEmployee(Employee employee) {
        Employee merge = em.merge(employee);
        em.flush();
        return merge;
    }

    /**
     * Get a list of all the possible countries in the Country enumeration.
     *
     * @return a list of all the possible Countries.
     */
    public List<String> getCountryList() {
        return Arrays.asList(Country.values()).stream().map(new Function<Country, String>() {
            @Override
            public String apply(final Country country) {
                return country.toString();
            }
        }).collect(Collectors.<String>toList());
    }

    /**
     * Get a list of all the existing sectors.
     *
     * @return a list of all existing sectors.
     */
    public List<String> getSectorList() {
        return em.createQuery("Select s.name From Sector s", String.class).getResultList();
    }

    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();
        for (Country c : Country.values()) {
            countries.add(c);
        }
        return countries;
    }

    public List<Visibility> getVisibilities() {
        List<Visibility> visibilities = new ArrayList<>();
        for (Visibility v : Visibility.values()) {
            visibilities.add(v);
        }
        return visibilities;
    }

    public <T> T merge(T obj) {
        T merge = em.merge(obj);
        em.flush();
        return merge;
    }

    public List<Skill> getSkills() {
        return em.createQuery("Select s From Skill s", Skill.class).getResultList();
    }
}
