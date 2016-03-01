package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Baxtalou on 26/02/2016.
 * Bean managing all messages sent to the notification
 * component lying in notification_composite.xhtml.
 */
@ManagedBean
@ViewScoped
public class NotificationBean extends Logger {


    public static final String DEFAULT_MSG = "Des erreurs ont été détectées dans vos champs.";

    public enum State {SUCCESS, ERROR, IDLE;}

    private State state = State.IDLE;
    private String message;

    public String getMessage() {
        return message;
    }

    public State getState() {
        return state;
    }

    public void setError(String error) {
        log("setError - " + error);
        state = State.ERROR;
        message = error;
    }

    public void setSuccess(String success) {
        log("setSuccess - " + success);
        state = State.SUCCESS;
        message = success;
    }

    public void clear() {
        log("clear - ");
        state = State.IDLE;
        message = null;
    }

    public String getId() {
        return "popNotification";
    }

    public boolean isRendered() {
        return state == State.SUCCESS || state == State.ERROR;
    }

    public String getStyleClass() {
        switch (state) {
            case SUCCESS:
                return "greenb";
            case ERROR:
                return "redb";
            case IDLE:
            default:
                return "";
        }
    }
}
