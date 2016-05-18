package teamfive.milearn;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;


public class Assignment {
    //private Calendar due_date;
    Date due_date;
    //private DateFormat dateFormat;
    private String description;
    private String title;
    private int points;

    public int submission(Student s, File file) {
        //stub
        return 1; //success
    }
    public Assignment()
    {
        title = "Untitled";
        //dateFormat= new SimpleDateFormat("yyyy/MM/dd");
        due_date = new Date();
        description = "Start homework early";
        points = 10;
        //System.out.println(dateFormat.format(cal.getTime()));
    }
    public Assignment(String t, String d, Date due, int pts)
    {
        title = t;
        //dateFormat= new SimpleDateFormat("yyyy/MM/dd");
        due_date = due;
        description = d;
        points = pts;
    }
    public String toString(){
        return "{\"title\":" + "\"" + title + "\"" + ", " + "\"due\":" + "\"" + due_date + "\"" + ", "
                + "\"description\":" + "\"" + description + "\"" + ", " + "\"points\":" + "\"" + points + "\"" + "]";
        //return title + "\nDue: " + due_date + "\nDescription: " + description
        //        + "\nPoints: " + points;
    }
    public String createJSON_String(){
        return this.toString();
    }
    public void createJSON_File(){
        try{
            PrintWriter file = new PrintWriter("JSON_assignment.txt");
            file.println(this.toString());
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }
}

