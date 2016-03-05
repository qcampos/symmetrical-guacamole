package fr.upem.hireanemployee;

import com.google.common.collect.Lists;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Formation;
import fr.upem.hireanemployee.profildata.School;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by Quentin on 26/02/2016.
 */
@Stateless
public class EmployeeFormationDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    /**
     * Update the formation corresponding to the given id with the provided values.
     *
     * @param id          the identifier of the modified formation.
     * @param name        the new name of the formation.
     * @param description the new description of the formation.
     * @param level       the new degree type of the formation.
     * @param school      the new school of the formation.
     * @return the modified formation.
     */
    public Formation updateFormation(final long id, final String name, final String description, final Formation.DegreeType level, final School school, final Date startDate, final Date endDate) {
        Formation formation = new Formation(name, description, level, school, startDate, endDate);
        formation.setId(id);
        em.merge(formation);
        em.flush();
        return formation;
    }


    /**
     * Add a formation to the the Employee corresponding to the given Id.
     *
     * @param name        name of the formation.
     * @param description description of the formation.
     * @param level       degree type of this formation.
     * @param school      school of the formation.
     * @param employeeId  id of the employee.
     * @return the list of all the formations of the employee.
     */
    public List<Formation> createFormation(final String name, final String description, final Formation.DegreeType level, final School school, final Date startDate, final Date endDate, final long employeeId) {
        Employee employee = em.find(Employee.class, employeeId);
        return createFormation(name, description, level, school, startDate, endDate, employee);
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
    public List<Formation> createFormation(final String name, final String description, final Formation.DegreeType level, final School school, final Date startDate, final Date endDate, final Employee employee) {
        Formation formation = new Formation(name, description, level, school, startDate, endDate);
        employee.addFormation(formation);
        em.merge(employee);
        em.flush();
        return Lists.newArrayList(employee.getFormations());
    }

}
