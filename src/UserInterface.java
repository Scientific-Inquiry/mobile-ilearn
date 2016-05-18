import java.util.ArrayList;
import java.util.Vector;

public interface UserInterface {
	public void displayClasses();
	public String getName();
	public void setName(String name);
	public Rank getRank();
	public void setRank(Rank rank);
	public ArrayList<Class> getClasses();
	public void setClasses(ArrayList<Class> classes);
	public String getNetid();
	public void setNetid(String netid);
	public int getSid();
	public void setSid(int sid);
	public ArrayList<ArrayList<Vector>> getSnames();
	public void setSnames(ArrayList<ArrayList<Vector>> s);
	public void writeJson(String filename, Rank rank, ArrayList<ArrayList<Vector>> snames);
}
