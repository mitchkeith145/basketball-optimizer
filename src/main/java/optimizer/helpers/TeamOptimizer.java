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

public class TeamOptimizer {
    private static int MaxLevel = 8;

    private int TopTierTeamCount = 20;
    private int OptionsPerPosition = 0;
    private int addCount = 0, secondaryAddCount = 0;
    private List<Team> TopTierTeams = new ArrayList<>();

    // instantiate a TreeSet (ie sorted set) to use a custom comparator, TeamComparator, for sorting
    private TreeSet<Team> sortedTopTierTeams = new TreeSet<>(new TeamComparator());

    private List<Player> PG_List = new ArrayList<>();
    private List<Player> SG_List = new ArrayList<>();
    private List<Player> G_List = new ArrayList<>();
    private List<Player> SF_List = new ArrayList<>();
    private List<Player> PF_List = new ArrayList<>();
    private List<Player> F_List = new ArrayList<>();
    private List<Player> C_List = new ArrayList<>();


    private List<Player> UTIL_List = new ArrayList<>();

    private List<Player> UtilityList = new ArrayList<>();

    private List<List<Player>> PlayerLists = new ArrayList<>();

    public TeamOptimizer(List<List<Player>> lists) {
        PlayerLists = lists;
        PG_List = lists.get(0);
        SG_List = lists.get(1);
        G_List = lists.get(2);
        SF_List = lists.get(3);
        PF_List = lists.get(4);
        F_List = lists.get(5);
        C_List = lists.get(6);
        UTIL_List = lists.get(7);
    }

    public TeamOptimizer(String spreadsheetPath) throws IOException
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

    private void RemoveFromPlayerLists(Player p)
    {
        for (List<Player> playerList : PlayerLists) {
            if (playerList.contains(p)) {
                playerList.remove(p);
            }
        }
    }

    public Player FindPlayer(String partialName)
    {
        for (Player p : this.UtilityList)
        {
            if (p.Name.indexOf(partialName) > -1)
            {
                return p;
            }
        }
        return null;
    }

    public Set<Team> FindBestTeams(int cashLimit, int optionsPerPosition, int topTierCount)
    {
        System.out.println("finding best...");

        List<Player> orderedPlayers = null;
        List<List<Player>> newPlayerLists = new ArrayList<>();
        TopTierTeamCount = topTierCount;

        OptionsPerPosition = optionsPerPosition;
        int i = 0;
        for (List<Player> playerList : PlayerLists)
        {
            //list.sort(Comparator.comparing(a -> a.attr));
            if (i < PlayerLists.size() - 1) {
                playerList.sort(Comparator.comparing(p -> p.ValueRatio));
                Collections.reverse(playerList);
                orderedPlayers = playerList;
            }
            else {
                playerList.sort(Comparator.comparing(p -> p.Ratio));
                Collections.reverse(playerList);
                orderedPlayers = playerList;
            }
            i++;
            newPlayerLists.add(orderedPlayers);
        }
        PlayerLists = newPlayerLists;



        System.out.println("starting recursion...");
        ExploreTeamspace(0, cashLimit, new Team());
        System.out.println("ending recursion...");
        System.out.println("Tried adding " + addCount + " different teams.");
        return this.sortedTopTierTeams;
    }

    private void ExploreTeamspace(int level, int cashLimit, Team teamSoFar)
    {
        if (level == MaxLevel)
        {

            tryToAddToTopTierTeams(teamSoFar);
//            TryAddToTopTierTeams(teamSoFar);
            return;
        }

        int options = this.OptionsPerPosition;
        if (level == 5 || level == 6) options = OptionsPerPosition * 2;
        if (level == 7) options = OptionsPerPosition * 4;

        for (int i = 0; i < options; i++)
        {
            Player newPlayer = PlayerLists.get(level).get(i);
            if (newPlayer.Salary > cashLimit) continue;
            if (teamSoFar.HasPlayer(newPlayer)) continue;
            ExploreTeamspace(level + 1, cashLimit - newPlayer.Salary, ExtendTeam(teamSoFar, newPlayer));
        }
    }
    private Team ExtendTeam(Team teamSoFar, Player newPlayer)
    {
        Team extendedTeam = teamSoFar.Clone();
        extendedTeam.Add(newPlayer);
        return extendedTeam;
    }

    private void tryToAddToTopTierTeams(Team team) {
        addCount++;

        sortedTopTierTeams.add(team.Clone());
        if (sortedTopTierTeams.size() > TopTierTeamCount) {
            sortedTopTierTeams.remove(sortedTopTierTeams.last());
        }

    }

    private void TryAddToTopTierTeams(Team team)
    {
        addCount++;
        int index = 0;

        for (Team topTierTeam : TopTierTeams) {
            if (team.isEquals(topTierTeam))
                return;

            if (team.TotalExpectedPoints() > topTierTeam.TotalExpectedPoints()) {
                TopTierTeams.add(index, team.Clone());
                if (TopTierTeams.size() >= TopTierTeamCount)
                    TopTierTeams.remove(TopTierTeams.size() - 1);
                return;
            }
            index++;
        }
        if (TopTierTeams.size() < TopTierTeamCount) {
            TopTierTeams.add(team.Clone());
        }
    }

    private void TruncatePlayerList(int truncateSize, List<Player> playerList)
    {
        int originalPlayerCount = playerList.size();

        for (int i=truncateSize; i<originalPlayerCount; i++)
        {
            playerList.remove(truncateSize);
        }
    }

    private void RemoveSuperLowScorers(int minPoints, List<Player> playerList)
    {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player p : playerList)
        {
            if (p.expectedPoints < minPoints) {
                playersToRemove.add(p);
            }
        }
        for (Player p : playersToRemove) {
            playerList.remove(p);
        }
    }
    private void RemoveCheapPlayers(int minSalary, List<Player> playerList)
    {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player p : playerList)
        {
            if (p.Salary < minSalary) {
                playersToRemove.add(p);
            }
        }
        for (Player p : playersToRemove) {
            playerList.remove(p);
        }
    }
    private void RemovePlayersWithLowMinutes(int minMinutes, List<Player> playerList)
    {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player p : playerList)
        {
            if (p.PredictedMinutes < minMinutes) {
                playersToRemove.add(p);
            }
        }
        for (Player p : playersToRemove) {
            playerList.remove(p);
        }
    }
}
