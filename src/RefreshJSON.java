import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/* Rewrite all the JSON files for the user that is currently logged in */
public class RefreshJSON {

    public static void connectDB()
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
        Connection connection = null;

        String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
        String user = "group5";
        String pass = "cs180group5";

        try {
            RefreshJSON.connection = DriverManager.getConnection(dbURL, user, pass);;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void closeDB()
    {
        try{
            RefreshJSON.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refreshUser()
    {
        String login = Login.login;
        Connection c = RefreshJSON.connection;

        try {
            PreparedStatement st = c.prepareStatement("SELECT * FROM Usr WHERE unetid = ?;");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            rs.next();
            Login.writeUser(rs.getString("uname"), login, rs.getString("upassword"), rs.getString("theme"), rs.getInt("notifyH"), rs.getInt("notifyM"), rs.getInt("notifyL"));
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] argv)
    {
        connectDB();
        closeDB();
    }

    public static Connection connection;
}

