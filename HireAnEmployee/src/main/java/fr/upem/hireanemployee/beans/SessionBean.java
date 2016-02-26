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

    /**
     * Connects the employee's session with the given Employee class.
     */
    public void setConnected(Employee employee) {
        this.employee = employee;
        isConnected = true;
        log("setConnected - " + employee.getFirstName());
    }

    /**
     * @return The id of the user if connected, -1 otherwise.
     */
    public long getId() {
        if (!isConnected) {
            return -1;
        }
        return employee.getId();
    }
}
