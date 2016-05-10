package teamfive.milearn;
import java.util.ArrayList;


public interface ClassInterface {
    String toString();
    String getNumber();
    void setNumber(String number);
    String getSection();
    void setSection(String section);
    String getName();
    void setName(String name);
    String getQuarter();
    void setQuarter(String quarter);
    /*ArrayList<String> getAssignments();
    void setAssignments(ArrayList<String> assignments);
    ArrayList<String> getCourseMaterial();
    void setCourseMaterial(ArrayList<String> courseMaterial);
    ArrayList<String> getGrades();
    void setGrades(ArrayList<String> courseMaterial);
    String getSyllabus();
    void setSyllabus(String syllabus);*/
    String getFaculty();
    void setFaculty(String faculty);
}
