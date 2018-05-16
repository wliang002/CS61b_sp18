package byog.Core;
import java.io.Serializable;

public class Monster implements Serializable {
    public Place place;
    public int health;
    private static final long serialVersionUID = 45494798734234L;
    public Monster(Place p, int h) {
        place = p;
        health = h;

    }
}
