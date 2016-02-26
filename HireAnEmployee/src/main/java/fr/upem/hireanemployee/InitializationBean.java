package fr.upem.hireanemployee;


import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeDescriptionDAO;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by Baxtalou on 10/02/2016.
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class InitializationBean extends Logger {


    @EJB
    private DatabaseDAO bdu;
    @EJB
    private EmployeeDescriptionDAO edao;

    public InitializationBean() {

    }

    @PostConstruct
    public void init() {
        logDB("init - initialializationBean called");
        if (!bdu.emailExists("nborie@upem.fr")) {
            bdu.signup("Nicolas", "Borie", "nborie@upem.fr", "pony17");
        }
        Employee employee = bdu.connect("nborie@upem.fr", "pony17");

        EmployeeDescription description = employee.getDescription();
        edao.updateSector(description, "Combinatorics");

        if (!bdu.emailExists("jmangue@u.com")) {
            bdu.signup("Jefferson", "Mangue", "jmangue@u.com", "12345");
        }
        employee = bdu.connect("jmangue@u.com", "12345");

        description = employee.getDescription();

        edao.updateCountry(description, Country.FRANCE);
        edao.updateSector(description, "Logiciels informatiques");
        edao.updateProfessionalTitle(description, "MSc in Project and Programme Management and Business Development");
        log("init - " + description.getEmployee().getFirstName());
    }
}
