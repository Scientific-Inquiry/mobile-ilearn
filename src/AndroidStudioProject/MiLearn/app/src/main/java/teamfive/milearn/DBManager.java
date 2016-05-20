package teamfive.milearn;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DBManager {

    private static S3Manager s3;
    DBManager(){
        s3 = new S3Manager();
    }
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

    public static int get_course_index(Connection connection, String netid, String courseName){
        ArrayList cids = new ArrayList();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");
            System.out.println("UID IS " + uid);

            st = connection.prepareStatement("SELECT T.cid FROM teaches T WHERE T.uid = ?");
            st.setInt(1, uid);
            rs = st.executeQuery();

            int j = 0;
            while (rs.next())
            {
                cids.add(rs.getInt("cid"));
                //System.out.println("CID IS "+ cids.get(j));
                j++;
            }

            System.out.println("cids are " + cids);
            st = connection.prepareStatement("SELECT C.cnum FROM Class C WHERE C.cid = ?");
            ArrayList<String> course = new ArrayList<String>();
            for(int i = 0; i < cids.size() ; i++) {
                st.setInt(1, (int)cids.get(i));
                System.out.println("CID IS "+ cids.get(i));
                rs = st.executeQuery();
                while(rs.next()){
                    course.add(rs.getString("cnum"));
                    System.out.println("RS LOOP:  " + rs.getString("cnum"));
                    //System.out.println(rs.getString("aname") + rs.getString("description") + rs.getInt("apts") + rs.getTimestamp("due"));
                }
            }

            Collections.sort(course);
            System.out.println("course: " + course);
            System.out.println("course list: " + courseName);
            System.out.println("CONTAINS : " + course.contains(courseName));
            int index = course.indexOf(courseName);
            System.out.println("INDEX IS " + index);

            st.close();
            rs.close();
            return index;
        }
        catch (SQLException e) {
            System.out.println("EXCEPTION");
            return -1;
        }

    }
    //--------------------ASSIGNMENTS--------------------------------------------------------

    /* Add an assignment to the Assignments table */
    /* Format for timestamp: new Timestamp(int year, int month, int day, int hour, int minute, int second, int nanosecond) */
    /* Note: for year, do year - 1900. Ex: for 2016, the given parameter must be 116 */

    //finds and returns aid of assignment with title and course number
    public int find_aid(Connection connection, String title, String cnum) {
        int aid = 0;
        try {
            PreparedStatement st = connection.prepareStatement("SELECT A.aid, A.aname, C.cnum" +
                    " FROM Assignments A, Class C WHERE A.aname = ? AND C.cnum = ?");
            st.setString(1, title);
            st.setString(2, cnum);
            ResultSet rs = st.executeQuery();
            rs.next();
            aid = Integer.parseInt(rs.getString(1));
            rs.close();
            st.close();
            return aid;
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return -1;
        }
    }

    //adds assignment to database
    public static void addAssignment(Connection connection, int classid, String name, String description, Date due, int points)
    {
        Timestamp timestamp = new java.sql.Timestamp(due.getTime());

        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Assignments (cid, aname, description, due, apts) VALUES (?, ?, ?, ?, ?);");
            st.setInt(1, classid);
            st.setString(2, name);
            st.setString(3, description);
            st.setTimestamp(4, timestamp);
            st.setInt(5, points);
            st.executeUpdate();

            st = connection.prepareStatement("SELECT cname FROM Class where cid = ? ");
            st.setInt(1, classid);
            ResultSet rs = st.executeQuery();
            rs.next();
            String courseName = rs.getString("cname");

            //Update and upload Json for Instructor first
            init_TAssignments(connection, Login.login);
            //Update and upload Json for Students
            for(int i = 0; i < Login.snames.size(); i++)
                init_SAssignments(connection, Login.snames.get(get_course_index(connection, Login.login, courseName)).get(i).get(1).toString());

            s3.generate_Path(Login.login, "assigner.json");
            System.out.println("username: " + Login.login);
            //s3.upload_file("Assignment");
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

    }

    //updates assignment name
    public static void update_Assignment_Name(Connection connection, int aid, String name)
    {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET aname = ? WHERE AID = ?");
            st.setString(1, name);
            st.setInt(2, aid);
            st.executeUpdate();

            st = connection.prepareStatement("SELECT C.cnum FROM Class C, Assignments A WHERE A.aid = ? AND " +
                    "A.cid = C.cid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();
            rs.next();

            String courseName= rs.getString("cnum");
            //String cid = rs.getString("aname");
            init_Assignments(connection, aid, Login.login);
            st.close();
            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        //Update and upload Json for Instructor first

    }

    //updates assignment description
    public static void update_Assignment_Description(Connection connection, int aid, String desc)
    {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET description = ? WHERE AID = ?");
            st.setString(1, desc);
            st.setInt(2, aid);
            st.executeUpdate();


            st = connection.prepareStatement("SELECT C.cnum FROM Class C, Assignments A WHERE A.aid = ? AND " +
                    "A.cid = C.cid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();
            rs.next();

            String courseName= rs.getString("cnum");
            //Update and upload Json for Instructor first
            init_Assignments(connection, aid, Login.login);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

    }

    //updates assignment points
    public static void update_Assignment_Points(Connection connection, int aid, int pts)
    {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET apts = ? WHERE AID = ?");
            st.setInt(1, pts);
            st.setInt(2, aid);
            st.executeUpdate();

            init_Assignments(connection, aid, Login.login);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        //Update and upload Json for Instructor first
        init_SAssignments(connection, Login.login);
        //Update and upload Json for Students
        //stub until student list from course.json format is finalized

    }

    //updates assignment due date
    public static void update_Assignment_Due(Connection connection, int aid, Date due)
    {
        Timestamp timestamp = new java.sql.Timestamp(due.getTime());
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET apts = ? WHERE AID = ?");
            st.setTimestamp(1, timestamp);
            st.setInt(2, aid);
            st.executeUpdate();

            init_Assignments(connection, aid, Login.login);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        //Update and upload Json for Instructor first
        init_SAssignments(connection, Login.login);
        //Update and upload Json for Students
        //stub until student list from course.json format is finalized

    }

    /* Delete an assignment given its id from the Assignments table */
    public static void deleteAssignment(Connection connection, int aid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM Assignments WHERE aid = ?");
            st.setInt(1, aid);
            st.executeUpdate();

            init_Assignments(connection, aid, Login.login);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Add a grade for a given assignment and a given student */
    public static void addGrade(Connection connection, int aid, String netid, String gnetid, int points)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, gnetid);
            ResultSet rs = st.executeQuery();
            rs.next();

            int id = rs.getInt("uid");

            st = connection.prepareStatement("INSERT INTO Grades (aid, uid, gpts) VALUES (?, ?, ?);");
            st.setInt(1, aid);
            st.setInt(2, id);
            st.setInt(3, points);
            st.executeUpdate();
            init_Grades(connection, aid, Login.login);
            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

    }

    public static void update_Grade(Connection connection, int aid, String netid, int points)
    {
        PreparedStatement st;
        try {
            st = connection.prepareStatement("UPDATE Grades g SET gpts = ? WHERE aid = ? AND aid = G.aid");
            st.setInt(1, points);
            st.setInt(2, aid);
            st.executeUpdate();
            init_Grades(connection, aid, Login.login);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /* Delete a grade given an assignment and a student */
    public static void deleteAssignment(Connection connection, int aid, String netid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();

            int id = rs.getInt("uid");

            st = connection.prepareStatement("DELETE FROM Grades WHERE aid = ? AND uid = ?");
            st.setInt(1, aid);
            st.setInt(2, id);
            st.executeUpdate();

            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        s3.generate_Path(Login.login, "assigner.json");
        s3.upload_file("Assignments.json");

        //now do students
        Vector<String> sid = new Vector<String>();
        for(int i = 0; i < sid.size(); i++) {
            s3.generate_Path(sid.elementAt(i), "assign.json");
            s3.upload_file("Assignments.json");
        }
    }

    //deletes grade of student
    public static void deleteGrade(Connection connection, int aid, String netid, String gnetid)
    {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();

            int id = rs.getInt("uid");
            st = connection.prepareStatement("DELETE FROM Grades WHERE aid = ? AND uid = ?");
            st.setInt(1, aid);
            st.setInt(2, id);
            st.executeUpdate();
            init_Grades(connection, aid, Login.login);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        /*
        s3.generate_Path(Login.login, "assigner.json");
        s3.upload_file("Assignments.json");

        //now do students
        Vector<String> sid = new Vector<String>();
        for(int i = 0; i < sid.size(); i++) {
            s3.generate_Path(sid.elementAt(i), "assign.json");
            s3.upload_file("Assignments.json");
        }
        */
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

    /* Initializes Assignments data structure with assignments for all users*/
    public static void init_Assignments(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        Assignments a = new Assignments();
        try
        {
            PreparedStatement st = connection.prepareStatement("SELECT C.cnum FROM Class C, Assignments A WHERE A.aid = ? AND " +
                    "A.cid = C.cid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();
            rs.next();

            String courseName= rs.getString("cnum");
            //String cid = rs.getString("aname");
            init_TAssignments(connection, Login.login);

            //Update and upload Json for Students
            System.out.println("students  "+ Login.snames.get(0));
            int index = get_course_index(connection, Login.login, courseName);
            for(int i = 0; i < Login.snames.get(index).size(); i++) {
                init_SAssignments(connection, Login.snames.get(index).get(i).get(1).toString());
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

    /* Initializes Assignments data structure with assignments for Teacher users*/
    public static void init_TAssignments(Connection connection, String unetid){
        ArrayList classes = new ArrayList();
        Assignments a = new Assignments();
        try
        {
            System.out.println("Unetid: " + unetid);
            /*
            PreparedStatement st = connection.prepareStatement("SELECT A.aname, A.description, A.due, A.apts " +
                    "FROM Assignments A, Usr U, enrolls_in E WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid");
            */
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");

            s3.generate_Path(unetid, "assigner.json");
            st = connection.prepareStatement("SELECT T.cid FROM teaches T WHERE T.uid = ?");
            st.setInt(1, uid);
            rs = st.executeQuery();

            int i = 0;
            while (rs.next())
            {
                classes.add(rs.getInt("cid"));
                System.out.println("CID IS "+ classes.get(i));
                i++;
            }
            System.out.print("INIT: CIDS are "+ classes);

            st = connection.prepareStatement("SELECT * FROM Assignments A, Class C WHERE C.cid = ? AND C.cid = A.cid");
            for(i = 0; i < classes.size() ; i++) {
                st.setInt(1, (int)classes.get(i));
                rs = st.executeQuery();
                while(rs.next()){
                    a.addAssign(new Assignment(rs.getString("aname"), rs.getString("description"), new Date(rs.getTimestamp("due").getTime()),
                            rs.getInt("apts"), rs.getString("cnum"), rs.getString("csection"), rs.getInt("aid")));
                    //System.out.println(rs.getString("aname") + rs.getString("description") + rs.getInt("apts") + rs.getTimestamp("due"));
                }
            }
            System.out.println(a);
            a.createJSON_File();
            s3.upload_file("Assignments.json");
            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    /* Initializes Assignments data structure with assignments for Student users*/
    public static void init_SAssignments(Connection connection, String unetid){
        ArrayList classes = new ArrayList();
        Assignments a = new Assignments();
        try
        {
            System.out.println("Unetid: " + unetid);
            /*
            PreparedStatement st = connection.prepareStatement("SELECT A.aname, A.description, A.due, A.apts " +
                    "FROM Assignments A, Usr U, enrolls_in E WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid");
            */
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");

            s3.generate_Path(unetid, "assign.json");
            st = connection.prepareStatement("SELECT E.cid FROM enrolls_in E WHERE E.uid = ?");
            st.setInt(1, uid);
            rs = st.executeQuery();

            int i = 0;
            while (rs.next())
            {
                classes.add(rs.getInt("cid"));
                System.out.println("CID IS "+ classes.get(i));
                i++;
            }
            System.out.print("INIT: CIDS are "+ classes);

            st = connection.prepareStatement("SELECT * FROM Assignments A, Class C WHERE C.cid = ? AND C.cid = A.cid");
            for(i = 0; i < classes.size() ; i++) {
                st.setInt(1, (int)classes.get(i));
                rs = st.executeQuery();
                while(rs.next()){
                    a.addAssign(new Assignment(rs.getString("aname"), rs.getString("description"), new Date(rs.getTimestamp("due").getTime()),
                            rs.getInt("apts"), rs.getString("cnum"), rs.getString("csection"), rs.getInt("aid")));
                    //System.out.println(rs.getString("aname") + rs.getString("description") + rs.getInt("apts") + rs.getTimestamp("due"));
                }
            }
            System.out.println(a);
            a.createJSON_File();
            s3.upload_file("Assignments.json");
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
            PreparedStatement st = connection.prepareStatement("SELECT U.uname, U.unetid, U.uid, G.aid ,G.gpts, A.apts," +
                    " A.cid FROM Usr U, Grades G, Assignments A WHERE A.aid = ? AND U.uid = G.uid AND G.aid = A.aid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                System.out.println("GID: " + rs.getInt("aid") + " CID: " +  rs.getInt("cid")+ " " + rs.getString("uname") + " (" +
                        rs.getString("unetid").trim() + " " + rs.getString("uid")+ ") : \t" + rs.getInt("gpts") + "/" + rs.getInt("apts"));
                System.out.println("---------------------------");
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

    /* Initializes Assignments data structure with grades for all users*/
    public static void init_Grades(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        Grades g = new Grades();
        try
        {
            System.out.println("Unetid: " + unetid);
            PreparedStatement st = connection.prepareStatement("SELECT C.cnum FROM Class C, Assignments A WHERE A.aid = ? AND " +
                    "A.cid = C.cid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();
            rs.next();

            String courseName= rs.getString("cnum");
            //String cid = rs.getString("aname");
            init_TGrades(connection, aid, Login.login);

            //Update and upload Json for Students
            System.out.println("students  "+ Login.snames.get(0));
            int index = get_course_index(connection, Login.login, courseName);
            for(int i = 0; i < Login.snames.get(index).size(); i++) {
                init_SGrades(connection, aid,Login.snames.get(index).get(i).get(1).toString());
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

    public static void init_TGrades(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        Grades g = new Grades();
        try
        {
            System.out.println("Unetid: " + unetid);
            /*
            PreparedStatement st = connection.prepareStatement("SELECT A.aname, A.description, A.due, A.apts " +
                    "FROM Assignments A, Usr U, enrolls_in E WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid");
            */
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");
            System.out.println("UID IS " + uid);

            s3.generate_Path(unetid, "graded.json");
            st = connection.prepareStatement("SELECT T.cid FROM teaches T WHERE T.uid = ?");
            st.setInt(1, uid);
            rs = st.executeQuery();

            int i = 0;
            while (rs.next())
            {
                classes.add(rs.getInt("cid"));
                System.out.println("CID IS "+ classes.get(i));
                i++;
            }
            st = connection.prepareStatement("SELECT U.uname, U.unetid, U.uid, G.aid, G.gpts, A.aname, A.apts, A.cid, " +
                    "A.description, A.due, C.cnum, C.csection FROM Usr U, " + "Grades G, Assignments A, Class C " +
                    "WHERE A.cid = ? AND A.cid = C.cid AND U.uid = G.uid AND G.aid = A.aid");
            for(i = 0; i < classes.size() ; i++) {
                st.setInt(1, (int)classes.get(i));
                rs = st.executeQuery();
                while(rs.next()){
                    //public Grade(Assignment a, int pts, String login, boolean tardy, boolean instr)
                    g.addGrade(new Grade(new Assignment(rs.getString("aname"), rs.getString("description"),
                            new Date(rs.getTimestamp("due").getTime()), rs.getInt("apts"), rs.getString("cnum"),
                            rs.getString("csection"), rs.getInt("aid")), rs.getInt("gpts"), rs.getString("unetid"),
                            isLate(connection, rs.getString("unetid"), 20),true));
                    System.out.println("LOGIN INFO: " + g);
                }
            }

            System.out.println(g);
            g.createJSON_File();
            s3.upload_file("Grades.json");
            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public static void init_SGrades(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        Grades g = new Grades();
        try
        {
            System.out.println("Unetid: " + unetid);
            /*
            PreparedStatement st = connection.prepareStatement("SELECT A.aname, A.description, A.due, A.apts " +
                    "FROM Assignments A, Usr U, enrolls_in E WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid");
            */
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");
            System.out.println("UID IS " + uid);

            s3.generate_Path(unetid, "grade.json");
            st = connection.prepareStatement("SELECT E.cid FROM enrolls_in E WHERE E.uid = ?");
            st.setInt(1, uid);
            rs = st.executeQuery();

            int i = 0;
            while (rs.next())
            {
                classes.add(rs.getInt("cid"));
                System.out.println("CID IS "+ classes.get(i));
                i++;
            }
            st = connection.prepareStatement("SELECT U.uname, U.unetid, U.uid, G.aid, G.gpts, A.aname, A.apts, A.cid, " +
                    "A.description, A.due, C.cnum, C.csection FROM Usr U, " + "Grades G, Assignments A, Class C " +
                    "WHERE A.cid = ? AND A.cid = C.cid AND U.uid = G.uid AND G.aid = A.aid");
            for(i = 0; i < classes.size() ; i++) {
                st.setInt(1, (int)classes.get(i));
                rs = st.executeQuery();
                while(rs.next()){
                    //public Grade(Assignment a, int pts, String login, boolean tardy, boolean instr)
                    g.addGrade(new Grade(new Assignment(rs.getString("aname"), rs.getString("description"),
                            new Date(rs.getTimestamp("due").getTime()), rs.getInt("apts"), rs.getString("cnum"),
                            rs.getString("csection"), rs.getInt("aid")), rs.getInt("gpts"), unetid,isLate(connection, unetid, aid), false));
                    System.out.println("aid is " + rs.getInt("aid"));
                }
            }

            System.out.println("SHOULD HAVE TRUE\n" + g);
            g.createJSON_File();
            s3.upload_file("Grades.json");
            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public void add_Submission(Connection connection, String netid, String cname ,int aid, File filename){
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int id = rs.getInt("uid");

            st = connection.prepareStatement("INSERT INTO Submissions (aid, uid, stime, attempts) VALUES (?, ?, ?, ?);");
            st.setInt(1, aid);
            st.setInt(2, id);
            Date date = new Date();
            st.setTimestamp(3, new java.sql.Timestamp(date.getTime()));
            st.setInt(4, 1);
            st.executeUpdate();

            //need to include student name with graded
            File temp = null;

            try{
                temp = File.createTempFile(aid + "_" + id + "_1" , s3.get_extension(filename.getName()));
            }catch(Exception e){
                System.out.println("ERROR");
            }
            s3.generate_Path(cname, "qyyyy" , temp.getName());
            s3.upload_file(temp.getPath());

            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void update_Submission(Connection connection, String netid, String cname_sect ,int aid, File filename){
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int id = rs.getInt("uid");

            st = connection.prepareStatement("SELECT attempts FROM Submissions where aid = ? AND uid = ? ");
            st.setInt(1, aid);
            st.setInt(2, id);
            rs = st.executeQuery();
            rs.next();
            int attempts = rs.getInt("attempts");

            st = connection.prepareStatement("UPDATE Submissions SET stime = ?, attempts = ? where aid = ? AND uid = ? ");
            Date date = new Date();
            st.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            st.setInt(2, ++attempts);
            st.setInt(3, aid);
            st.setInt(4, id);

            st.executeUpdate();
            rs.close();
            st.close();
            File temp = null;
            //File tempFile = File.createTempFile(filename,aid + "_" + id);

            try{
                temp = File.createTempFile(aid + "_" + id + "_" + attempts, s3.get_extension(filename.getName()));
            }catch(Exception e){
                System.out.println("ERROR");
            }
            //System.out.println("NAME OF FILE: " + temp.getName());
            //System.out.println("PATH OF FILE: "+ temp.getAbsolutePath());
            s3.copy_file(filename, temp);

            s3.generate_Path(cname_sect, "qyyyy" , aid + "_" + id + "_" + attempts);
            System.out.println("PATH ****" + s3.path);
            try {
                s3.upload_file(temp.getAbsolutePath());
            }catch(Exception E){
                System.out.println("UPLOADING FAILED!");
            }
            temp.delete();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    //returns true if late
    public static boolean isLate(Connection connection, String netid, int aid){
        try {
            PreparedStatement st = connection.prepareStatement("SELECT uid FROM Usr WHERE unetid = ?");
            st.setString(1, netid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int id = rs.getInt("uid");
            System.out.println("UID: " + id);

            st = connection.prepareStatement("SELECT stime FROM Submissions where aid = ? AND uid = ? ");
            st.setInt(1, aid);
            st.setInt(2, id);
            rs = st.executeQuery();
            rs.next();
            Date submitted = new Date(rs.getTimestamp("stime").getTime());
            System.out.println("SUBMITTED TIME: " + submitted);

            st = connection.prepareStatement("SELECT A.due FROM Assignments A WHERE A.aid = ? ");
            st.setInt(1, aid);
            System.out.println("AID IS : " + aid);
            rs = st.executeQuery();
            rs.next();
            Date due = new Date(rs.getTimestamp("due").getTime());

            System.out.println("ASSIGN TIMESTAMP: " + rs.getTimestamp("due"));
            System.out.println("ASSIGN DUE: " + due);

            if(submitted.after(due)){
                return true;
            }
        }catch(Exception e){
            System.out.println("Late query failed!");
            e.printStackTrace();
            return true;//means there was no subnission
        }
        return false;
    }

    public void managing_ADatabase() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connected to the database!");

            /* Do your cool stuff in this try block */
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        try {
            Statement st = connection.createStatement();
            //String sql = "DROP TABLE Assignments";

            String sql = "CREATE TABLE Assignments (" +
                    "aid SERIAL UNIQUE not NULL, " +
                    "cid INT not NULL, " +
                    "aname VARCHAR(50) not NULL, " +
                    "description VARCHAR(150), " +
                    "due TIMESTAMP not NULL, " +
                    "apts INT not NULL, " +
                    "PRIMARY KEY(aid))";


            st.executeUpdate(sql);
            System.out.println("Created Assignments Database");
        }catch(Exception e)
        {
            System.out.println("Creating Assignments failed!");
        }
        finally
        {
            try{
                connection.close();
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void managing_SDatabase() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connected to the database!");

            /* Do your cool stuff in this try block */
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        try {
            Statement st = connection.createStatement();
            //String sql = "DROP TABLE Submissions";

            String sql = "CREATE TABLE Submissions (" +
                    "aid INT not NULL, " +
                    "uid INT not NULL, " +
                    "stime TIMESTAMP, " +
                    "attempts INT, " +
                    "PRIMARY KEY(aid))";

            st.executeUpdate(sql);
            System.out.println("Created Submissions Database");
        }catch(Exception e)
        {
            System.out.println("Creating Submissions failed!");
        }
        finally
        {
            try{
                connection.close();
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void managing_GDatabase() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connected to the database!");

            /* Do your cool stuff in this try block */
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        try {
            Statement st = connection.createStatement();
            //String sql = "DROP TABLE Grades";

            String sql = "CREATE TABLE Grades (" +
                    "aid INT not NULL, " +
                    "uid INT not NULL, " +
                    "gpts INT, " +
                    "late BOOLEAN,  " +
                    "PRIMARY KEY(aid, uid))";

            st.executeUpdate(sql);
            System.out.println("Created Grades Database");
        }catch(Exception e)
        {
            System.out.println("Creating Grades failed!");
        }
        finally
        {
            try{
                connection.close();
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
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