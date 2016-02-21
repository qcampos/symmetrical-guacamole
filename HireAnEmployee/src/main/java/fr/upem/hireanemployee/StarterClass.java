package fr.upem.hireanemployee;


import fr.upem.hireanemployee.profildata.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Baxtalou on 10/02/2016.
 */
@ManagedBean
public class StarterClass {


    @EJB
    private DatabaseDAO bdu;
    @EJB
    private EmployeeDescriptionDAO edao;

    public StarterClass() {

    }

    @PostConstruct
    public void init() {

    }

    public String printCoucou() {

//        Language l = new Language("Ponylang");
//        List<Language> languages = Arrays.asList(l);
//        bdu.register(l);
//
//        Employee e = new Employee("Nicolas", "Borie", "Teacher", Country.FRANCE, new Sector("Combinatorics"), "nborie@award.fr", "pony77");
//
//        School upem = new School("UPEM", Country.FRANCE);
//        Degree degree = new Degree("Doctorat en combinatoire", Degree.DegreeType.PHD, upem);
//        Experience experience = new Experience("C&A", "Hôte de caisse", "Mon rôle fut de tenir une caisse", "Un poste éprouvant", Date.from(Instant.now()), Date.from(Instant.now()));
//
//
//        e.addFormation(degree);
//        e.addExperience(experience);
//        bdu.register(e);
//
//
//        Employee employeeByMail = bdu.getEmployeeByMail("nborie@award.fr");
//
//        bdu.cleanAll();
//        return "Founded : " + employeeByMail.toString();

        if (!bdu.emailExists("nborie@upem.fr")) {
            bdu.signup("Nicolas", "Borie", "nborie@upem.fr", "pony17");
        }
        Employee employee = bdu.connect("nborie@upem.fr", "pony17");

        EmployeeDescription description = employee.getDescription();
        edao.updateSector(description, "Combinatorics");
        return employee.toString();


    }
}
