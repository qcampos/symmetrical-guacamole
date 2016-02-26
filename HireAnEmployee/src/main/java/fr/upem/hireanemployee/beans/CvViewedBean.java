package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Experience;
import javafx.event.Event;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.util.Collection;

/**
 * Created by Baxtalou on 22/02/2016.
 * The main bean for the page : cv.xhtml.
 * Manages the employee's data bootsrape to feed : EmployeeDescriptionBean,
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

    // Current CV id
    private long id;

    /**
     * Method call on the viewAction of page cv.xhtml. It provides initialization capabilities
     * and prevent multiple calls when post init or setter are used on ViewScoped bean.
     * Delegated backing beans like EmployeeDescriptionBean of EmployeeExperienceBean will have
     * direct injections of the description and experience of the retrieved user, thanks to
     * this lifecycle use.
     *
     * @return The current page if the id corresponds to an Employee. The 404 error page otherwise.
     * TODO redirect on a research page + the message "unknonw employee" instead.
     */
    public String viewActionInit() {
        log("viewActionInit - id " + id);
        // Retrieving the employee in the database.
        employee = dao.getEmployeeByID(id);
        if (employee == null) {
            // TODO redirect on a special page for unknown employee + search bar.
            return "404";
        }
        // Setting fields accordingly.
        employeeDescription = employee.getDescription();
        employeeExperiences = employee.getExperiences();
        log("init - employee " + employee.getFirstName());
        return Constants.CURRENT_PAGE;
    }

    public long getId() {
        log("getId - " + id);
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EmployeeDescription getEmployeeDescription() {
        return employeeDescription;
    }

    public Collection<Experience> getEmployeeExperiences() {
        return employeeExperiences;
    }
}
