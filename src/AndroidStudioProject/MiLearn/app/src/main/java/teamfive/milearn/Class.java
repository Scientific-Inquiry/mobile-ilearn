package teamfive.milearn;
import java.util.Vector;

public abstract class Class implements ClassInterface
{
	public String toString(Rank rank, Vector snames)
	{
		if (rank.toString().equals("STUDENT"))
		    return "{\"courseName\":" + "\"" + this.getName() + "\", " + "\"courseNumber\":\"" + this.getNumber() +
				"\", \"courseSection\":\"" + this.getSection() + "\", \"quarter\":\"" + this.getQuarter() + 
				"\", \"instructor\":\"" + this.getFaculty() + "\"}";
        else
        {
			String[] name = Login.messWithName((String) snames.get(0));
            String firstName = name[0];
            String lastName = name[1];

            return "{\"courseName\":" + "\"" + this.getName() + "\", " + "\"courseNumber\":\"" + this.getNumber()
                    + "\", \"quarter\":\"" + this.getQuarter() + "\", \"sname\":\"" + lastName + ", "
                    + firstName + "\", \"slogin\":\"" + snames.get(1) + "\"}";
        }
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
    protected String faculty; /* Faculty info */
}
