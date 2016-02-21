package fr.upem.hireanemployee;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Created by Baxtalou on 20/02/2016.
 * Class able to log itself.
 */
public class Logger implements Serializable {

    private static final boolean DEBUG = true;
    private static final String BEAN = "[BEAN]";
    private static final String DATABASE = "[DATABASE]";

    public Logger() {
    }

    /**
     * Logs a message on stdout with BEAN filter.
     *
     * @param msg the message to log.
     */
    public void log(String msg) {
        log(this, msg, BEAN);
    }

    /**
     * Logs a message on stdout with DATABASE filter.
     *
     * @param msg the message to log.
     */
    public void logDB(String msg) {
        log(this, msg, DATABASE);
    }

    private void log(Object caller, String msg, String filter) {
        if (!DEBUG) {
            return;
        }
        System.out.println(filter + " " + caller.getClass().getCanonicalName() + " : " + msg);
    }
}
