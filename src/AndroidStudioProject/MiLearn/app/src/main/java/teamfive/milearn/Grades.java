package teamfive.milearn;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

public class Grades {
    private Vector<Grade> gradeList;
    public File json;
    public Grades() {
        gradeList = new Vector<Grade>();
    }

    public Grades(Vector<Grade> g){
        gradeList = new Vector<Grade>();
        gradeList = g;
    }

    public void addGrade(Grade g){
        gradeList.add(g);
    }

    public void removeGrade(Assignment a){
        gradeList.removeElement(a);
    }
    public String toString(){
        String temp = "", begin = "{\"Grades\":[\n", end = "\n]}";

        for(int i = 0; i < gradeList.size(); i++) {
            if(i != 0)
                temp += ",";
            temp +=gradeList.get(i).toString() + "\n";
        }
        temp = temp.replace("[","");
        temp = temp.replace("]","");
        temp = temp.trim();
        return begin + temp + end;
    }

    public void createJSON_File(){
        try{
            PrintWriter file = new PrintWriter("/data/data/teamfive.milearn/files/Grades.json");
            file.println(this.toString());
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
        init_file();
    }

    private void init_file(){
        json = new File("Grades.json");
    }
}