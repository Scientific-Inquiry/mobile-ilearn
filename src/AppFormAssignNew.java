import org.postgresql.Driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppFormAssignNew extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        String date = request.getParameter("dueDate");
        Timestamp ts;
        if (date.isEmpty()) {
            int tmp = Integer.parseInt(new Timestamp(new java.util.Date().getTime()).toString());
            tmp += 86400;
            date = Integer.toString(tmp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            try {
                java.util.Date parsedDate = dateFormat.parse(date);
                ts = new java.sql.Timestamp(parsedDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
            date = date.substring(0,10) + " " + date.substring(11) + ":00";
        System.out.println("Due date: " + date);
        String title = request.getParameter("title");
        if (title.isEmpty())
            title = "Assignment";
        System.out.println("Title: " + title);
        String description = request.getParameter("description");
        if (description.isEmpty())
            description = "There is no description for this assignment.";
        System.out.println("Description: " + description);
        Integer g = Integer.parseInt(request.getParameter("grade"));
        if (g == null)
            g = 10;
        int grade = g;
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

            /* Select all the existing themes to check that the user didn't cheat and sent the id of
            a theme that does not exist */
            PreparedStatement st = connection.prepareStatement("SELECT T.cid, C.csection, C.cnum FROM teaches T, Usr U, Class C WHERE U.unetid = ? AND T.uid = U.uid");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            int idClass = 0;
            while (rs.next())
            {
                String tmp = rs.getString("cnum") + "-" + rs.getString("csection");
                if (tmp.equals(request.getParameter("tmp"))) {
                    idClass = rs.getInt("cid");
                    break;
                }
            }

            rs.close();

            st = connection.prepareStatement("INSERT INTO Assignments(cid, aname, description, due, apts) VALUES (?, ?, ?, ?, ?)");
            st.setInt(1, idClass);
            st.setString(2, title);
            st.setString(3, description);
            st.setString(4, date);
            st.setInt(5, grade);
            st.executeUpdate();

            st.close();

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