package optimizer.helpers;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by mitch on 11/23/16.
 */
public class Team {
    public ArrayList<Player> Players = new ArrayList<>();
    public Double totalExpectedPoints = 0.0;
    public Integer rank = -1;
    ArrayList<PlayerConstraint> playerConstraints = new ArrayList<>();
    public Team() {

    }

    public Team(List<Player> players) {
        for (Player p: players) {
            Players.add(p);
        }
    }

    public Team Clone() {
        Team clone = new Team(Players);
        return clone;
    }
    public void Add(Player player) {
        Players.add(player);
    }

    public Boolean HasPlayer(Player p) {
        for (Player player : Players) {
            if (player.Name.equals(p.Name)) {
                return true;
            }
        }
        return false;
    }

    public boolean HasPlayer(String name) {
        for (Player player : Players) {
            if (player.Name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public double TotalExpectedPoints() {
        if (totalExpectedPoints > 0.0) {
            return totalExpectedPoints;
        }

        double sum = 0.0;
        for (Player p : Players) {
            sum += p.expectedPoints;
        }
        sum = Util.round(sum, 6);
        totalExpectedPoints = sum;
        return sum;
    }

    public Integer TotalSalary() {
        Integer sum = 0;
        for (Player p : Players) {
            sum += p.Salary;
        }
        return sum;
    }

    public String Show(int number) {
        System.out.println();
        System.out.println(number + ") Total Expected Points: " + String.format("%.2f", TotalExpectedPoints()));
        String table = "<table class='table table-striped'><thead>" +
                "<th>Name</td><th>Position</td><th>Salary</td><th>Expected Points</td><th>Ratio</td><th>Minutes</td>" +
                "</thead><tbody>";


        for (Player p : Players) {
            table += p.Show();
        }
        table += "<tr><td>Totals:</td><td>&nbsp;</td><td>" + TotalSalary() + "</td><td>" + TotalExpectedPoints() + "</td><td>&nbsp;</td><td>&nbsp;</td>";
        table += "</tbody></table>";
        return table;
    }

    public String toJson() {
        /**
         *
         * Returns JSON of the form:
         * {
         *    "expected_points": XYZ.abc,
         *    "roster": [ {
         *        "name": "blah",
         *        "pos": "blah",
         *        ...
         *        "pts": 42
         *        },
         *        "name": "blah",
         *        "pos": "blah",
         *        ...
         *        "pts": 42
         *        },
         *        ...
         *        ...
         *        ...
         *     ]
         * }
         *
         */
        String json = "{";
        json += "\"expected_points\":" + totalExpectedPoints + ",";
        json += "\"roster\":[";
        int playerCount = 0;
        for (Player p : Players) {
            json += p.toJson();
            if (playerCount < Players.size() - 1) {
                json += ",";
            }
            playerCount++;
        }
        json += "]}";
        return json;
    }

    public Boolean isEquals(Team t) {
        return Math.abs(t.TotalExpectedPoints() - TotalExpectedPoints()) < 0.00001;
    }

    public boolean MeetsConstraints(List<PlayerConstraint> constraints) {
        for (PlayerConstraint constraint : constraints) {
            if (this.HasConstraintForPlayer(constraint.playerName))
                return false;
            if (!MeetsConstraint(constraint))
                return false;
        }
        return true;
    }

    public boolean HasConstraintForPlayer(String playerName) {
        for (PlayerConstraint constraint : playerConstraints) {
            if (constraint.playerName.equals(playerName))
                return true;
        }
        return false;
    }

    public boolean MeetsConstraint(PlayerConstraint constraint) {
        for (Player p : Players) {
            if (p.Name.equals(constraint.playerName)) {
                return constraint.in;
            }
        }

        if (constraint.in)
            return false;

        return true;
    }

}
