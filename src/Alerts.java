import com.google.gson.Gson;
import java.util.Date;


//Returns 0 if no alert, 1 if assignment due within 7 days, 2 if assignment due within 3 days, 3 if assignment due within 24 hours
//Need to add parameter to find for specific assignment name maybe.
static int alertLevel()    {

        String jsonStr = new Scanner(new File("/data/jdoe35/assign.json")).useDelimiter("\\Z").next(); // FIX THIS TO CHANGE USERNAME APPROPRIATELY

        //String jsonStr = "{\"status\": \"OK\",\"origin_addresses\": [ \"Vancouver, BC, Canada\", \"Seattle, État de Washington, États-Unis\" ],\"destination_addresses\": [ \"San Francisco, Californie, États-Unis\", \"Victoria, BC, Canada\" ],\"rows\": [ {\"elements\": [ {\"status\": \"OK\",\"duration\": {\"value\": 340110,\"text\": \"3 jours 22 heures\"},\"distance\": {\"value\": 1734542,\"text\": \"1 735 km\"}}, {\"status\": \"OK\",\"duration\": {\"value\": 24487,\"text\": \"6 heures 48 minutes\"},\"distance\": {\"value\": 129324,\"text\": \"129 km\"}} ]}, {\"elements\": [ {\"status\": \"OK\",\"duration\": {\"value\": 288834,\"text\": \"3 jours 8 heures\"},\"distance\": {\"value\": 1489604,\"text\": \"1 490 km\"}}, {\"status\": \"OK\",\"duration\": {\"value\": 14388,\"text\": \"4 heures 0 minutes\"},\"distance\": {\"value\": 135822,\"text\": \"136 km\"}} ]} ]}";

        Data data = new Gson().fromJson(json, Data.class);
        string dueDate = Data.toString();
        Date current = new Date();
        Date dueD;
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE MMM dd HH:mm:ss z yyyy");

        try {
        dueD = formatter.parse(dueDate);
        }
        catch (ParseException e) {
        e.printStackTrace();
        }

        long difference = dueD.getTime() - current.getTime();
        double daysLeft = difference/(1000.0 * 60.0 * 60.0 * 24.0);
        if(daysLeft > 7 )
            return 0;
        else if (daysLeft <= 7 && daysLeft >= 3)
            return 1;
        else if (daysLeft >= 1 && daysLeft <= 3)
            return 2;
        else if (daysLeft <= 1)
            return 3;
}

static boolean isNotInstructor(User u)  {
    u.getRank().equals("STUDENT") ? return true : return false;
}

//Need methods to retrieve students list to send messages to
static void instructorMessage(String str, User u) {
    //Make sure user is an instructor, if not an instructor then quit
    (isNotInstructor(u)) ? return : sendMessage(str);
    return;
    
}

private static void sendMessage(String str)  {
    //List of students function needed
    List<String> arrayList = new ArrayList<String>();
    retriveStudents(arrayList);
    for(unsigned i = 0; i < arrayList.size(); i++) 
        pushAlert(arrayList.get(i);
}

private static void pushAlert() {
    //Store alert into appropriate student databases
}

class Data {
    private String dueDate;

    public String toString() {
        return dueDate;
    }
}

    /*int main()
    {
        alertLevel();
        return 0;
    }*/
