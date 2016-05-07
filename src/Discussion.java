import java.util.ArrayList;

public class Discussion extends Class {

	public Discussion(String number, String section, String name, String quarter, String faculty)
	{
		this.number = new String (number);
		this.section = new String(section);
		this.name = new String(name);
		this.quarter = new String(quarter);
		this.faculty = new String(faculty);
	}
	
	public String toString()
	{
		return "Discussion " + super.toString();
	}
	
	public Discussion clone()
	{
		return new Discussion(getNumber(), getSection(), getName(), getQuarter(), getFaculty());
	}
}
