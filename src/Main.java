import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class Main{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Connection
        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        String title1 = "Project 2", title2 = "Lab 3: Investigate a Mugging", title3 = "Investigation Article 4",
                title4 = "Investigation Article 5";
        String desc1 = "Design and build a projectile machine. You will need to turn in your design document, a blueprint, and the prototype of your machine.",
                desc2 = "You will be taken to a fake crime scene on campus and have the entire lab time to gather as much evidence as you can from the scene and write a report about your findings.<br /><br />You should be as thorough and conscientious as possible when deciding what evidence that you will use in your report as well use proper investigative principles.",
                desc3 = "Pick up your assigned evidence to write an article about an ongoing investigation. You will be penalized for for not writing about what evidence you have.",
                desc4 = "Pick up your assigned evidence to write an article about an ongoing investigation. You will be penalized for for not writing about what evidence you have.";

        int points1 = 100, points2 = 10, points3 = 50, points4 = 60;
        String courseNum1 = "PHYS163", courseNum2 = "BIOL124L", courseNum3 = "CRWT177", courseNum4 = "CRWT177";

        String courseSec1 = "001", courseSec2 = "002", courseSec3 = "021", courseSec4 = "001";
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

        String due1 = "Wed May 4 18:30:00 PDT 2016", due2 = "Thu May 5 14:30:00 PDT 2016", due3 = "Mon May 9 16:00:00 PDT 2016"
                , due4 = "Mon May 16 16:00:00 PDT 2016";

        Date d1 = new Date(), d2 = new Date(), d3 = new Date(), d4 = new Date();
        try {
            d1 = formatter.parse(due1);
            d2 = formatter.parse(due2);
            d3 = formatter.parse(due3);
            d4 = formatter.parse(due4);
        }catch(Exception e){
            System.out.println("Error in making date!");
        }

        //String t, String d, Date due, int pts, String cnum,int csect
        Assignment a1 = new Assignment(title1, desc1, d1, points1, courseNum1, courseSec1);
        Assignment a2 = new Assignment(title2, desc2, d2, points2, courseNum2, courseSec2);
        Assignment a3 = new Assignment(title3, desc3, d3, points3, courseNum3, courseSec3);
        Assignment a4= new Assignment(title4, desc4, d4, points4, courseNum4, courseSec4);

        Assignments collection = new Assignments();
        collection.addAssign(a1);
        collection.addAssign(a2);
        collection.addAssign(a3);
        collection.addAssign(a4);
        System.out.println(a1);
        Grade score1 = new Grade(a1, 25), score2 = new Grade(a3, 5);
        Grades g_collection = new Grades();

        Login.set_uid();
        //g_collection.addGrade(score1);
        //g_collection.createJSON_File();
        //g_collection.init_file();

        S3Manager def = new S3Manager();
        DBManager DBdef = new DBManager();
        //DBdef.
        System.out.println("SUBMISSIONS");
        DBdef.displayTable(def.connection, "Submissions");
        //DBdef.displayTable(def.connection, "");
        String name = "Investigation Article 3", description = "Pick up your assigned evidence to write an article"
                + " about an ongoing investigation. You will be penalized for not writing about what evidence you have.";
        int points = 50, classid = 1;
;
        System.out.println("" + Login.snames);
        //DBdef.update_Assignment_Name(def.connection, 20,"Checking updates");
        //DBdef.addGrade(def.connection, 20, Login.login, "bwayn052", 10);
        //DBdef.deleteGrade(def.connection, 20, Login.login);
        //DBdef.update_Grade(def.connection, 20, Login.login, 30)

        //DBdef.init_Assignments(def.connection, 20, Login.login);
        DBdef.isLate(def.connection, "bwayn052", 20);
        //DBdef.add_Assignment();
        //System.out.println(def.check_Folders("data/users/ckent038"));
        //DBdef.displayTable(connection, "Assignments");
        SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy HH:mm:ss", Locale.US);
        Date due;

        try {
            due = format.parse("06 10 2016 11:59:59");
            System.out.println(due);
        }catch (Exception e){
            System.out.println("ERROR");
        }


        //DBdef.deleteGrade(connection, 20, Login.login);
        //DBdef.listGrades(connection, 20);

        //DBdef.find_aid(connection, "Investigation Article 3", "CRWT177");
        //Connection connection, String netid, String cname ,int aid, String filename, String suffix
        File submit = new File("Submission.txt");
        DBdef.update_Assignment_Name(def.connection, 20, "UPDATED JSON");
        //DBdef.update_Submission(def.connection, "bwayn052", "CRWT177-001", 20, submit);
        DBdef.update_Grade(def.connection, 20, Login.login, 10);
        //DBdef.displayTable(connection, "Submissions");
        //DBdef.update_AssignmentDescription(connection, 20, "Follow specified format as discussed in class.");
        //DBdef.addGrade(connection, 20, Login.login, 25);

        //DBdef.init_Grades(connection, Login.login, 10);
        //DBdef.init_Assignments(connection, Login.login);
        //DBdef.addAssignment(connection, classid, name, description, due, points);
        //DBdef.addAssignment(connection, 1, "Investigation Article 4", "Pick up your assigned evidence to write an article"
        //+ "about an ongoing investigation. You will be penalized for not writing about what evidence you have.", due, 50);


        //DBdef.init_Assignments(connection, Login.login);
        //System.out.println("Login is "+ Login.login);
        //def.generate_Path("bwayn052","grade.json");
        //System.out.println(def);
        //def.check_Folders("data");
        //System.out.println("-----UPDATING-----");

        //def.upload_file("Grades.json");

        //def.read();

        //def.create_Folder("end/reallyend");
        //def.delete_Object("temp/user/moody/end/reallyend");

        //def.read();


        /*
        //111 is cid in database
        //b.create_Assignment();
        b.set_aid(111);
        b.obtain_Assignment();

        b.update_Bucket();
        //b.delete_Assignment();
        //b.obtain_Assignment();
        //score1.add_Grade(1234);
        //score1.obtain_Grade();
        //score1.update_Grade(1234, 25);
        //score1.obtain_Grade();
        //score1.managing_GDatabase();
        */
    }
}