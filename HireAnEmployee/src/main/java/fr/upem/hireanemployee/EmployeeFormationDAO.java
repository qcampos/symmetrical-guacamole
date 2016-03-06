package fr.upem.hireanemployee;

import com.google.common.collect.Lists;
import fr.upem.hireanemployee.profildata.Formation;
import fr.upem.hireanemployee.profildata.School;
import fr.upem.hireanemployee.profildata.Visibility;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;


/**
 * Handles Formation' database transactions.
 */
@Stateless
public class EmployeeFormationDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    /**
     * Update the formation with the provided values into the database.
     */
    public void updateFormation(final Formation formation, final String name, final String description, final Formation.DegreeType level,
                                final School school, final Date startDate, final Date endDate, final Visibility visibility) {

        formation.setName(name);
        formation.setDescription(description);
        formation.setLevel(level);
        formation.setSchool(school);
        formation.setVisibility(visibility);
        formation.setStartDate(startDate);
        formation.setEndDate(endDate);
        em.merge(formation);
        em.flush();
    }

    /**
     * Add the formation corresponding to the given values to the given employee.
     *
     * @param name        name of the formation.
     * @param description description of the formation.
     * @param level       degree type of this formation.
     * @param school      school of the formation.
     * @param employee    to employee.
     * @return the list of all the formations of the employee.
     */
    public List<Formation> createFormation(final String name, final String description, final Formation.DegreeType level,
                                           final School school, final Date startDate, final Date endDate, final Employee employee,
                                           Visibility visibility) {
        Formation formation = new Formation(name, description, level, school, startDate, endDate, visibility);
        employee.addFormation(formation);
        em.merge(employee);
        em.flush();
        return Lists.newArrayList(employee.getFormations());
    }

}
