package teamfive.milearn;
public class Lecture extends Class {
    public Lecture(String number, String section, String name, String quarter, String faculty)
    {
        this.number = new String (number);
        this.section = new String(section);
        this.name = new String(name);
        this.quarter = new String(quarter);
        this.faculty = new String(faculty);
    }

    public String toString() {
        return super.toString();
    }

    public Lecture clone()
    {
        return new Lecture(getNumber(), getSection(), getName(), getQuarter(), getFaculty());
    }
}
