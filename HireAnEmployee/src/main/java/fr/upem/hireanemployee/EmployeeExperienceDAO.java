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
 * Created by Quentin on 21/02/2016.
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
        Logger.log("[BEAN] experience created with id " + experience.getId(), Logger.BEAN);
        employee.addExperience(experience);
        return mergeEmployeeExperiences(employee);
    }

    /**
     * Deletes the given experience of identifier ids in employee.
     *
     * @return The updated list of experiences from the database.
     */
    public List<Experience> deleteExperience(final Employee employee) {
        // Employee employee = em.find(Employee.class, employeeId);
        // employee.removeExperienceById(ids);
        return mergeEmployeeExperiences(employee);
        /*
        Query query = em.createQuery("DELETE FROM Experience e WHERE e.id = :ids");
        int deletedCount = query.setParameter("ids", ids.get(0)).executeUpdate();
        Logger.log("deleteExperience : number : " + deletedCount, Logger.BEAN);
        return em.createQuery("Select e From Experience e WHERE e.id = :employeeId", Experience.class)
                .setParameter("employeeId", employeeId).getResultList();
         */
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
        Logger.log("Entities in merge result : " + Experience.printIds(merge.getExperiences()), Logger.BEAN);
        return Lists.newArrayList(employee.getExperiences());
    }
}
