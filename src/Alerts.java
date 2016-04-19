import com.google.gson.Gson;


static int alertLevel()    {
        //add open and close to set json str

        String json =
        "{"
        + "'title': 'Computing and Information systems',"
        + "'id' : 1,"
        + "'children' : 'true',"
        + "'groups' : [{"
        + "'title' : 'Level one CIS',"
        + "'id' : 2,"
        + "'children' : 'true',"
        + "'groups' : [{"
        + "'title' : 'Intro To Computing and Internet',"
        + "'id' : 3,"
        + "'children': 'false',"
        + "'groups':[]"
        + "}]"
        + "}]"
        + "}";


        //String jsonStr = new Scanner(new File("/data/jdoe35/assign.json")).useDelimiter("\\Z").next(); // FIX THIS TO CHANGE USERNAME APPROPRIATELY



        //String jsonStr = "{\"status\": \"OK\",\"origin_addresses\": [ \"Vancouver, BC, Canada\", \"Seattle, État de Washington, États-Unis\" ],\"destination_addresses\": [ \"San Francisco, Californie, États-Unis\", \"Victoria, BC, Canada\" ],\"rows\": [ {\"elements\": [ {\"status\": \"OK\",\"duration\": {\"value\": 340110,\"text\": \"3 jours 22 heures\"},\"distance\": {\"value\": 1734542,\"text\": \"1 735 km\"}}, {\"status\": \"OK\",\"duration\": {\"value\": 24487,\"text\": \"6 heures 48 minutes\"},\"distance\": {\"value\": 129324,\"text\": \"129 km\"}} ]}, {\"elements\": [ {\"status\": \"OK\",\"duration\": {\"value\": 288834,\"text\": \"3 jours 8 heures\"},\"distance\": {\"value\": 1489604,\"text\": \"1 490 km\"}}, {\"status\": \"OK\",\"duration\": {\"value\": 14388,\"text\": \"4 heures 0 minutes\"},\"distance\": {\"value\": 135822,\"text\": \"136 km\"}} ]} ]}";

        /*try {
            JSONObject rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject
            JSONArray rows = rootObject.getJSONArray("rows"); // Get all JSONArray rows

            for(int i=0; i < rows.length(); i++) { // Loop over each each row
                JSONObject row = rows.getJSONObject(i); // Get row object
                JSONArray elements = row.getJSONArray("elements"); // Get all elements for each row as an array

                for(int j=0; j < elements.length(); j++) { // Iterate each element in the elements array
                    JSONObject element =  elements.getJSONObject(j); // Get the element object
                    JSONObject duration = element.getJSONObject("duration"); // Get duration sub object
                    JSONObject distance = element.getJSONObject("distance"); // Get distance sub object

                    System.out.println("Duration: " + duration.getInt("value")); // Print int value
                    System.out.println("Distance: " + distance.getInt("value")); // Print int value
                }
            }
        } catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }*/
        Data data = new Gson().fromJson(json, Data.class);
        System.out.println(data);
        return 1;
        }

class Data {
    private String title;
    private Long id;
    private Boolean children;
    private List<Data> groups;

    public String getTitle() { return title; }
    public Long getId() { return id; }
    public Boolean getChildren() { return children; }
    public List<Data> getGroups() { return groups; }

    public void setTitle(String title) { this.title = title; }
    public void setId(Long id) { this.id = id; }
    public void setChildren(Boolean children) { this.children = children; }
    public void setGroups(List<Data> groups) { this.groups = groups; }

    public String toString() {
        return String.format("title:%s,id:%d,children:%s,groups:%s", title, id, children, groups);
    }
}

    int main()
    {
        alertLEvel();
        return 0;
    }
