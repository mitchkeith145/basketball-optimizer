package optimizer.helpers;

import javafx.geometry.Pos;
import org.apache.commons.csv.CSVRecord;
import org.json.*;
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

    public Player(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        Name = jsonObj.get("name").toString();
        Position = jsonObj.get("position").toString();
        Ratio = Double.parseDouble(jsonObj.get("ratio").toString());
        Salary = Integer.parseInt(jsonObj.get("salary").toString());
        PredictedMinutes = Integer.parseInt(jsonObj.get("min").toString());
        expectedPoints = Double.parseDouble(jsonObj.get("pts").toString());
        Rank = Integer.parseInt(jsonObj.get("rank").toString());
        ValueRatio = Double.parseDouble(jsonObj.get("value_ratio").toString());
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
                "\"value_ratio\":" + ValueRatio + "," + "\"rank\":" + Rank + "" + "}";
    }



    public String toString() {
        return Name;
    }
}
