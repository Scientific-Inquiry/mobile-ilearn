package teamfive.milearn;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class S3Manager {
    //data for curr_quarter
    public static int sMonth1 = 1, sMonth2 = 3, sMonth3 = 6, sMonth4 = 9;
    public static int sDay1 = 4, sDay2 = 29, sDay3 = 20, sDay4 = 20, eDay1 = 28, eDay2 = 19, eDay3 = 19, eDay4 = 21;

    public static AWSCredentials credentials;
    public static AmazonS3 s3Client;
    public static String bucketName;
    public static String curr_quarter;
    public static String path;

    S3Manager(){
        DateFormat dateFormat = new SimpleDateFormat("dyyyy");
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        if((date.getMonth() >= sMonth1  && date.getMonth() <= sMonth2 && date.getDay() >= sDay1)
                || (date.getMonth() >= sMonth2 && date.getMonth() <= sMonth1 && date.getDay() <= eDay1))
            date.setDate(1);

        else if (((date.getMonth() >= sMonth2 && date.getMonth() <= sMonth3) && date.getDay() >= sDay2)
                || (date.getMonth() >= sMonth2 && date.getMonth() <= sMonth3 && date.getDay() <= eDay2))
            date.setDate(2);

        else if (((date.getMonth() >= sMonth3 && date.getMonth() <= sMonth4) && date.getDay() >= sDay3)
                || (date.getMonth() >= sMonth3 && date.getMonth() <= sMonth4 && date.getDay() >= eDay3))
            date.setDate(3);

        else
            date.setDate(4);

        bucketName = "milearn";
        credentials = new BasicAWSCredentials("AKIAJWYCYKZJ3BZ5XEBA", "NGJuCS16bH3R6ywlJf7m2NSmdTPd0yA0qANIUDkM");
        s3Client = new AmazonS3Client(credentials);
        path = "";
        curr_quarter= dateFormat.format(date);
    }

    //must instantiate path desired with generate path functions
    public String toString(){
        return "Bucket Name: " + bucketName + " | Path: " + path + " | Current Quarter: " + curr_quarter;
    }

    //update Bucket with the your filename
    public void update_Bucket(String fileName){

        //check_Folders();

        if(find_Item()) {
            System.out.println("Found the File: " + path);
            upload_file(fileName);
        }
        else
            System.out.println("File Not Found: " + path);
    }

    //reads the contents of the file
    //can check if updated with this function
    public void read(){
        S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, path));
        //System.out.println(s3object.getObjectMetadata().getContentType());
        //System.out.println(s3object.getObjectMetadata().getContentLength());

        BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
        String line;
        try{
            while((line = reader.readLine()) != null) {
                // can copy the content locally as well
                // using a buffered writer
                System.out.println(line);
            }
        }catch (Exception e){
            System.out.println("Reading failed!");
        }
    }

    //generates path string
    public void generate_Path(String uid, String filename){
        path = "data/users/"+ uid + "/" + filename;
    }

    //generates path string with current time
    public void generate_Path_curr(String classname, String filename){
        path = "data/classes/"+ curr_quarter + "/" + classname + "/" + filename;
    }

    //generates path string specified time
    public void generate_Path(String classname, String qyyyy, String filename){
        path = "data/classes/"+ qyyyy + "/" + classname + "/" + filename;
    }

    //returns true if file exists
    //set path correctly before using
    public boolean find_Item(){
        boolean isValidFile = true;
        try {
            ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucketName, path);
        } catch (AmazonS3Exception s3e) {
            if (s3e.getStatusCode() == 404) {
                isValidFile = false;
            }
            else {
                throw s3e;
            }
        }

        return isValidFile;
    }

    //creates a folder at root if path is empty
    //otherwise creates a folder at the path
    public void create_Folder(String folderName) {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
               path + "/" + folderName + "/", emptyContent, metadata);
        // send request to S3 to create folder
        s3Client.putObject(putObjectRequest);
    }

    //uploads a file on the prefix specified by path
    public void upload_file(String fileName){
        s3Client.putObject(new PutObjectRequest(bucketName, path,
                new File(fileName)));
    }

    //prints folders in bucket from root
    //prints folders which match the path if path is used instead of folderName
    public void check_Folders(String folderName) {
        ObjectListing objects = s3Client.listObjects(bucketName, folderName);
        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
        }
        /*
        try {
            System.out.println("Listing objects");
            ListObjectsRequest lor = new ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withPrefix(prefix);
            ObjectListing objectListing = s3Client.listObjects(lor);
            for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error with processing folders");
        }
        */
    }

    //prints buckets in s3
    public void check_Buckets(){
        for (Bucket bucket : s3Client.listBuckets()) {
            bucketName = bucket.getName();
            System.out.println("Updating - " + bucket.getName()); // Lists the buckets
        }
    }

    //delete objects/folder recursively
    //be careful~!
    void delete_Object(String folderPath) {
        for (S3ObjectSummary file : s3Client.listObjects(bucketName, folderPath).getObjectSummaries()){
            s3Client.deleteObject(bucketName, file.getKey());
        }
    }

}
