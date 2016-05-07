import java.util.ArrayList;
import java.util.Vector;

public class Instructor extends User {
	public Instructor(String name, String netid, int sid, ArrayList<Class> classes, ArrayList<ArrayList<Vector>> snames)
	{
		this.name = name;
        this.netid = netid;
        this.sid = sid;
		this.rank = Rank.INSTRUCTOR;
		this.classes = (ArrayList<Class>) classes.clone();
		this.snames = (ArrayList<ArrayList<Vector>>) snames.clone();
		this.writeJson("course.json", Rank.INSTRUCTOR, snames);
	}

	public Object clone()
    {
        return new Instructor(getName(), getNetid(), getSid(), getClasses(), getSnames());
    }
}

