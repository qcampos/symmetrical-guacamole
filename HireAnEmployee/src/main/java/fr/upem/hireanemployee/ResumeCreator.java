package fr.upem.hireanemployee;

import fr.upem.hireanemployee.profildata.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

/**
 * This class allows the creation of a LaTeX cv from a given Employee.
 * Created by Quentin on 11/03/2016.
 */
public class ResumeCreator {

    private static final String DOCUMENTCLASS = "\\documentclass[12pt,a4paper]{moderncv}\n";
    private static final String PACKAGES = "\\usepackage[utf8]{inputenc}\n" +
            "\\usepackage[T1]{fontenc}\n" +
            "\\usepackage[top=1.3cm, bottom=1.1cm, left=2cm, right=2cm]{geometry}\n";
    private static final String PACKAGECONF = "\\moderncvtheme[blue]{casual}\n";

    private static final String BEGIN = "\\begin{document}\n";
    private static final String END = "\\end{document}\n";
    private static final String TITLE = "\\maketitle\n";


    /**
     * Generate a LaTeX file corresponding to a CV from the given Employee.
     *
     * @param e the employee to create the CV.
     *
     * @return the path to the created file.
     *
     * @throws IOException if the file could not be created.
     */
    public static String generateCv(Employee e) throws IOException, InterruptedException {
        String content = generateLatexString(e);
        Path path = Paths.get(e.getId() + ".tex");
        Path pdfPath = Paths.get("cv" + e.getId() + ".pdf");
        Files.write(path, Arrays.asList(content.split("\n")), Charset.defaultCharset());

        Runtime r = Runtime.getRuntime();
        Process p = r.exec(String.format("pandoc %s -o %s", path.toString(), pdfPath.toString()));
        p.waitFor();

        return pdfPath.toAbsolutePath().toString();
    }

    private static String generateLatexString(final Employee e) {
        StringBuilder sb = new StringBuilder();
        // Put initial headers.
        sb.append(DOCUMENTCLASS).append(PACKAGES).append(PACKAGECONF);
        // Put users data.
        generateUserData(sb, e);
        // Beginning of the document.
        sb.append(BEGIN).append(TITLE);
        // Add The formations of the user
        generateUserFormations(sb, e);
        // Add experiences
        generateUserExperiences(sb, e);
        // Add skills
        generateUserSkills(sb, e);
        // End of the document.
        sb.append(END);
        return sb.toString().replace("%", "\\%");
    }

    private static void generateUserSkills(final StringBuilder sb, final Employee e) {
        if (e.getSkills().isEmpty()) {
            return;
        }
        sb.append("\\section{Skills}\n");
        for (SkillAssociation skill : e.getSkills()) {
            String s = String.format("\\cvitem{%s}{%s}", skill.getSkill().getName(), skill.getLevel());
            sb.append(s).append("\n");
        }
    }

    private static void generateUserExperiences(final StringBuilder sb, final Employee e) {
        if (e.getExperiences().isEmpty()) {
            return;
        }
        sb.append("\\section{Experience}\n");
        for (Experience experience : e.getExperiences()) {
            if (experience.getVisibility().equals(Visibility.PUBLIC)) {
                String s = String.format("\\cventry{%s--%s}{%s}{%S}{%s}{}{%s}", experience.getStartDate().getYear(), experience.getEndDate().getYear(), experience.getJobName(), experience.getCompanyName(), experience.getCountry(), experience.getJobDescription());
                sb.append(s).append("\n");
            }
        }
    }

    private static void generateUserFormations(final StringBuilder sb, final Employee e) {
        if (e.getFormations().isEmpty()) {
            return;
        }
        sb.append("\\section{Formation}\n");
        for (Formation formation : e.getFormations()) {
            if (formation.getVisibility().equals(Visibility.PUBLIC)) {
                String s = String.format("\\cventry{%d -- %d}{%s}{%s}{%s}{\\textit{%s}}{%s}",
                        formation.getStartDate().getYear(), formation.getEndDate().getYear(),
                        formation.getName(), formation.getSchool().getName(), formation.getSchool().getCountry(),
                        formation.getLevel(), formation.getDescription());
                sb.append(s).append("\n");
            }
        }
    }

