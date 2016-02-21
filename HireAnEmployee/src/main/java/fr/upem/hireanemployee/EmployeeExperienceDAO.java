package fr.upem.hireanemployee;

import com.google.common.collect.Lists;
import fr.upem.hireanemployee.profildata.Experience;

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

    public Experience updateExperience(long id, final String companyName, final String jobName, final String jobAbstract, final String jobDescription, final Date startDate, final Date endDate) {
        Experience experience = new Experience(companyName, jobName, jobAbstract, jobDescription, startDate, endDate);
        experience.setId(id);
        em.merge(experience);
        em.flush();
        return experience;
    }

    public List<Experience> createExperience(final String companyName, final String jobName, final String jobAbstract, final String jobDescription, final Date startDate, final Date endDate, final long employeeId) {
        Experience experience = new Experience(companyName, jobName, jobAbstract, jobDescription, startDate, endDate);
        Employee employee = em.find(Employee.class, employeeId);
        employee.addExperience(experience);
        em.merge(employee);
        em.flush();
        return Lists.newArrayList(employee.getExperiences());
    }

}
