import java.util.ArrayList;

public class Student extends User {
	public Student(String name, String netid, int sid, ArrayList<Class> classes)
	{
		this.name = name;
		this.netid = netid;
		this.sid = sid;
		this.rank = Rank.STUDENT;
		this.classes = (ArrayList<Class>) classes.clone();
		this.writeJson("classes.json", Rank.STUDENT);
	}

	public Object clone()
	{
		return new Student(getName(), getNetid(), getSid(), getClasses());
	}

}
