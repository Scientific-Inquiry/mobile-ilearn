package teamfive.milearn;

//import java.util.List;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import java.sql.*;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

public class Assignment {
    private int aid;
    private Date due_date;
    private String description;
    private String title;
    private int points;
    private boolean late;
    private Date sub_date;
    private String course_num;
    private String course_section;
    private String current_user;

    public Assignment()
    {
        current_user = Login.login;
        Login.rank.toString().equals("instructor");
        aid = 0;
        title = "Untitled";
        course_num = "Unkown";
        course_section = "001";
        //dateFormat= new SimpleDateFormat("yyyy/MM/dd");
        due_date = new Date();
        sub_date = null;
        description = "Start homework early";
        points = 10;
        late = false;
        //System.out.println(dateFormat.format(cal.getTime()));
    }

    public Assignment(String t, String d, Date due, int pts, String cnum, String csect)
    {
        current_user = Login.login;
        aid = 0;
        title = t;
        course_num = cnum;
        course_section = csect;
        due_date = due;
        description = d;
        points = pts;
        late = false;
    }

    public Assignment(String t, String d, Date due, int pts, String cnum, String csect, int id)
    {
        current_user = Login.login;
        aid = id;
        title = t;
        course_num = cnum;
        course_section = csect;
        due_date = due;
        description = d;
        points = pts;
        late = false;
    }

    public Assignment(String t, String d, Date due, int pts, String cnum, String csect, int id, String login)
    {
        current_user = Login.login;
        aid = id;
        title = t;
        course_num = cnum;
        course_section = csect;
        due_date = due;
        description = d;
        points = pts;
        late = false;
    }
    public String toString(){
        return "{\"title\":" + "\"" + title + "\"" + ", " + "\"due\":" + "\"" + due_date + "\"" + ", "
                + "\"desc\":" + "\"" + description + "\"" + ", " + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"courseNum\":" + "\"" + course_num.replace(" ", "") + "\"" + ", " + "\"courseSec\":" + "\"" + course_section + "\"" + ", "
                + "\"aid\":" + "\"" + aid + "\"" + "}";
    }

    public void createJSON_File(){
        try{
            PrintWriter file = new PrintWriter("Assignment.json");
            file.println(this.toString());
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

//--------------------------DATABASE INTERACTIONS----------------------------------------

    public void update_JSON(){

    }

    public void obtain_Submission(int uid, String name) {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connected to the database!");
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        try{
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Assignments WHERE aname = ? AND uid = ?");
            st.setString(1, name);
            st.setInt(2, uid);
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                System.out.print("Row returned ");
                System.out.println(rs.getString(1));
            }
            rs.close();
            st.close();
        }catch(Exception e) {
            System.out.println("Retrieving Data Failed!");
        }

        finally
        {
            try{
                connection.close();
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void create_Assignment(){
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connected to the database!");
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        Timestamp timestamp = new java.sql.Timestamp(due_date.getTime());
        try {
            Statement st = connection.createStatement();
            //(aid, cid, aname, description, due, apts)
            String sql = "INSERT INTO Assignments VALUES(DEFAULT, 111, '" +
                    title + "', '" + description + "', '" + timestamp + "', " +
                    points + ")";
            System.out.println(sql);
            st.executeUpdate(sql);
            System.out.println("Created Assignment Database");
        }catch(Exception e)
        {
            System.out.println("Creating Assignment failed!");
        }
        finally
        {
            try{
                connection.close();
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void create_Submission()
    {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.out.println("Driver registration failed!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Connected to the database!");

            /* Do your cool stuff in this try block */
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        try {
            Statement st = connection.createStatement();
            String sql = "INSERT INTO Assignment(aid, aname, due, apts, cnum, csect, stime) VALUES(" +
                    title + ", " + new java.sql.Timestamp(due_date.getTime()) + ", " +
                    points;
            if(sub_date != null)
                sql += ", " + new java.sql.Timestamp(sub_date.getTime()) + ")";
            else
                sql += ")";

            st.executeUpdate(sql);
            System.out.println("Created Database");
        }catch(Exception e)
        {
            System.out.println("creating table failed!");
        }
        finally
        {
            try{
                connection.close();
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Date getDate(){
        return due_date;
    }

    public String getDescription(){
        return description;
    }

    public String getTitle(){
        return title;
    }

    public int getPoints(){
        return points;
    }

    public boolean getLate(){
        return late;
    }

    public int getAid(){
        return aid;
    }

    public String getCourseNum(){
        return course_num;
    }

    public String getCourseSection(){
        return course_section;
    }
}
