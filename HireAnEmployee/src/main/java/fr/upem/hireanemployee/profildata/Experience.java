package fr.upem.hireanemployee.profildata;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Quentin on 18/02/2016.
 */
@Entity
public class Experience {

    @Id
    @GeneratedValue
    private long id;

    private String CompanyName;
    private String jobName;
    private String jobAbstract;
    private Country country;
    private Visibility visibility;
    @Column(length = 2000)
    private String jobDescription;

    @Temporal(javax.persistence.TemporalType.DATE)
    // @Past
    private Date startDate;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;

    public Experience() {
    }

    public Experience(final String companyName, final String jobName, final String jobAbstract, final String jobDescription,
                      Country country, final Date startDate, final Date endDate, final Visibility visibility) {
        CompanyName = companyName;
        this.jobName = jobName;
        this.jobAbstract = jobAbstract;
        this.jobDescription = jobDescription;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visibility = visibility;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(final String companyName) {
        CompanyName = companyName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    public String getJobAbstract() {
        return jobAbstract;
    }

    public void setJobAbstract(final String jobAbstract) {
        this.jobAbstract = jobAbstract;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(final String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date from) {
        this.startDate = from;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date to) {
        this.endDate = to;
    }

    public Country getCountry() {
        return country;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", CompanyName='" + CompanyName + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobAbstract='" + jobAbstract + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        // Careful, our equal is a handle with an id
        // (only thing needed inside EmployeeExperienceBean class).
        if (!(obj instanceof Experience)) {
            return false;
        }
        Experience experience = (Experience) obj;
        return id == experience.id;
    }

    /**
     * @return The concatenation of every ids in the given Experience Collection.
     */
    public static String printIds(Collection<? extends Experience> experiences) {
        StringBuilder n = new StringBuilder();
        for (Experience e : experiences) {
            n.append(e.getId()).append(' ');
        }
        return n.toString();
    }
}
