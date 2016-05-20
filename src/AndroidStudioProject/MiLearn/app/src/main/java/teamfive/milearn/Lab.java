package teamfive.milearn;

import java.util.ArrayList;

public class Lab extends Class
{
	public Lab(String number, String section, String name, String quarter, String faculty)
	{
		this.number = new String (number);
		this.section = new String(section);
		this.name = new String(name);
		this.quarter = new String(quarter);
		this.faculty = new String(faculty);
	}
	
	public String toString()
	{
		return "Lab " + super.toString();
	}
	
	public Lab clone()
	{
		return new Lab(getNumber(), getSection(), getName(), getQuarter(), getFaculty());
	}
}
