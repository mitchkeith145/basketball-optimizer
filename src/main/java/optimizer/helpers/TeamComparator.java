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

public class TeamComparator implements Comparator<Team> {

    @Override
    public int compare(Team team1, Team team2) {
        return ((Double)team2.TotalExpectedPoints()).compareTo(team1.TotalExpectedPoints());
    }

}
