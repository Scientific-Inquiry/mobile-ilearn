import java.util.ArrayList;

public abstract class Class implements ClassInterface
{
	public String toString() 
	{
		return "{\"courseName\":" + "\"" + this.getName() + "\", " + "\"courseNumber\":\"" + this.getNumber() + 
				"\", \"courseSection\":\"" + this.getSection() + "\", \"quarter\":\"" + this.getQuarter() + 
				"\", \"instructor\":\"" + this.getFaculty() + "\"}";
	}
	
	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number) 
	{
		this.number = number;
	}

	public String getSection() 
	{
		return section;
	}

	public void setSection(String section) 
	{
		this.section = section;
	}

    public String getName()
	{
		return name;
	}

    public void setName(String name)
	{
		this.name = name;
	}

    public String getQuarter()
	{
		return quarter;
	}

    public void setQuarter(String quarter)
	{
		this.quarter = quarter;
	}

/*    public ArrayList<String> getAssignments()
    {
        return (ArrayList<String>) this.assignments.clone();
    }

    public void setAssignments(ArrayList<String> assignments)
    {
        this.assignments = (ArrayList<String>) assignments.clone();
    }

    public ArrayList<String> getCourseMaterial()
    {
        return (ArrayList<String>) this.courseMaterial.clone();
    }

    public void setCourseMaterial(ArrayList<String> courseMaterial)
    {
        this.courseMaterial = (ArrayList<String>) courseMaterial.clone();
    }

    public ArrayList<String> getGrades()
    {
        return (ArrayList<String>) this.grades.clone();
    }

    public void setGrades(ArrayList<String> grades)
    {
        this.grades = (ArrayList<String>) grades.clone();
    }

    public String getSyllabus()
    {
        return this.syllabus;
    }

    public void setSyllabus(String syllabus)
    {
        this.syllabus = new String(syllabus);
    }
*/
    public String getFaculty()
    {
        return this.faculty;
    }

    public void setFaculty(String faculty)
    {
        this.faculty = new String(faculty);
    }


    protected String number;
	protected String section;
	protected String name;
	protected String quarter;
	/* Meant to be an array of Assignments objects */
	/*protected ArrayList<String> assignments;*/
	/* Meant to be an array of Course Material objects */
	/*protected ArrayList<String> courseMaterial;*/
    /* Meant to be an array of Grades objects */
    /*protected ArrayList<String> grades;*/
    /*protected String syllabus;*/
    protected String faculty; /* Faculty info */
}
