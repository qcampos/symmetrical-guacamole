package fr.upem.hireanemployee.validators;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Baxtalou on 05/03/2016.
 */
public class DateTranslator {

    public static String toDate(Date startDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        return format.format(startDate) + " - " + format.format(endDate) + " " + dateDifference(startDate, endDate, false);
    }

    public static String toDateYears(Date startDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(startDate) + " - " + format.format(endDate) + " " + dateDifference(startDate, endDate, true);
    }

    private static String dateDifference(Date startDate, Date endDate, boolean onlyYears) {
        //in milliseconds

        long duration = endDate.getTime() - startDate.getTime();

        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        long diffYear = diffInDays / 365;
        diffInDays -= (diffYear * 365);
        long diffMonths = Math.round(diffInDays / 31.0);

        if (diffMonths + diffYear == 0) {
            return "";
        }
        if (diffMonths > 0 && !onlyYears) {
            if (diffYear > 0) {
                return "(" + diffYear + " an" + (diffYear > 1 ? "s" : "") + " et " + diffMonths + " mois)";
            }
            return "(" + diffMonths + " mois)";
        }
        return diffYear > 0 ? "(" + diffYear + " an" + (diffYear > 1 ? "s" : "") + ")" : "";
    }

    public static List<String> getMonths() {
        return Arrays.asList("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "aout", "septembre",
                "octobre", "novembre", "décembre");
    }
}
