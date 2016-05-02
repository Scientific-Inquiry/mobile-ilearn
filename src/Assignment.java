import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;
import java.sql.*;

public class Assignment {
    private int aid;
    private Date due_date;
    private String description;
    private String title;
    private int points;
    private boolean late;
    private Date sub_date;


    public int submission(User s, File file) {
        //stub
        return 1; //success
    }

    public Assignment()
    {
        aid = 0;
        title = "Untitled";
        //dateFormat= new SimpleDateFormat("yyyy/MM/dd");
        due_date = new Date();
        sub_date = null;
        description = "Start homework early";
        points = 10;
        late = false;
        //System.out.println(dateFormat.format(cal.getTime()));
    }
    public Assignment(String t, String d, Date due, int pts)
    {
        aid = 0;
        title = t;
        //dateFormat= new SimpleDateFormat("yyyy/MM/dd");
        due_date = due;
        description = d;
        points = pts;
        late = false;
    }
    public String toString(){
        return "{\"title\":" + "\"" + title + "\"" + ", " + "\"due\":" + "\"" + due_date + "\"" + ", "
                + "\"description\":" + "\"" + description + "\"" + ", " + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"late\":" + "\"" + late + "\"" + "}";
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

    public void Database_Connecion(){
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

    public void set_aid(int c_id) {
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
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Assignments WHERE aname = ? AND cid = ?");
            st.setString(1, title);
            st.setInt(2, c_id);
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                System.out.print("Row returned ");
                System.out.println(rs.getString(1));
                aid = Integer.parseInt(rs.getString(1));
                System.out.println("aid is " + aid);
            }
            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
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

    public void obtain_Assignment() {
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
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Assignments WHERE aid = ?");
            st.setInt(1, aid);
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                System.out.println("Row returned ");
                System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3) + ", "
                        + rs.getString(4) + ", " + rs.getString(5) + ", " + rs.getString(6) );
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

    public void delete_Assignment(){
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
            System.out.println("aid is " + aid);
            PreparedStatement st = connection.prepareStatement("DELETE FROM Assignments WHERE aid = ?" +
                    "IN SELECT ");
            st.setInt(1, aid);
            st.close();
            System.out.println("Deleting Data Succeeded!");
        }catch(Exception e) {
            System.out.println("Deleting Data Failed!");
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

    public void managing_ADatabase() {
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
            //String sql = "DROP TABLE Assignments";

            String sql = "CREATE TABLE Assignments (" +
                    "aid SERIAL UNIQUE not NULL, " +
                    "cid INT not NULL, " +
                    "aname VARCHAR(50) not NULL, " +
                    "description VARCHAR(150), " +
                    "due TIMESTAMP not NULL, " +
                    "apts INT not NULL, " +
                    "PRIMARY KEY(aid))";


            st.executeUpdate(sql);
            System.out.println("Created Assignments Database");
        }catch(Exception e)
        {
            System.out.println("Creating Assignments failed!");
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

    public void managing_SDatabase() {
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
            //String sql = "DROP TABLE Submissions";

            String sql = "CREATE TABLE Submissions (" +
                    "aid INT not NULL, " +
                    "uid INT not NULL, " +
                    "stime TIMESTAMP, " +
                    "PRIMARY KEY(aid))";

            st.executeUpdate(sql);
            System.out.println("Created Submissions Database");
        }catch(Exception e)
        {
            System.out.println("Creating Submissions failed!");
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
}

