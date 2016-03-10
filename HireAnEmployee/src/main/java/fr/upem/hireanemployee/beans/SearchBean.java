package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.converters.FormControlWrapper;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.profildata.*;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
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
    private List<FormControlWrapper<Country>> countriesSelected;

    // Added values to current research.
    private Country countryAdded;
    private Country countryRemoved;

    public void listener(ValueChangeEvent ajaxBehaviorEvent) {
        log("listener - " + ajaxBehaviorEvent + " " + ajaxBehaviorEvent.getNewValue());
    }

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
        dao.getSectorList();
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

    public String removeCountry(FormControlWrapper<Country> wrapper) {
        log("removeCountry - removing " + wrapper.get());
        wrapper.setDead(true);
        // countriesSelected.lastIndexOf(wrapper);
        // countriesSelected.remove(wrapper);
        return Constants.CURRENT_PAGE;
    }

    public String performSearch() {
        log("performSearch - click " + search);
        return Navigations.redirect(Constants.SEARCH) + (search == null ? "" : "search=" + search);
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
        log("setCountryAdded " + countryAdded);
        this.countryAdded = countryAdded;
    }

    public Country getCountryRemoved() {
        return countryRemoved;
    }

    public void setCountryRemoved(Country countryRemoved) {
        this.countryRemoved = countryRemoved;
    }
}
