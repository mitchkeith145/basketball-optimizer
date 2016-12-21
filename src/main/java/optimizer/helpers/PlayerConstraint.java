package optimizer.helpers;

/**
 * Created by mitch on 12/14/16.
 */
public class PlayerConstraint {

    public String playerName;
    public Double factor;
    public boolean in;

    public PlayerConstraint(String name, Integer percent) {
        playerName = name;
        factor = percent / 100.0;
        in = true;
    }

    public PlayerConstraint(String name, Double percent) {
        playerName = name;
        factor = percent;
        in = true;
    }

    public PlayerConstraint inverse() {
        Integer percent = (int) ((1 - factor) * 100);
        PlayerConstraint inverse = new PlayerConstraint(playerName, percent);
        inverse.in = !in;
        return inverse;
    }

    @Override
    public  String toString() {
        return this.playerName + ": " + this.in;
    }
}
