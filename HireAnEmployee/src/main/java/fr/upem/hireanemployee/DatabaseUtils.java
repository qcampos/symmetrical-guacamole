package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.Language;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Quentin on 18/02/2016.
 */
@Stateless
public class DatabaseUtils {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    public void register(Object o) {
        try {
            em.persist(o);
            em.flush();
        } catch (Exception e) {
            throw new IllegalStateException("There was an error while adding an item to the database", e);
        }
    }

    public List<Language> getSkillsByName(final String name) {
        return em.createQuery("SELECT s FROM Language s WHERE s.name LIKE :name").setParameter("name", name).getResultList();
    }

    public void cleanAll() {
        em.clear();
    }

    public Employee getEmployeeByMail(final String email) {
        return em.createQuery("SELECT e from Employee e where e.email = :email", Employee.class).setParameter("email", email).getSingleResult();
    }
}
