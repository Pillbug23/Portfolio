package byow.Core;

import java.io.Serializable;

public class Avatar implements Serializable {
    public int x;
    public int y;

    public Avatar(int x,int y) {
        this.x = x;
        this.y = y;
    }
}
