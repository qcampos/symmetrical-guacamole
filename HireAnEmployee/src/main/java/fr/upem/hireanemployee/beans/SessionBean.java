package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Experience;
import org.omg.PortableInterceptor.DISCARDING;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Collection;

/**
 * Created by Baxtalou on 21/02/2016.
 */
@ManagedBean
@SessionScoped
public class SessionBean extends Logger {

    public enum State {CONNECTED, DISCONNECTED}

    private State state = State.DISCONNECTED;
    private Employee employee;

    /**
     * Connects the employee's session with the given Employee class.
     */
    public void setConnected(Employee employee) {
        this.employee = employee;
        state = State.CONNECTED;
        log("setConnected - " + employee.getFirstName());
    }

    /**
     * @return Session's state connected or disconnected.
     */
    public State getState() {
        return state;
    }

    /**
     * @return The id of the user if connected, -1 otherwise.
     */
    public long getId() {
        if (state == State.DISCONNECTED) {
            return -1;
        }
        return employee.getId();
    }
}
