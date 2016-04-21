import java.util.ArrayList;

public interface UserInterface {
	public void displayClasses();
	public String getName();
	public void setName(String name);
	public Rank getRank();
	public void setRank(Rank rank);
	public ArrayList<Class> getClasses();
	public void setClasses(ArrayList<Class> classes);
}
