package fr.upem.hireanemployee.navigation;

/**
 * Created by Abwuds on 20/02/2016.
 * Navigation utilities method.
 */
public class Navigations {

    /**
     * @param page The page to redirect to.
     * @return the formatted url to redirect on the page.
     */
    public static String redirect(String page) {
        return page + "?faces-redirect=true";
    }
}
