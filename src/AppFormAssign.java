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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppFormAssign extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        int assignId = Integer.parseInt(request.getParameter("aid"));
        System.out.println("Assignment ID: " + assignId);
        String title = request.getParameter("title");
        System.out.println("Title: " + title);
        String description = request.getParameter("description");
        System.out.println("Description :" + description);
        Integer grade = Integer.parseInt(request.getParameter("grade"));
        System.out.println("Grade :" + grade);
        String date = request.getParameter("dueDate");
        String login = request.getParameter("slogin");


        /* Register driver (absolutely needed for Tomcat) */
        try {
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

            if (!title.isEmpty())
            {
                PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET aname = ? WHERE aid = ?");
                st.setString(1, title);
                st.setInt(2, assignId);
                st.executeUpdate();
                st.close();
            }

            if (!description.isEmpty())
            {
                PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET description = ? WHERE aid = ?");
                st.setString(1, description);
                st.setInt(2, assignId);
                st.executeUpdate();
                st.close();
            }

            if (!date.isEmpty())
            {
                Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = null;
                try {
                    parsedDate = dateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timestamp = new java.sql.Timestamp(parsedDate.getTime());
                PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET due = ? WHERE aid = ?");
                st.setTimestamp(1, timestamp);
                st.setInt(2, assignId);
                st.executeUpdate();
                st.close();
            }

            if (grade != null)
            {
                PreparedStatement st = connection.prepareStatement("UPDATE Assignments SET apts = ? WHERE aid = ?");
                st.setInt(1, grade);
                st.setInt(2, assignId);
                st.executeUpdate();
                st.close();
            }

            /* Write assigner.json */
            PreparedStatement st = connection.prepareStatement("SELECT C.cquarter, C.cnum, C.csection, C.cid FROM Class C, Assignments A WHERE A.aid = ? AND C.cid = A.cid");
            st.setInt(1, assignId);
            ResultSet rs = st.executeQuery();
            rs.next();
            String cnum = rs.getString("cnum").trim();
            String csection = rs.getString("csection").trim();
            String cquarter = rs.getString("cquarter").trim();
            int cid = rs.getInt("cid");


            PrintWriter file = new PrintWriter("assigner.json");
            file.println("[");
            st = connection.prepareStatement("SELECT COUNT(*) FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid");
            st.setInt(1, cid);
            rs = st.executeQuery();
            rs.next();
            int nbRows = rs.getInt(1);

            st = connection.prepareStatement("SELECT A.* FROM Assignments A, Class C WHERE C.cid = ? AND A.cid = C.cid ORDER BY due ASC");
            st.setInt(1, cid);
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
            st.setInt(1, cid);
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

                tmp = connection.prepareStatement("SELECT A.* FROM Assignments A, Class C, enrolls_in E, Usr U WHERE U.unetid = ? AND E.uid = U.uid AND C.cid = E.cid AND A.cid = C.cid ORDER BY due ASC");
                tmp.setString(1, rs.getString("unetid").trim());
                rtmp = tmp.executeQuery();

                cpt = 1;
                while (rtmp.next())
                {
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

}