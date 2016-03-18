package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Employee;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.ResumeCreator;
import fr.upem.hireanemployee.navigation.Constants;
import fr.upem.hireanemployee.profildata.*;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;

/**
 * Created by Baxtalou on 22/02/2016.
 * The main bean for the page : cv.xhtml.
 * Manages the employee's data bootsrap to feed : EmployeeDescriptionBean,
 * EmployeeExperiencesBean and EmployeeFormationBean.
 */
@ManagedBean
@ViewScoped
public class CvViewedBean extends Logger {

    @EJB
    private DatabaseDAO dao;

    private Employee employee;
    private EmployeeDescription employeeDescription;
    private Collection<Experience> employeeExperiences;
    private Collection<Formation> employeeFormations;

    // Current CV id
    private String idToParse;
    private long id;
    private boolean initialized;

    /**
     * Method call on the viewAction of page cv.xhtml. It provides initialization capabilities
     * and prevent multiple calls when post init or setter are used on ViewScoped bean.
     * Delegated backing beans like EmployeeDescriptionBean of EmployeeExperienceBean will have
     * direct injections of the description and experience of the retrieved user, thanks to
     * this lifecycle use.
     *
     * @return The current page if the id corresponds to an Employee. The 404 error page otherwise.
     * TODO redirect on a research page + the message "unknown employee" instead.
     */
    public String viewActionInit() {
        // Guard needed because of viewParam Bug with ajax in the API 2.2
        if (initialized) {
            return Constants.CURRENT_PAGE;
        }
        initialized = true;
        log("viewActionInit - id " + id);
        // Retrieving the employee in the database.
        employee = dao.getEmployeeByID(id);
        if (employee == null) {
            // TODO redirect on a special page for unknown employee + search bar.
            return "404";
        }
        // Setting fields accordingly.
        employeeDescription = employee.getDescription();
        employeeExperiences = employee.getExperiences();
        employeeFormations = employee.getFormations();
        log("init - employee " + employee.getFirstName() + " Exp : " + employeeExperiences + " Desc : " + employeeDescription
                + " Form : " + employeeFormations);
        return Constants.CURRENT_PAGE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        // Guard needed because of viewParam Bug with ajax in the API 2.2
        if (initialized) {
            return;
        }
        this.id = id;
    }

    public String getIdToParse() {
        return idToParse;
    }

    public void setIdToParse(String idToParse) {
        if (initialized) {
            return;
        }
        try {
            this.id = Long.parseLong(idToParse);
        } catch (NumberFormatException e) {
            log("ERROR setId numberFormat for : " + idToParse);
            this.id = 0;
        }
    }

    public Employee getEmployee() {
        // Java null pattern. This case is to prevent unusual problems like pressing the back button.
        // Jsf does not automatically save form's indexes valid even if it keeps the page's state.
        // We have to implement a post-get-redirect to pass get values in the back direction in the near future.

        // EDIT : Setting no cache works on every browser except Safari 9.0.3.
        // So we have to put the null pattern.
        return employee == null ? new Employee("", "", "", Country.NONE, new Sector(Sector.DEFAULT_NAME), "", "") : employee;
    }

    public EmployeeDescription getEmployeeDescription() {
        return employeeDescription;
    }

    public Collection<Experience> getEmployeeExperiences() {
        return employeeExperiences;
    }

    public Collection<Formation> getEmployeeFormations() {
        return employeeFormations;
    }

    public void downloadCV() throws IOException {
        log("downloadCV - Generating the CV.");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            String filePath = ResumeCreator.generateCv(employee);
            log("downloadCV - CV Generated in path : " + filePath);
            File file = new File(filePath);

            String fileName = file.getName();
            log("downloadCV - CV read from the system : " + fileName);
            String contentType = ec.getMimeType(fileName); // JSF 1.x: ((ServletContext) ec.getContext()).getMimeType(fileName);
            int contentLength = (int) file.length();

            ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
            ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
            ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

            OutputStream output = ec.getResponseOutputStream();
            // Now you can write the InputStream of the file to the above OutputStream the usual way.
            // ...

            Files.copy(file.toPath(), output);
            fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
