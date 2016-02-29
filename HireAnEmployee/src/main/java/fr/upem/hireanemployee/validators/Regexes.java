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
    public static final String ALPHANUM = "^[_A-z0-9]+$";
    public static final String EMAIL = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
            "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

    /**
     * ALPHA only regex.
     */
    public static String getAlpha() {
        return ALPHA;
    }

    /**
     * EMAIL only regex.
     */
    public static String getEmail() {
        return EMAIL;
    }

    public static String getAlphaNum() {
        return ALPHANUM;
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
