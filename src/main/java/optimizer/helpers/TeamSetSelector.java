package optimizer.helpers;

import org.apache.commons.csv.CSVRecord;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mitch on 11/23/16.
 */
public class TeamSetSelector {
    int GapCount = 0;
    int NumberOfTeamsToSelect;
    ArrayList<Team> SelectedTeams = null;
    ArrayList<Team> TopTierTeams = null;
    ArrayList<PlayerConstraint> PlayerConstraints = new ArrayList<PlayerConstraint>();

    public TeamSetSelector(int numberOfTeams, ArrayList<Team> topTierTeams) {
        NumberOfTeamsToSelect = numberOfTeams;
        TopTierTeams = topTierTeams;

        //initially our selected N teams are the top N teams
        this.SelectedTeams = GetTopNTeams(numberOfTeams, TopTierTeams);
    }

    public void ShowStats() {
        System.out.println("Constraint Results:");
        System.out.println("GapCount: " + this.GapCount);
        System.out.println("Player     PercentTarget     Actual");
        for (PlayerConstraint constraint : PlayerConstraints)
        {
            if (constraint.in) {
                int playerCount = CountOfTeamsHavingPlayer(constraint.playerName, SelectedTeams);
                int percentTarget = (int)(constraint.factor * SelectedTeams.size());
                System.out.println(constraint.playerName + "; " + percentTarget + "; " + playerCount);
            }
        }

        HashMap<String, Integer> playerCounts = this.GetPlayerCounts(this.TopTierTeams);
        int count = 0;
        System.out.println("--------------------------------------");
        System.out.println("Player Distribution of TopTier (" + TopTierTeams.size() + ") Teams:");

        for (String key : playerCounts.keySet())
        {
            System.out.println(count++ + ") " + key + "; " + playerCounts.get(key));
        }
        System.out.println("--------------------------------------");
        System.out.println("");

        playerCounts = this.GetPlayerCounts(SelectedTeams);
        count = 0;
        System.out.println("--------------------------------------");
        System.out.println("Player Distribution of Selected Teams:");
        for(String key : playerCounts.keySet())
        {
            System.out.println(count++ + ") " + key + "; " + playerCounts.get(key));
        }
        System.out.println("--------------------------------------");
        //var sortedDict = from entry in playerCounts orderby entry.Value ascending select entry;
    }


    public HashMap<String, Integer> getSelectedPlayerDistribution() {
        return this.GetPlayerCounts(this.SelectedTeams);
    }

    public ArrayList<Team> SelectTeams_V1(ArrayList<PlayerConstraint> playerConstraints) {
        this.PlayerConstraints.addAll(playerConstraints);

        //get constraints for both being in and out of selected teams
        ArrayList<PlayerConstraint> bothInAndOutPlayerConstraints = GetInAndOutPlayerConstraints(playerConstraints);

        for (PlayerConstraint playerConstraint : bothInAndOutPlayerConstraints)
        {
            int numberOfTeamsNotHavingConstraint = UpdateSelectedTeamsWithConstraint(playerConstraint, SelectedTeams);
            for (int i = 0; i < numberOfTeamsNotHavingConstraint; i++) {
                if (! FindTeamToMeetConstraint(playerConstraint, SelectedTeams))
                    GapCount++;
            }
        }
        return SelectedTeams;
    }

    public ArrayList<Team> SelectTeams(ArrayList<PlayerConstraint> playerConstraints) {
        this.PlayerConstraints.addAll(playerConstraints);

        //get constraints for both being in and out of selected teams
        ArrayList<PlayerConstraint> bothInAndOutPlayerConstraints = GetInAndOutPlayerConstraints(playerConstraints);

        for (PlayerConstraint playerConstraint : bothInAndOutPlayerConstraints) {
            int numberOfTeamsNotHavingConstraint = UpdateSelectedTeamsWithConstraint(playerConstraint, SelectedTeams);
            for (int i = 0; i < numberOfTeamsNotHavingConstraint; i++) {
                int indexOfTeamWithoutConstraint = GetTeamWithoutConstraint(playerConstraint, SelectedTeams);
                ArrayList<PlayerConstraint> constraintList = FormNewConstraints(playerConstraint, SelectedTeams.get(indexOfTeamWithoutConstraint));
                Team teamWithConstraint = FindTeam(NumberOfTeamsToSelect, TopTierTeams, constraintList);
                if (teamWithConstraint != null) {
                    teamWithConstraint.playerConstraints = constraintList;
                    SelectedTeams.set(indexOfTeamWithoutConstraint, teamWithConstraint);
                }
                else
                    GapCount++;
            }
        }
        return SelectedTeams;
    }

