package fr.upem.hireanemployee;

import com.google.common.collect.Lists;
import com.oracle.tools.packager.Log;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Visibility;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Handles Experiences' database transactions.
 */
@Stateless
public class EmployeeExperienceDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    public void updateExperience(Experience experience, final String companyName, final String jobName, final String jobAbstract,
                                 final String jobDescription, final Country country, final Visibility visibility,
                                 final Date startDate, final Date endDate) {
        experience.setCompanyName(companyName);
        experience.setJobAbstract(jobAbstract);
        experience.setJobName(jobName);
        experience.setCountry(country);
        experience.setStartDate(startDate);
        experience.setEndDate(endDate);
        experience.setJobDescription(jobDescription);
        experience.setVisibility(visibility);
        em.merge(experience);
        em.flush();
    }

    /**
     * Surcharge needed for some cases.
     */
    public List<Experience> createExperience(final String companyName, final String jobName, final String jobAbstract,
                                             final String jobDescription, final Country country, final Visibility visibility,
                                             final Date startDate, final Date endDate, final Employee employee) {
        Experience experience = new Experience(companyName, jobName, jobAbstract, jobDescription, country, startDate, endDate, visibility);
        employee.addExperience(experience);
        return mergeEmployeeExperiences(employee);
    }

    /**
     * Merges the employee experiences into the database.
     *
     * @return The updated list of experiences from the database.
     */
    private List<Experience> mergeEmployeeExperiences(Employee employee) {
        Employee merge = em.merge(employee);
        em.flush();
        employee.setExperiences(merge.getExperiences());
        Logger.log("Entities in merge result : " + Experience.printIds(merge.getExperiences()), Logger.DATABASE);
        return Lists.newArrayList(employee.getExperiences());
    }
}
