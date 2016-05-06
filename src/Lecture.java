public class Lecture extends Class {
    public Lecture(String number, String section, String name, String quarter, String faculty)
    {
        this.number = new String (number);
        this.section = new String(section);
        this.name = new String(name);
        this.quarter = new String(quarter);
        /*this.assignments = (ArrayList<String>) assignments.clone();
        this.courseMaterial = (ArrayList<String>) courseMaterial.clone();
        this.grades = (ArrayList<String>) grades.clone();
        this.syllabus = new String (syllabus);*/
        this.faculty = new String(faculty);
    }

    public String toString() {
        return super.toString();
    }

    public Lecture clone()
    {
        return new Lecture(getNumber(), getSection(), getName(), getQuarter(), /*getAssignments(), getCourseMaterial(), getGrades(), getSyllabus(),*/ getFaculty());
    }

    public static void main(String[] args)
    {
        Lecture lec = new Lecture("CS 183", "001", "Intro to Software Engineering", "Sping 2016", "Oben");
        System.out.println(lec.toString());
    }
}
