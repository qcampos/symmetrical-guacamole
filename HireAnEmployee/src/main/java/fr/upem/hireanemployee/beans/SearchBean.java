package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeSelectionDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.converters.FormControlWrapper;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.Sector;
import fr.upem.hireanemployee.profildata.Skill;
import fr.upem.hireanemployee.profildata.SkillAssociation;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
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
    @EJB
    private EmployeeSelectionDAO sdao;
    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;


    // Current search.
    private String search;
    private boolean initialized;
    private boolean filteringEmployeeSelection;
    private List<EmployeeSearchResult> employees;
    private List<Country> countries;
    private List<Skill> skillList;
    private List<String> sectorList;
    private List<FormControlWrapper<Country>> countriesSelected;
    private List<SkillFilterBundle> skillSelected;

    // Added values to current research.
    private Country countryAdded;
    private Skill skillAdded;
    private String skillAddedMinLevel;
    private String sectorAdded;

    // Cache Control data.
    private Employee currentEmployee;


    public String searchActionInit() {
        // Guard needed because of viewParam Bug with ajax in the API 2.2
        if (initialized) {
            return Constants.CURRENT_PAGE;
        }
        initialized = true;
        currentEmployee = sessionBean.isConnected() ? dao.getEmployeeByID(sessionBean.getId()) : null;
        log("searchActionInit - search " + search);
        // Retrieving the results in the database.
        employees = getEmployeesSearchResults();
        // Getting the list of country.
        countries = dao.getCountries();
        // Getting the list of skills.
        skillList = dao.getSkills();

        // Automatically filtering the employee's initial selection if no search input.
        filteringEmployeeSelection = (search == null);
        // Country selected.
        countriesSelected = new ArrayList<>();
        countryAdded = Country.NONE;
        // Sectors.
        sectorList = dao.getSectorList();
        // Skills selected.
        skillSelected = new ArrayList<>();
        skillAdded = null;
        // Setting default sector.
        sectorAdded = Sector.DEFAULT_NAME;
        log("init - search result number " + employees.size());
        return Constants.CURRENT_PAGE;
    }

    /**
     * Creates a wrapper for employees founds in the research, with all their informations
     * put in cache.
     *
     * @return the list created.
     */
    private List<EmployeeSearchResult> getEmployeesSearchResults() {
        List<Employee> employees = dao.searchEmployeeByName(search);
        return wrappEmployeeList(employees);
    }

    private List<EmployeeSearchResult> wrappEmployeeList(List<Employee> employees) {
        List<EmployeeSearchResult> employeesResults = new ArrayList<>();
        for (Employee e : employees) {
            employeesResults.add(new EmployeeSearchResult(e));
        }
        return employeesResults;
    }

    public String addCountry() {
        log("addCountry - adding " + countryAdded);
        FormControlWrapper<Country> wrapper = new FormControlWrapper<>(countryAdded);
        // Checking if the wrapper already exists. If so, turning it to alive.
        // Otherwise, adding it to the countries selected if not equals to NONE.
        int index;
        if ((index = countriesSelected.indexOf(wrapper)) != -1) {
            FormControlWrapper<Country> added = countriesSelected.get(index);
            if (added.isDead()) {
                added.setDead(false);
                performAdvancedSearch();
            }
        } else {
            if (countryAdded != Country.NONE) {
                log("addCountry - " + countryAdded + " added.");
                countriesSelected.add(wrapper);
                performAdvancedSearch();
            }
        }
        log("addCountry - end");
        return Constants.CURRENT_PAGE;
    }

    public String addSkill() {
        log("addSkill - adding " + skillAdded + " " + skillAddedMinLevel);
        // Retrieving integer value.
        int minLevelValue;
        try {
            minLevelValue = Integer.parseInt(skillAddedMinLevel);
        } catch (NumberFormatException e) {
            log("[ERROR] addSkill - " + skillAdded + " min level of : " + skillAddedMinLevel + " not added.");
            skillAddedMinLevel = null;
            return Constants.CURRENT_PAGE;
        }
        // Checking if the bundle already exists. If so, turning it to alive.
        // Otherwise, adding it to the skills selected.
        for (SkillFilterBundle bundle : skillSelected) {
            if (bundle.get().equals(skillAdded)) {
                // Values already handled.
                if (!bundle.isDead() && bundle.getMinLevel() == minLevelValue) {
                    return Constants.CURRENT_PAGE;
                }
                bundle.setDead(false);
                bundle.setMinLevel(minLevelValue);
                performAdvancedSearch();
                log("addSkill - " + skillAdded + " min level of : " + skillAddedMinLevel + " turned alive.");
                return Constants.CURRENT_PAGE;
            }
        }
        skillSelected.add(new SkillFilterBundle(new FormControlWrapper<>(skillAdded), minLevelValue));
        performAdvancedSearch();
        log("addSkill - " + skillAdded + " min level of : " + skillAddedMinLevel + " added.");
        return Constants.CURRENT_PAGE;
    }

    public String removeCountry(FormControlWrapper<Country> wrapper) {
        if (wrapper != null) {
            log("removeCountry - removing " + wrapper.get());
            wrapper.setDead(true);
            performAdvancedSearch();
        }
        return Constants.CURRENT_PAGE;
    }

    public String removeSkill(SkillFilterBundle wrapper) {
        if (wrapper != null) {
            log("removeCountry - removing " + wrapper.get());
            wrapper.setDead(true);
            performAdvancedSearch();
        }
        return Constants.CURRENT_PAGE;
    }

    public String performSearch() {
        log("performSearch - click " + search);
        return Navigations.redirect(Constants.SEARCH) + (search == null ? "" : "search=" + search);
    }

    public String performAdvancedSearch() {
        log("performAdvancedSearch - " + search + " " + countryAdded + " " + sectorAdded + " " + skillAdded +
                " list : Countries : " + countriesSelected + " Sectors : " + sectorAdded + " skills : " + skillSelected);
        List<Country> countries = new ArrayList<>();
        // Countries filters.
        for (FormControlWrapper<Country> c : countriesSelected) {
            if (c.isDead()) continue;
            countries.add(c.get());
        }
        // Skills filters.
        List<SkillFilterBundle> skills = new ArrayList<>();
        for (SkillFilterBundle s : skillSelected) {
            if (s.isDead()) continue;
            skills.add(s);
        }
        employees = wrappEmployeeList(dao.advancedSearchedEmployee(sectorAdded, countries,
                skills, currentEmployee, filteringEmployeeSelection));
        log("performAdvancedSearch - Employee retrieved : " + employees);
        return Constants.CURRENT_PAGE;
    }

    /**
     * Sets the filteringEmployeeSelection, and performs an advanced search.
     */
    public void filterWithSelection(boolean filteringEmployeeSelection) {
        log("filterWithSelection - called with : " + filteringEmployeeSelection);
        this.filteringEmployeeSelection = filteringEmployeeSelection;
        performAdvancedSearch();
    }

    public String getSectorAdded() {
        return sectorAdded;
    }

    public void setSectorAdded(String sectorAdded) {
        log("setSectorAdded - " + sectorAdded);
        this.sectorAdded = sectorAdded;
        performAdvancedSearch();
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

        @Override
        public String toString() {
            return wrapper.get().toString() + " level : " + minLevel;
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

    /**
     * Handles and put in cache an employee's results, all its informations specific to the research.
     */
    public class EmployeeSearchResult {
        private final Employee employee;
        private final SkillAssociation maxSkill;
        private boolean isSelected;

        public EmployeeSearchResult(Employee employee) {
            this.employee = employee;
            maxSkill = dao.getEmployeeMaxSkill(employee);
            isSelected = false;
            if (sessionBean.isConnected()) {
                isSelected = sdao.isSelected(currentEmployee, employee);
            }
        }

        public SkillAssociation getMaxSkill() {
            return maxSkill;
        }

        public Employee getEmployee() {
            return employee;
        }

        public void setSelected(boolean selected) {
            log("setSelected - called with value : " + selected + " and employee connected : " + sessionBean.isConnected());
            if (currentEmployee == null) {
                return;
            }
            isSelected = selected;
            // Updating the state into the database.
            if (!isSelected) {
                sdao.removeSelection1(currentEmployee, employee);
                performAdvancedSearch();
                return;
            }
            sdao.addSelection1(currentEmployee, employee);
        }

        public boolean isSelected() {
            return isSelected;
        }
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public List<EmployeeSearchResult> getEmployees() {
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
        return skillAddedMinLevel == null ? "" : skillAddedMinLevel;
    }

    public void setSkillAddedMinLevel(String skillAddedMinLevel) {
        this.skillAddedMinLevel = skillAddedMinLevel;
    }

    public List<String> getSectorList() {
        return sectorList;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public boolean isFilteringEmployeeSelection() {
        return filteringEmployeeSelection;
    }

    public void setFilteringEmployeeSelection(boolean filteringEmployeeSelection) {
        this.filteringEmployeeSelection = filteringEmployeeSelection;
    }
}