    public ArrayList<Team> RunAutoPercent(int maxPercent) {
        ArrayList<PlayerConstraint> abundantConstraints = new ArrayList<PlayerConstraint>();

        ArrayList<String> abundantPlayers = GetAbundantPlayers(maxPercent);
        for (String abundantName : abundantPlayers) {
            abundantConstraints.add(new PlayerConstraint(abundantName, maxPercent));
        }
        return this.SelectTeams(abundantConstraints);
    }

    private ArrayList<String> GetAbundantPlayers(int maxPercent) {
        ArrayList<String> abundantPlayers = new ArrayList<String>();

        HashMap<String, Integer> playerCounts = this.GetPlayerCounts(SelectedTeams);
        int maxAmount = (int)(maxPercent / 100.0 * SelectedTeams.size());

        for (String nameKey : playerCounts.keySet()) {
            if (playerCounts.get(nameKey) > maxAmount) {
                abundantPlayers.add(nameKey);
            }
        }
        return abundantPlayers;
    }

    private HashMap<String, Integer> GetPlayerCounts(ArrayList<Team> selectedTeams) {
        HashMap<String, Integer> playerCounts = new HashMap<String, Integer>();

        for (Team team : selectedTeams) {
            for (Player player : team.Players) {
                if (playerCounts.containsKey(player.Name)) {
                    Integer x = playerCounts.remove(player.Name);
                    playerCounts.put(player.Name, ++x);
                }
                else {
                    playerCounts.put(player.Name, 1);
                }
            }
        }

        return playerCounts;
    }

    private int CountOfTeamsHavingPlayer(String playerName, ArrayList<Team> selectedTeams) {
        int count = 0;
        for(Team team : selectedTeams)
        if (team.HasPlayer(playerName)) count++;
        return count;
    }

    private void ReplaceInList(Team newTeam, Team oldTeam, ArrayList<Team> teamList) {
        teamList.set(teamList.indexOf(oldTeam), newTeam);
//        int i = teamList.FindIndex(x => x == oldTeam);
//        teamList[i] = newTeam;
    }

    private ArrayList<PlayerConstraint> FormNewConstraints(PlayerConstraint playerConstraint, Team teamWithoutConstraint) {
        teamWithoutConstraint.playerConstraints.add(playerConstraint);
        return teamWithoutConstraint.playerConstraints;
    }

    private ArrayList<PlayerConstraint> GetInAndOutPlayerConstraints(ArrayList<PlayerConstraint> playerConstraints) {
        ArrayList<PlayerConstraint> inAndOutConstraints = new ArrayList<PlayerConstraint>();
        for (PlayerConstraint inConstraint : playerConstraints)
        {
            inAndOutConstraints.add(inConstraint);
            inAndOutConstraints.add(inConstraint.inverse());
        }

        return inAndOutConstraints;
    }

    private int GetTeamWithoutConstraint(PlayerConstraint playerConstraint, ArrayList<Team> selectedTeams) {
        ArrayList<PlayerConstraint> listWithOppConstraint = new ArrayList<PlayerConstraint>();
        listWithOppConstraint.add(playerConstraint.inverse());
        return FindTeamReversed(selectedTeams, listWithOppConstraint);
    }

    private boolean FindTeamToMeetConstraint(PlayerConstraint playerConstraint, ArrayList<Team> selectedTeams) {
        int minimumStartIndex = 10;
        int startIndex = selectedTeams.size() - 1;

        ArrayList<PlayerConstraint> listWithOppConstraint = new ArrayList<PlayerConstraint>();
        listWithOppConstraint.add(playerConstraint.inverse());

        while (true) {
            int indexOfTeamWithoutConstraint = FindTeamReversed(startIndex, selectedTeams, listWithOppConstraint);
            if (indexOfTeamWithoutConstraint <= minimumStartIndex)
                break;
            ArrayList<PlayerConstraint> constraintList = FormNewConstraints(playerConstraint, SelectedTeams.get(indexOfTeamWithoutConstraint));
            Team teamWithConstraint = FindTeam(NumberOfTeamsToSelect, TopTierTeams, constraintList);
            if (teamWithConstraint != null) {
                teamWithConstraint.playerConstraints = constraintList;
                SelectedTeams.set(indexOfTeamWithoutConstraint, teamWithConstraint);
                return true;
            }

            if (--startIndex <= minimumStartIndex)
                break;
        }
        return false;
    }

