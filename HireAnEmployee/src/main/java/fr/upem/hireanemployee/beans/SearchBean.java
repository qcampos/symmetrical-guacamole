package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.navigation.Navigations;
import fr.upem.hireanemployee.profildata.*;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collection;
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

        dao.getSectorList();
        log("init - search result number " + employees.size());
        return Constants.CURRENT_PAGE;
    }

    public String performSearch() {
        log("performSearch - click " + search);
        return Navigations.redirect(Constants.SEARCH) + "search=" + search;
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
}