    private static void generateUserData(final StringBuilder sb, final Employee e) {
        // Put users data
        String userData = String.format("\\firstname{%s}\n", e.getFirstName()) +
                String.format("\\familyname{%s}\n", e.getLastName()) +
                String.format("\\title{%s}\n", e.getDescription().getProfessionalTitle()) +
                String.format("\\email{%s}\n", e.getEmail());
        sb.append(userData);
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Employee e = new Employee("Jean-Yves", "Thibon", "Master of conferences", Country.FRANCE,
                new Sector("Combinatoire"), "jyt@upem.fr", "pony");
        Employee e2 = new Employee("Jean-Yves", "Thibon", "Master of conferences", Country.FRANCE,
                new Sector("Combinatoire"), "jyt@upem.fr", "pony");

        e.addExperience(new Experience("NSA", "Chef du service d'investigation", "Un travail super éprouvant",
                "Le poste occupé relève du secret absolu. Il n'est pas permis d'en parler, absolument pas",
                Country.FRANCE, new Date(114, 6, 1), new Date(114, 11, 29), Visibility.PUBLIC));
        e.addExperience(new Experience("CIA", "Agent de liaison sur le terrain", "Activités de communication",
                "Cryptage et décryptage de messages confidentiels, combat rapproché, artillerie lourde.",
                Country.FRANCE, new Date(114, 4, 1), new Date(114, 6, 1), Visibility.PUBLIC));

        e.addExperience(new Experience("Cartier", "Chargé du retailing", "jobAbstract", null, Country.AMERICAN_SAMOA,
                new Date(113, 2, 1), new Date(113, 10, 29), Visibility.PUBLIC));
        e.addExperience(new Experience("Weekendesk", "Asistant Marketing Manager", "jobAbstract",
                "Weekendesk is an European Online Travel Agency, operating in the French, Spanish, Belgian, Dutch and Italian markets, " +
                        "focused on delivering the best experience for short breaks and holidays. http://www.weekendesk.fr/ As a Marketing " +
                        "Assistant, I managed the digital presence of the Weekendesk brand in Spain on social networks: Facebook, Twitter and " +
                        "Google +.", Country.UNITED_ARAB_EMIRATES,
                new Date(114, 6, 1), new Date(114, 11, 29), Visibility.PRIVATE));
        // Begun the 1 and ended the 31 always. (1 Juillet <-> 29 Décembre (problem with 31 Fevrier does not exists).

        e.addExperience(new Experience("Dassault System", "MSc in Project and Programme Management and Business Development", "jobAbstract",
                "Member of the ICT Team The ICT team will deliver all communications and related services, " +
                        "as well as hardware and specialised solutions and applications during the event. ICT is responsible for the concept, " +
                        "set-up, operations and change management of all deliverables within the scope of ICT.", Country.ANTIGUA_AND_BARBUDA,
                new Date(115, 11, 1), new Date(116, 1, 29), Visibility.PUBLIC));

        e.addFormation(new Formation("Master’s Degree, Master of Science in Project and Programme Management and Business Development",
                "Programme entirely taught in English with in-company projects and assignments. Professional project management certifications: PRINCE 2 and PMI " +
                        "The only MSc programme in France that is 100% project management oriented. Ranked 3rd in France and 11th worldwide in its category by SMBG " +
                        "Global project management framework, tools and techniques; Sustainable project management and operational readiness; Organisation and Leadership, Strategy and business development ...",
                Formation.DegreeType.MASTER, new School("SKEMA Business School", Country.FRANCE), new Date(115, 1, 1), new Date(116, 1, 1), Visibility.PUBLIC));


        e.addFormation(
                new Formation("PhD in COmbinatorics", "Thesis with Shutz", Formation.DegreeType.PHD,
                        new School("Université Paris-Est Marne-la-Vallée",
                                Country.FRANCE), new Date(1515, 12, 25), new Date(1518, 12, 25), Visibility.PUBLIC)
        );

        generateCv(e);
    }


}