    private int UpdateSelectedTeamsWithConstraint(PlayerConstraint playerConstraint, ArrayList<Team> selectedTeams) {
        //go through selected teams, find all that meet constraint, and add that constraint to each such team
        int countMeetingConstraint = 0;
        int countOfTeamsToMeetConstraint = (int)(selectedTeams.size() * playerConstraint.factor);

        for(Team team : selectedTeams) {
            if (team.MeetsConstraint(playerConstraint)) {
                team.playerConstraints.add(playerConstraint);
                if (++countMeetingConstraint == countOfTeamsToMeetConstraint)
                    return 0;
            }
        }
        return countOfTeamsToMeetConstraint - countMeetingConstraint;
    }

    private ArrayList<Team> GetTopNTeams(int n, ArrayList<Team> topTierTeams) {
        return new ArrayList<>(topTierTeams.subList(0, n));
    }

    private Team FindTeam(int startIndex, ArrayList<Team> topTierTeams, ArrayList<PlayerConstraint> constraints) {
        for (int i = startIndex; i < topTierTeams.size(); i++) {
            Team team = topTierTeams.get(i);
            if (team.playerConstraints.size() == 0)
            {
                if (team.MeetsConstraints(constraints))
                    return team;
            }
        }
        return null;
    }

    private int FindTeamReversed(ArrayList<Team> selectedTeams, ArrayList<PlayerConstraint> constraints) {
        for (int i = selectedTeams.size() - 1; i >= 0; i--)
        {
            Team team = selectedTeams.get(i);
            if (team.MeetsConstraints(constraints))
                return i;
        }
        return -1;
    }

    private int FindTeamReversed(int startIndex, List<Team> selectedTeams, List<PlayerConstraint> constraints) {
        for (int i = startIndex; i >= 0; i--)
        {
            Team team = selectedTeams.get(i);
            if (team.MeetsConstraints(constraints))
                return i;
        }
        return -1;
    }
}

