package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Formation;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collection;

/**
 * Created by Baxtalou on 22/02/2016.
 * The main bean for the page : cv.xhtml.
 * Manages the employee's data bootsrap to feed : EmployeeDescriptionBean,
 * EmployeeExperiencesBean and EmployeeFormationBean.
 */
@ManagedBean
@ViewScoped
public class CvViewedBean extends Logger {

    @EJB
    private DatabaseDAO dao;

    private Employee employee;
    private EmployeeDescription employeeDescription;
    private Collection<Experience> employeeExperiences;
    private Collection<Formation> employeeFormations;

    // Current CV id
    private String idToParse;
    private long id;
    private boolean initialized;

    /**
     * Method call on the viewAction of page cv.xhtml. It provides initialization capabilities
     * and prevent multiple calls when post init or setter are used on ViewScoped bean.
     * Delegated backing beans like EmployeeDescriptionBean of EmployeeExperienceBean will have
     * direct injections of the description and experience of the retrieved user, thanks to
     * this lifecycle use.
     *
     * @return The current page if the id corresponds to an Employee. The 404 error page otherwise.
     * TODO redirect on a research page + the message "unknown employee" instead.
     */
    public String viewActionInit() {
        // Guard needed because of viewParam Bug with ajax in the API 2.2
        if (initialized) {
            return Constants.CURRENT_PAGE;
        }
        initialized = true;
        log("viewActionInit - id " + id);
        // Retrieving the employee in the database.
        employee = dao.getEmployeeByID(id);
        if (employee == null) {
            // TODO redirect on a special page for unknown employee + search bar.
            return Navigations.redirect(Constants.ERROR);
        }
        // Setting fields accordingly.
        employeeDescription = employee.getDescription();
        employeeExperiences = employee.getExperiences();
        employeeFormations = employee.getFormations();
        log("init - employee " + employee.getFirstName() + " Exp : " + employeeExperiences + " Desc : " + employeeDescription
                + " Form : " + employeeFormations);
        return Constants.CURRENT_PAGE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        // Guard needed because of viewParam Bug with ajax in the API 2.2
        if (initialized) {
            return;
        }
        this.id = id;
    }

    public String getIdToParse() {
        return idToParse;
    }

    public void setIdToParse(String idToParse) {
        if (initialized) {
            return;
        }
        try {
            this.id = Long.parseLong(idToParse);
        } catch (NumberFormatException e) {
            log("ERROR setId numberFormat for : " + idToParse);
            this.id = -1;
        }
    }

    public Employee getEmployee() {
        return employee;
    }

    public EmployeeDescription getEmployeeDescription() {
        return employeeDescription;
    }

    public Collection<Experience> getEmployeeExperiences() {
        return employeeExperiences;
    }

    public Collection<Formation> getEmployeeFormations() {
        return employeeFormations;
    }
}
