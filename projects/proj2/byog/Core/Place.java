package byog.Core;

import java.io.Serializable;

public class Place implements Serializable {
    private static final long serialVersionUID = 45498234798734234L;
    int x;
    int y;
    Place(int xi, int yi) {
        x = xi;
        y = yi;
    }

    @Override
    public String toString() {
        return "Place{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
