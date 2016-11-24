package optimizer.helpers;

import javafx.geometry.Pos;
import org.apache.commons.csv.CSVRecord;

/**
 * Created by mitch on 11/23/16.
 */
public class Player {
    public String Name, Position;
    public Integer Salary, Rank, PredictedMinutes;
    public Double expectedPoints, Ratio, ValueRatio;

    public Player(CSVRecord record) {
        Name = record.get("Name");
        Position = record.get("Pos");
        String sal = record.get("Price");
        String salary = String.join("", sal.split("\\$")[1].split(","));
        Salary = Integer.parseInt(salary);
        expectedPoints = Double.parseDouble(record.get("Value"));
        Rank = Integer.parseInt(record.get("Rank"));
        Ratio = Double.parseDouble(record.get("Ratio"));
        PredictedMinutes = Integer.parseInt(record.get("m/g"));
        ValueRatio = (Ratio * Ratio * expectedPoints);
        Show();
    }

    public String Show() {
        System.out.println("\t" + Name + " (" + Position + ", Ratio=" + String.format("%.2f", Ratio)
                + ", Salary=" + Salary + ", Pts=" + String.format("%.2f", expectedPoints) + ", Min=" + PredictedMinutes + ")");

        String tableRow = "<tr>" + "<td>" + Name + "</td>" + "<td>" + Position + "</td>" +
                "<td>" + Salary + "</td>" + "<td>" + expectedPoints + "</td>" +
                "<td>" + Ratio + "</td>" + "<td>" + PredictedMinutes + "</td>" +
                "</tr>";
        return tableRow;
    }

    public String toString() {
        return Name;
    }
}
