package fr.upem.hireanemployee;

import fr.upem.hireanemployee.beans.SearchBean;
import fr.upem.hireanemployee.profildata.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.upem.hireanemployee.profildata.Country.*;

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
        Employee e = new Employee(firstName, lastName, "", NONE, emptySector, email, encrypt(password));
        Logger.log("signup - description : " + e.getDescription(), Logger.BEAN); // TODO delete debug.
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
        return em.createQuery("SELECT e FROM Employee e WHERE upper(e.firstName) LIKE upper(:name) OR upper(e.lastName) LIKE upper(:name)")
                .setParameter("name", "%" + name + "%").getResultList();
    }

    /**
     * Finds a skill by its name.
     *
     * @param name has to be the exact skill's name.
     * @return the skill found, null otherwise.
     */
    public Skill getSkillByName(final String name) {
        // We know for sure that only query inside the module are done on it. (see usage shortcut).
        return em.createQuery("SELECT s FROM Skill s WHERE s.name = :name", Skill.class).setParameter("name", name)
                .getResultList().get(0);
    }

    public SkillAssociation getEmployeeMaxSkill(final Employee employee) {
        try {
            Skill skill = em.createQuery("SELECT s FROM Employee e JOIN e.skills es " +
                    "JOIN es.skill s ORDER BY es.level DESC", Skill.class)
                    .setMaxResults(1).getSingleResult();
            return employee.getSkillAssociation(skill);
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Employee> advancedSearchedEmployee(String sector, List<Country> countries,
                                                   List<SearchBean.SkillFilterBundle> skills,
                                                   Employee requester,
                                                   boolean filteringEmployeeSelection) {
        System.out.println("[BEAN] Advanced string countries : " + countries);
        boolean setCountryParam = false;
        boolean setSectorParam = false;
        try {
            String queryString = "SELECT emp FROM Employee emp JOIN emp.description descr LEFT JOIN emp.skills ES ";
            // No countries.
            if (countries.size() == 0) {
                // The sector.
                if (!sector.equals(Sector.DEFAULT_NAME)) {
                    setSectorParam = true;
                    queryString = queryString + "WHERE (descr.sector.name = :sector) ";
                }
            } else {
                setCountryParam = true;
                queryString = queryString + "WHERE (descr.country IN :countries) ";
                // The sector.
                if (!sector.equals(Sector.DEFAULT_NAME)) {
                    setSectorParam = true;
                    queryString = queryString + "AND (descr.sector.name = :sector) ";
                }
            }
            // The final filter.
            StringBuilder sb = generateSkillConstraint(skills, setCountryParam || setSectorParam);
            queryString = queryString + sb.toString() + "GROUP BY emp HAVING COUNT(emp) >= " + skills.size();
            TypedQuery<Employee> query = em.createQuery(queryString, Employee.class);
            if (setCountryParam) {
                query.setParameter("countries", countries);
            }
            if (setSectorParam) {
                query.setParameter("sector", sector);
            }
            List<Employee> resultList = query.getResultList();
            if (requester != null && filteringEmployeeSelection) {
                // Filtering selected employee.
                Iterator<Employee> it = resultList.iterator();
                Collection<Employee> selection1 = requester.getSelection1();
                while (it.hasNext()) {
                    if (!selection1.contains(it.next())) {
                        it.remove();
                    }
                }
            }
            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }

    private StringBuilder generateSkillConstraint(List<SearchBean.SkillFilterBundle> skills, boolean otherSkills) {
        StringBuilder sb = new StringBuilder();

        if (skills.size() > 0) {
            if (otherSkills) {
                sb.append(" AND (");
            } else {
                sb.append(" WHERE (");
            }
            for (int i = 0; i < skills.size() - 1; i++) {
                SearchBean.SkillFilterBundle skill = skills.get(i);
                sb.append("(ES.skillId = " + skill.get().getId() + " AND ES.level >= " + skill.getMinLevel() + ") OR ");
            }
            SearchBean.SkillFilterBundle last = skills.get(skills.size() - 1);
            if (last != null) {
                sb.append("(ES.skillId = " + last.get().getId() + " AND ES.level >= " + last.getMinLevel() + ")");
            }
            sb.append(") ");
        }
        return sb;
    }

    @SuppressWarnings("unused")
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
        return Arrays.asList(values()).stream().map(new Function<Country, String>() {
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
        for (Country c : values()) {
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
