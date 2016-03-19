package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.Skill;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collection;

/**
 * Handles Employee's selections.
 */
@Stateless
public class EmployeeSelectionDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    /**
     * Adds selected employee to employee's selection1 list.
     *
     * @param employee the employee selecting selected.
     * @param selected the selected employee.
     * @return the updated list.
     */
    public Collection<Employee> addSelection1(Employee employee, Employee selected) {
        employee.addToSelection1(selected);
        return mergeSetGet(employee);
    }

    /**
     * Removes removed employee from employee's selection1 list.
     *
     * @param employee the employee removing removed.
     * @param removed  the removed employee.
     * @return the updated list.
     */
    public Collection<Employee> removeSelection1(Employee employee, Employee removed) {
        employee.removeSelection1(removed);
        return mergeSetGet(employee);
    }

    private Collection<Employee> mergeSetGet(Employee employee) {
        Employee merge = em.merge(employee);
        em.flush();
        Collection<Employee> selection1 = merge.getSelection1();
        employee.setSelection1(selection1);
        return selection1;
    }

    public boolean isSelected(Employee employee, Employee employeeTested) {
        if (employee == null || employeeTested == null) {
            return false;
        }
        return employee.getSelection1().contains(employeeTested);
    }
}
