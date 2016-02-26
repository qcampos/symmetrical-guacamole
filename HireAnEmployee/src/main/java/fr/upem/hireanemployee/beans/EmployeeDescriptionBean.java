package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeDescriptionDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;

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
    }

    public String updateNames() {
        // TODO if == null : notificationBean(ViewScope).notifyError();
        log("updateNames - " + newFirstName + " " + newLastName);
        dao.updateNames(employeeDescription, newFirstName, newLastName);
        // Updating the corresponding field for printing.
        Employee employee = employeeDescription.getEmployee();
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        names = firstName + " " + lastName;
        log("updateNames - names " + names);
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

    public void setNewFirstName(String newFirstName) {
        log("setNewFirstName " + newFirstName);
        this.newFirstName = newFirstName;
    }

    public void setNewLastName(String newLastName) {
        log("setNewLastName " + newLastName);
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
