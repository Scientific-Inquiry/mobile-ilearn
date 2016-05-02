import java.io.PrintWriter;
import java.io.File;
import java.sql.*;

public class Grade {
    private Assignment assign;
    private boolean late;
    private int points;
    private String course;

    public Grade() {
        late = false;
        points = 10;
    }

    public Grade(boolean tardy, String c) {
        late = tardy;
        course = c;
    }

    public Grade(String c, Assignment a, int pts) {
        assign = a;
        late = a.getLate();
        points = pts;
        course = c;

    }
    public String toString(){
        return "{\"late\":" + "\"" + late + "\"" + ", " + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"total\":" + "\"" + assign.getPoints() + "\"" + ", " + "\"course\":" + "\"" + course + "\""
                + ", " + "\"assignment\":" + "\"" + assign.getTitle() + "\"" + "}";
    }

    public void createJSON_File(){
        try{
            PrintWriter file = new PrintWriter("Grade.json");
            file.println(this.toString());
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    public void add_Grade(int uid){
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

        try {
            Statement st = connection.createStatement();
            //(aid, uid, gpts)
            String sql = "INSERT INTO Grades VALUES(" + assign.getAid() + ", " + uid + ", " + points + ")";

            System.out.println(sql);
            st.executeUpdate(sql);
            System.out.println("Created Grade");
        }catch(Exception e)
        {
            System.out.println("Creating Grade failed!");
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
    public void obtain_Grade() {
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
            PreparedStatement st = connection.prepareStatement("SELECT * FROM Grades WHERE aid = ?");
            st.setInt(1, assign.getAid());
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                System.out.println("Row returned ");
                System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3));
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
    public void managing_GDatabase() {
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
            //String sql = "DROP TABLE Grades";

            String sql = "CREATE TABLE Grades (" +
                    "aid INT not NULL, " +
                    "uid INT not NULL, " +
                    "gpts INT, " +
                    "PRIMARY KEY(aid, uid))";

            st.executeUpdate(sql);
            System.out.println("Created Grades Database");
        }catch(Exception e)
        {
            System.out.println("Creating Grades failed!");
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
    public boolean getLate(){
        return late;
    }
    public int getPoints(){
        return points;
    }
    public int getTotalpts(){
        return assign.getPoints();
    }
    public String getCourse(){
        return course;
    }
    public String getAssignName(){
        return assign.getTitle();
    }
}
