package fr.upem.hireanemployee.profildata;

import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.validators.CollectionsSort;

import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.persistence.metamodel.CollectionAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Quentin on 21/02/2016.
 */
@Entity(name = "EMPLOYEE_DESCRIPTION")
public class EmployeeDescription {

    @Id
    @GeneratedValue
    private long id;
    @OneToOne(mappedBy = "description")
    private Employee employee;
    private String professionalTitle;
    private Country country;
    @ManyToOne(cascade = CascadeType.ALL)
    private Sector sector;

    public EmployeeDescription() {
    }

    public EmployeeDescription(final Employee employee, final String professionalTitle, final Country country, final Sector sector) {
        this.employee = employee;
        this.professionalTitle = professionalTitle;
        this.country = country;
        this.sector = sector;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(final Employee employee) {
        this.employee = employee;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public void setProfessionalTitle(final String currentJob) {
        this.professionalTitle = currentJob;
    }

    public String getFormation() {
        ArrayList<Formation> sortedList = new ArrayList<>(employee.getFormations());
        CollectionsSort.sortFormation(sortedList);
        return sortedList.size() == 0 ? "" : sortedList.get(0).getSchool().getName();
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(final Sector sector) {
        this.sector = sector;
    }

    public BufferedImage getImage() throws IOException {
        File output = new File(getImagePath(id));
        BufferedImage image = ImageIO.read(output);
        return image;
    }

    public void setBufferedImage(BufferedImage image) throws IOException {
        File output = new File(getImagePath(id));
        ImageIO.write(image, "png", output);
    }

    public int getNbRelations() {
        return employee.getNbRelations();
    }


    private static String getImagePath(long id) {
        Path path = Paths.get("resources", "img", "employees", Long.toString(id) + "png");
//        Path path = Paths.get(Long.toString(id) + "png");
        System.err.println("Rainbow path : " + path.toAbsolutePath().toString());
        return path.toString();
    }

    @Override
    public String toString() {
        return "EmployeeDescription{" +
                "id=" + id +
                ", professionalTitle='" + professionalTitle + '\'' +
                ", country=" + country +
                ", sector=" + sector +
                '}';
    }

}
