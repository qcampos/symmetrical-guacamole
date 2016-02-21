package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Baxtalou on 21/02/2016.
 */
@ManagedBean
@SessionScoped
public class SessionBean extends Logger {

    private Employee employee;

    public SessionBean() {
        super();
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
