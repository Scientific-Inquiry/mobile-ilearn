
public class User {
    private long id;
    private int perm_level;
    public User()
    {
        id = perm_level = 0;
    }
    public User(long u_id, int u_perm_level)
    {
        id = u_id;
        perm_level = u_perm_level;
    }
}