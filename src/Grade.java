import java.io.PrintWriter;
import java.io.File;
import java.sql.*;

public class Grade {
    private Assignment assign;
    private int aid;
    private boolean late;
    private boolean instructor;
    private int points;
    private String courseNum;
    private String sectionNum;
    private String slogin;

    public Grade() {
        late = false;
        points = 10;
        slogin = "";
        instructor = false;
    }

    public Grade(boolean tardy, String c, String s) {
        late = tardy;
        courseNum = c;
        sectionNum = s;
        slogin = "";
        aid = 0;
        instructor = false;
    }

    public Grade(Assignment a, int pts) {
        assign = a;
        late = true;
        points = pts;
        courseNum = assign.getCourseNum();
        sectionNum = assign.getCourseSection();
        slogin = "";
        aid = a.getAid();
        instructor = false;
    }

    public Grade(Assignment a, int pts, String login, boolean tardy, boolean instr) {
        assign = a;
        late = tardy;
        points = pts;
        courseNum = assign.getCourseNum();
        sectionNum = assign.getCourseSection();
        slogin = login;
        aid = a.getAid();
        instructor = instr;
    }

    public Grade(Assignment a, int pts, String login) {
        assign = a;
        late = true;
        points = pts;
        courseNum = assign.getCourseNum();
        sectionNum = assign.getCourseSection();
        slogin = login;
        aid = a.getAid();
        instructor = false;
    }

    public Grade(Assignment a, int pts, String login, boolean instr) {
        assign = a;
        late = true;
        points = pts;
        courseNum = assign.getCourseNum();
        sectionNum = assign.getCourseSection();
        slogin = login;
        aid = a.getAid();
        instructor = instr;
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
        if(!instructor)
            return "{" + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"total\":" + "\"" + getTotalpts() + "\"" + ", " + "\"course\":" + "\"" + courseNum + "\""
                + ", " + "\"section\":" + "\"" + getSectionNum() + "\""
                + ", " + "\"assignment\":" + "\"" + getTitle() + "\""
                + ", " + "\"late\":" + "\"" + late + "\""
                + ", " + "\"aid\":" + "\"" + assign.getAid() + "\"" +"}";

        return "{" + "\"points\":" + "\"" + points + "\"" + ", "
                + "\"total\":" + "\"" + getTotalpts() + "\"" + ", " + "\"course\":" + "\"" + courseNum + "\""
                + ", " + "\"section\":" + "\"" + getSectionNum() + "\""
                + ", " + "\"assignment\":" + "\"" + getTitle() + "\""
                + ", "+ "\"slogin\":" + "\"" + slogin
                + ", " + "\"late\":" + "\"" + late + "\""
                + ", " + "\"aid\":" + "\"" + assign.getAid() + "\"" +"}";
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
    public void setInstructor(Boolean value){
        instructor = value;
    }
}