/*

public class TeamSetSelector
    {
        int GapCount = 0;
        int NumberOfTeamsToSelect;
        List<Team> SelectedTeams = null;
        List<Team> TopTierTeams = null;
        List<PlayerConstraint> PlayerConstraints = new List<PlayerConstraint>();

        public TeamSetSelector(int numberOfTeams, List<Team> topTierTeams)
        {
            NumberOfTeamsToSelect = numberOfTeams;
            TopTierTeams = topTierTeams;

            //initially our selected N teams are the top N teams
            this.SelectedTeams = GetTopNTeams(numberOfTeams, TopTierTeams);
        }
        public void ShowStats()
        {
            Console.WriteLine("Constraint Results:");
            Console.WriteLine("GapCount: " + this.GapCount);
            Console.WriteLine("Player     PercentTarget     Actual");
            foreach (PlayerConstraint constraint in PlayerConstraints)
            {
                if (constraint.In)
                {
                    int playerCount = CountOfTeamsHavingPlayer(constraint.PlayerName, SelectedTeams);
                    int percentTarget = (int)(constraint.Factor * SelectedTeams.Count);
                    Console.WriteLine(constraint.PlayerName.PadRight(20) + percentTarget.ToString().PadRight(10) + playerCount);
                }
            }

            Dictionary<string, int> playerCounts = this.GetPlayerCounts(this.TopTierTeams);
            int count = 0;
            Console.WriteLine("--------------------------------------");
            Console.WriteLine("Player Distribution of TopTier (" + TopTierTeams.Count + ") Teams:");

            foreach (string key in playerCounts.Keys)
            {
                Console.WriteLine(count++ + ") " + key.PadRight(20) + playerCounts[key]);
            }
            Console.WriteLine("--------------------------------------");
            Console.WriteLine("");

            playerCounts = this.GetPlayerCounts(SelectedTeams);
            count = 0;
            Console.WriteLine("--------------------------------------");
            Console.WriteLine("Player Distribution of Selected Teams:");
            foreach (string key in playerCounts.Keys)
            {
                Console.WriteLine(count++ + ") " + key.PadRight(20) + playerCounts[key]);
            }
            Console.WriteLine("--------------------------------------");
            //var sortedDict = from entry in playerCounts orderby entry.Value ascending select entry;
        }
        public List<Team> SelectTeams_V1(List<PlayerConstraint> playerConstraints)
        {
            this.PlayerConstraints.AddRange(playerConstraints);

            //get constraints for both being in and out of selected teams
            List<PlayerConstraint> bothInAndOutPlayerConstraints = GetInAndOutPlayerConstraints(playerConstraints);

            foreach (PlayerConstraint playerConstraint in bothInAndOutPlayerConstraints)
            {
                int numberOfTeamsNotHavingConstraint = UpdateSelectedTeamsWithConstraint(playerConstraint, SelectedTeams);
                for (int i = 0; i < numberOfTeamsNotHavingConstraint; i++)
                {
                    bool status = FindTeamToMeetConstraint(playerConstraint, SelectedTeams);
                    if (status == false) GapCount++;
                }
            }
            return SelectedTeams;
        }
        public List<Team> SelectTeams(List<PlayerConstraint> playerConstraints)
        {
            this.PlayerConstraints.AddRange(playerConstraints);

            //get constraints for both being in and out of selected teams
            List<PlayerConstraint> bothInAndOutPlayerConstraints = GetInAndOutPlayerConstraints(playerConstraints);

            foreach (PlayerConstraint playerConstraint in bothInAndOutPlayerConstraints)
            {
                int numberOfTeamsNotHavingConstraint = UpdateSelectedTeamsWithConstraint(playerConstraint, SelectedTeams);
                for (int i = 0; i < numberOfTeamsNotHavingConstraint; i++)
                {
                    int indexOfTeamWithoutConstraint = GetTeamWithoutConstraint(playerConstraint, SelectedTeams);
                    List<PlayerConstraint> constraintList = FormNewConstraints(playerConstraint, SelectedTeams[indexOfTeamWithoutConstraint]);
                    Team teamWithConstraint = FindTeam(NumberOfTeamsToSelect, TopTierTeams, constraintList);
                    if (teamWithConstraint != null)
                    {
                        teamWithConstraint.PlayerConstraints = constraintList;
                        SelectedTeams[indexOfTeamWithoutConstraint] = teamWithConstraint;
                    }
                    else
                        GapCount++;
                }
            }
            return SelectedTeams;
        }
        public List<Team> RunAutoPercent(int maxPercent)
        {
            List<PlayerConstraint> abundantConstraints = new List<PlayerConstraint>();

            List<string> abundantPlayers = GetAbundantPlayers(maxPercent);
            foreach (string abundantName in abundantPlayers)
            {
                abundantConstraints.Add(new PlayerConstraint(abundantName, maxPercent));
            }
            return this.SelectTeams(abundantConstraints);
        }

        private List<string> GetAbundantPlayers(int maxPercent)
        {
            List<string> abundantPlayers = new List<string>();

            Dictionary<string, int> playerCounts = this.GetPlayerCounts(SelectedTeams);
            int maxAmount = (int)(maxPercent / 100.0 * SelectedTeams.Count);

            foreach (string nameKey in playerCounts.Keys)
            {
                if (playerCounts[nameKey] > maxAmount)
                    abundantPlayers.Add(nameKey);
            }
            return abundantPlayers;
        }
        private Dictionary<string, int> GetPlayerCounts(List<Team> selectedTeams)
        {
            Dictionary<string, int> playerCounts = new Dictionary<string, int>();

            foreach (Team team in selectedTeams)
            {
                foreach (Player player in team.Players)
                {
                    if (playerCounts.ContainsKey(player.Name))
                        playerCounts[player.Name]++;
                    else
                        playerCounts.Add(player.Name, 1);
                }
            }

            return playerCounts;
        }
        private int CountOfTeamsHavingPlayer(string playerName, List<Team> selectedTeams)
        {
            int count = 0;
            foreach (Team team in selectedTeams)
                if (team.HasPlayer(playerName)) count++;
            return count;
        }
        private void ReplaceInList(Team newTeam, Team oldTeam, List<Team> teamList)
        {
            int i = teamList.FindIndex(x => x == oldTeam);
            teamList[i] = newTeam;
        }
        private List<PlayerConstraint> FormNewConstraints(PlayerConstraint playerConstraint, Team teamWithoutConstraint)
        {
            teamWithoutConstraint.PlayerConstraints.Add(playerConstraint);
            return teamWithoutConstraint.PlayerConstraints;
        }
        private List<PlayerConstraint> GetInAndOutPlayerConstraints(List<PlayerConstraint> playerConstraints)
        {
            List<PlayerConstraint> inAndOutConstraints = new List<PlayerConstraint>();
            foreach (PlayerConstraint inConstraint in playerConstraints)
            {
                inAndOutConstraints.Add(inConstraint);
                inAndOutConstraints.Add(inConstraint.Inverse());
            }
            return inAndOutConstraints; ;
        }
        private int GetTeamWithoutConstraint(PlayerConstraint playerConstraint, List<Team> selectedTeams)
        {
            List<PlayerConstraint> listWithOppConstraint = new List<PlayerConstraint>();
            listWithOppConstraint.Add(playerConstraint.Inverse());
            return FindTeamReversed(selectedTeams, listWithOppConstraint);
        }
        private bool FindTeamToMeetConstraint(PlayerConstraint playerConstraint, List<Team> selectedTeams)
        {
            int minimumStartIndex = 10;
            int startIndex = selectedTeams.Count - 1;

            List<PlayerConstraint> listWithOppConstraint = new List<PlayerConstraint>();
            listWithOppConstraint.Add(playerConstraint.Inverse());

            while (true)
            {
                int indexOfTeamWithoutConstraint = FindTeamReversed(startIndex, selectedTeams, listWithOppConstraint);
                if (indexOfTeamWithoutConstraint <= minimumStartIndex)
                    break;
                List<PlayerConstraint> constraintList = FormNewConstraints(playerConstraint, SelectedTeams[indexOfTeamWithoutConstraint]);
                Team teamWithConstraint = FindTeam(NumberOfTeamsToSelect, TopTierTeams, constraintList);
                if (teamWithConstraint != null)
                {
                    teamWithConstraint.PlayerConstraints = constraintList;
                    SelectedTeams[indexOfTeamWithoutConstraint] = teamWithConstraint;
                    return true;
                }

                if (--startIndex <= minimumStartIndex)
                    break;
            }
            return false;
        }
        private int UpdateSelectedTeamsWithConstraint(PlayerConstraint playerConstraint, List<Team> selectedTeams)
        {
            //go through selected teams, find all that meet constraint, and add that constraint to each such team
            int countMeetingConstraint = 0;
            int countOfTeamsToMeetConstraint = (int)(selectedTeams.Count * playerConstraint.Factor);

            foreach (Team team in selectedTeams)
            {
                if (team.MeetsConstraint(playerConstraint))
                {
                    team.PlayerConstraints.Add(playerConstraint);
                    if (++countMeetingConstraint == countOfTeamsToMeetConstraint)
                        return 0;
                }
            }
            return countOfTeamsToMeetConstraint - countMeetingConstraint;
        }
        private List<Team> GetTopNTeams(int n, List<Team> topTierTeams)
        {
            List<Team> topN = new List<Team>();
            topN = topTierTeams.GetRange(0, n);
            return topN;
        }
        private Team FindTeam(int startIndex, List<Team> topTierTeams, List<PlayerConstraint> constraints)
        {
            for (int i = startIndex; i < topTierTeams.Count; i++)
            {
                Team team = topTierTeams[i];
                if (team.PlayerConstraints.Count == 0)
                {
                    if (team.MeetsConstraints(constraints))
                        return team;
                }
            }
            return null;
        }
        private int FindTeamReversed(List<Team> selectedTeams, List<PlayerConstraint> constraints)
        {
            for (int i = selectedTeams.Count - 1; i >= 0; i--)
            {
                Team team = selectedTeams[i];
                if (team.MeetsConstraints(constraints))
                    return i;
            }
            return -1;
        }
        private int FindTeamReversed(int startIndex, List<Team> selectedTeams, List<PlayerConstraint> constraints)
        {
            for (int i = startIndex; i >= 0; i--)
            {
                Team team = selectedTeams[i];
                if (team.MeetsConstraints(constraints))
                    return i;
            }
            return -1;
        }
    }



 */

class MutableInt {
    int value = 1; // note that we start at 1 since we're counting
    public void increment () { ++value;      }
    public int  get ()       { return value; }
}