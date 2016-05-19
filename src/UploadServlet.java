import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.postgresql.Driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
public class UploadServlet extends HttpServlet {

    private final static Logger LOGGER =
            Logger.getLogger(UploadServlet.class.getCanonicalName());

    protected void doPost(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Create path components to save the file
        final String path = "/var/lib/tomcat8/webapps/ROOT/data/";//request.getParameter("destination");
        final Part filePart = request.getPart("file");
        final String fileName = getFileName(filePart);

        OutputStream out = null;
        InputStream filecontent = null;

        try {
            /* Write the file on the Tomcat server */
            out = new FileOutputStream(new File(path + File.separator
                    + fileName));
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            /* File has been uploaded on the Tomcat server */

            /* Write submission into the database (and get the number of submissions) */
            DriverManager.registerDriver(new Driver());
            Connection connection = null;

            /* Connect to the database that is stored on AWS' RDS */
            String dbURL = "jdbc:postgresql://dbmilearn.c8o8famsdyyy.us-west-2.rds.amazonaws.com:5432/dbmilearn";
            String user = "group5";
            String pass = "cs180group5";

            connection = DriverManager.getConnection(dbURL, user, pass); /* Now connected! */

            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM Submissions S, Usr U WHERE S.aid = ? AND U.unetid = ? AND S.uid = U.uid");
            st.setInt(1, Integer.parseInt(request.getParameter("aid")));
            st.setString(2, request.getParameter("slogin"));

            ResultSet rs = st.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();

            if (count == 1) /* The user has already submitted a file */
            {
                st = connection.prepareStatement("UPDATE Submissions S SET stime = ?, attempts = attempts + 1 FROM Usr U WHERE S.aid = ? AND U.unetid = ? AND S.uid = U.uid;");
                st.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
                st.setInt(2, Integer.parseInt(request.getParameter("aid")));
                st.setString(3, request.getParameter("slogin"));
                st.executeUpdate();
            }
            else
            {
                st = connection.prepareStatement("INSERT INTO Submissions(aid, uid, stime, attempts) VALUES (?, ?, ?, 1)");
                st.setInt(1, Integer.parseInt(request.getParameter("aid")));
                PreparedStatement tmp = connection.prepareStatement("SELECT U.uid FROM Usr U WHERE U.unetid = ?");
                tmp.setString(1, request.getParameter("slogin"));
                rs = tmp.executeQuery();
                rs.next();
                int uid = rs.getInt("uid");
                st.setInt(2, uid);
                st.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
                st.executeUpdate();
            }
            /* Wrote submission into the database */
            System.out.println("Wrote into the database!");

            /* Rename file */
            if (filecontent != null) {
                filecontent.close();
            }
            String extension = "." + fileName.toString().substring(fileName.toString().indexOf(".") + 1, fileName.toString().length());
            PreparedStatement tmp = connection.prepareStatement("SELECT C.cnum, C.csection, C.quarter, S.attempts, S.uid FROM Submissions S, Usr U, Class C, Assignments A WHERE S.aid = ? AND A.aid = S.aid AND C.cid = A.cid AND U.unetid = ? AND S.uid = U.uid");
            tmp.setInt(1, Integer.parseInt(request.getParameter("aid")));
            tmp.setString(2, request.getParameter("slogin"));
            rs = tmp.executeQuery();
            rs.next();
            int attempts = rs.getInt("attempts");
            int uid = rs.getInt("uid");
            String classC = rs.getString("cnum").trim() + "-" + rs.getString("csection").trim();


            String newName = request.getParameter("aid") + "_" + uid + "_" + attempts + extension;
            File f = null;
            File f1 = null;
            f = new File(path + File.separator
                    + fileName);
            f1 = new File(path + File.separator
                    + newName);
            f.renameTo(f1);
            /* File renamed */
            System.out.println("File renamed!");

            /* Upload to S3 */
            String pathS3 = "data/classes/" + rs.getString("cquarter") + "/" + classC + "/" + newName;

            String bucketName = "milearn";
            AWSCredentials credentials = new BasicAWSCredentials("AKIAJWYCYKZJ3BZ5XEBA", "NGJuCS16bH3R6ywlJf7m2NSmdTPd0yA0qANIUDkM");

            new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3,
                    new File(path + File.separator
                            + newName)));



            /* Redirect to the static website */
            String site = new String("http://milearn.s3-website-us-west-2.amazonaws.com/");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", site);

            rs.close();
            st.close();

        } catch (Exception e) {
            e.getMessage();

            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
                    new Object[]{e.getMessage()});
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}