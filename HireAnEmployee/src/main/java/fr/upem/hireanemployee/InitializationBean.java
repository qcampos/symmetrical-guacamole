package fr.upem.hireanemployee;


import fr.upem.hireanemployee.profildata.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.Date;
import java.util.List;

/**
 * Created by Baxtalou on 10/02/2016.
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class InitializationBean extends Logger {


    @EJB
    private DatabaseDAO dao;
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

        // TODO load the play test in the persistence.xml
        //dao.initScript("insert.sql");

        if (!dao.emailExists("nborie@upem.fr")) {
            dao.signup("Nicolas", "Borie", "nborie@upem.fr", "pony17");
        }
        Employee employee = dao.connect("nborie@upem.fr", "pony17");

        EmployeeDescription description = employee.getDescription();
        edao.updateSector(description, "Combinatorics");
        edao.updateSector(description, "Logiciels informatiques");

        if (!dao.emailExists("jmangue@u.com")) {
            dao.signup("Jefferson", "Mangue", "jmangue@u.com", "12345");
        }
        employee = dao.connect("jmangue@u.com", "12345");

        description = employee.getDescription();

        edao.updateCountry(description, Country.FRANCE);
        edao.updateProfessionalTitle(description, "MSc in Project and Programme Management and Business Development");

        fdao.createFormation("Master’s Degree, Master of Science in Project and Programme Management and Business Development",
                "Programme entirely taught in English with in-company projects and assignments. Professional project management certifications: PRINCE 2 and PMI " +
                        "The only MSc programme in France that is 100% project management oriented. Ranked 3rd in France and 11th worldwide in its category by SMBG " +
                        "Global project management framework, tools and techniques; Sustainable project management and operational readiness; Organisation and Leadership, Strategy and business development ...",
                Formation.DegreeType.MASTER, new School("SKEMA Business School", Country.FRANCE), new Date(115, 1, 1), new Date(116, 1, 1), employee, Visibility.PUBLIC);

        fdao.createFormation("", "",
                Formation.DegreeType.MASTER, new School("SKEMA Business School", Country.FRANCE), null, null, employee, Visibility.PUBLIC);

        exdao.createExperience("Cartier", "Chargé du retailing", "jobAbstract", null, Country.AMERICAN_SAMOA, Visibility.PUBLIC, new Date(113, 2, 1), new Date(113, 10, 29), employee);

        exdao.createExperience("Weekendesk", "Asistant Marketing Manager", "jobAbstract",
                "Weekendesk is an European Online Travel Agency, operating in the French, Spanish, Belgian, Dutch and Italian markets, " +
                        "focused on delivering the best experience for short breaks and holidays. http://www.weekendesk.fr/ As a Marketing " +
                        "Assistant, I managed the digital presence of the Weekendesk brand in Spain on social networks: Facebook, Twitter and " +
                        "Google +.", Country.UNITED_ARAB_EMIRATES, Visibility.PRIVATE,
                new Date(114, 6, 1), new Date(114, 11, 29), employee);
        // Begun the 1 and ended the 31 always. (1 Juillet <-> 29 Décembre (problem with 31 Fevrier does not exists).

        exdao.createExperience("Dassault System", "MSc in Project and Programme Management and Business Development", "jobAbstract",
                "Member of the ICT Team The ICT team will deliver all communications and related services, " +
                        "as well as hardware and specialised solutions and applications during the event. ICT is responsible for the concept, " +
                        "set-up, operations and change management of all deliverables within the scope of ICT.", Country.ANTIGUA_AND_BARBUDA,
                Visibility.PUBLIC, new Date(115, 11, 1), new Date(116, 1, 29), employee);
        // Begun the 1 and ended the 31 always. (1 Décembre <-> 29 Février (problem with 31 Fevrier does not exists).

        // Adding skills.
        dao.merge(new Skill("JAVA"));
        dao.merge(new Skill("C"));
        dao.merge(new Skill("C++"));
        dao.merge(new Skill("CORBA"));
        dao.merge(new Skill("Gestion de projet"));
        dao.merge(new Skill("Big Data"));
        dao.merge(new Skill("Map Reduce"));
        dao.merge(new Skill("Java Enterprise Edition 7"));
        dao.merge(new Skill("Échec"));
        List<Skill> skills = dao.getSkills();

        for (Skill s : skills) {
            employee.addSkill(s);
        }
        dao.mergeEmployee(employee);
        log("init - skills : " + employee.getSkills().size());
        employee.removeSkill(skills.get(8));
        log("init - skills : " + employee.getSkills().size());
        dao.mergeEmployee(employee);
        log("init - " + description.getEmployee().getFirstName());
    }
}
