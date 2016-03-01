package fr.upem.hireanemployee;


import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.EmployeeDescriptionDAO;
import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Formation;
import fr.upem.hireanemployee.profildata.School;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

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
    @EJB
    private EmployeeFormationDAO fdao;
    @EJB
    private EmployeeExperienceDAO exdao;

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
        edao.updateSector(description, "Logiciels informatiques");

        if (!bdu.emailExists("jmangue@u.com")) {
            bdu.signup("Jefferson", "Mangue", "jmangue@u.com", "12345");
        }
        employee = bdu.connect("jmangue@u.com", "12345");

        description = employee.getDescription();

        edao.updateCountry(description, Country.FRANCE);
        edao.updateProfessionalTitle(description, "MSc in Project and Programme Management and Business Development");

        fdao.createFormation("SKEMA Business School", "Master’s Degree, Master of Science in Project and Programme Management and Business Development",
                Formation.DegreeType.MASTER, new School("SKEMA Business School", Country.FRANCE), employee);

        exdao.createExperience("Weekendesk", "Asistant Marketing Manager", "jobAbstract",
                "Weekendesk is an European Online Travel Agency, operating in the French, Spanish, Belgian, Dutch and Italian markets, " +
                        "focused on delivering the best experience for short breaks and holidays. http://www.weekendesk.fr/ As a Marketing " +
                        "Assistant, I managed the digital presence of the Weekendesk brand in Spain on social networks: Facebook, Twitter and " +
                        "Google +.",
                new Date(114, 6, 1), new Date(114, 11, 29), employee);
        // Begun the 1 and ended the 31 always. (1 Juillter <-> 29 Décembre (problem with 31 Fevrier does not exists).

        exdao.createExperience("Dassault System", "MSc in Project and Programme Management and Business Development", "jobAbstract",
                "Member of the ICT Team The ICT team will deliver all communications and related services, " +
                        "as well as hardware and specialised solutions and applications during the event. ICT is responsible for the concept, " +
                        "set-up, operations and change management of all deliverables within the scope of ICT.",
                new Date(115, 11, 1), new Date(116, 1, 29), employee);
        // Begun the 1 and ended the 31 always. (1 Décembre <-> 29 Février (problem with 31 Fevrier does not exists).


        log("init - " + description.getEmployee().getFirstName());
    }
}
