package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.validators.ErrorHandler;

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
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    /* Controller informations */
    private String lastName;
    private String email;
    private String password;
    private String firstName;

    /* Error handler */
    private final ErrorHandler errorHandler;

    public ConnectionBean() {
        errorHandler = new ErrorHandler();
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
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            badFieldConnection();
            return Constants.CURRENT_PAGE;
        }

        // Trying to connect to the database.
        Employee employee = dao.connect(email, password);

        // If the connection has failed.
        if (employee == null) {
            badInformationConnection();
            return Constants.CURRENT_PAGE;
        }

        // Connection successful.
        errorHandler.clear();
        log("connect - connection successful.");

        // Setting the employee in the session and redirecting to the CV page.
        sessionBean.setConnected(employee);
        return Navigations.redirect(Constants.CV);
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
                lastName.trim().isEmpty()) {
            badFieldConnection();
            return Constants.CURRENT_PAGE;
        }

        // trying to create in the database.
        Employee employee = dao.signup(firstName, lastName, email, password);

        // If the creation has failed. (Already in the database).
        if (employee == null) {
            alreadyExistsCreation();
            return Constants.CURRENT_PAGE;
        }

        // Creation successful.
        errorHandler.clear();
        log("create - creation successful.");

        // Setting the employee in the session and redirecting to the CV page.
        sessionBean.setConnected(employee);
        return Navigations.redirect(Constants.CV);
    }

    private void badInformationConnection() {
        log("connect - connection failed. Not in the database");
        setErrorMsg("Sorry, your are not in our database.");
    }

    private void alreadyExistsCreation() {
        log("connect - connection failed. Wrong fields");
        setErrorMsg("Sorry " + email + " already exists in our database.");
    }

    private void badFieldConnection() {
        setErrorMsg("Some error are detected. Please correct your fields.");
        log("connect - connection failed. Wrong fields");
    }

    public boolean isErrorHandler() {
        return errorHandler.isError();
    }

    public void setErrorMsg(String msg) {
        errorHandler.setError(msg);
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getErrorMsg() {
        return errorHandler.getMsg();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
