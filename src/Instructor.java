import java.io.PrintWriter;
import java.util.ArrayList;

public class Instructor extends User {
	public Instructor(String name, String netid, int sid, ArrayList<Class> classes)
	{
		this.name = name;
        this.netid = netid;
        this.sid = sid;
		this.rank = Rank.INSTRUCTOR;
		this.classes = (ArrayList<Class>) classes.clone();
		
		/* Writes course.json as soon as a new instructor is created */
		try{
			PrintWriter file = new PrintWriter("course.json");
			file.println("\"course\":[");
			for (int i = 0; i < this.getClasses().size(); i++)
			{
				file.println("{\"courseName\":" + "\"" + this.getClasses().get(i).getName() + "\", " + "\"courseNumber\":\"" + this.getClasses().get(i).getNumber() + 
					"\", \"quarter\":\"" + this.getClasses().get(i).getQuarter() + "\"}");
			}
			file.println("]");
	        file.close();
	    }catch(Exception e) {
	        System.out.println("ERROR");
	        e.printStackTrace();
	    }
	}

	public Object clone()
    {
        return new Instructor(getName(), getNetid(), getSid(), getClasses());
    }
	
	public static void main(String[] args)
	{
		ArrayList<Class> classes = new ArrayList<Class>();
		classes.add(new Lecture("CS 180", "001", "Intro to Software Engineering", "Sping 2016", "Oben"));
		classes.add(new Lecture("CS 183", "002", "Intro to Software Engineering", "Sping 2016", "Oben"));
		Instructor me = new Instructor("John Smith", "jsmit090", 987654321, classes);
		System.out.println("Done (instructor)!");
	}
}
