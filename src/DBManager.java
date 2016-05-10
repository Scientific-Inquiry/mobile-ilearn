import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class DBManager {

    /* Add a new user to the Usr table */
    public static void addUser(Connection connection, int id, String netid, String name, String password, String mail, String rank)
    {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Usr (uid, unetid, uname, upassword, umail, urank) VALUES (?, ?, ?, ?, ?, ?);");
            st.setInt(1, id);
            st.setString(2, netid);
            st.setString(3, name);
            st.setString(4, password);
            st.setString(5, mail);
            st.setString(6, rank);
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a user from the Usr table given the user's NetID */
    public static void deleteUser(Connection connection, String unetid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM Usr WHERE unetid = ?");
            st.setString(1, unetid);
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add a new lecture to the Class table */
    public static void addLecture(Connection connection, String number, String section, String name, String quarter)
    {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Class (cname, csection, cnum, cquarter, ctype) VALUES (?, ?, ?, ?, ?);");
            st.setString(1, name);
            st.setString(2, section);
            st.setString(3, number);
            st.setString(4, quarter);
            st.setString(5, "LECTURE");
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a lecture from the Class table */
    public static void deleteLecture(Connection connection, String number, String quarter)
    {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM Class WHERE cnum = ? AND cquarter = ? AND ctype = ?");
            st.setString(1, number);
            st.setString(2, quarter);
            st.setString(3, "LECTURE");
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add a new discussion to the Class table */
    public static void addDiscussion(Connection connection, String number, String section, String name, String quarter)
    {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Class (cname, csection, cnum, cquarter, ctype) VALUES (?, ?, ?, ?, ?);");
            st.setString(1, name);
            st.setString(2, section);
            st.setString(3, number);
            st.setString(4, quarter);
            st.setString(5, "DISCUSSION");
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a discussion from the Class table */
    public static void deleteLecture(Connection connection, String number, String section, String quarter)
    {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM Class WHERE cnum = ? AND cquarter = ? AND csection = ? AND ctype = ?");
            st.setString(1, number);
            st.setString(2, quarter);
            st.setString(3, section);
            st.setString(4, "DISCUSSION");
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add a new lab to the Class table */
    public static void addLab(Connection connection, String number, String section, String name, String quarter)
    {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Class (cname, csection, cnum, cquarter, ctype) VALUES (?, ?, ?, ?, ?);");
            st.setString(1, name);
            st.setString(2, section);
            st.setString(3, number);
            st.setString(4, quarter);
            st.setString(5, "LAB");
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a lab from the Class table */
    public static void deleteLab(Connection connection, String number, String section, String quarter)
    {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM Class WHERE cnum = ? AND cquarter = ? AND csection = ? AND ctype = ?");
            st.setString(1, number);
            st.setString(2, quarter);
            st.setString(3, section);
            st.setString(4, "LAB");
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }


    /* Add a teacher teaching a class to the teaches table */
    public static void addTeacher(Connection connection, int classid, String netid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();

            rs.next();
            int id = rs.getInt(1);

            st = connection.prepareStatement("INSERT INTO teaches(uid, cid) VALUES (?, ?)");
            st.setInt(1, id);
            st.setInt(2, classid);
            st.executeUpdate();

            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a teacher teaching a class from the teaches table */
    public static void deleteTeacher(Connection connection, int classid, String netid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();

            rs.next();
            int id = rs.getInt(1);

            st = connection.prepareStatement("DELETE FROM teaches WHERE cid = ? AND uid = ?");
            st.setInt(1, classid);
            st.setInt(2, id);
            st.executeUpdate();

            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add a student to a class to the enrolls_in table */
    public static void enrollClass(Connection connection, int classid, String netid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();

            rs.next();
            int id = rs.getInt(1);

            st = connection.prepareStatement("INSERT INTO enrolls_in(uid, cid) VALUES (?, ?)");
            st.setInt(1, id);
            st.setInt(2, classid);
            st.executeUpdate();

            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Make a student drop a class in the enrolls_in table */
    public static void dropClass(Connection connection, int classid, String netid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();

            rs.next();
            int id = rs.getInt(1);

            st = connection.prepareStatement("DELETE FROM enrolls_in WHERE cid = ? AND uid = ?");
            st.setInt(1, classid);
            st.setInt(2, id);
            st.executeUpdate();

            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add an assignment to the Assignments table */
    /* Format for timestamp: new Timestamp(int year, int month, int day, int hour, int minute, int second, int nanosecond) */
    /* Note: for year, do year - 1900. Ex: for 2016, the given parameter must be 116 */
    public static void addAssignment(Connection connection, int classid, String name, String description, Timestamp due, int points)
    {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Assignments (cid, aname, description, due, apts) VALUES (?, ?, ?, ?, ?);");
            st.setInt(1, classid);
            st.setString(2, name);
            st.setString(3, description);
            st.setTimestamp(4, due);
            st.setInt(5, points);
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete an assignment given its id from the Assignments table */
    public static void deleteAssignment(Connection connection, int aid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM Assignments WHERE aid = ?");
            st.setInt(1, aid);
            st.executeUpdate();

            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add a grade for a given assignment and a given student */
    public static void addGrade(Connection connection, int assignmentid, String netid, int points)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();

            int id = rs.getInt("uid");

            st = connection.prepareStatement("INSERT INTO Grades (aid, uid, gpts) VALUES (?, ?, ?);");
            st.setInt(1, assignmentid);
            st.setInt(2, id);
            st.setInt(3, points);
            st.executeUpdate();

            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a grade given an assignment and a student */
    public static void deleteAssignment(Connection connection, int assignmentid, String netid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();

            int id = rs.getInt("uid");

            st = connection.prepareStatement("DELETE FROM Grades WHERE aid = ? AND uid = ?");
            st.setInt(1, assignmentid);
            st.setInt(2, id);
            st.executeUpdate();

            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Print all the rows of a table with all their attributes */
    public static void displayTable(Connection connection, String table)
    {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            String[] attributes = new String[count];

            for (int i = 1; i <= count; i++) {
                attributes[i - 1] = metaData.getColumnName(i);
            }

            while (rs.next()) {
                for (int i = 0; i < count; i++) {
                    System.out.println(attributes[i] + "\t " + rs.getString(i + 1));
                }
                System.out.println("__________");
            }

            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    /* Return the number of rows in a table */
    public static int countRows(Connection connection, String table)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM " + table);
            ResultSet rs = st.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            st.close();
            return count;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    /* List students enrolled in a class */
    public static void listStudents(Connection connection, String number, String section, String quarter)
    {
        try
        {
            PreparedStatement st = connection.prepareStatement("SELECT U.uid, U.unetid, U.uname FROM Usr U, enrolls_in E, Class C WHERE C.cnum = ? AND C.csection = ? AND C.cquarter = ? AND E.cid = C.cid AND U.uid = E.uid");
            st.setString(1, number);
            st.setString(2, section);
            st.setString(3, quarter);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                System.out.println(rs.getString("uname") + rs.getString("unetid") + " (" + rs.getInt("uid") + ")");
            }

            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    /* List the assignments of a given class */
    public static void listAssignments(Connection connection, String number, String section, String quarter)
    {
        try
        {
            PreparedStatement st = connection.prepareStatement("SELECT A.aname, A.description, A.due, A.apts FROM Assignments A, Class C WHERE C.cnum = ? AND C.csection = ? AND C.cquarter = ? AND C.cid = A.cid");
            st.setString(1, number);
            st.setString(2, section);
            st.setString(3, quarter);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                System.out.println(rs.getString("aname") + " (due date: " + rs.getTimestamp("due") + ") worth " + rs.getString("apts") + " points\n" + rs.getString("description"));
            }

            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    /* List the grades of all the students that have been for a given assignment */
    public static void listGrades(Connection connection, int aid)
    {
        try
        {
            PreparedStatement st = connection.prepareStatement("SELECT U.uname, U.unetid, G.gpts, A.apts FROM Usr U, Grades G, Assignments A WHERE A.aid = ? AND U.uid = G.uid AND G.aid = A.aid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                System.out.println(rs.getString("uname") + " (" + rs.getString("unetid").trim() + "): \t" + rs.getInt("gpts") + "/" + rs.getInt("apts"));
            }

            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] argv){
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
            connection = DriverManager.getConnection(dbURL, user, pass);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
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

}
