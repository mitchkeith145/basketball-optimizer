package optimizer.helpers;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch on 11/23/16.
 */
public class Team {
    public ArrayList<Player> Players = new ArrayList<>();
    public Double totalExpectedPoints;
    public Team()
    {

    }

    public Team(List<Player> players)
    {
        for (Player p: players) {
            Players.add(p);
        }
    }

    public Team Clone()
    {
        Team clone = new Team(Players);
        return clone;
    }
    public void Add(Player player)
    {
        Players.add(player);
    }

    public Boolean HasPlayer(Player p)
    {
        return Players.contains(p);
    }

    public double TotalExpectedPoints() {
        double sum = 0;
        for (Player p : Players) {
            sum += p.expectedPoints;
        }
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

    public String Show(int number)
    {
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

    public Boolean isEquals(Team t) {
        return t.TotalExpectedPoints() == TotalExpectedPoints();
    }
}