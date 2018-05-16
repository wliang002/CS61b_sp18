package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    private static final long serialVersionUID = 45498234798734234L;
    private Place bottomLeft;
    private int _width;
    private int _height;
    private Boolean[] halls;
    private int MAX_OFFSET = 3;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;

    Room(int w, int h, Place bl) {
        _width = w;
        _height = h;
        bottomLeft = bl;
        halls = new Boolean[4];
        for (int i = 0; i < 4; i++) {
            halls[i] = true;
        }

    }


    public Place getBottomLeft() {
        return bottomLeft;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public Boolean[] getHalls() {
        return halls;
    }


    public boolean haveExit() {
        boolean haveE = false;
        for (boolean h: this.halls) {
            haveE = haveE || h;
        }
        return haveE;
    }

    public void avoidWalls() {
        if (bottomLeft.x - MAX_OFFSET < 0) {
            halls[0] = false;
        }
        if (bottomRight().x + MAX_OFFSET > WIDTH - 2) {
            halls[1] = false;
        }
        if (topLeft().y + MAX_OFFSET >  HEIGHT - 5) {
            halls[2] = false;
        }
        if (bottomLeft.y - MAX_OFFSET < 0) {
            halls[3] = false;
        }
    }



    public ArrayList<Place> bottomWall() {
        ArrayList bottom = new ArrayList<>();
        int bx = bottomLeft.x;
        for (int i = 1; i <= _width; i++) {
            Place p = new Place(bx + i, bottomLeft.y);
            bottom.add(p);
        }
        return bottom;
    }

    public ArrayList<Place> topWall() {
        ArrayList top = new ArrayList<>();
        int tx = this.topLeft().x;
        for (int i = 1; i <= _width; i++) {
            Place p = new Place(tx + i, this.topLeft().y);
            top.add(p);
        }
        return top;
    }

    public ArrayList<Place> leftWall() {
        ArrayList left = new ArrayList<>();
        int ly = bottomLeft.y;
        for (int i = 1; i <= _height; i++) {
            Place p = new Place(bottomLeft.x, ly + i);
            left.add(p);
        }
        return left;
    }

    public int leftsize() {
        return _height;
    }

    public int bottomsize() {
        return _width;
    }

    public ArrayList<Place> rightWall() {
        ArrayList right = new ArrayList<>();
        int ry = bottomRight().y;
        for (int i = 1; i <= _height; i++) {
            Place p = new Place(bottomRight().x, ry + i);
            right.add(p);
        }
        return right;
    }

    public Place bottomRight() {
        int x = bottomLeft.x + _width + 1;
        int y = bottomLeft.y;
        return new Place(x, y);
    }

    public Place topLeft() {
        int x = bottomLeft.x;
        int y = bottomLeft.y + _height + 1;
        return new Place(x, y);
    }

    public Place topRight() {
        int x = bottomLeft.x + _width + 1;
        int y = bottomLeft.y + _height + 1;
        return new Place(x, y);
    }

    public boolean noConflicts(TETile[][] finalWorldFrame) {
        int xPosition = bottomLeft.x;
        int yPosition = bottomLeft.y;
        if (xPosition + _width + 1 < WIDTH && yPosition + _height + 1 < HEIGHT) {
            for (int i = 0; i <= _width + 1; i++) {
                for (int j = 0; j <= _height + 1; j++) {
                    if (finalWorldFrame[xPosition + i][yPosition + j] == Tileset.FLOOR
                            || finalWorldFrame[xPosition + i][yPosition + j] == Tileset.WALL) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean noConflicts2(TETile[][] finalWorldFrame) {
        int xPosition = bottomLeft.x;
        int yPosition = bottomLeft.y;
        if (xPosition + _width + 1 < WIDTH && yPosition + _height + 1 < HEIGHT) {
            for (int i = 0; i <= _width + 1; i++) {
                for (int j = 0; j <= _height + 1; j++) {
                    if (finalWorldFrame[xPosition + i][yPosition + j] == Tileset.PATH
                            || finalWorldFrame[xPosition + i][yPosition + j] == Tileset.GRASS) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}


