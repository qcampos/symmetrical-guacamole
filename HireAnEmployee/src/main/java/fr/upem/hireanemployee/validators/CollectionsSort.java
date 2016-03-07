package fr.upem.hireanemployee.validators;

import fr.upem.hireanemployee.profildata.Experience;
import fr.upem.hireanemployee.profildata.Formation;
import fr.upem.hireanemployee.profildata.Skill;
import fr.upem.hireanemployee.profildata.SkillAssociation;

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
        Comparator<Experience> comparator = new Comparator<Experience>() {
            @Override
            public int compare(final Experience o1, final Experience o2) {
                return compareDate(o1.getEndDate(), o2.getEndDate());
            }
        };
        sortCollection(experiences, sortedList, comparator);
    }

    /**
     * Sorts by date a collection of formation.
     */
    public static void sortFormation(Collection<Formation> formations) {
        if (formations.size() <= 1) return;
        ArrayList<Formation> sortedList = new ArrayList<>(formations);
        Comparator<Formation> comparator = new Comparator<Formation>() {
            @Override
            public int compare(final Formation o1, final Formation o2) {
                return compareDate(o1.getEndDate(), o2.getEndDate());
            }
        };
        sortCollection(formations, sortedList, comparator);
    }

    public static void sortSkillByLevel(Collection<SkillAssociation> skills) {
        if (skills.size() <= 1) return;
        ArrayList<SkillAssociation> sortedList = new ArrayList<>(skills);
        Comparator<SkillAssociation> comparator = new Comparator<SkillAssociation>() {
            @Override
            public int compare(final SkillAssociation o1, final SkillAssociation o2) {
                return o2.getLevel() - o1.getLevel();
            }
        };
        sortCollection(skills, sortedList, comparator);
    }

    private static <E> void sortCollection(Collection<E> results, List<E> list,
                                           Comparator<E> comparator) {
        Collections.sort(list, comparator);
        results.clear();
        results.addAll(list);
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