import java.util.ArrayList;

public class Student extends User {
	public Student(String name, ArrayList<Class> classes)
	{
		this.name = name;
		this.rank = Rank.STUDENT;
		this.classes = (ArrayList<Class>) classes.clone();
		this.writeJson();
	}

	public Object clone()
	{
		return new Student(getName(), getClasses());
	}
	
	public static void main(String[] args)
	{
		ArrayList<Class> classes = new ArrayList<Class>();
		classes.add(new Lecture("CS 180", "001", "Intro to Software Engineering", "Sping 2016", "Oben"));
		classes.add(new Lecture("CS 183", "002", "Intro to Software Engineering", "Sping 2016", "Oben"));
		Student me = new Student("John Doe", classes);
		System.out.println("Done!");
	}
}
