package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Experience;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Collection;

/**
 * Created by Baxtalou on 21/02/2016.
 */
@ManagedBean
@SessionScoped
public class SessionBean extends Logger {

    private Employee employee;
    private boolean isConnected;
    private EmployeeDescription employeeDescription;
    private Collection<Experience> employeeExperiences;
    private boolean isMyID;

    public SessionBean() {
        super();
    }

    public void setConnected(Employee employee) {
        this.employee = employee;
        isConnected = true;
        // Updating fields now, to prevent db flood due to the getters.
        employeeDescription = employee.getDescription();
        employeeExperiences = employee.getExperiences();
        log("setConnected " + employee);
    }

    public EmployeeDescription getEmployeeDescription() {
        return employeeDescription;
    }

    public boolean isMyID(int value) {
        return value == 12;
    }
}
