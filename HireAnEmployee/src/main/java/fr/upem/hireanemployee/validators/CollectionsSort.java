package fr.upem.hireanemployee.validators;

import fr.upem.hireanemployee.profildata.Experience;

import java.util.*;


/**
 * Sorts utilities.
 */
public class CollectionsSort {

    /**
     * Sorts by date a collection of experience.
     */
    public static void sortExperience(Collection<Experience> experiences) {
        if (experiences.size() <= 1) return;
        ArrayList<Experience> sortedList = new ArrayList<>(experiences);
        Collections.sort(sortedList, new Comparator<Experience>() {
            @Override
            public int compare(final Experience o1, final Experience o2) {
                Date endDate1 = o1.getEndDate();
                Date endDate2 = o2.getEndDate();
                return compareDate(endDate1, endDate2);
            }
        });
        experiences.clear();
        experiences.addAll(sortedList);
    }

    private static int compareDate(Date endDate1, Date endDate2) {
        if (endDate1 == null) {
            return -1;
        }
        if (endDate2 == null) {
            return 1;
        }
        return endDate2.getYear() - endDate1.getYear();
    }
}