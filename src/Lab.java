import java.util.ArrayList;

public class Lab extends Class
{
	public Lab(String number, String section, String name, String quarter, /*ArrayList<String> assignments, ArrayList<String> courseMaterial, ArrayList<String> grades, String syllabus,*/ String faculty)
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
	
	public String toString()
	{
		return "Lab " + super.toString();
	}
	
	public Lab clone()
	{
		return new Lab(getNumber(), getSection(), getName(), getQuarter(), /*getAssignments(), getCourseMaterial(), getGrades(), getSyllabus(),*/ getFaculty());
	}
}
