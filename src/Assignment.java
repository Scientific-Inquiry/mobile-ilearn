
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;


public class Assignment {
    private Date due_date;
    private String description;
    private String title;
    private int points;
    private boolean late;

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
        late = false;
        //System.out.println(dateFormat.format(cal.getTime()));
    }
    public Assignment(String t, String d, Date due, int pts)
    {
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
}

