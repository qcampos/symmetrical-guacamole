package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.EmployeeDescription;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * Created by Baxtalou on 22/02/2016.
 * Manages the description section during the cv display.
 */
@ManagedBean
@ViewScoped
public class EmployeeDescriptionBean extends Logger {


    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employeeDescription}")
    private EmployeeDescription employeeDescription;

    @PostConstruct
    private void init() {
        log("init - description " + employeeDescription);
    }

    public EmployeeDescription getEmployeeDescription() {
        return employeeDescription;
    }

    public void setEmployeeDescription(EmployeeDescription employeeDescription) {
        this.employeeDescription = employeeDescription;
    }
}
