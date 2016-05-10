import java.io.PrintWriter;
import java.util.ArrayList;

public class TA extends User
{
        public TA(String name, String netid, int sid, ArrayList<Class> classes, ArrayList<Class> taught)
        {
            this.name = name;
            this.netid = netid;
            this.sid = sid;
            this.rank = Rank.TA;
            this.classes = (ArrayList<Class>) classes.clone();
            this.taught = (ArrayList<Class>) taught.clone();
            this.writeJson();

            /* Writes course.json as soon as a new instructor is created */
            try{
                PrintWriter file = new PrintWriter("course.json");
                file.println("[");
                for (int i = 0; i < this.getClasses().size(); i++)
                {
                    file.println("{\"courseName\":" + "\"" + this.getClasses().get(i).getName() + "\", " + "\"courseNumber\":\"" + this.getClasses().get(i).getNumber() +
                            "\", \"quarter\":\"" + this.getClasses().get(i).getQuarter() + "\"}");
                }
                file.println("]");
                file.close();
            }catch(Exception e) {
                System.out.println("ERROR");
                e.printStackTrace();
            }
        }

        public ArrayList<Class> getTaught()
        {
            return this.taught;
        }

        public void setTaught(ArrayList<Class> taught)
        {
            this.taught = (ArrayList<Class>) taught.clone();
        }

        public Object clone()
        {
            return new TA(getName(), getNetid(), getSid(), getClasses(), getTaught());
        }

        private ArrayList<Class> taught;
}

