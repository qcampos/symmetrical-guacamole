package fr.upem.hireanemployee;

import com.google.common.collect.Lists;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Visibility;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Quentin on 21/02/2016.
 */
@Stateless
public class EmployeeExperienceDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    public Experience updateExperience(long id, final String companyName, final String jobName, final String jobAbstract,
                                       final String jobDescription, final Country country, final Visibility visibility,
                                       final Date startDate, final Date endDate) {
        Experience experience = new Experience(companyName, jobName, jobAbstract, jobDescription, country, startDate, endDate, visibility);
        experience.setId(id);
        em.merge(experience);
        em.flush();
        return experience;
    }


    /**
     * With employee ID.
     */
    public List<Experience> createExperience(final String companyName, final String jobName, final String jobAbstract,
                                             final String jobDescription, final Country country, final Visibility visibility,
                                             final Date startDate, final Date endDate, final long employeeId) {
        Employee employee = em.find(Employee.class, employeeId);
        return createExperience(companyName, jobName, jobAbstract, jobDescription, country, visibility, startDate, endDate, employee);
    }

    /**
     * Surcharge needed for some cases.
     */
    public List<Experience> createExperience(final String companyName, final String jobName, final String jobAbstract,
                                             final String jobDescription, final Country country, final Visibility visibility,
                                             final Date startDate, final Date endDate, final Employee employee) {
        Experience experience = new Experience(companyName, jobName, jobAbstract, jobDescription, country, startDate, endDate, visibility);
        employee.addExperience(experience);
        em.merge(employee);
        em.flush();
        return Lists.newArrayList(employee.getExperiences());
    }
}
