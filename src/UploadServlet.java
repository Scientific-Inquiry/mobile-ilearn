import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

            /* Rename file */
            if (filecontent != null) {
                filecontent.close();
            }
            String extension = "." + fileName.toString().substring(fileName.toString().indexOf(".") + 1, fileName.toString().length());
            String newName = "renamed_file" + extension;
            File f = null;
            File f1 = null;
            f = new File(path + File.separator
                    + fileName);
            f1 = new File(path + File.separator
                    + newName);
            f.renameTo(f1);
            /* File renamed */

            /* Upload to S3 */
            String pathS3 = "testUpload/Class/Assignment/" + newName;

            String bucketName = "milearn";
            AWSCredentials credentials = new BasicAWSCredentials("AKIAJWYCYKZJ3BZ5XEBA", "NGJuCS16bH3R6ywlJf7m2NSmdTPd0yA0qANIUDkM");

            new AmazonS3Client(credentials).putObject(new PutObjectRequest(bucketName, pathS3,
                    new File(path + File.separator
                            + newName)));

            /*File file = new File("classes.json");
            String path = file.getAbsolutePath();
            System.out.println(path);
            s3.path = "testCandice/user/" + this.getUsername() + "/classes.json";
            s3.upload_file(path);
            file.delete();*/
            /* Uploaded to S3 */

            /* Write submission into the database */

            /* Wrote submission into the database */

        } catch (FileNotFoundException fne) {
            fne.getMessage();

            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
                    new Object[]{fne.getMessage()});
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