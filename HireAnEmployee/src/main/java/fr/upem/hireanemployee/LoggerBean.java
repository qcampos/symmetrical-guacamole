package fr.upem.hireanemployee;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Created by Baxtalou on 20/02/2016.
 * Class to inject to delegate the log to.
 */
@ManagedBean(name = "loggerBean")
@SessionScoped
public class LoggerBean implements Serializable {

    private static final boolean DEBUG = true;
    private static final String BEAN = "[BEAN]";
    private static final String DATABASE = "[DATABASE]";

    public LoggerBean() {
    }

    /**
     * Logs a message on stdout with BEAN filter.
     *
     * @param msg the message to log.
     */
    public void logB(Object caller, String msg) {
        log(caller, msg, BEAN);
    }

    /**
     * Logs a message on stdout with DATABASE filter.
     *
     * @param msg the message to log.
     */
    public void logDB(Object caller, String msg) {
        log(caller, msg, DATABASE);
    }

    private void log(Object caller, String msg, String filter) {
        if (!DEBUG) {
            return;
        }
        System.out.println(filter + " " + caller.getClass().getCanonicalName() + " : " + msg);
    }
}
