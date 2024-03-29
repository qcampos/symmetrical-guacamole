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
    @EJB
    private EmployeeSkillDAO sdao;
    @EJB
    private EmployeeSelectionDAO esdao;

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
        Employee employee1 = dao.connect("nborie@upem.fr", "pony17");

        EmployeeDescription description;

        if (!dao.emailExists("jmangue@u.com")) {
            dao.signup("Jefferson", "Mangue", "jmangue@u.com", "12345");
        }
        Employee employee = dao.connect("jmangue@u.com", "12345");

        description = employee.getDescription();
        log("init - Description : " + description);
        edao.updateCountry(description, Country.FRANCE);
        edao.updateSector(description, "Armée");
        edao.updateProfessionalTitle(description, "MSc in Management and Business Development");

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
        // FIXME Begun the 1 and ended the 31 always. (1 Décembre<->29 Février(problem with 31 Fevrier does not exists).

        //Adding skills.

        log("init - skills : " + employee.getSkills().size());
        sdao.increaseSkill(employee, "JAVA", employee1);
        sdao.increaseSkill(employee, "CORBA", employee1);
        sdao.increaseSkill(employee, "C++", employee1);
        sdao.decreaseSkill(employee, "C++", employee1);

        Employee linus = dao.signup("Linus", "Torvald", "jmangue@test.com", "12345");
        sdao.increaseSkill(employee, "Big Data", linus);
        sdao.increaseSkill(employee, "Big Data", employee1);


        /* Selections */
        esdao.addSelection1(employee, employee1);
        Employee signup = dao.signup("Lisa", "Ok", "a@az.com", "12345");
        edao.updateCountry(signup.getDescription(), Country.ALBANIA);
        Employee signup1 = dao.signup("Lisa2", "Ok", "a@az2.com", "12345");
        Employee signup2 = dao.signup("Lisa3", "Ok", "a@az3.com", "12345");
        Employee signup3 = dao.signup("Lisa4", "Ok", "a@az4.com", "12345");
        log("init - employee Lisa : " + signup);
        esdao.addSelection1(employee, signup);
        esdao.addSelection1(employee, signup1);
        esdao.addSelection1(employee, signup2);
        esdao.addSelection1(employee, signup3);
        esdao.removeSelection1(employee, signup);
        esdao.removeSelection1(employee, signup1);
        esdao.removeSelection1(employee, signup2);
        esdao.removeSelection1(employee, signup3);
        esdao.removeSelection1(employee, employee1);
        esdao.removeSelection1(employee, employee1);



        log("init - skills : " + employee.getSkills().size());
        log("init - " + description.getEmployee().getFirstName());
    }
}
