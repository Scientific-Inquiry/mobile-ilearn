import java.util.ArrayList;
import java.util.Vector;

public abstract class Class implements ClassInterface
{
	public String toString(Rank rank, ArrayList<Vector> snames)
	{
		if (rank.toString().equals("STUDENT"))
		    return "{\"courseName\":" + "\"" + this.getName() + "\", " + "\"courseNumber\":\"" + this.getNumber() +
				"\", \"courseSection\":\"" + this.getSection() + "\", \"quarter\":\"" + this.getQuarter() + 
				"\", \"instructor\":\"" + this.getFaculty() + "\"}";
        else
        {
			String s = ", \"sname\":\"";
			for (int i = 0; i < snames.size(); i++)
			{
				if (i != snames.size()-1)
					s = s + snames.get(i).get(0) + ", ";
				else
					s = s + snames.get(i).get(0);
			}
			s = s + "\", \"slogin\":\"";
			for (int i = 0; i < snames.size(); i++)
			{
				if (i != snames.size()-1)
					s = s + snames.get(i).get(1) + ", ";
				else
					s = s + snames.get(i).get(1);
			}
			s = s + "\"";
            return "{\"courseName\":" + "\"" + this.getName() + "\", " + "\"courseNumber\":\"" + this.getNumber() +
                    "\", \"quarter\":\"" + this.getQuarter() + "\"" + s + "}";
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
