import java.util.ArrayList;
import java.sql.*;

public class Login {
    /* Assume that the user has already "sent" a login and a password.
    So far, that's the only thing we are parsing.
    Need to see how to get this data from the user
    (probably from the UI, the user types something which is "saved" as strings and directly
    given as parameters here)
     */

    public Login(String username, String password)
    {
        this.username = new String(username);
        this.password = new String(password);
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = new String(username);
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = new String(password);
    }

    /* Written in pseudo-code since there's no usable database so far */
    public User checkCredentials()
    {
        /* Request checkUser = new String ('SELECT * FROM User U WHERE U.username = %s AND U.password = %s;', getUsername(), getPassword());

        if (checkUser.execute() == 1) // If the request returns something
        {
            ArrayList<Class> classes = load all the classes for this user
            if (result[rank] == INSTRUCTOR)
            {
              return new Instructor(username, classes); // Creates the course.json file as well
            }
            else
            {
              return new Student(username, classes); // Creates the classes.json file as well
            }
        }
        else
        {
          return;
        }


         */
        ArrayList<Class> classes = new ArrayList<Class>();
        return new Student(username, classes);
    }

    public static void main(String[] argv)
    {
        System.out.println("-------- PostgreSQL "
                + "JDBC Connection Testing ------------");

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

            Statement st = connection.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS Usr(uid int PRIMARY KEY, unetid char(10), uname char(50), upassword char(50), umail char(50), urank char(10))");
            st.close();

            st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Usr");
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            while (rs.next())
            {
                int i = 1;
                while(i <= numCols) {
                    System.out.println(rs.getString(i));
                    i++;
                }
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

    private String username;
    private String password;
}
