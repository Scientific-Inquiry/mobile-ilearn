import java.io.IOException;
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

        String theme = request.getParameter("themeName");
        System.out.println("Theme: " + theme);

        if (theme.length() > 1)
            theme = "a";

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
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Theme");
            ResultSet rs = st.executeQuery();
            boolean b = false;
            while (rs.next())
            {
                if (theme.equals(rs.getString("id")))
                    b = true;
            }
            if (b == false)
                theme = "a";

            st = connection.prepareStatement("UPDATE Usr SET theme = ? WHERE unetid = ?;");
            st.setString(1, theme);
            st.setString(2, Login.login);
            st.executeUpdate();

            rs.close();
            st.close();
            RefreshJSON.closeDB();

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
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}