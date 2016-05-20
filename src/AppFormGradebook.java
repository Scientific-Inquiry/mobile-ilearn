import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.postgresql.Driver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppFormGradebook extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        int total_points = Integer.parseInt(request.getParameter("points"));
        System.out.println("Total points: " + total_points);
        int aid = Integer.parseInt(request.getParameter("aid"));
        System.out.println("AID: " + aid);
        String login = request.getParameter("slogin");

        /* Register driver (absolutely needed for Tomcat) */
        try
        {
            DriverManager.registerDriver(new Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }
        Connection connection = null;

        /* Connect to the database that is stored on AWS' RDS */
        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass); /* Now connected! */

            /* Update total points */
            PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET apts = ? WHERE aid = ?");
            st.setInt(1, total_points);
            st.setInt(2, aid);
            st.executeUpdate();
            System.out.println("Updated maximum grade for assignment ID" + aid + ": " + total_points);

            st = connection.prepareStatement("SELECT U.uid, U.unetid FROM Usr U, Class C, Assignments A, enrolls_in E WHERE A.aid = ? AND C.cid = A.cid AND E.cid = C.cid AND U.uid = E.uid ORDER BY unetid ASC");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                Integer grade = Integer.parseInt(request.getParameter(rs.getString("unetid").trim()));

                if(grade != null) {
                    System.out.println("Grade for " + rs.getString("unetid").trim() + ": " + grade );
                    PreparedStatement stmp = connection.prepareStatement("UPDATE Grades SET gpts = ? WHERE aid = ? AND uid = ?");
                    stmp.setInt(1, grade);
                    stmp.setInt(2, aid);
                    stmp.setInt(3, rs.getInt("uid"));
                    stmp.executeUpdate();
                }
                else
                    System.out.println("No grade retrieved!");
            }

            rs.close();
            st.close();

            /* Write graded.json */
            st = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Class C WHERE C.cid = A.cid AND A.cid = ?");
            st.setInt(1, aid);
            rs = st.executeQuery();
            rs.next();
            int nbAssign = rs.getInt(1);
            st = connection.prepareStatement("SELECT A.* FROM Assignments A, Class C WHERE C.cid = A.cid AND A.cid = ? ORDER BY A.due ASC");
            st.setInt(1, aid);
            rs = st.executeQuery();

            PrintWriter file = new PrintWriter("graded.json");
            file.println("[");

            int cptAssign = 1;

            while (rs.next())
            {
                int cpt = 1;

                PreparedStatement tmp = connection.prepareStatement("SELECT COUNT(*) FROM Grades G, Usr U WHERE G.aid = ? AND U.uid = G.uid");
                tmp.setInt(1, rs.getInt("aid"));
                ResultSet rtmp = tmp.executeQuery();
                rtmp.next();
                int nb = rtmp.getInt(1);

                tmp = connection.prepareStatement("SELECT G.*, U.unetid FROM Grades G, Usr U WHERE G.aid = ? AND U.uid = G.uid ORDER BY U.unetid");
                tmp.setInt(1, rs.getInt("aid"));
                rtmp = tmp.executeQuery();

                while (rtmp.next())
                {
                    String late;
                    if (rtmp.getBoolean("late") == false)
                        late = "false";
                    else
                        late = "true";

                    if (cpt < nb && cptAssign < nbAssign)
                        file.println("{" + "\"grade\":" + "\"" + rtmp.getInt("gpts") + "\""
                                + ", " + "\"total\":" + "\"" + rs.getInt("apts") + "\"" + ", " + "\"courseNum\":" + "\"" + rs.getString("cnum").trim() + "\""
                                + ", " + "\"courseSec\":" + "\"" + rs.getString("csection").trim() + "\""
                                + ", " + "\"title\":" + "\"" + rs.getString("aname").trim() + "\""
                                + ", "+ "\"slogin\":" + "\"" + rtmp.getString("unetid").trim() + "\""
                                + ", " + "\"late\":" + "\"" + late + "\""
                                + ", " + "\"aid\":" + "\"" + rs.getInt("aid") + "\"" + "},");
                    else
                        file.println("{" + "\"grade\":" + "\"" + rtmp.getInt("gpts") + "\""
                                + ", " + "\"total\":" + "\"" + rs.getInt("apts") + "\"" + ", " + "\"courseNum\":" + "\"" + rs.getString("cnum").trim() + "\""
                                + ", " + "\"courseSec\":" + "\"" + rs.getString("csection").trim() + "\""
                                + ", " + "\"title\":" + "\"" + rs.getString("aname").trim() + "\""
                                + ", "+ "\"slogin\":" + "\"" + rtmp.getString("unetid").trim() + "\""
                                + ", " + "\"late\":" + "\"" + late + "\""
                                + ", " + "\"aid\":" + "\"" + rs.getInt("aid") + "\"" + "}");
                    cpt++;
                }
                cptAssign++;
            }
            file.println("]");
            file.close();
            System.out.println("Reached file.close()!");
            File f = new File("graded.json");
            System.out.println("Wrote graded.json!");

            /* Send graded.json to S3 */
            String pathS3 = "data/users/" + login + "/graded.json";
            String bucketName = "milearn";
            AWSCredentials credentials = new BasicAWSCredentials("AKIAJWYCYKZJ3BZ5XEBA", "NGJuCS16bH3R6ywlJf7m2NSmdTPd0yA0qANIUDkM");

            new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3, f));
            f.delete();

            /* Redirect to the static website */
            String site = new String("http://milearn.s3-website-us-west-2.amazonaws.com/");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", site);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

        finally
        {
            /* Close connection with the database */
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static String grade_Student_Format(int points, int total, String cnum, String csec, String ctitle, boolean late, int aid){

        return "{" + "\"grade\":" + "\"" + points + "\""
                + ", " + "\"total\":" + "\"" + total + "\"" + ", " + "\"courseNum\":" + "\"" + cnum.replace(" ", "") + "\""
                + ", " + "\"courseSec\":" + "\"" + csec + "\""
                + ", " + "\"title\":" + "\"" + ctitle + "\""
                + ", " + "\"late\":" + "\"" + late + "\""
                + ", " + "\"aid\":" + "\"" + aid + "\"" +"}";
    }
    public static String grade_Teacher_Format(int points, int total, String cnum, String csec, String ctitle, boolean late, int aid, String slogin) {
        return "{" + "\"grade\":" + "\"" + points + "\""
                + ", " + "\"total\":" + "\"" + total + "\"" + ", " + "\"courseNum\":" + "\"" + cnum.replace(" ", "") + "\""
                + ", " + "\"courseSec\":" + "\"" + csec + "\""
                + ", " + "\"title\":" + "\"" + ctitle + "\""
                + ", "+ "\"slogin\":" + "\"" + slogin + "\""
                + ", " + "\"late\":" + "\"" + late + "\""
                + ", " + "\"aid\":" + "\"" + aid + "\"" + "}";
    }

    /*public static ArrayList<ArrayList<Vector>> fill_snames(Connection connection, int tid, String netid) {
        ArrayList<ArrayList<Vector>> snames = new ArrayList<ArrayList<Vector>>();
        try{
            PreparedStatement st = connection.prepareStatement("SELECT C.cid, C.cname, C.csection, C.cnum, C.cquarter, C.ctype FROM Class C, teaches T, Usr U WHERE U.uid = ? AND T.uid = U.uid AND C.cid = T.cid ORDER BY cnum ASC, csection ASC");
            st.setInt(1, tid);
            ResultSet rs = st.executeQuery();
            ArrayList<Class> classes = new ArrayList<Class>();

            while (rs.next()) {
                String cname = rs.getString("cname").trim();
                String cnum = rs.getString("cnum").trim();
                String csection = rs.getString("csection").trim();
                String cquarter = rs.getString("cquarter").trim();


                // For each class, get the list of the students' name and login, and save it
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
            return snames;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //return new ArrayList<Class>();
        }
        return snames;
    }

    public static String generate_path(String uid, String filename){
        return "data/users/"+ uid + "/" + filename;
    }

    public static void upload_file(AmazonS3 s3Client, String bucketName, String path, String fileName){
        s3Client.putObject(new PutObjectRequest(bucketName, path,
                new File(fileName)));
    }

    public static void createJSON_File(ArrayList<String> grades){
        try{
            PrintWriter file = new PrintWriter("Grades.json");
            file.println(grades.toString());
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    public static void TM_init_Grades(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        try
        {
            System.out.println("Unetid: " + unetid);

            PreparedStatement st = connection.prepareStatement("SELECT C.cnum FROM Class C, Assignments A WHERE A.aid = ? AND " +
                    "A.cid = C.cid");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();
            rs.next();
            String courseName= rs.getString("cnum");
            System.out.println("CNUM " + courseName);

            st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            rs = st.executeQuery();
            rs.next();
            int tid = rs.getInt("uid");
            System.out.println("uid "+ tid);

            //String cid = rs.getString("aname");
            TM_init_TGrades(connection, aid, unetid);

            //Update and upload Json for Students
            ArrayList<ArrayList<Vector>> snames = fill_snames(connection, tid, unetid);
            //System.out.println("students  "+ snames.get(0));
            int index = get_course_index(connection, unetid, courseName);
            System.out.println("Index is " + index);
            for(int i = 0; i < snames.get(index).size(); i++) {
                TM_init_SGrades(connection, aid,snames.get(index).get(i).get(1).toString());
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

    public static void TM_init_TGrades(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        try
        {
            System.out.println("Unetid: " + unetid);
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");
            System.out.println("UID IS " + uid);

            String path = generate_path(unetid, "graded.json");

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

            ArrayList<String> grades = new ArrayList<>();
            for(i = 0; i < classes.size() ; i++) {
                st.setInt(1, (int)classes.get(i));
                rs = st.executeQuery();
                while(rs.next()){

                    //grade_Teacher_Format(int points, int total, String cnum, String csec, String ctitle, boolean late, int aid, String slogin)
                    grades.add(grade_Teacher_Format(rs.getInt("gpts"), rs.getInt("apts"), rs.getString("cnum"), rs.getString("csection"),
                            rs.getString("aname"), isLate(connection, rs.getString("unetid"), rs.getInt("aid")), rs.getInt("aid"), rs.getString("unetid")));
                    //public Grade(Assignment a, int pts, String login, boolean tardy, boolean instr)
                    //g.addGrade(new Grade(new Assignment(rs.getString("aname"), rs.getString("description"),
                    //        new Date(rs.getTimestamp("due").getTime()), rs.getInt("apts"), rs.getString("cnum"),
                    //        rs.getString("csection"), rs.getInt("aid")), rs.getInt("gpts"), rs.getString("unetid"),
                    //       isLate(rs.getString("unetid"), 20),true));
                    //System.out.println("LOGIN INFO: ");
                    System.out.println("TITLE " + rs.getString("aname"));
                }
            }

            System.out.println("JSON GRADES" + grades);
            createJSON_File(grades);
            //AmazonS3 s3Client, String bucketName, String path,String fileName
            //upload_file(s3.s3Client, s3.bucketName, path, "Grades.json");
            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public static void TM_init_SGrades(Connection connection, int aid, String unetid){
        ArrayList classes = new ArrayList();
        try
        {
            System.out.println("Unetid: " + unetid);
        *//*
        PreparedStatement st = s3.connection.prepareStatement("SELECT A.aname, A.description, A.due, A.apts " +
                "FROM Assignments A, Usr U, enrolls_in E WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid");
        *//*
            PreparedStatement st = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
            st.setString(1, unetid);
            ResultSet rs = st.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");
            System.out.println("UID IS " + uid);

            String path = generate_path(unetid, "grade.json");
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
                    "A.description, A.due, C.cnum, C.csection FROM Usr U, Grades G, Assignments A, Class C " +
                    "WHERE A.cid = ? AND U.uid = ? AND A.aid = G.aid AND U.uid = G.uid AND G.aid = A.aid");

            ArrayList<String> grades = new ArrayList();
            for(i = 0; i < classes.size() ; i++) {
                st.setInt(1, (int)classes.get(i));
                st.setInt(2, uid);
                rs = st.executeQuery();
                if(rs.next()){
                    //grade_Student_Format(int points, int total, String cnum, String csec, String ctitle, boolean late, int aid)
                    grades.add(grade_Student_Format(rs.getInt("gpts"), rs.getInt("apts"), rs.getString("cnum"), rs.getString("csection"),
                            rs.getString("aname"), isLate(connection, rs.getString("unetid"), rs.getInt("aid")), rs.getInt("aid")));
                    //System.out.println("TITLE " + rs.getString("aname"));
                }
            }

            System.out.println("GRADES ARE\n" + grades);
            createJSON_File(grades);
            //s3.upload_file("Grades.json");
            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }
*/
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

  /*  public static int get_course_index(Connection connection, String netid, String courseName){
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

    }*/

}