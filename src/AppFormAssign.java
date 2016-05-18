import org.postgresql.Driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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