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

    public Player(CSVRecord record) { // instantiate Player from CSV data
        this.Name = record.get("Name");
        this.Position = record.get("Pos");
        String sal = record.get("Price");
        String salary = String.join("", sal.split("\\$")[1].split(","));
        this.Salary = Integer.parseInt(salary);
        this.expectedPoints = Double.parseDouble(record.get("Value"));
        this.Rank = Integer.parseInt(record.get("Rank"));
        this.Ratio = Double.parseDouble(record.get("Ratio"));
        this.PredictedMinutes = Integer.parseInt(record.get("m/g"));
        this.ValueRatio = (Ratio * expectedPoints);
        Show();
    }

    public Player(String json) throws JSONException { // instantiate Player from JSON data
        JSONObject jsonObj = new JSONObject(json);
        this.Name = jsonObj.get("name").toString();
        this.Position = jsonObj.get("position").toString();
        this.Ratio = Double.parseDouble(jsonObj.get("ratio").toString());
        this.Salary = Integer.parseInt(jsonObj.get("salary").toString());
        this.PredictedMinutes = Integer.parseInt(jsonObj.get("min").toString());
        this.expectedPoints = Double.parseDouble(jsonObj.get("pts").toString());
        this.Rank = Integer.parseInt(jsonObj.get("rank").toString());
        this.ValueRatio = Double.parseDouble(jsonObj.get("value_ratio").toString());
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
