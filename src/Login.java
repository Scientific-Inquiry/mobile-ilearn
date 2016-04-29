import java.util.ArrayList;
import java.sql.*;

public class Login {
    /* Assume that the user has already "sent" a login and a password.
    So far, that's the only thing we are parsing.
    Need to see how to get this data from the user
    (probably from the UI, the user types something which is "saved" as strings and directly
    given as parameters here)
     */

    private Login()
    {
        this.username = null;
        this.password = null;
        this.user = null;
    }

    private Login(String username, String password)
    {
        this.username = new String(username);
        this.password = new String(password);
        this.user = null;
    }

    private String getUsername()
    {
        return this.username;
    }

    private void setUsername(String username)
    {
        this.username = new String(username);
    }

    private String getPassword()
    {
        return this.password;
    }

    private void setPassword(String password)
    {
        this.password = new String(password);
    }

    private User getUser()
    {
        return this.user;
    }

    private void setUser(User user)
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


    private void checkCredentials(Connection connection)
    {
        try
        {
            /* Checks that there is a user in the Usr table with that username and that password (counts) */
            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM Usr U WHERE U.unetid = ? AND U.upassword = ?");
            st.setString(1, this.getUsername());
            st.setString(2, this.getPassword());
            ResultSet rs = st.executeQuery();

            /* Places the "cursor" on the first (and only, in that case) result */
            rs.next();

            if (rs.getInt(1) == 1) /* If there is such a user */
            {
                System.out.println("User found!");

                /* Selects all information about that user */
                st = connection.prepareStatement("SELECT * FROM Usr U WHERE U.unetid = ?");
                st.setString(1, this.getUsername());
                rs = st.executeQuery();
                rs.next();

                /* Saves that info to use it later */
                String name = rs.getString("uname");
                int sid = rs.getInt("uid");
                String rank = rs.getString("urank").trim();

                /* Prepares two query strings: one for a student, one for an instructor, since the JSON to generate are different */
                String queryStudent = new String("SELECT C.cname, C.csection, C.cnum, C.cquarter, C.ctype, U2.uname FROM Class C, enrolls_in E, teaches T, Usr U, Usr U2 WHERE C.cid = E.cid AND U.uid = ? AND U2.uid = T.uid ORDER BY cnum ASC, csection ASC");
                String queryInstructor = new String("SELECT C.cname, C.csection, C.cnum, C.cquarter, C.ctype FROM Class C, teaches T, Usr U WHERE C.cid = T.cid AND U.uid = ? ORDER BY cnum ASC, csection ASC");

                /* Prepares the final query depending on the rank of the logged-in user */
                if(rank.equals("STUDENT"))
                    st = connection.prepareStatement(queryStudent);
                else
                    st = connection.prepareStatement(queryInstructor);

                /* Sets the unknown parameter */
                st.setInt(1, sid);
                rs = st.executeQuery();

                /* Initializes the array of classes */
                ArrayList<Class> classes = new ArrayList<Class> ();

                /* Fills the array using the query results */
                while(rs.next())
                {
                    String cname = rs.getString("cname").trim();
                    String cnum = rs.getString("cnum").trim();
                    String csection = rs.getString("csection").trim();
                    String cquarter = rs.getString("cquarter").trim();
                    String uname;

                    /* Depending on the rank, we don't get the name of the instructor in the query.
                       Handling the case here
                     */
                    if(rank.equals("STUDENT"))
                        uname = rs.getString("uname").trim();
                    else
                        uname = name;

                    /* Differentiates lecture, discussion and lab */
                    if(rs.getString("ctype").trim().equals("LECTURE"))
                    {
                        Lecture lec = new Lecture(cnum, csection, cname, cquarter, uname);
                        classes.add(lec);
                    }
                    else if(rs.getString("ctype").trim().equals("DISCUSSION"))
                    {
                        Discussion dis = new Discussion(cnum, csection, cname, cquarter, uname);
                        classes.add(dis);
                    }
                    else
                    {
                        Lab lab = new Lab(cnum, csection, cname, cquarter, uname);
                        classes.add(lab);
                    }
                }

                /* Creates the final user (needs to recheck the rank to create the correct object) */
                if (rank.trim().equals("STUDENT"))
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

            Statement st = connection.createStatement();
            //st.execute("CREATE TABLE IF NOT EXISTS enrolls_in(uid int, cid int, FOREIGN KEY (uid) REFERENCES Usr(uid) ON DELETE CASCADE, FOREIGN KEY (cid) REFERENCES Class(cid) ON DELETE CASCADE, PRIMARY KEY (uid, cid))");
            //st.execute("DROP TABLE Class");
            //st.close();
            st.execute("ALTER TABLE Usr ADD UNIQUE (unetid)");

            Login log = new Login("ckent038", "iamsuperman");
            log.checkCredentials(connection);

            /*st = connection.createStatement();
            st.executeUpdate("INSERT INTO enrolls_in(uid, cid) VALUES (861236524, 1)");
            st.close();*/

            //st = connection.createStatement();

            st.close();

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

    private String username;
    private String password;
    private User user;
}
