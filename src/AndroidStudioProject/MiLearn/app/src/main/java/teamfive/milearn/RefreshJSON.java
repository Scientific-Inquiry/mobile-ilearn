package teamfive.milearn;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

/* Rewrite all the JSON files for the user that is currently logged in */
public class RefreshJSON {

    public static void connectDB()
    {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }
        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            RefreshJSON.connection = DriverManager.getConnection(dbURL, user, pass);;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void closeDB()
    {
        try{
            RefreshJSON.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refreshUser()
    {
        String login = Login.login;
        Connection c = RefreshJSON.connection;

        try {
            PreparedStatement st = c.prepareStatement("SELECT * FROM Usr WHERE unetid = ?;");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            rs.next();
            Login.writeUser(rs.getString("uname"), login, rs.getString("upassword"), rs.getString("theme"), rs.getInt("notifyH"), rs.getInt("notifyM"), rs.getInt("notifyL"));
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public static void refreshClasses() {
        if (Login.rank.toString().equals("STUDENT") || Login.rank.toString().equals("TA")) {
            String login = Login.login;
            Connection c = RefreshJSON.connection;

            try {
                PreparedStatement st = connection.prepareStatement("SELECT DISTINCT C.cid, C.cname, C.csection, C.cnum, C.cquarter, C.ctype, U2.uname FROM Class C, enrolls_in E, teaches T, Usr U, Usr U2 WHERE U.unetid = ? AND U.uid = E.uid AND C.cid = E.cid AND T.cid = E.cid AND U2.uid = T.uid ORDER BY cnum ASC, csection ASC, U2.uname ASC");
                st.setString(1, login);
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
                    while (rstudents.next()) {
                        Vector v = new Vector(2);
                        v.add(0, rstudents.getString("uname").trim());
                        v.add(1, rstudents.getString("unetid").trim());
                        tmp.add(v);
                    }
                    snames.add(tmp);

                    PrintWriter file = new PrintWriter("classes.json");
                    file.println("[");
                    for (int i = 0; i < classes.size(); i++)
                    {
                        for (int j = 0; j < snames.get(i).size(); j++)
                        {
                            if (i != classes.size()-1)
                                file.println(classes.get(i).toString(Login.rank, snames.get(i).get(j)) + ",");
                            else
                                file.println(classes.get(i).toString(Login.rank, snames.get(i).get(j)));
                        }
                    }
                    file.println("]");
                    file.close();
                    File f = new File("classes.json");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static void refreshCourse() {
        if (Login.rank.toString().equals("INSTRUCTOR") || Login.rank.toString().equals("TA")) {
            String login = Login.login;
            Connection c = RefreshJSON.connection;

            try {
                PreparedStatement st = connection.prepareStatement("SELECT C.cid, C.cname, C.csection, C.cnum, C.cquarter, C.ctype, U.uname FROM Class C, teaches T, Usr U WHERE U.unetid = ? AND T.uid = U.uid AND C.cid = T.cid ORDER BY cnum ASC, csection ASC");
                st.setString(1, login);
                ResultSet rs = st.executeQuery();
                ArrayList<Class> classes = new ArrayList<Class>();
                ArrayList<ArrayList<Vector>> snames = new ArrayList<ArrayList<Vector>>();

                while (rs.next()) {
                    String cname = rs.getString("cname").trim();
                    String cnum = rs.getString("cnum").trim();
                    String csection = rs.getString("csection").trim();
                    String cquarter = rs.getString("cquarter").trim();
                    String cfaculty = rs.getString("uname").trim();

                    if (rs.getString("ctype").trim().equals("LECTURE")) {
                        Lecture lec = new Lecture(cnum, csection, cname, cquarter, cfaculty);
                        classes.add(lec);
                    } else if (rs.getString("ctype").trim().equals("DISCUSSION")) {
                        Discussion dis = new Discussion(cnum, csection, cname, cquarter, cfaculty);
                        classes.add(dis);
                    } else {
                        Lab lab = new Lab(cnum, csection, cname, cquarter, cfaculty);
                        classes.add(lab);
                    }

                    /* For each class, get the list of the students' name and login, and save it */
                    PreparedStatement students = connection.prepareStatement("SELECT U.uname, U.unetid FROM Usr U, enrolls_in E WHERE E.cid = ? AND E.uid = U.uid ORDER BY unetid ASC");
                    students.setInt(1, rs.getInt("cid"));
                    ResultSet rstudents = students.executeQuery();

                    ArrayList<Vector> tmp = new ArrayList<Vector>();
                    while (rstudents.next()) {
                        Vector v = new Vector(2);
                        v.add(0, rstudents.getString("uname").trim());
                        v.add(1, rstudents.getString("unetid").trim());
                        tmp.add(v);
                    }
                    snames.add(tmp);
                }
                PrintWriter file = new PrintWriter("course.json");
                file.println("[");
                for (int i = 0; i < classes.size(); i++)
                {
                    for (int j = 0; j < snames.get(i).size(); j++)
                    {
                        if (i != classes.size()-1)
                            file.println(classes.get(i).toString(Login.rank, snames.get(i).get(j)) + ",");
                        else
                            file.println(classes.get(i).toString(Login.rank, snames.get(i).get(j)));
                    }
                }
                file.println("]");
                file.close();
                File f = new File("course.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argv)
    {
        connectDB();
        Login.login = "ckent038";
        Login.rank = Rank.INSTRUCTOR;
        refreshClasses();
        refreshUser();
        refreshCourse();
        closeDB();
    }

    public static Connection connection;
}
