package fr.upem.hireanemployee.profildata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Domain {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    public Domain() {
    }

    public Domain(final String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
