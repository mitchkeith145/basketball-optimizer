package optimizer.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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

//    public List<Team> reverseListTeam(List<Team> list) {
//        List<Team> l = new ArrayList<>();
//
//        for (int i = list.size() - 1; i >= 0; i--) {
//            l.add(list.get(i));
//        }
//
//        return l;
//    }
}
