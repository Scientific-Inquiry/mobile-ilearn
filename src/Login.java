import java.util.ArrayList;
import java.sql.*;

public class Login {
    /* Assume that the user has already "sent" a login and a password.
    So far, that's the only thing we are parsing.
    Need to see how to get this data from the user
    (probably from the UI, the user types something which is "saved" as strings and directly
    given as parameters here)
     */

    public Login()
    {
        this.username = null;
        this.password = null;
        this.user = null;
    }

    public Login(String username, String password)
    {
        this.username = new String(username);
        this.password = new String(password);
        this.user = null;
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

    public User getUser()
    {
        return this.user;
    }

    public void setUser(User user)
    {
        if (user instanceof Student)
        {
            this.user = (Student) ((Student)user).clone();
        }
        else
        {
            this.user = (Instructor) ((Instructor)user).clone();
        }
    }



    /* Written in pseudo-code since there's no usable database so far */
    public void checkCredentials(Connection connection)
    {
        try
        {
            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM Usr WHERE unetid = ? AND upassword = ?");
            st.setString(1, this.getUsername());
            st.setString(2, this.getPassword());
            ResultSet rs = st.executeQuery();

            rs.next();

            if (rs.getInt(1) == 1)//if (Integer.parseInt(rs.getString(1)) == 1)
            {
                System.out.println("User found!");
                st = connection.prepareStatement("SELECT * FROM Usr WHERE unetid = ?");
                st.setString(1, this.getUsername());
                rs = st.executeQuery();
                rs.next();

                String name = rs.getString("uname");
                int sid = rs.getInt("uid");

                /*
                    Request to get all the user's classes
                    Store them into an array list
                 */
                ArrayList<Class> classes = new ArrayList<Class> ();

                if (rs.getString("urank").trim().equals("STUDENT"))
                    this.user = new Student (name, this.getUsername(), sid, classes);
                else
                    this.user = new Instructor (name, this.getUsername(), sid, classes);
            }
            else
            {
                System.out.println("User not found!");
            }
            rs.close();
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

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

            Login log = new Login("bwayn052", "iambatman");
            log.checkCredentials(connection);

            while (log.getUser() != null)
            {
                /*
                The user is connected, do something
                We go out of the while loop once the user has logged out
                 */

                /* At some point, the user logs out */
                log = new Login();
            }

            /* Need to see how to get arguments and what to do once the first user logged out */
            System.out.println("Logged out!");
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
    private User user;
}
