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
              return Instructor(username, classes); // Creates the course.json file as well
            }
            else
            {
              return Student(username, classes); // Creates the classes.json file as well
            }
        }
        else
        {
          return void;
        }


         */
    }

    private String username;
    private String password;
}
