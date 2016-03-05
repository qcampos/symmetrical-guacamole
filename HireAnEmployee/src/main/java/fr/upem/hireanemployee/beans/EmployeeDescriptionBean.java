package fr.upem.hireanemployee.beans;

import java.nio.file.Paths;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeDescriptionDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Sector;
import fr.upem.hireanemployee.validators.Regexes;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Created by Baxtalou on 22/02/2016.
 * Manages the description section during the cv display.
 */
@ManagedBean
@ViewScoped
public class EmployeeDescriptionBean extends Logger {

    @EJB
    private DatabaseDAO ddao;
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
    private String newCountry;
    private Part newImage;
    private String newSector;
    private String newFormation;
    private List<Country> countries;
    private List<String> sectors;
    private String imagePath;

    @PostConstruct
    private void init() {
        log("init - description " + employeeDescription);
        if (employeeDescription == null) {
            throw new NullPointerException("The current description is null.");
        }
        Employee employee = employeeDescription.getEmployee();
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        url = firstName + lastName;
        email = employee.getEmail();
        names = firstName + " " + lastName;
        country = employeeDescription.getCountry().equals(Country.NONE) ? "" : employeeDescription.getCountry().toString();
        sector = employeeDescription.getSector().toString();
        formation = employeeDescription.getFormation().equals("") ? "" : employeeDescription.getFormation();
        nbRelations = employeeDescription.getNbRelations();
        professionalTitle = employeeDescription.getProfessionalTitle();

        // Normalizing null values. We program with the option preventing the use of empty strings.
        // Null strings are handle ate the printing to place placeholder independent from this very model.
        // It allows us for example to translate placeholders outside the model.
        country = setNullIfEmpty(country);
        sector = setNullIfEmpty(sector);
        professionalTitle = setNullIfEmpty(professionalTitle);
        formation = setNullIfEmpty(formation);

        // Allowing default values by setting these forms.
        newFirstName = firstName;
        newLastName = lastName;
        newProfessionalTitle = professionalTitle;
        newCountry = employeeDescription.getCountry().name();
        newSector = sector;
        countries = ddao.getCountries();
        sectors = ddao.getSectorList();

        imagePath = "img/photo1.jpg";
    }

    public String setNullIfEmpty(String str) {
        return str.isEmpty() ? null : str;
    }

    public String updateNames() {
        Employee employee = employeeDescription.getEmployee();
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        log("updateNames - " + newFirstName + " " + newLastName + " " + firstName + " " + lastName);
        notificationBean.clear();

        if ((newFirstName == null || newFirstName.isEmpty() || newFirstName.equals(firstName)) &&
                (newLastName == null || newLastName.isEmpty() || newLastName.equals(lastName))) {
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
            newFirstName = firstName;
            newLastName = lastName;
            return Constants.CURRENT_PAGE;
        }
        // Updating the database.
        dao.updateNames(employeeDescription, newFirstName, newLastName);
        // Updating the corresponding field for next renderings by this very bean.
        firstName = employee.getFirstName();
        lastName = employee.getLastName();
        names = firstName + " " + lastName;
        newFirstName = firstName;
        newLastName = lastName;
        log("updateNames - names " + names);
        notificationBean.setSuccess("Names updated to : " + newFirstName + " " + newLastName);
        return Constants.CURRENT_PAGE;
    }

    public String updateTitle() {
        log("updateTitle - " + newProfessionalTitle);
        notificationBean.clear();
        if (newProfessionalTitle != null && newProfessionalTitle.equals(professionalTitle)) {
            return Constants.CURRENT_PAGE;
        }
        dao.updateProfessionalTitle(employeeDescription, newProfessionalTitle);
        professionalTitle = newProfessionalTitle;
        log("updateTitle - title " + professionalTitle);
        notificationBean.setSuccess("New Professional title set.");
        return Constants.CURRENT_PAGE;
    }

    public String updateCountryAndSector() {
        log("updateCountryAndSector - " + newCountry + " " + newSector);
        notificationBean.clear();
        if ((newCountry == null || newCountry.equals(country)) &&
                (newSector == null || newSector.equals(sector))) {
            return Constants.CURRENT_PAGE;
        }
        try {
            // Updating the country.
            Country resultCountry = Country.stringToCountry(newCountry);
            dao.updateCountry(employeeDescription, resultCountry);
            this.country = resultCountry.toString();
            // updating the sector.
            dao.updateSector(employeeDescription, newSector);
            this.sector = newSector;
            log("updateCountryAndSector - country " + resultCountry + " sector " + newSector);
            notificationBean.setSuccess("New Country and Sector set.");
        } catch (IllegalArgumentException e) {
            notificationBean.setError("The value : " + newCountry + " is not correct. " + e.getMessage());
        }
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

    public void setNewCountry(String newCountry) {
        this.newCountry = newCountry;
    }

    public void setNewImage(Part newImage) {
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

    public String getNewCountry() {
        return newCountry;
    }

    public Part getNewImage() {
        return newImage;
    }

    public String getNewSector() {
        return newSector;
    }

    public String getNewFormation() {
        return newFormation;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public List<String> getSectors() {
        return sectors;
    }


    public String upload() throws IOException {
        log("upload - " + newImage);
        if (newImage == null) {
            log("upload - part image null");
            return "404";
        }

        String bd_folder = "/Users/Baxtalou/Documents/Master2/JAVAEE/cv_online/symmetrical-guacamole/HireAnEmployee/bd_images";
        java.nio.file.Path file = Files.createTempFile(Paths.get(bd_folder), "ll", ".png");

        try (InputStream input = newImage.getInputStream()) {
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
        }

        imagePath = bd_folder + "ll.png";
// ...

        return Constants.CURRENT_PAGE;
    }

    public String getImagePath() {
        return imagePath;
    }
}
