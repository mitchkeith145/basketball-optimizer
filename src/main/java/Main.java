/**
 * Created by mitch on 11/23/16.
 */
//import spark.TemplateEngine.FreeMarkerEngine;
import java.io.*;

import static spark.Spark.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import spark.ModelAndView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.csv.CSVParser;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;
import optimizer.helpers.Player;
import optimizer.helpers.Team;
import optimizer.helpers.TeamOptimizer;


public class Main {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
        get("/doUpload", (req, res) -> {
            return "<!DOCTYPE html>\n" +
                    "<html><head><title>Upload form</title></head>\n" +
                    "<body>\n" +
                    "\t<h3>Upload form</h3>\n" +
                    "\t<form action=\"/upload\" enctype=\"multipart/form-data\" method=\"post\">\n" +
                    "\t\t<label for=\"title\">Title</label>\n" +
                    "\t\t<input type=\"text\" name=\"title\"><br>\n" +
                    "\t\t<label for=\"upfile\">File to send</label>\n" +
                    "\t\t<input type=\"file\" name=\"upfile\"><br>\n" +
                    "\t\t<input type=\"submit\" value=\"Upload\">\n" +
                    "\t</form>\n" +
                    "</body>\n" +
                    "</html>";
        });
        post("/upload", "multipart/form-data", (request, response) -> {

            String location = "image";          // the directory location where files will be stored
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            Collection<Part> parts = request.raw().getParts();

            for (Part part : parts) {
                System.out.println("Name: " + part.getName());
                System.out.println("Size: " + part.getSize());
                System.out.println("Filename: " + part.getSubmittedFileName());
            }
            System.out.println("After for...");
            String fName = request.raw().getPart("upfile").getSubmittedFileName();
            String fTitle = request.raw().getParameter("title");
            System.out.println("Title: " + fTitle);
            System.out.println("File: " + fName);

            Part uploadedFile = request.raw().getPart("upfile");
            Path out = Paths.get("uploads/" + fTitle + "-" + fName);
            try (final InputStream in = uploadedFile.getInputStream()) {
                System.out.println("1");
                Files.copy(in, out);
                System.out.println("2");
                uploadedFile.delete();
                System.out.println("3");
            }
            catch (Exception e) {
                System.out.println("Failed to copy uploaded file.");
                System.out.println(e.getMessage());
            }
            System.out.println("4");

            multipartConfigElement = null;
            parts = null;
            uploadedFile = null;

            File csvData = new File("uploads/" + fTitle + "-" + fName );
            String plain = "";
            try {
                System.out.println("Team optimization started...");
                long startTime = System.currentTimeMillis();


                TeamOptimizer optimizer = new TeamOptimizer("uploads/" + fTitle + "-" + fName);
                List<Team> topTeams = optimizer.FindBestTeams(50000, 7, 100);
                long endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Team optimization finished in " + (totalTime * 0.001) + " seconds.");
                String page = "<!DOCTYPE html>\n" +
                        "<html><head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\"><title>Best Teams</title></head>\n" +
                        "<body>\n";
                int i = 0;
                for (Team team : topTeams) {
                    page += team.Show(i);
//                    team.Show(i);
                    i++;
                }
                page += "</body>\n" +
                        "</html>";
                return page;
            }
            catch (IOException e) {
                System.out.println("Exception Found:");
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }

            return "OK";
        });
    }
}
