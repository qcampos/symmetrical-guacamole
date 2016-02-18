package fr.upem.hireanemployee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Degree {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int level;
}
