import java.io.PrintWriter;
import java.io.File;

public class Grade {
    private boolean late;
    private int points;
    private int totalpts;
    private String course;
    private String assignName;

    public Grade() {
        late = false;
        course = "Unknown";
        assignName = "Untitled";
        points = 10;
        totalpts = 10;
    }

    public Grade(boolean tardy, String c, String a, int pts, int total) {
        late = tardy;
        course = c;
        assignName = a;
        points = pts;
        totalpts = total;
    }
    public Grade(String c, Assignment a, int pts) {
        late = a.getLate();
        course = c;
        assignName = a.getTitle();
        points = pts;
        totalpts = a.getPoints();
    }
    public String toString(){
        return "{\"late\":" + "\"" + late + "\"" + ", " + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"total\":" + "\"" + totalpts + "\"" + ", " + "\"course\":" + "\"" + course + "\""
                + ", " + "\"assignment\":" + "\"" + assignName + "\"" + "}";
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

    public boolean getLate(){
        return late;
    }
    public int getPoints(){
        return points;
    }
    public int getTotalpts(){
        return totalpts;
    }
    public String getCourse(){
        return course;
    }
    public String getAssignName(){
        return assignName;
    }
}
