import org.postgresql.Driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppFormSetPriority extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        int high = Integer.parseInt(request.getParameter("priHigh"));
        System.out.println("High priority: " + high);
        int medium = Integer.parseInt(request.getParameter("priMed"));
        System.out.println("Medium priority: " + medium);
        int low = Integer.parseInt(request.getParameter("priLow"));
        System.out.println("Low priority: " + low);
        String login = request.getParameter("slogin");
        System.out.println("Login: " + login);


        String theme = "";
        /* Check validity of the values that the user has given to the attribute */

        /* High priority (hours): 1 - 72h */
        if (high > 72)
            high = 72;
        if (high < 1)
            high = 1;

        /* Medium priority (days): 3 - 7d */
        if (medium > 7)
            medium = 7;
        if (medium < 3)
            medium = 3;

        /* Low priority (days): 7 - 21d */
        if (low > 21)
            low = 21;
        if (low < 7)
            low = 7;

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

            /* The priority values are now ready to be added to the database */
            PreparedStatement st = connection.prepareStatement("UPDATE Usr SET notifyH = ?, notifyM = ?, notifyL = ? WHERE unetid = ?");
            st.setInt(1, high);
            st.setInt(2, medium);
            st.setInt(3, low);
            st.setString(4, login);

            st.executeUpdate();
            System.out.println("Update effectué!");

            st.close();

            /* Redirect to the static website */
            String site = new String("http://milearn.s3-website-us-west-2.amazonaws.com/");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", site);
            System.out.println("Redirection effectuée!");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }
        finally
        {
            /* Close connection with the database */
            try
            {
                connection.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}