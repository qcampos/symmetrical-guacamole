package fr.upem.hireanemployee.validators;

/**
 * Created by Baxtalou on 22/02/2016.
 * Handles error inside the logic of ManagedBeans.
 * This object is then queried to notify the view of any problem
 * occurring.
 */
public class ErrorHandler {

    private boolean isError;
    private String msg;


    public void setError(String msg) {
        this.msg = msg;
        isError = true;
    }

    public void clear() {
        msg = null;
        isError = false;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isError() {
        return isError;
    }
}
