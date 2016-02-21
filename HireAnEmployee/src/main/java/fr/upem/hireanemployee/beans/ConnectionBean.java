package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;

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
    private boolean errorState;
    private String email;
    private String password;
    private String errorMsg;

    public ConnectionBean() {

    }

    /**
     * Connects the user to the system.
     * And sets the errorState flag accordingly. To be used if the connection failed.
     *
     * @return empty string if the connection has failed. The new url otherwise.
     */
    public String connect() {
         log("connect " + "mail : " + email + " password : " + password);

        // If fields are not filled. Returning to the same page.
        // And setting the bad information flag.
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
        log("connect " + "connection successful.");
        // Setting the employee in the session.
        sessionBean.setConnected(employee);
        return Navigations.redirect(Constants.CV);
    }

    private void badInformationConnection() {
        setErrorState(true);
        log("connect " + "connection failed. Not in the database");
        errorMsg = "Sorry, your are not in our database.";
    }

    private void badFieldConnection() {
        setErrorState(true);
        log("connect " + "connection failed. Wrong fields");
        errorMsg = "Some error are detected. Please correct your fields.";
    }


    public boolean isErrorState() {
        log("isErrorState " + errorState);
        return errorState;
    }

    public void setErrorState(boolean errorState) {
        this.errorState = errorState;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
