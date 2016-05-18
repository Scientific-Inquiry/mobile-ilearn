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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppFormSetTheme extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        String theme = request.getParameter("themeName");
        System.out.println("Theme: " + theme);
        String login = request.getParameter("slogin");
        System.out.println("Login: " + login);

        /* Check validity of the value that the user has given to the attribute */
        if (theme.length() > 1)
            theme = "a"; /* If the parameter is longer than one, then the user has "cheated"

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

            /* Select all the existing themes to check that the user didn't cheat and sent the id of
            a theme that does not exist */
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Theme");
            ResultSet rs = st.executeQuery();
            boolean b = false;
            while (rs.next())
            {
                /* If the theme given by the user matches one that is in the database, everything
                is fine */
                if (theme.equals(rs.getString("id")))
                    b = true;
            }
            if (!b) /* If no theme matched, set the parameter to the default theme */
                theme = "a";

            /* Process to the database update */
            st = connection.prepareStatement("UPDATE Usr U SET theme = ? WHERE U.unetid = ?;");
            st.setString(1, theme);
            st.setString(2, login);
            st.executeUpdate();

            rs.close();
            st.close();

            st = connection.prepareStatement("SELECT * FROM Usr WHERE unetid = ?;");
            st.setString(1, login);
            rs = st.executeQuery();
            rs.next();

            int rank;
            if (rs.getString("urank").trim().equals("STUDENT"))
                rank = 0;
            else if (rs.getString("urank").trim().equals("INSTRUCTOR"))
                rank = 1;
            else
                rank = 2; // TA

            PrintWriter file = new PrintWriter("user.json");
            file.println("[");
            file.println("{\"name\":\"" + rs.getString("uname").trim() + "\", \"login\":\"" + login + "\", \"password\":\""
                    + rs.getString("upassword").trim() + "\", \"theme\":\"" + rs.getString("theme") + "\", \"notifyH\":\"" + rs.getInt("notifyH") + "\", \"notifyM\":\""
                    + rs.getInt("notifyM") + "\", \"notifyL\":\"" + rs.getInt("notifyL") + "\", \"type\":\"" + rank + "\"}");
            file.println("]");
            file.close();
            File f = new File("user.json");
            System.out.println("Wrote user.json!");

            String pathS3 = "data/users/" + login + "/user.json";

            String bucketName = "milearn";
            AWSCredentials credentials = new BasicAWSCredentials("AKIAJWYCYKZJ3BZ5XEBA", "NGJuCS16bH3R6ywlJf7m2NSmdTPd0yA0qANIUDkM");

            new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3,
                    f));
            f.delete();

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