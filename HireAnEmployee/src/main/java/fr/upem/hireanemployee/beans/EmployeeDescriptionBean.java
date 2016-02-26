package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeDescriptionDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.validators.Regexes;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.awt.image.BufferedImage;

/**
 * Created by Baxtalou on 22/02/2016.
 * Manages the description section during the cv display.
 */
@ManagedBean
@ViewScoped
public class EmployeeDescriptionBean extends Logger {

    @EJB
    private EmployeeDescriptionDAO dao;

    // Managed fields.
    @ManagedProperty("#{cvViewedBean.employeeDescription}")
    private EmployeeDescription employeeDescription;
    @ManagedProperty("#{notificationBean}")
    private NotificationBean notificationBean;

    // Data fields to show.
    private String professionalTitle;
    private String names;
    private String country;
    private String sector;
    private String formation;
    private String url;
    private String email;
    private int nbRelations;

    // Data fields to edit.
    private String newFirstName;
    private String newLastName;
    private String newProfessionalTitle;
    private Country newCountry;
    private BufferedImage newImage;
    private String newSector;
    private String newFormation;

    @PostConstruct
    private void init() {
        log("init - description " + employeeDescription);
        Employee employee = employeeDescription.getEmployee();
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        url = firstName + lastName;
        email = employee.getEmail();
        names = firstName + " " + lastName;
        country = employeeDescription.getCountry().toString();
        sector = employeeDescription.getSector().toString();
        formation = employeeDescription.getFormation().equals("") ? "TODO Formation dans InitializationBean" : employeeDescription.getFormation();
        nbRelations = employeeDescription.getNbRelations();
        professionalTitle = employeeDescription.getProfessionalTitle();
        // Allowing default values by setting these forms.
        newFirstName = firstName;
        newLastName = lastName;
    }

    public String updateNames() {
        // TODO if == null : notificationBean(ViewScope).notifyError();
        log("updateNames - " + newFirstName + " " + newLastName);
        notificationBean.clear();
        // No new values.
        Employee employee = employeeDescription.getEmployee();
        if ((newFirstName == null || newFirstName.equals(employee.getFirstName())) &&
                (newLastName == null || newLastName.equals(employee.getLastName()))) {
            return Constants.CURRENT_PAGE;
        }
        // Verifying empty fields (they are not both required at the same time).
        newFirstName = (newFirstName == null) ? employee.getFirstName() : newFirstName;
        newLastName = (newLastName == null) ? employee.getLastName() : newLastName;
        // Checking if values are not pur alpha. This is also checked on the view side.
        boolean b1 = !Regexes.parseAlpha(newFirstName);
        if (b1 || !Regexes.parseAlpha(newLastName)) {
            log("updateNames - error in the parsing phase");
            notificationBean.setError("The value : " + (b1 ? newFirstName : newLastName) + " is not correct.");
            return Constants.CURRENT_PAGE;
        }
        // Updating the database.
        dao.updateNames(employeeDescription, newFirstName, newLastName);
        // Updating the corresponding field for next renderings by this very bean.
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        names = firstName + " " + lastName;
        newFirstName = firstName;
        newLastName = lastName;
        log("updateNames - names " + names);
        notificationBean.setSuccess("Names updated to : " + newFirstName + " " + newLastName);
        return Constants.CURRENT_PAGE;
    }

    public void setEmployeeDescription(EmployeeDescription employeeDescription) {
        this.employeeDescription = employeeDescription;
    }

    public String getNames() {
        return names;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public String getCountry() {
        return country;
    }

    public String getSector() {
        return sector;
    }

    public String getFormation() {
        return formation;
    }

    public String getNbRelations() {
        return Integer.toString(nbRelations);
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    public void setNotificationBean(NotificationBean notificationBean) {
        this.notificationBean = notificationBean;
    }

    public void setNewFirstName(String newFirstName) {
        this.newFirstName = newFirstName;
    }

    public void setNewLastName(String newLastName) {
        this.newLastName = newLastName;
    }

    public void setNewProfessionalTitle(String newProfessionalTitle) {
        this.newProfessionalTitle = newProfessionalTitle;
    }

    public void setNewCountry(Country newCountry) {
        this.newCountry = newCountry;
    }

    public void setNewImage(BufferedImage newImage) {
        this.newImage = newImage;
    }

    public void setNewSector(String newSector) {
        this.newSector = newSector;
    }

    public void setNewFormation(String newFormation) {
        this.newFormation = newFormation;
    }

    public String getNewFirstName() {
        return newFirstName;
    }

    public String getNewLastName() {
        return newLastName;
    }

    public String getNewProfessionalTitle() {
        return newProfessionalTitle;
    }

    public Country getNewCountry() {
        return newCountry;
    }

    public BufferedImage getNewImage() {
        return newImage;
    }

    public String getNewSector() {
        return newSector;
    }

    public String getNewFormation() {
        return newFormation;
    }
}
