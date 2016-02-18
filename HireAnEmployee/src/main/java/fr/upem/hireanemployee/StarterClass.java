package fr.upem.hireanemployee;


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
    private DatabaseUtils bdu;

    public StarterClass() {

    }

    @PostConstruct
    public void init() {

    }

    public String printCoucou() {

        Language l = new Language("Ponylang");
        List<Language> languages = Arrays.asList(l);
        bdu.register(l);

        Employee e = new Employee("Nicolas", "Borie", "nborie@borieawardofficial.fr", "pony77", languages);
        bdu.register(e);


        Employee employeeByMail = bdu.getEmployeeByMail("nborie@borieawardofficial.fr");

        bdu.cleanAll();
        return "Founded : " + employeeByMail.toString();
    }
}
