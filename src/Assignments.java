import java.io.File;
import java.io.PrintWriter;
import java.util.*;//vectors

public class Assignments {
    private Vector<Assignment> assignList;
    public File json;
    public Assignments() {
        assignList = new Vector<Assignment>();
    }

    public Assignments(Vector<Assignment> a){
        assignList = new Vector<Assignment>();
        assignList = a;
    }

    public void addAssign(Assignment a){
        assignList.add(a);
    }

    public void removeAssign(Assignment a){
        assignList.removeElement(a);
    }
    public String toString(){
        String temp = "", begin = "[\n", end = "\n]";

        for(int i = 0; i < assignList.size(); i++) {
            if(i != 0)
                temp += ",";
            temp +=assignList.get(i).toString() + "\n";
        }
        temp = temp.replace("[","");
        temp = temp.replace("]","");
        temp = temp.trim();
        return begin + temp + end;
    }

    public void obtain_assignments(){

    }

    public void createJSON_File(){
        try{
            PrintWriter file = new PrintWriter("Assignments.json");
            file.println(this.toString());
            file.close();
        }catch(Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
        init_file();
    }

    private void init_file(){
        json = new File("Assignments.json");
    }
}
