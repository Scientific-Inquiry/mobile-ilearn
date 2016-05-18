package teamfive.milearn;
import java.util.ArrayList;
import java.util.Vector;

public class Student extends User {
	public Student(String name, String netid, int sid, ArrayList<Class> classes, ArrayList<ArrayList<Vector>> snames)
	{
		this.name = name;
		this.netid = netid;
		this.sid = sid;
		this.rank = Rank.STUDENT;
		this.classes = (ArrayList<Class>) classes.clone();
		this.snames = (ArrayList<ArrayList<Vector>>) snames.clone();
		this.writeJson("classes.json", Rank.STUDENT, getSnames());
	}

	public Object clone()
	{
		return new Student(getName(), getNetid(), getSid(), getClasses(), getSnames());
	}

}
