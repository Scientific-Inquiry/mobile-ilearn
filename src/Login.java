import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.sql.*;
import java.util.Vector;

public class Login {
    private Login()
    {
        this.username = null;
        this.password = null;
        this.user = null;
        this.snames = null;
    }

    /* Constructor. user takes a value only once a user has logged in. */
    private Login(String username, String password)
    {
        this.username = new String(username);
        this.password = new String(password);
        this.user = null;
    }

    private String getUsername()
    {
        return this.username;
    }

    private void setUsername(String username)
    {
        this.username = new String(username);
    }

    private String getPassword()
    {
        return this.password;
    }

    private void setPassword(String password)
    {
        this.password = new String(password);
    }

    private User getUser()
    {
        return this.user;
    }

    private void setUser(User user)
    {
        if (user instanceof Student)
        {
            this.user = (Student) ((Student)user).clone();
        }
        else
        {
            this.user = (Instructor) ((Instructor)user).clone();
        }
    }

    /* Checks if the user trying to log in exists and logs in if so. Does nothing if not. */
    private boolean checkCredentials(Connection connection, S3Manager s3)
    {
        try
        {
            /* Checks that there is a user in the Usr table with that username and that password (counts) */
            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM Usr U WHERE U.unetid = ? AND U.upassword = ?");
            st.setString(1, this.getUsername());
            st.setString(2, this.getPassword());
            ResultSet rs = st.executeQuery();

            /* Places the "cursor" on the first (and only, in that case) result */
            rs.next();

            if (rs.getInt(1) == 1) /* If there is such a user */
            {
                System.out.println("User found!");

                /* Selects all information about that user */
                st = connection.prepareStatement("SELECT * FROM Usr U WHERE U.unetid = ?");
                st.setString(1, this.getUsername());
                rs = st.executeQuery();
                rs.next();

                /* Saves that info to use it later (User objects generation & Json generation) */
                String name = rs.getString("uname").trim();
                String password = this.getPassword();
                String login = this.getUsername();
                int sid = rs.getInt("uid");
                String theme = rs.getString("theme").trim();
                int notifyH = rs.getInt("notifyH");
                int notifyM = rs.getInt("notifyM");
                int notifyL = rs.getInt("notifyL");
                String rank = rs.getString("urank").trim();


                /* All JSON files are written locally, uploaded to S3, and then removed from the local memory */
                if (rank.trim().equals("STUDENT"))
                {
                    ArrayList<Class> classes = classesStudent(connection, sid);
                    this.user = new Student (name, this.getUsername(), sid, classes, this.snames);
                    /*s3.path = "testCandice/user/" + this.getUsername() + "/classes.json";
                    s3.upload_file("classes.json");
                    File file = new File("classes.json");
                    file.delete();*/
                    Login.rank = Rank.STUDENT;
                }
                else if (rank.trim().equals("INSTRUCTOR"))
                {
                    ArrayList<Class> classes = classesInstructor(connection, sid, name);
                    this.user = new Instructor(name, this.getUsername(), sid, classes, this.snames);
                    /*s3.path = "testCandice/user/" + this.getUsername() + "/course.json";
                    s3.upload_file("course.json");
                    File file = new File("course.json");
                    file.delete();*/
                    Login.rank = Rank.INSTRUCTOR;
                }
                else
                {
                    ArrayList<Class> classes = classesStudent(connection, sid);
                    ArrayList<Class> taught = classesInstructor(connection, sid, name);
                    this.user = new TA(name, this.getUsername(), sid, classes, taught, this.snames);
                    /*s3.path = "testCandice/user/" + this.getUsername() + "/classes.json";
                    s3.upload_file("classes.json");
                    File file = new File("classes.json");
                    file.delete();
                    s3.path = "testCandice/user/" + this.getUsername() + "/course.json";
                    s3.upload_file("course.json");
                    file = new File("course.json");
                    file.delete();*/
                    Login.rank = Rank.TA;
                }
                writeUser(name, login, password, theme, notifyH, notifyM, notifyL);
                /*s3.path = "testCandice/user/" + this.getUsername() + "/user.json";
                s3.upload_file("user.json");
                File file = new File("user.json");
                file.delete();*/
                rs.close();
                st.close();
                return true;
            }
            else
            {
                System.out.println("User not found!");
                rs.close();
                st.close();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Creates the classes array necessary to the creation of the Student or TA object */
    private ArrayList<Class> classesStudent(Connection connection, int sid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT DISTINCT C.cid, C.cname, C.csection, C.cnum, C.cquarter, C.ctype, U2.uname FROM Class C, enrolls_in E, teaches T, Usr U, Usr U2 WHERE U.uid = ? AND U.uid = E.uid AND C.cid = E.cid AND T.cid = E.cid AND U2.uid = T.uid ORDER BY cnum ASC, csection ASC, U2.uname ASC");
            st.setInt(1, sid);
            ResultSet rs = st.executeQuery();
            ArrayList<Class> classes = new ArrayList<Class>();
            ArrayList<ArrayList<Vector>> snames = new ArrayList<ArrayList<Vector>>();

            while (rs.next()) {
                String cname = rs.getString("cname").trim();
                String cnum = rs.getString("cnum").trim();
                String csection = rs.getString("csection").trim();
                String cquarter = rs.getString("cquarter").trim();
                String uname = rs.getString("uname").trim();

                if (rs.getString("ctype").trim().equals("LECTURE")) {
                    Lecture lec = new Lecture(cnum, csection, cname, cquarter, uname);
                    classes.add(lec);
                } else if (rs.getString("ctype").trim().equals("DISCUSSION")) {
                    Discussion dis = new Discussion(cnum, csection, cname, cquarter, uname);
                    classes.add(dis);
                } else {
                    Lab lab = new Lab(cnum, csection, cname, cquarter, uname);
                    classes.add(lab);
                }

                /* For each class, get the list of the students' name and login, and save it */
                /* Here, it is useless, but we need to do it to avoid throwing NullPointer exceptions */
                PreparedStatement students = connection.prepareStatement("SELECT U.uname, U.unetid FROM Usr U, enrolls_in E WHERE E.cid = ? AND E.uid = U.uid ORDER BY unetid ASC");
                students.setInt(1, rs.getInt("cid"));
                ResultSet rstudents = students.executeQuery();

                ArrayList<Vector> tmp = new ArrayList<Vector>();
                while (rstudents.next())
                {
                    Vector v = new Vector(2);
                    v.add(0, rstudents.getString("uname").trim());
                    v.add(1, rstudents.getString("unetid").trim());
                    tmp.add(v);
                }
                snames.add(tmp);
            }
            studentsList(snames);

            //severalInstructors(classes);
            return classes;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<Class>();
        }
    }

    /* Creates the classes (or taught) array necessary to the creation of the Instructor or TA object */
    private ArrayList<Class> classesInstructor(Connection connection, int tid, String name)
    {
        try
        {
            PreparedStatement st = connection.prepareStatement("SELECT C.cid, C.cname, C.csection, C.cnum, C.cquarter, C.ctype FROM Class C, teaches T, Usr U WHERE U.uid = ? AND T.uid = U.uid AND C.cid = T.cid ORDER BY cnum ASC, csection ASC");
            st.setInt(1, tid);
            ResultSet rs = st.executeQuery();
            ArrayList<Class> classes = new ArrayList<Class>();
            ArrayList<ArrayList<Vector>> snames = new ArrayList<ArrayList<Vector>>();

            while (rs.next()) {
                String cname = rs.getString("cname").trim();
                String cnum = rs.getString("cnum").trim();
                String csection = rs.getString("csection").trim();
                String cquarter = rs.getString("cquarter").trim();

                if (rs.getString("ctype").trim().equals("LECTURE")) {
                    Lecture lec = new Lecture(cnum, csection, cname, cquarter, name);
                    classes.add(lec);
                } else if (rs.getString("ctype").trim().equals("DISCUSSION")) {
                    Discussion dis = new Discussion(cnum, csection, cname, cquarter, name);
                    classes.add(dis);
                } else {
                    Lab lab = new Lab(cnum, csection, cname, cquarter, name);
                    classes.add(lab);
                }

                /* For each class, get the list of the students' name and login, and save it */
                PreparedStatement students = connection.prepareStatement("SELECT U.uname, U.unetid FROM Usr U, enrolls_in E WHERE E.cid = ? AND E.uid = U.uid ORDER BY unetid ASC");
                students.setInt(1, rs.getInt("cid"));
                ResultSet rstudents = students.executeQuery();

                ArrayList<Vector> tmp = new ArrayList<Vector>();
                while (rstudents.next())
                {
                    Vector v = new Vector(2);
                    v.add(0, rstudents.getString("uname").trim());
                    v.add(1, rstudents.getString("unetid").trim());
                    tmp.add(v);
                }
                snames.add(tmp);
            }
            studentsList(snames);
            return classes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<Class>();
        }
    }

    public static String[] messWithName(String name)
    {
        String[] parts = name.split(" ");
        return parts;
    }

    /* Make sure that the list of the students is saved */
    private void studentsList(ArrayList<ArrayList<Vector>> snames) {
        this.snames = snames;
    }


    /* Checks if some classes are taught by several instructors. If so, "merge" all the lines
       corresponding to those classes to have one line per class with the names of all the instructors. */
    private static void severalInstructors(ArrayList<Class> classes)
    {
        for (int i = 0; i < classes.size() - 1; i++)
        {
            for (int j = i+1; j < classes.size(); j++)
            {
                if (classes.get(i).getNumber().equals(classes.get(j).getNumber())
                        && classes.get(i).getSection().equals(classes.get(j).getSection())
                        && !classes.get(i).getFaculty().equals(classes.get(j).getFaculty()))
                {
                    classes.get(i).setFaculty(classes.get(i).getFaculty() + ", " + classes.get(j).getFaculty());
                    classes.remove(j);
                    j = j - 1; // We want to be sure that we are not "jumping" over a class
                }
            }
        }
    }

    public static void writeUser(String name, String login, String password, String theme, int notifyH, int notifyM, int notifyL)
    {
        try{
            int rank;
            if (Login.rank.toString().equals("STUDENT"))
                rank = 0;
            else if (Login.rank.toString().equals("INSTRUCTOR"))
                rank = 1;
            else
                rank = 2; // TA

            PrintWriter file = new PrintWriter("user.json");
            file.println("[");
            file.println("{\"name\":\"" + name + "\", \"login\":\"" + login + "\", \"password\":\""
                    + password + "\", \"theme\":\"" + theme + "\", \"notifyH\":\"" + notifyH + "\", \"notifyM\":\""
                    + notifyM + "\", \"notifyL\":\"" + notifyL + "\", \"type\":\"" + rank + "\"}");
            file.println("]");
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    /* If the function returns an empty string, the authentication failed. If it returns a non-empty string, it worked and the string can be used to access the correct directory */
    public static boolean login(String username, String password)
    {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return false;
        }
        Connection connection = null;
        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);

            Statement st = connection.createStatement();

            Login log = new Login(username, password);
            S3Manager s3 = new S3Manager();
            if (log.checkCredentials(connection, s3)) {
                login = log.getUsername();
                return true;
            }
            else
            {
                rank = null;
                return false;
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        finally
        {
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /* Might actually not be useful */
    public void logout()
    {
        this.username = null;
        this.password = null;
        this.user = null;
        this.snames = null;
        login = null;
        rank = null;
    }


    public static void main(String[] argv)
    {
        login("balle056", "iamtheflash");
        String[] name = messWithName("Bruce Wayne");
        System.out.println(name[1] + ", " + name[0]);
    }

    private String username;
    private String password;
    private User user;
    public static String login;
    public static Rank rank; /* rank.toString().equals("BLAHBLAH"); */
    private ArrayList<ArrayList<Vector>> snames;
}
