package optimizer.helpers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by mitch on 11/23/16.
 */

public class MonsterParser {
    public static int MaxLevel = 8;

    int TopTierTeamCount = 20;
    int OptionsPerPosition = 0;

    List<Team> TopTierTeams = new ArrayList<>();
    List<List<Player>> AllPlayers = new ArrayList<>();

    List<Player> GuardList = new ArrayList<>();
    List<Player> ForwardList = new ArrayList<>();
    List<Player> CenterList = new ArrayList<>();

    List<Player> PG_List = new ArrayList<>();
    List<Player> SG_List = new ArrayList<>();
    List<Player> G_List = new ArrayList<>();
    List<Player> SF_List = new ArrayList<>();
    List<Player> PF_List = new ArrayList<>();
    List<Player> F_List = new ArrayList<>();
    List<Player> C_List = new ArrayList<>();
    public List<Player> UTIL_List = new ArrayList<>();

    public List<Player> UtilityList = new ArrayList<>();

    List<List<Player>> PlayerLists = new ArrayList<>();

    public MonsterParser() {

    }

    public MonsterParser(String spreadsheetPath) throws IOException
    {
        System.out.println("Spreadsheet locale");
        System.out.println(spreadsheetPath);
        File csvData = new File(spreadsheetPath);
        try {
            CSVParser parser = CSVParser.parse(csvData,
                    Charset.defaultCharset(),
                    CSVFormat.RFC4180.withHeader("", "Like", "Rank", "Price", "Ratio", "Value", "CompV1", "Name", "Inj",
                            "Team", "Pos", "g", "Ease", "Rest", "RestA", "m/g", "Opp", "3", "r", "a", "s", "b", "2d",
                            "3d", "to", "pV", "3V", "rV", "aV", "sV", "bV", "2dV", "3dV", "toV"));
            boolean header = true;
            for (CSVRecord csvRecord : parser.getRecords()) {
                if (header) {
                    header = false;
                    continue;
                }

                Player player = new Player(csvRecord);

                // TODO: add next logic to its own function that takes a list of players.
                if (player.Position.contains("/")) {
                    String pos1 = player.Position.split("/")[0];
                    String pos2 = player.Position.split("/")[1];
                    Boolean g = false, f = false;

                    if (pos1.equals("PG") || pos2.equals("PG")) {
                        PG_List.add(player);
                        g = true;
                    }
                    if (pos1.equals("SG") || pos2.equals("SG")) {
                        SG_List.add(player);
                        g = true;
                    }
                    if (pos1.equals("SF") || pos2.equals("SF")) {
                        SF_List.add(player);
                        f = true;
                    }
                    if (pos1.equals("PF") || pos2.equals("PF")) {
                        PF_List.add(player);
                        f = true;
                    }
                    if (pos1.equals("C") || pos2.equals("C")) {
                        C_List.add(player);
                    }

                    if (g) {
                        G_List.add(player);
                    }
                    if (f) {
                        F_List.add(player);
                    }
                }
                else {
                    if (player.Position.equals("PG")) {
                        PG_List.add(player);
                        G_List.add(player);
                    }
                    else if (player.Position.equals("SG")) {
                        SG_List.add(player);
                        G_List.add(player);
                    }
                    else if (player.Position.equals("SF")) {
                        SF_List.add(player);
                        F_List.add(player);
                    }
                    else if (player.Position.equals("PF")) {
                        PF_List.add(player);
                        F_List.add(player);
                    }
                    else if (player.Position.equals("C")) {
                        C_List.add(player);
                    }
                }
                UTIL_List.add(player);

            }
        }
        catch (IOException e) {
            System.out.println("Exception Found:");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        System.out.println("List lengths:");
        System.out.println(PG_List.size());
        System.out.println(SG_List.size());
        System.out.println(G_List.size());
        System.out.println(SF_List.size());
        System.out.println(PF_List.size());
        System.out.println(F_List.size());
        System.out.println(UTIL_List.size());


        PlayerLists.add(PG_List);
        PlayerLists.add(SG_List);
        PlayerLists.add(G_List);
        PlayerLists.add(SF_List);
        PlayerLists.add(PF_List);
        PlayerLists.add(F_List);
        PlayerLists.add(C_List);
        PlayerLists.add(UTIL_List);
    }

    public Map<String, String> parseIdCsv(String spreadsheetPath) {
        System.out.println("parsing the id csv file...");
        File csvData = new File(spreadsheetPath);
        try {
            CSVParser parser = CSVParser.parse(csvData,
                    Charset.defaultCharset(),
                    CSVFormat.RFC4180);
            boolean header = true;
            List<Player> completeList = new ArrayList<>();
            int i = 0;
            boolean recording = false;
            Map<String, String> nameIdMapper = new HashMap<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                System.out.println("Record " + i + ":");
                System.out.println(csvRecord.get(10) + "; " + csvRecord.get(11) + "; " + csvRecord.get(12) + "; " + csvRecord.get(13));
                if ((csvRecord.get(11).length() > 0 && ! csvRecord.get(11).contains("Name")) &&
                        (csvRecord.get(10).length() > 0 && ! csvRecord.get(10).contains("Name + ID"))) {
                    nameIdMapper.put(csvRecord.get(11), csvRecord.get(10));
                }
                i++;
            }
            return nameIdMapper;
        }
        catch (IOException e) {
            System.out.println("Exception Found:");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return null;
        }
    }

    public List<Player> parseCsv(String spreadsheetPath) {
        File csvData = new File(spreadsheetPath);
        try {
            CSVParser parser = CSVParser.parse(csvData,
                    Charset.defaultCharset(),
                    CSVFormat.RFC4180.withHeader("", "Like", "Rank", "Price", "Ratio", "Value", "CompV1", "Name", "Inj",
                            "Team", "Pos", "g", "Ease", "Rest", "RestA", "m/g", "Opp", "3", "r", "a", "s", "b", "2d",
                            "3d", "to", "pV", "3V", "rV", "aV", "sV", "bV", "2dV", "3dV", "toV"));
            boolean header = true;
            List<Player> completeList = new ArrayList<>();

            for (CSVRecord csvRecord : parser.getRecords()) {
                if (header) {
                    header = false;
                    continue;
                }

                Player player = new Player(csvRecord);
                completeList.add(player);
            }
            return completeList;
        }
        catch (IOException e) {
            System.out.println("Exception Found:");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return null;
        }
    }

    public List<List<Player>> getLists() {
        return PlayerLists;
    }
}
