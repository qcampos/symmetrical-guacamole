package fr.upem.hireanemployee.validators;

import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;

/**
 * Created by Baxtalou on 26/02/2016.
 * Regroups every regex needed to validate the code.
 */
@Stateless
@ManagedBean
public class Regexes {

    public static final String ALPHA = "^[_A-z]+$";

    /**
     * ALPHA only regex.
     */
    public static String getAlpha() {
        return ALPHA;
    }

    /**
     * Parses s with the ALPHA regex.
     *
     * @return true if and only if it matches.
     */
    public static boolean parseAlpha(String s) {
        return s.matches(ALPHA);
    }
}
