package optimizer.helpers;

import javafx.geometry.Pos;
import org.apache.commons.csv.CSVRecord;
import org.json.*;
/**
 * Created by mitch on 11/23/16.
 */
public class Player {
    public String Name, Position, IdString;
    public Integer Salary, Rank, PredictedMinutes;
    public Double expectedPoints, Ratio, ValueRatio;

    public Player(CSVRecord record) { // instantiate Player from CSV data
        /**
         * CSVFormat.RFC4180.withHeader("", "Like", "Rank", "Price", "Ratio", "Value", "CompV1", "Name", "Inj",
         "Team", "Pos", "g", "Ease", "Rest", "RestA", "m/g", "Opp", "3", "r", "a", "s", "b", "2d",
         "3d", "to", "pV", "3V", "rV", "aV", "sV", "bV", "2dV", "3dV", "toV"));
         */
        Name = record.get("Name");
        Position = record.get("Pos");
        String sal = record.get("Price");
        String salary = String.join("", sal.split("\\$")[1].split(","));
        Salary = Integer.parseInt(salary.split("\\.")[0]);
        expectedPoints = Double.parseDouble(record.get("Value"));
        Rank = Integer.parseInt(record.get("Rank"));
        Ratio = Double.parseDouble(record.get("Ratio"));
        PredictedMinutes = Integer.parseInt(record.get("m/g"));
        ValueRatio = (Ratio * expectedPoints);
        IdString = getNameId() + Salary;
        Show();
    }

    public Player(String json) throws JSONException { // instantiate Player from JSON data
        JSONObject jsonObj = new JSONObject(json);
        Name = jsonObj.get("name").toString();
        Position = jsonObj.get("position").toString();
        Ratio = Double.parseDouble(jsonObj.get("ratio").toString());
        Salary = Integer.parseInt(jsonObj.get("salary").toString());
        PredictedMinutes = Integer.parseInt(jsonObj.get("min").toString());
        expectedPoints = Double.parseDouble(jsonObj.get("pts").toString());
        Rank = Integer.parseInt(jsonObj.get("rank").toString());
        ValueRatio = Double.parseDouble(jsonObj.get("value_ratio").toString());
        IdString = jsonObj.get("id").toString();
        Show();
    }

    public String Show() {
        String json = "{'name':'" + Name + "'," + "'position':'" + Position + "'," +
                "'ratio':'" + String.format("%.5f", Ratio) + "'," + "'salary':'" + Salary + "'," +
                "'pts':'" + String.format("%.5f", expectedPoints) + "'," + "'min':'" + PredictedMinutes + "'," +  "}";

        System.out.println("\t" + Name + " (" + Position + ", Ratio=" + String.format("%.5f", Ratio)
                + ", Salary=" + Salary + ", Pts=" + String.format("%.5f", expectedPoints) + ", Min=" + PredictedMinutes + ")");

        String tableRow = "<tr>" + "<td>" + Name + "</td>" + "<td>" + Position + "</td>" +
                "<td>" + Salary + "</td>" + "<td>" + expectedPoints + "</td>" +
                "<td>" + Ratio + "</td>" + "<td>" + PredictedMinutes + "</td>" +
                "</tr>";
        return tableRow;
    }

    public String toJson() {
        return "{\"name\":\"" + Name + "\"," + "\"position\":\"" + Position + "\"," +
                "\"ratio\":" + String.format("%.5f", Ratio) + "," + "\"salary\":" + Salary + "," +
                "\"pts\":" + String.format("%.5f", expectedPoints) + "," + "\"min\":" + PredictedMinutes + "," +
                "\"value_ratio\":" + ValueRatio + "," + "\"rank\":" + Rank + "," +
                "\"id\":\"" + IdString + "\" }";
    }



    public String toString() {
        return Name;
    }

    private String getNameId() {
        String first = Name.split(" ")[0];
        String last = Name.split(" ")[1];
        first = first.toLowerCase();
        first = first.substring(0, 1);
        last = last.replaceAll("[^a-zA-Z]", "");
        last = last.toLowerCase();
        return first + last;
    }
}
