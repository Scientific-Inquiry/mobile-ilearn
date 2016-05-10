import java.io.PrintWriter;
import java.io.File;
import java.sql.*;

public class Grade {
    private Assignment assign;
    private boolean late;
    private int points;
    private String courseNum;
    private String sectionNum;
    private String slogin;

    public Grade() {
        late = false;
        points = 10;
        slogin = "";
    }

    public Grade(boolean tardy, String c, String s) {
        late = tardy;
        courseNum = c;
        sectionNum = s;
        slogin = "";
    }

    public Grade(Assignment a, int pts) {
        assign = a;
        late = assign.getLate();
        points = pts;
        courseNum = assign.getCourseNum();
        sectionNum = assign.getCourseSection();
        slogin = "";
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


    //defining object string
    public String toString(){
        if(Login.rank.toString() != "INSTRUCTOR")
            return "{" + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"total\":" + "\"" + getTotalpts() + "\"" + ", " + "\"course\":" + "\"" + courseNum + "\""
                + ", " + "\"section\":" + "\"" + getSectionNum() + "\""
                + ", " + "\"assignment\":" + "\"" + getTitle() + "\""
                + ", " + "\"late\":" + "\"" + late + "\"" + "}";

        return "{" + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"total\":" + "\"" + getTotalpts() + "\"" + ", " + "\"course\":" + "\"" + courseNum + "\""
                + ", " + "\"section\":" + "\"" + getSectionNum() + "\""
                + ", " + "\"assignment\":" + "\"" + getTitle() + "\""
                + ", "+ "\"slogin\":" + "\"" + slogin
                + ", " + "\"late\":" + "\"" + late + "\"" + "}";
    }
    //accessor functions
    public boolean getLate(){
        return late;
    }
    public int getPoints(){
        return points;
    }
    public int getTotalpts(){
        return assign.getPoints();
    }
    public String getTitle(){
        return assign.getTitle();
    }
    public String getCourseNum(){
        return assign.getCourseNum();
    }
    public String getSectionNum(){
        return assign.getCourseSection();
    }
    public void setSlogin(String user){
        slogin = user;
    }
}
