package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.LoggerBean;
import fr.upem.hireanemployee.navigation.Navigations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by Abwuds on 20/02/2016.
 * Handles the connection page actions : connection.xhtml
 */
@ManagedBean
@ViewScoped
public class ConnectionBean implements Serializable {

    @ManagedProperty(value = "#{loggerBean}")
    private LoggerBean log;


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
         log.logB(this, "connect " + "mail : " + email + " password : " + password);
        // If fields are not filled. Returning to the same page.
        // And setting the bad information flag.
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            setErrorState(true);
            log.logB(this, "connect " + "connection failed.");
            errorMsg = "Please fill the forms entirely";
            return "";
        }

        // TODO : put a specific message into the errorState.
        // Use a class with errorState.set("Can not blah.")
        // Or errorState.setFalse();
        log.logB(this, "connect " + "connection successful.");
        setErrorState(false);
        return Navigations.redirect("cv.xhtml"); // TODO constants.
    }


    public boolean isErrorState() {
        log.logB(this, "isErrorState " + errorState);
        return errorState;
    }

    public void setErrorState(boolean errorState) {
        this.errorState = errorState;
    }

    public void setLog(LoggerBean log) {
        this.log = log;
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
