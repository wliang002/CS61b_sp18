package byog.Core;

import java.io.Serializable;

public class Enter implements Serializable {
    Place place;
    int side;
    private static final long serialVersionUID = 4549823479734234L;

    public Enter(Place p, int s) {
        place = p;
        side = s;
    }


}
