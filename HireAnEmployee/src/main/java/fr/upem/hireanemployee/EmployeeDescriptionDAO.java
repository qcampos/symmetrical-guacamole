package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.Country;
import fr.upem.hireanemployee.profildata.EmployeeDescription;
import fr.upem.hireanemployee.profildata.Sector;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Created by Quentin on 21/02/2016.
 */
@Stateless
public class EmployeeDescriptionDAO {

    @PersistenceContext(unitName = "hireaePU")
    private EntityManager em;

    public EmployeeDescription updateNames(EmployeeDescription desc, String firstName, String lastName) {
        desc.getEmployee().setFirstName(firstName);
        desc.getEmployee().setLastName(lastName);
        em.merge(desc);
        em.flush();
        return desc;
    }

    public EmployeeDescription updateProfessionalTitle(EmployeeDescription employeeDescription, String professionalTitle) {
        employeeDescription.setProfessionalTitle(professionalTitle);
        em.merge(employeeDescription);
        em.flush();
        return employeeDescription;
    }

    public EmployeeDescription updateImage(EmployeeDescription employeeDescription, BufferedImage image) {
        try {
            employeeDescription.setBufferedImage(image);
            em.merge(employeeDescription);
            em.flush();
        } catch (IOException e) {
            // FIXME i/o error on a save. The image changing is impossible
            throw new UncheckedIOException("Cannot save the new image on the disk for EmployeeDescription " + employeeDescription.getId(), e);
        }
        return employeeDescription;
    }

    public EmployeeDescription updateCountry(EmployeeDescription employeeDescription, Country country) {
        employeeDescription.setCountry(country);
        em.merge(employeeDescription);
        em.flush();
        return employeeDescription;
    }

    public EmployeeDescription updateSector(EmployeeDescription employeeDescription, String sectorName) {
        Sector sector;
        try {
            sector = em.createQuery("Select s from Sector s where s.name = :sectorName", Sector.class).setParameter("sectorName", sectorName).getSingleResult();
        } catch (NoResultException e) {
            sector = new Sector(sectorName);
            em.persist(sector);
            em.flush();
        }
        employeeDescription.setSector(sector);
        em.merge(employeeDescription);
        return employeeDescription;
    }

}
