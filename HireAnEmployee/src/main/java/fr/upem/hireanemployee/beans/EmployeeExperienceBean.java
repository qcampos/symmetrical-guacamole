package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.*;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.validators.Regexes;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

/**
 * Created by Baxtalou on 22/02/2016.
 * Manages the description section during the cv display.
 */
@ManagedBean
@ViewScoped
public class EmployeeExperienceBean extends Logger {

    @EJB
    private EmployeeExperienceDAO dao;

    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employeeExperiences}")
    private Collection<Experience> employeeExperiences;
    @ManagedProperty("#{cvViewedBean.employee}")
    private Employee employee;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;

    // Data fields to show.
    private String professionalTitle;


    // Data fields to edit.
    private String newFirstName;


    @PostConstruct
    private void init() {
        log("init - experiences " + employeeExperiences);
        if (employeeExperiences == null) {
            throw new NullPointerException("The current experiences are null.");
        }
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();


        // Normalizing null values. We program with the option preventing the use of empty strings.
        // Null strings are handle ate the printing to place placeholder independent from this very model.
        // It allows us for example to translate placeholders outside the model.
        //professionalTitle = setNullIfEmpty(professionalTitle);s

        // Allowing default values by setting these forms.
//        newFirstName = firstName;

    }

    public String setNullIfEmpty(String str) {
        return str.isEmpty() ? null : str;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setNotificationBean(NotificationBean notificationBean) {
        this.notificationBean = notificationBean;
    }

    public void setEmployeeExperiences(Collection<Experience> employeeExperiences) {
        this.employeeExperiences = employeeExperiences;
    }

    public Collection<Experience> getEmployeeExperiences() {
        return employeeExperiences;
    }
}
