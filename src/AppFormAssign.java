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

public class AppFormAssign extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        /* Get the parameters that were given by the user in the form */
        String assignId = request.getParameter("aid");
        System.out.println("Assignment ID: " + assignId);
        String title = request.getParameter("title");
        System.out.println("Title: " + title);
        String description = request.getParameter("description");
        System.out.println("Description :" + description);
        String grade = request.getParameter("grade");
        System.out.println("Grade :" + grade);

        /* Check validity of the value that the user has given to the attribute */
        /*if (theme.length() > 1)
            theme = "a"; *//* If the parameter is longer than one, then the user has "cheated"

        *//* Register driver (absolutely needed for Tomcat) *//*
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

        *//* Connect to the database that is stored on AWS' RDS *//*
        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass); *//* Now connected! *//*

            *//* Select all the existing themes to check that the user didn't cheat and sent the id of
            a theme that does not exist *//*
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Theme");
            ResultSet rs = st.executeQuery();
            boolean b = false;
            while (rs.next())
            {
                *//* If the theme given by the user matches one that is in the database, everything
                is fine *//*
                if (theme.equals(rs.getString("id")))
                    b = true;
            }
            if (!b) *//* If no theme matched, set the parameter to the default theme *//*
                theme = "a";

            *//* Process to the database update *//*
            st = connection.prepareStatement("UPDATE Usr U SET theme = ? WHERE U.unetid = ?;");
            st.setString(1, theme);
            st.setString(2, login);
            st.executeUpdate();

            rs.close();
            st.close();

            *//* Redirect to the static website *//*
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
            *//* Close connection with the database *//*
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
    }

}