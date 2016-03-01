package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.validators.ErrorHandler;
import fr.upem.hireanemployee.validators.Regexes;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * Created by Abwuds on 20/02/2016.
 * Handles the connection page actions : connection.xhtml
 */
@ManagedBean
@ViewScoped
public class ConnectionBean extends Logger {

    /* Application's beans */
    @EJB
    private DatabaseDAO dao;
    @ManagedProperty(value = "#{notificationBean}")
    private NotificationBean notificationBean;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    /* Controller informations */
    private String lastName;
    private String email;
    private String password;
    private String firstName;
    private Employee employee;

    public ConnectionBean() {
    }

    /**
     * Connects the user to the system.
     * If an error as occurred, sets the errorHandler flag accordingly to notify the user.
     *
     * @return empty string if the connection has failed. The new url otherwise.
     */
    public String connect() {
        log("connect - mail : " + email + " password : " + password);

        // If fields are not filled. Returning to the same page.
        // And setting the flag in the errorHandler object.
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()
                || !email.matches(Regexes.getEmail()) || !password.matches(Regexes.getAlphaNum())) {
            badFieldConnection();
            return Constants.CURRENT_PAGE;
        }

        // Trying to connect to the database.
        employee = dao.connect(email, password);

        // If the connection has failed.
        if (employee == null) {
            badInformationConnection();
            return Constants.CURRENT_PAGE;
        }

        // Connection successful.
        notificationBean.clear();
        log("connect - connection successful.");

        // Setting the employee in the session and redirecting to the CV page.
        sessionBean.setConnected(employee);
        return redirectToCvWithID();
    }

    /**
     * Creates the user inside the database.
     * If an error as occured, sets the errorHandler flag accordingly to notify the user.
     *
     * @return empty string if the connection has failed. The new url otherwise.
     */
    public String create() {
        log("create - " + firstName + " " + lastName + " " + email + " " + password);

        // If fields are not filled. Returning to the same page.
        // And setting the flag in the errorHandler object.
        if (email == null || password == null || firstName == null || lastName == null ||
                email.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() ||
                lastName.trim().isEmpty() || !email.matches(Regexes.getEmail()) || !firstName.matches(Regexes.getName())
                || !lastName.matches(Regexes.getName()) || !password.matches(Regexes.getAlphaNum())) {
            badFieldConnection();
            return Constants.CURRENT_PAGE;
        }

        // trying to create in the database.
        employee = dao.signup(firstName, lastName, email, password);

        // If the creation has failed. (Already in the database).
        if (employee == null) {
            alreadyExistsCreation();
            return Constants.CURRENT_PAGE;
        }

        // Creation successful.
        notificationBean.clear();
        log("create - creation successful.");

        // Setting the employee in the session and redirecting to the CV page.
        sessionBean.setConnected(employee);
        return redirectToCvWithID();
    }

    /**
     * s
     * Notification message.
     */
    private void badInformationConnection() {
        log("connect - connection failed. Not in the database");
        setErrorMsg("Désolé, l'adresse et le mot de passe ne correspondent pas.");
    }

    /**
     * Notification message.
     */
    private void alreadyExistsCreation() {
        log("connect - connection failed. Wrong fields");
        setErrorMsg("Désolé, " + email + " existe déjà.");
    }

    /**
     * Notification message.
     */
    private void badFieldConnection() {
        setErrorMsg(NotificationBean.DEFAULT_MSG);
        log("connect - connection failed. Wrong fields");
    }

    private String redirectToCvWithID() {
        return Navigations.redirect(Constants.CV) + "id=" + employee.getId();
    }

    public void setErrorMsg(String msg) {
        notificationBean.setError(msg);
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setEmail(String email) {
        this.email = email;
        log("setEmail - " + email);
    }

    public void setNotificationBean(NotificationBean notificationBean) {
        this.notificationBean = notificationBean;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getId() {
        if (employee == null) return -1;
        return employee.getId();
    }
}
