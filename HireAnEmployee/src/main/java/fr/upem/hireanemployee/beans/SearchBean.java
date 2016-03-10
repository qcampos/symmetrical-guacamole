package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.converters.FormControlWrapper;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Skill;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baxtalou on 22/02/2016.
 * The main bean for the page : search.xhtml.
 * Manages search prints.
 */
@ManagedBean
@ViewScoped
public class SearchBean extends Logger {

    @EJB
    private DatabaseDAO dao;

    // Current search.
    private String search;
    private boolean initialized;
    private List<Employee> employees;
    private List<Country> countries;
    private List<Skill> skillList;
    private List<String> sectorList;
    private List<FormControlWrapper<Country>> countriesSelected;
    private List<SkillFilterBundle> skillSelected;

    // Added values to current research.
    private Country countryAdded;
    private Skill skillAdded;
    private int skillAddedMinLevel;

    public String searchActionInit() {
        // Guard needed because of viewParam Bug with ajax in the API 2.2
        if (initialized) {
            return Constants.CURRENT_PAGE;
        }
        initialized = true;
        log("searchActionInit - search " + search);
        // Retrieving the results in the database.
        // TODO switch on research type.
        // TODO on null research special message info (no result or take relations).
        employees = dao.searchEmployeeByName(search);
        // Getting the list of country.
        countries = dao.getCountries();
        // Getting the list of skills.
        skillList = dao.getSkills();
        // Country selected.
        countriesSelected = new ArrayList<>();
        countryAdded = Country.NONE;
        // Sectors.
        sectorList = dao.getSectorList();
        // Skills selected.
        skillSelected = new ArrayList<>();
        skillAdded = null;
        log("init - search result number " + employees.size());
        return Constants.CURRENT_PAGE;
    }

    public String addCountry() {
        log("addCountry - adding " + countryAdded);
        FormControlWrapper<Country> wrapper = new FormControlWrapper<>(countryAdded);
        // Checking if the wrapper already exists. If so, turning it to alive.
        // Otherwise, adding it to the countries selected if not equals to NONE.
        int index;
        if ((index = countriesSelected.indexOf(wrapper)) != -1) {
            countriesSelected.get(index).setDead(false);
        } else {
            if (countryAdded != Country.NONE) {
                log("addCountry - " + countryAdded + " added.");
                countriesSelected.add(wrapper);
            }
        }
        return Constants.CURRENT_PAGE;
    }

    public String addSkill() {
        log("addSkill - adding " + skillAdded);
        // Checking if the bundle already exists. If so, turning it to alive.
        // Otherwise, adding it to the skills selected.
        for (SkillFilterBundle bundle : skillSelected) {
            if (bundle.get().equals(skillAdded)) {
                bundle.setDead(false);
                log("addSkill - " + skillAdded + " min level of : " + skillAddedMinLevel + " turned alive.");
                return Constants.CURRENT_PAGE;
            }
        }

        log("addSkill - " + skillAdded + " min level of : " + skillAddedMinLevel + " added.");
        skillSelected.add(new SkillFilterBundle(new FormControlWrapper<>(skillAdded), skillAddedMinLevel));
        return Constants.CURRENT_PAGE;
    }

    public String removeCountry(FormControlWrapper<Country> wrapper) {
        if (wrapper != null) {
            log("removeCountry - removing " + wrapper.get());
            wrapper.setDead(true);
        }
        return Constants.CURRENT_PAGE;
    }

    public String removeSkill(SkillFilterBundle wrapper) {
        if (wrapper != null) {
            log("removeCountry - removing " + wrapper.get());
            wrapper.setDead(true);
        }
        return Constants.CURRENT_PAGE;
    }

    public String performSearch() {
        log("performSearch - click " + search);
        return Navigations.redirect(Constants.SEARCH) + (search == null ? "" : "search=" + search);
    }

    @SuppressWarnings("unused")
    public String performAdvancedSearch() {
        return Constants.CURRENT_PAGE;
    }

    /**
     * Class handling a skill and its minimum level for the advanced search filter.
     */
    public class SkillFilterBundle {
        private final FormControlWrapper<Skill> wrapper;
        private int minLevel;

        public SkillFilterBundle(FormControlWrapper<Skill> wrapper, int minLevel) {
            this.wrapper = wrapper;
            this.minLevel = minLevel;
        }

        public Skill get() {
            return wrapper.get();
        }

        public boolean isDead() {
            return wrapper.isDead();
        }

        public void setDead(boolean dead) {
            wrapper.setDead(dead);
        }

        public int getMinLevel() {
            return minLevel;
        }

        public void setMinLevel(int minLevel) {
            this.minLevel = minLevel;
        }
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public List<FormControlWrapper<Country>> getCountriesSelected() {
        return countriesSelected;
    }

    public Country getCountryAdded() {
        return countryAdded;
    }

    public void setCountryAdded(Country countryAdded) {
        this.countryAdded = countryAdded;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    public List<SkillFilterBundle> getSkillSelected() {
        return skillSelected;
    }

    public void setSkillSelected(List<SkillFilterBundle> skillSelected) {
        this.skillSelected = skillSelected;
    }

    public Skill getSkillAdded() {
        return skillAdded;
    }

    public void setSkillAdded(Skill skillAdded) {
        this.skillAdded = skillAdded;
    }

    public String getSkillAddedMinLevel() {
        return skillAddedMinLevel == 0 ? "" : Integer.toString(skillAddedMinLevel);
    }

    public void setSkillAddedMinLevel(int skillAddedMinLevel) {
        this.skillAddedMinLevel = skillAddedMinLevel;
    }

    public List<String> getSectorList() {
        return sectorList;
    }
}
