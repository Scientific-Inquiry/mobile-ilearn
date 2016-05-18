package teamfive.milearn;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

public class TA extends User
{
        public TA(String name, String netid, int sid, ArrayList<Class> classes, ArrayList<Class> taught, ArrayList<ArrayList<Vector>> snames)
        {
            this.name = name;
            this.netid = netid;
            this.sid = sid;
            this.rank = Rank.TA;
            this.classes = (ArrayList<Class>) classes.clone();
            this.taught = (ArrayList<Class>) taught.clone();
            this.snames = (ArrayList<ArrayList<Vector>>) snames.clone();
            this.writeJson("classes.json", Rank.STUDENT, getSnames());

            /* Writes course.json as soon as a new instructor is created */
            try{
                PrintWriter file = new PrintWriter("course.json");
                file.println("[");
                for (int i = 0; i < this.getTaught().size(); i++)
                {
                    for (int j = 0; j < snames.get(i).size(); j++)
                    {
                        if (i != this.getClasses().size()-1)
                            file.println(classes.get(i).toString(rank, snames.get(i).get(j)) + ",");
                        else
                            file.println(classes.get(i).toString(rank, snames.get(i).get(j)));
                    }
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
            return new TA(getName(), getNetid(), getSid(), getClasses(), getTaught(), getSnames());
        }

        private ArrayList<Class> taught;
}

