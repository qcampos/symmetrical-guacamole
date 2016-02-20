package fr.upem.hireanemployee.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class StartBean {

    private String result;
    private int redirect = 0;

    public String search(){
        result = "hooray";
        if (redirect < 1) {
            redirect++;
            return "";
        }

        return "index_mock?faces-redirect=true";
    }


    public String showResult(){
        return "result?faces-redirect=true";
    }

    public String getResult() {
        return result;
    }

    public int getCount() {
        System.out.println("Count called " + redirect);
        return redirect;
    }
}