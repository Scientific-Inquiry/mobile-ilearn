import java.io.IOException;
import java.sql.Connection;
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

        RefreshJSON.connectDB();
        Connection connection = RefreshJSON.connection;

        try {
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
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }

    }

}