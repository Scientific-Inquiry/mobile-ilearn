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
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
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

            while (rs.next()) {
                Integer grade = Integer.parseInt(request.getParameter(rs.getString("unetid").trim()));

                if (grade != null) {
                    System.out.println("Grade for " + rs.getString("unetid").trim() + ": " + grade);
                    PreparedStatement stmp = connection.prepareStatement("UPDATE Grades SET gpts = ? WHERE aid = ? AND uid = ?");
                    stmp.setInt(1, grade);
                    stmp.setInt(2, aid);
                    stmp.setInt(3, rs.getInt("uid"));
                    stmp.executeUpdate();
                } else
                    System.out.println("No grade retrieved!");
            }

            rs.close();
            st.close();

            /* Write graded.json */
            st = connection.prepareStatement("SELECT C.cid FROM Class C, Assignments A WHERE A.aid = ? AND C.cid = A.cid");
            st.setInt(1, aid);
            rs = st.executeQuery();
            rs.next();
            int idClass = rs.getInt(1);

            st = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid");
            st.setInt(1, idClass);
            rs = st.executeQuery();
            rs.next();
            int nbAssign = rs.getInt(1);
            st = connection.prepareStatement("SELECT A.*, C.* FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid ORDER BY A.due ASC");
            st.setInt(1, idClass);
            rs = st.executeQuery();

            PrintWriter file = new PrintWriter("graded.json");
            file.println("[");

            int cptAssign = 1;

            while (rs.next()) {
                int cpt = 1;

                PreparedStatement tmp = connection.prepareStatement("SELECT COUNT(*) FROM Grades G, Usr U WHERE G.aid = ? AND U.uid = G.uid");
                tmp.setInt(1, rs.getInt("aid"));
                ResultSet rtmp = tmp.executeQuery();
                rtmp.next();
                int nb = rtmp.getInt(1);

                tmp = connection.prepareStatement("SELECT G.*, U.unetid FROM Grades G, Usr U WHERE G.aid = ? AND U.uid = G.uid ORDER BY U.unetid ASC");
                tmp.setInt(1, rs.getInt("aid"));
                rtmp = tmp.executeQuery();

                while (rtmp.next()) {
                    String late;
                    if (rtmp.getBoolean("late") == false)
                        late = "false";
                    else
                        late = "true";

                    if (cpt < nb || cptAssign < nbAssign - 1)
                        file.println("{" + "\"grade\":" + "\"" + rtmp.getInt("gpts") + "\""
                                + ", " + "\"total\":" + "\"" + rs.getInt("apts") + "\"" + ", " + "\"courseNum\":" + "\"" + rs.getString("cnum").trim() + "\""
                                + ", " + "\"courseSec\":" + "\"" + rs.getString("csection").trim() + "\""
                                + ", " + "\"title\":" + "\"" + rs.getString("aname").trim() + "\""
                                + ", " + "\"slogin\":" + "\"" + rtmp.getString("unetid").trim() + "\""
                                + ", " + "\"late\":" + "\"" + late + "\""
                                + ", " + "\"aid\":" + "\"" + rs.getInt("aid") + "\"" + "},");
                    else
                        file.println("{" + "\"grade\":" + "\"" + rtmp.getInt("gpts") + "\""
                                + ", " + "\"total\":" + "\"" + rs.getInt("apts") + "\"" + ", " + "\"courseNum\":" + "\"" + rs.getString("cnum").trim() + "\""
                                + ", " + "\"courseSec\":" + "\"" + rs.getString("csection").trim() + "\""
                                + ", " + "\"title\":" + "\"" + rs.getString("aname").trim() + "\""
                                + ", " + "\"slogin\":" + "\"" + rtmp.getString("unetid").trim() + "\""
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


            /* Write grade.json */
            // Get all the students that are in at least one of the class of the teacher and make an array of it
            // For each student, update grade.json
            ArrayList<String> studentNames = new ArrayList<String>();
            st = connection.prepareStatement("SELECT DISTINCT U.* FROM Usr U, enrolls_in E, Class C, teaches T, Usr U2 WHERE U2.unetid = ? AND T.uid = U2.uid AND E.cid = T.cid ORDER BY U.unetid ASC");
            st.setString(1, login);
            rs = st.executeQuery();

            //while (rs.next())
            //    studentNames.add(rs.getString("unetid"));

            studentNames.add("bwayn052");

            // For each student, get all the classes
            // For each class, get all assignments
            // ==> Get all assignments in only one query
            for (int i = 0; i < studentNames.size(); i++) {
                st = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Grades G, Usr U, enrolls_in E WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid AND G.aid = A.aid");
                st.setString(1, studentNames.get(i));
                rs = st.executeQuery();
                cptAssign = rs.getInt(1);

                st = connection.prepareStatement("SELECT A.*, G.* FROM Assignments A, Grades G, Usr U, enrolls_in E, Class C WHERE U.unetid = ? AND E.uid = U.uid AND A.cid = E.cid AND G.aid = A.aid AND C.cid = E.cid ORDER BY C.cnum, C.csection ASC");
                st.setString(1, studentNames.get(i));
                rs = st.executeQuery();

                file = new PrintWriter("grade.json");
                file.println("[");

                while (rs.next()) {
                    int cpt = 1;
                    String late;
                    if (rs.getBoolean("late") == false)
                        late = "false";
                    else
                        late = "true";

                    if (cpt < cptAssign - 1)
                        file.println("{" + "\"grade\":" + "\"" + rs.getInt("gpts") + "\""
                                + ", " + "\"total\":" + "\"" + rs.getInt("apts") + "\"" + ", " + "\"courseNum\":" + "\"" + rs.getString("cnum").trim() + "\""
                                + ", " + "\"courseSec\":" + "\"" + rs.getString("csection").trim() + "\""
                                + ", " + "\"title\":" + "\"" + rs.getString("aname").trim() + "\""
                                + ", " + "\"late\":" + "\"" + late + "\""
                                + ", " + "\"aid\":" + "\"" + rs.getInt("aid") + "\"" + "},");
                    else
                        file.println("{" + "\"grade\":" + "\"" + rs.getInt("gpts") + "\""
                                + ", " + "\"total\":" + "\"" + rs.getInt("apts") + "\"" + ", " + "\"courseNum\":" + "\"" + rs.getString("cnum").trim() + "\""
                                + ", " + "\"courseSec\":" + "\"" + rs.getString("csection").trim() + "\""
                                + ", " + "\"title\":" + "\"" + rs.getString("aname").trim() + "\""
                                + ", " + "\"late\":" + "\"" + late + "\""
                                + ", " + "\"aid\":" + "\"" + rs.getInt("aid") + "\"" + "}");
                    cpt++;
                }
            }

            /*st = connection.prepareStatement("SELECT C.*, A.*, G.* FROM Class C, enrolls_in E, Usr U, Assignments A");



            st = connection.prepareStatement("SELECT C.cid FROM Class C, Assignments A WHERE A.aid = ? AND C.cid = A.cid");
            st.setInt(1, aid);
            rs = st.executeQuery();
            rs.next();
            idClass = rs.getInt(1);

            st = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid");
            st.setInt(1, idClass);
            rs = st.executeQuery();
            rs.next();
            nbAssign = rs.getInt(1);
            st = connection.prepareStatement("SELECT A.*, C.* FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid ORDER BY A.due ASC");
            st.setInt(1, idClass);
            rs = st.executeQuery();

            file = new PrintWriter("graded.json");
            file.println("[");

            cptAssign = 1;

            while (rs.next())
            {
                int cpt = 1;

                PreparedStatement tmp = connection.prepareStatement("SELECT COUNT(*) FROM Grades G, Usr U WHERE G.aid = ? AND U.uid = G.uid");
                tmp.setInt(1, rs.getInt("aid"));
                ResultSet rtmp = tmp.executeQuery();
                rtmp.next();
                int nb = rtmp.getInt(1);

                tmp = connection.prepareStatement("SELECT G.*, U.unetid FROM Grades G, Usr U WHERE G.aid = ? AND U.uid = G.uid ORDER BY U.unetid ASC");
                tmp.setInt(1, rs.getInt("aid"));
                rtmp = tmp.executeQuery();

                while (rtmp.next())
                {
                    String late;
                    if (rtmp.getBoolean("late") == false)
                        late = "false";
                    else
                        late = "true";

                    if (cpt < nb || cptAssign < nbAssign-1)
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
            f = new File("graded.json");
            System.out.println("Wrote graded.json!");

            *//* Send grade.json to S3 *//*
            pathS3 = "data/users/" + login + "/grade.json";
            bucketName = "milearn";

            new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3, f));
            f.delete();
*/


            /* Redirect to the static website */
            String site = new String("http://milearn.s3-website-us-west-2.amazonaws.com/");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", site);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            /* Close connection with the database */
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static String grade_Student_Format(int points, int total, String cnum, String csec, String ctitle, boolean late, int aid) {

        return "{" + "\"grade\":" + "\"" + points + "\""
                + ", " + "\"total\":" + "\"" + total + "\"" + ", " + "\"courseNum\":" + "\"" + cnum.replace(" ", "") + "\""
                + ", " + "\"courseSec\":" + "\"" + csec + "\""
                + ", " + "\"title\":" + "\"" + ctitle + "\""
                + ", " + "\"late\":" + "\"" + late + "\""
                + ", " + "\"aid\":" + "\"" + aid + "\"" + "}";
    }

    public static String grade_Teacher_Format(int points, int total, String cnum, String csec, String ctitle, boolean late, int aid, String slogin) {
        return "{" + "\"grade\":" + "\"" + points + "\""
                + ", " + "\"total\":" + "\"" + total + "\"" + ", " + "\"courseNum\":" + "\"" + cnum.replace(" ", "") + "\""
                + ", " + "\"courseSec\":" + "\"" + csec + "\""
                + ", " + "\"title\":" + "\"" + ctitle + "\""
                + ", " + "\"slogin\":" + "\"" + slogin + "\""
                + ", " + "\"late\":" + "\"" + late + "\""
                + ", " + "\"aid\":" + "\"" + aid + "\"" + "}";
    }

}