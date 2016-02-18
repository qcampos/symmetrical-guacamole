package fr.upem.hireanemployee.profildata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class School {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private Country country;

    public School() {
    }

    public School(final String name, final Country country) {
        this.name = name;
        this.country = country;
    }
}
