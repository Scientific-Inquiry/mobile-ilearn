import org.postgresql.Driver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    PreparedStatement stmp = connection.prepareStatement("INSERT INTO Grade(aid, uid, gpts, late) VALUES (?, ?, ?, false)");
                    stmp.setInt(1, aid);
                    stmp.setInt(2, rs.getInt("uid"));
                    stmp.setInt(3, grade);
                    stmp.executeUpdate();
                }
                else
                    System.out.println("No grade retrieved!");
            }

            rs.close();
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