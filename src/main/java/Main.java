/**
 * Created by mitch on 11/23/16.
 */
//import spark.TemplateEngine.FreeMarkerEngine;
import java.io.*;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import optimizer.helpers.MonsterParser;
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
import spark.Spark;
import org.json.*;

public class Main {
    public static void main(String[] args) {
        Spark.staticFiles.location("/public");
        Spark.port(8080);

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

        post("/parse", "multipart/form-data", (request, response) -> {
            String location = "uploads";          // the directory location where files will be stored
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            Collection<Part> parts = request.raw().getParts();

            String[] fileList = new String[parts.size()];
            int i = 0;
            for (Part part : parts) {
                System.out.println("Name: " + part.getName());
                System.out.println("Size: " + part.getSize());
                System.out.println("Filename: " + part.getSubmittedFileName());
                fileList[i] = part.getSubmittedFileName();
                i++;
            }

            Part uploadedFile = null;
            String fName = "", fTitle = "";
            try {
                fName = request.raw().getPart("file").getSubmittedFileName();
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";

            }
            catch( Exception e) {
                fName = fileList[0];
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";
            }

            System.out.println("File: " + fName);
            System.out.println("Title: " + fTitle);
            uploadedFile = request.raw().getPart("file");
            System.out.println("1");
            Path out = Paths.get("uploads/" + fTitle + "-" + fName);
            System.out.println("2");

            try (final InputStream in = uploadedFile.getInputStream()) {
                System.out.println("2-1");
                Files.copy(in, out);
                uploadedFile.delete();
                System.out.println("2-2");
            }
            catch (Exception e) {
                System.out.println("Failed to copy uploaded file.");
                System.out.println(e.getMessage());
            }

            multipartConfigElement = null;
            parts = null;
            uploadedFile = null;


            try {
                System.out.println("3");
                MonsterParser parser = new MonsterParser("uploads/" + fTitle + "-" + fName);
                List<List<Player>> playerLists = parser.getLists();
                JsonNodeFactory factory = JsonNodeFactory.instance;
                String data = "{";
                String[] positions = new String[] { "PG", "SG", "G", "SF", "PF", "F", "C", "UTIL" };
                int listCount = 0;
                for (List<Player> list : playerLists) {
                    data += "\"" + positions[listCount] + "\":[";
                    int playerCount = 0;
                    for (Player p : list) {
                        data += p.toJson();
                        if (playerCount < list.size() - 1) {
                            data += ",";
                        }
                        playerCount++;
                    }
                    if (listCount < positions.length - 1) {
                        data += "],";
                    }
                    else {
                        data += "]";
                    }
                    listCount++;
                }
                data += "}";
                System.out.println(data);
                return data;
            }
            catch (IOException e) {
                System.out.println("Exception Found:");
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
                return "Error";
            }
        });

        post("/testPath", "multipart/form-data", (request, response) -> {
            return "Yes";
        });

        post("/parseIdCsv", "multipart/form-data", (request, response) -> {
            String location = "uploads";          // the directory location where files will be stored
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            Collection<Part> parts = request.raw().getParts();

            String[] fileList = new String[parts.size()];
            int i = 0;
            for (Part part : parts) {
                System.out.println("Name: " + part.getName());
                System.out.println("Size: " + part.getSize());
                System.out.println("Filename: " + part.getSubmittedFileName());
                fileList[i] = part.getSubmittedFileName();
                i++;
            }
            System.out.println("After for...");
//            try {
//
//            }
//            catch (Exception e) {
//
//            }
            Part uploadedFile = null;
            String fName = "", fTitle = "";
            try {
                fName = request.raw().getPart("file").getSubmittedFileName();
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";

            }
            catch( Exception e) {
                fName = fileList[0];
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";
            }

            System.out.println("File: " + fName);
            System.out.println("Title: " + fTitle);
            uploadedFile = request.raw().getPart("file");
            System.out.println("1");
            Path out = Paths.get("uploads/" + fTitle + "-" + fName);
            System.out.println("2");

            try (final InputStream in = uploadedFile.getInputStream()) {
                Files.copy(in, out);
                uploadedFile.delete();
            }
            catch (Exception e) {
                System.out.println("Failed to copy uploaded file.");
                System.out.println(e.getMessage());
            }

            MonsterParser parser = new MonsterParser();
            Map<String, String> list = parser.parseIdCsv("uploads/" + fTitle + "-" + fName);
//            String data = "{\"players\":[";
//            int playerCount = 0;
//            for (Player p : completeList) {
//                data += p.toJson();
//                if (playerCount < completeList.size() - 1) {
//                    data += ",";
//                }
//                playerCount++;
//            }
//            data += "]}";
            String data = "{";
            int keyCount = 0;
            for (String key : list.keySet()) {
                data += "\"" + key + "\":" + "\"" + list.get(key) + "\"";
                if (keyCount < list.keySet().size() - 1) {
                    data += ",";
                }
                keyCount++;
            }
            data += "}";
            return data;
        });

        post("/parseCsv", "multipart/form-data", (request, response) -> {
            String location = "uploads";          // the directory location where files will be stored
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            Collection<Part> parts = request.raw().getParts();

            String[] fileList = new String[parts.size()];
            int i = 0;
            for (Part part : parts) {
                System.out.println("Name: " + part.getName());
                System.out.println("Size: " + part.getSize());
                System.out.println("Filename: " + part.getSubmittedFileName());
                fileList[i] = part.getSubmittedFileName();
                i++;
            }
            System.out.println("After for...");
//            try {
//
//            }
//            catch (Exception e) {
//
//            }
            Part uploadedFile = null;
            String fName = "", fTitle = "";
            try {
                fName = request.raw().getPart("file").getSubmittedFileName();
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";

            }
            catch( Exception e) {
                fName = fileList[0];
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";
            }

            System.out.println("File: " + fName);
            System.out.println("Title: " + fTitle);
            uploadedFile = request.raw().getPart("file");
            System.out.println("1");
            Path out = Paths.get("uploads/" + fTitle + "-" + fName);
            System.out.println("2");

            try (final InputStream in = uploadedFile.getInputStream()) {
                Files.copy(in, out);
                uploadedFile.delete();
            }
            catch (Exception e) {
                System.out.println("Failed to copy uploaded file.");
                System.out.println(e.getMessage());
            }

            multipartConfigElement = null;
            parts = null;
            uploadedFile = null;


            try {
                MonsterParser parser = new MonsterParser("uploads/" + fTitle + "-" + fName);
                List<Player> completeList = parser.parseCsv("uploads/" + fTitle + "-" + fName);
                String data = "{\"players\":[";
                int playerCount = 0;
                for (Player p : completeList) {
                    data += p.toJson();
                    if (playerCount < completeList.size() - 1) {
                        data += ",";
                    }
                    playerCount++;
                }
                data += "]}";
                return data;
//
//                String data = "{";
//                String[] positions = new String[] { "PG", "SG", "G", "SF", "PF", "F", "C", "UTIL" };
//                int listCount = 0;
//                for (List<Player> list : playerLists) {
//                    data += "\"" + positions[listCount] + "\":[";
//                    int playerCount = 0;
//                    for (Player p : list) {
//                        data += p.toJson();
//                        if (playerCount < list.size() - 1) {
//                            data += ",";
//                        }
//                        playerCount++;
//                    }
//                    if (listCount < positions.length - 1) {
//                        data += "],";
//                    }
//                    else {
//                        data += "]";
//                    }
//                    listCount++;
//                }
//                data += "}";
//                System.out.println(data);
//                return data;
            }
            catch (IOException e) {
                System.out.println("Exception Found:");
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
                return "Error";
            }
        });



        post("/generate", "application/json", (request, response) -> {
            /**
             * JSONObject jsonObj = new JSONObject(json);
             Name = jsonObj.get("name").toString();
             Position = jsonObj.get("position").toString();
             Ratio = Double.parseDouble(jsonObj.get("ratio").toString());
             Salary = Integer.parseInt(jsonObj.get("salary").toString());
             PredictedMinutes = Integer.parseInt(jsonObj.get("min").toString());
             expectedPoints = Double.parseDouble(jsonObj.get("pts").toString());
             Rank = Integer.parseInt(jsonObj.get("rank").toString());
             ValueRatio = Double.parseDouble(jsonObj.get("value_ratio").toString());
             */

            String body = request.body();
            System.out.println(body);
            JSONObject playerList = new JSONObject(body);
            JSONArray lists = playerList.getJSONArray("lists");
            System.out.println("Num of lists: " + lists.length());
            String[] positions = new String[]{"PG", "SG", "G", "SF", "PF", "F", "C", "UTIL"};
            List<List<Player>> playerLists = new ArrayList<>();
            for (int i = 0; i < lists.length(); i++) {
                List<Player> positionList = new ArrayList<Player>();
                for (int j = 0; j < lists.getJSONArray(i).length(); j++) {
                    positionList.add(new Player(lists.getJSONArray(i).get(j).toString()));
                }
                playerLists.add(positionList);
            }

            int count = 0;
            for (List<Player> positionList : playerLists) {
                System.out.println("===== " + positions[count] + " List =====");
                count++;
                for (Player player : positionList) {
                    player.Show();
                }
            }
            System.out.println("Team optimization started...");
            long startTime = System.currentTimeMillis();
            TeamOptimizer optimizer = new TeamOptimizer(playerLists);
            Set<Team> topTeams = optimizer.FindBestTeams(50000, 7, 50000);

            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Got " + topTeams.size() + " total teams.");
            System.out.println("Team optimization finished in " + (totalTime * 0.001) + " seconds.");

//            JSONObject root = new JSONObject();
//            JSONArray teams = new JSONArray();
//            for (Team team : topTeams) {
//                teams.put(new JSONObject(team.toJson()));
//            }
//            root.append("teams", teams);
            int teamCount = 0;
            String json = "{\"teams\":[";

            for (Team team : topTeams) {
                json += team.toJson();
                if (teamCount == 250) {
                    json += "]}";
                    return json;
                }
                if (teamCount < topTeams.size() - 1) {
                    json += ",";
                }
                teamCount++;
            }
            json += "]}";

            return json;
        });

        post("/upload", "multipart/form-data", (request, response) -> {

            String location = "uploads";          // the directory location where files will be stored
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            Collection<Part> parts = request.raw().getParts();

            String[] fileList = new String[parts.size()];
            int i = 0;
            for (Part part : parts) {
                System.out.println("Name: " + part.getName());
                System.out.println("Size: " + part.getSize());
                System.out.println("Filename: " + part.getSubmittedFileName());
                fileList[i] = part.getSubmittedFileName();
                i++;
            }
            System.out.println("After for...");
//            try {
//
//            }
//            catch (Exception e) {
//
//            }
            Part uploadedFile = null;
            String fName = "", fTitle = "";
            try {
                fName = request.raw().getPart("file").getSubmittedFileName();
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";

            }
            catch( Exception e) {
                fName = fileList[0];
                fTitle = request.raw().getParameter("title");
                fTitle = fTitle.length() > 0 ? fTitle : "temp";
            }

            System.out.println("File: " + fName);
            System.out.println("Title: " + fTitle);
            uploadedFile = request.raw().getPart("file");
            System.out.println("1");
            Path out = Paths.get("uploads/" + fTitle + "-" + fName);
            System.out.println("2");

            try (final InputStream in = uploadedFile.getInputStream()) {
                Files.copy(in, out);
                uploadedFile.delete();
            }
            catch (Exception e) {
                System.out.println("Failed to copy uploaded file.");
                System.out.println(e.getMessage());
            }

            multipartConfigElement = null;
            parts = null;
            uploadedFile = null;

            File csvData = new File("uploads/" + fTitle + "-" + fName );
            String plain = "";
            try {
                System.out.println("Team optimization started...");
                long startTime = System.currentTimeMillis();


                TeamOptimizer optimizer = new TeamOptimizer("uploads/" + fTitle + "-" + fName);
                Set<Team> topTeams = optimizer.FindBestTeams(50000, 7, 1000);
                long endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Got " + topTeams.size() + " total teams.");
                System.out.println("Team optimization finished in " + (totalTime * 0.001) + " seconds!!!");
                String page = "<!DOCTYPE html>\n" +
                        "<html><head><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\"><title>Best Teams</title></head>\n" +
                        "<body>\n";
                i = 0;
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
