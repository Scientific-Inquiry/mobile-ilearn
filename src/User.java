import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;

public abstract class User implements UserInterface {
	public void displayClasses()
	{
		for (int i = 0; i < classes.size(); i++)
		{
			System.out.println(classes.get(i).toString());
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	public void setName(String name)
	{
		this.name = new String(name);
	}
	public Rank getRank()
	{
		return this.rank;
	}
	public void setRank(Rank rank)
	{
		this.rank = rank;
	}
	public ArrayList<Class> getClasses()
	{
		return this.classes;
	}
	public void setClasses(ArrayList<Class> classes)
	{
		this.classes = (ArrayList<Class>) classes.clone();
	}
	
	public void writeJson()
	{
		try{
			PrintWriter file = new PrintWriter("classes.json");
			file.println("\"classes\":[");
			for (int i = 0; i < this.getClasses().size(); i++)
			{
				file.println(classes.get(i).toString());
			}
			file.println("]");
	        file.close();
	    }catch(Exception e) {
	        System.out.println("ERROR");
	        e.printStackTrace();
	    }
	}
		


	protected String name;
	protected Rank rank;
	protected ArrayList<Class> classes;
}