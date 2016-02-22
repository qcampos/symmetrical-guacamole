package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Baxtalou on 22/02/2016.
 */
@ManagedBean
@ViewScoped
public class CVIdBean extends Logger {

    private long id;

    public CVIdBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        log("setId - " + id);
        this.id = id;
    }

    public String getString() {
        log("getString - " + id + " " + this);
        return "CVIdBean";
    }
}
