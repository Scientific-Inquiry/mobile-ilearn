import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppFormAssignNew extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        String title = request.getParameter("title");
        if (title.isEmpty())
            title = "Assignment";
        System.out.println("Title: " + title);
        String description = request.getParameter("description");
        if (description.isEmpty())
            description = "There is no description for this assignment.";
        System.out.println("Description: " + description);
        String g = request.getParameter("grade");
        if (g.isEmpty())
            g = "10";
        int grade = Integer.parseInt(g);
        System.out.println("Grade: " + grade);
        String login = request.getParameter("slogin");
        System.out.println("Login: " + login);

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

            String date = request.getParameter("dueDate");
            Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
            timestamp.setTime(timestamp.getTime() + (long) 86400);
            if (!date.isEmpty()) {
                date = date.substring(0, 10) + " " + date.substring(11) + ":00";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(date);
                timestamp = new java.sql.Timestamp(parsedDate.getTime());
            }

            /* Retrieve the class number for the assignment that is being added */
            PreparedStatement st = connection.prepareStatement("SELECT C.cid, C.csection, C.cnum FROM teaches T, Usr U, Class C WHERE U.unetid = ? AND T.uid = U.uid AND C.cid = T.cid");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            int idClass = 0;
            while (rs.next())
            {
                String tmp = rs.getString("cnum").trim() + "-" + rs.getString("csection").trim();
                String r = request.getParameter("className");
                if (r.trim().equals(tmp)) {
                    idClass = Integer.parseInt(rs.getString("cid"));
                    break;
                }
            }

            st = connection.prepareStatement("INSERT INTO Assignments(cid, aname, description, due, apts) VALUES (?, ?, ?, ?, ?)");
            st.setInt(1, idClass);
            st.setString(2, title);
            st.setString(3, description);
            st.setTimestamp(4, timestamp);
            st.setInt(5, grade);
            st.executeUpdate();

            /* Write assigner.json */
            st = connection.prepareStatement("SELECT C.cquarter, C.cnum, C.csection FROM Class C WHERE C.cid = ?");
            st.setInt(1, idClass);
            rs = st.executeQuery();
            rs.next();
            String cnum = rs.getString("cnum").trim();
            String csection = rs.getString("csection").trim();
            String cquarter = rs.getString("cquarter").trim();


            PrintWriter file = new PrintWriter("assigner.json");
            file.println("[");
            st = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid");
            st.setInt(1, idClass);
            rs = st.executeQuery();
            rs.next();
            int nbRows = rs.getInt(1);

            st = connection.prepareStatement("SELECT A.* FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid ORDER BY due ASC");
            st.setInt(1, idClass);
            rs = st.executeQuery();

            int cpt = 1;
            while (rs.next())
            {
                if (cpt < nbRows)
                    file.println("{\"title\":\"" + rs.getString("aname").trim() + "\", \"due\":\"" + new Date(rs.getTimestamp("due").getTime()) + "\", \"desc\":\""
                        + rs.getString("description").trim() + "\", \"points\":\"" + rs.getInt("apts") + "\", \"courseNum\":\"" + cnum + "\", \"courseSec\":\""
                        + csection + "\", \"aid\":\"" + rs.getInt("aid") + "\"},");
                else
                    file.println("{\"title\":\"" + rs.getString("aname").trim() + "\", \"due\":\"" + new Date(rs.getTimestamp("due").getTime()) + "\", \"desc\":\""
                            + rs.getString("description").trim() + "\", \"points\":\"" + rs.getInt("apts") + "\", \"courseNum\":\"" + cnum + "\", \"courseSec\":\""
                            + csection + "\", \"aid\":\"" + rs.getInt("aid") + "\"}");
                cpt++;
            }
            file.println("]");
            file.close();
            System.out.println("Reached file.close()!");
            File f = new File("assigner.json");
            System.out.println("Wrote assigner.json!");

            /* Sends assigner.json to S3 */
            String pathS3 = "data/users/" + login + "/assigner.json";
            System.out.println(pathS3);
            String bucketName = "milearn";
            AWSCredentials credentials = new BasicAWSCredentials("AKIAJWYCYKZJ3BZ5XEBA", "NGJuCS16bH3R6ywlJf7m2NSmdTPd0yA0qANIUDkM");

            new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3, f));
            f.delete();


            /* Write assign.json */
            st = connection.prepareStatement("SELECT U.unetid FROM Usr U, enrolls_in E, Class C WHERE C.cid = ? AND E.cid = C.cid AND U.uid = E.uid");
            st.setInt(1, idClass);
            rs = st.executeQuery();
            while (rs.next()) /* For each student of this class, update assignments */
            {
                file = new PrintWriter("assign.json");
                file.println("[");

                PreparedStatement tmp = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Class C, enrolls_in E, Usr U WHERE U.unetid = ? AND E.uid = U.uid AND C.cid = E.cid AND A.cid = C.cid");
                tmp.setString(1, rs.getString("unetid").trim());
                ResultSet rtmp = tmp.executeQuery();
                rtmp.next();
                nbRows = rtmp.getInt(1);

                tmp = connection.prepareStatement("SELECT A.*, C.* FROM Assignments A, Class C, enrolls_in E, Usr U WHERE U.unetid = ? AND E.uid = U.uid AND C.cid = E.cid AND A.cid = C.cid ORDER BY due ASC");
                tmp.setString(1, rs.getString("unetid").trim());
                rtmp = tmp.executeQuery();

                cpt = 1;
                while (rtmp.next())
                {
                    cnum = rs.getString("cnum").trim();
                    csection = rs.getString("csection").trim();
                    if (cpt < nbRows)
                        file.println("{\"title\":\"" + rtmp.getString("aname").trim() + "\", \"due\":\"" + new Date(rtmp.getTimestamp("due").getTime()) + "\", \"desc\":\""
                                + rtmp.getString("description").trim() + "\", \"points\":\"" + rtmp.getInt("apts") + "\", \"courseNum\":\"" + cnum + "\", \"courseSec\":\""
                                + csection + "\", \"aid\":\"" + rtmp.getInt("aid") + "\"},");
                    else
                        file.println("{\"title\":\"" + rtmp.getString("aname").trim() + "\", \"due\":\"" + new Date(rtmp.getTimestamp("due").getTime()) + "\", \"desc\":\""
                                + rtmp.getString("description").trim() + "\", \"points\":\"" + rtmp.getInt("apts") + "\", \"courseNum\":\"" + cnum + "\", \"courseSec\":\""
                                + csection + "\", \"aid\":\"" + rtmp.getInt("aid") + "\"}");
                    cpt++;
                }
                file.println("]");
                file.close();
                System.out.println("Reached file.close()!");
                f = new File("assign.json");
                System.out.println("Wrote assign.json!");

                /* Send assign.json to S3 */
                pathS3 = "data/users/" + rs.getString("unetid").trim() + "/assign.json";
                System.out.println(pathS3);

                new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3, f));
                f.delete();
            }

            /* Redirect to the static website */
            String site = new String("http://milearn.s3-website-us-west-2.amazonaws.com/");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", site);
        }
        catch (Exception e)
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
}