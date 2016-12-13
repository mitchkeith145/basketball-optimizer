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

// custom comparator for teams
public class TeamComparator implements Comparator<Team> {

    @Override
    public int compare(Team team1, Team team2) {
        // uses the native double comparator on the teams' expected points.
        return ((Double)team2.TotalExpectedPoints()).compareTo(team1.TotalExpectedPoints());
    }

}
