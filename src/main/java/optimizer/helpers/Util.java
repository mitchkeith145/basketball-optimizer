package optimizer.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mitch on 12/11/16.
 */
public class Util {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static ArrayList<Team> convertSetToList(Set<Team> topTeams) {
        ArrayList<Team> topTeamList = new ArrayList<>();
        for (Team t : topTeams) {
            topTeamList.add(t);
        }
        return topTeamList;
    }

}