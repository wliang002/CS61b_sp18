package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final long SEED = 2873981;
    private static final Random RANDOM = new Random(SEED);
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;
    public static int size;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        size = 3;
        Position p = new Position(size, 6);
        drawRandomVerticalHexes(world, p, size, 3);
        Position p1 = bottomRightNeighbor(p, size);
        drawRandomVerticalHexes(world, p1, size, 4);
        Position p2 = bottomRightNeighbor(p1, size);
        drawRandomVerticalHexes(world, p2, size, 5);
        Position p3 = topRightNeighbor(p2, size);
        drawRandomVerticalHexes(world, p3, 3, 4);
        Position p4 = topRightNeighbor(p3, size);
        drawRandomVerticalHexes(world, p4, 3, 3);
        ter.renderFrame(world);
    }


    public static Position topRightNeighbor(Position p, int s) {
        int x = p.x + s - 1 + Math.abs(xOffset(s, s));
        int y = p.y + s;
        return new Position(x, y);
    }

    public static Position bottomRightNeighbor(Position p, int s) {
        int x = p.x + s - 1 + Math.abs(xOffset(s, s));
        int y = p.y - s;
        return new Position(x, y);
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.WATER;
            case 3: return Tileset.SAND;
            case 4: return Tileset.GRASS;
            case 5: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }

    public static void drawRandomVerticalHexes(TETile[][] world, Position p,
                                               int s, int n) {

        Position nextP = new Position(p.x, p.y);
        for(int i = 0; i < n; i++) {
            addHexagon(world, nextP, s, randomTile());
            nextP.y = nextP.y + s * 2;

        }
    }

    /**
     * add a hexagon.
     * @param world world
     * @param p bottom left position
     * @param s size
     * @param t tile
     */
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        if (s < 2) {
            throw new IllegalArgumentException("Hexagon must be at least size 2.");
        }
        for (int i = 0; i < s * 2; i++) {
            int yCoor = p.y + i;
            int xCoor = p.x + xOffset(s, i);
            Position startP = new Position(xCoor, yCoor);
            int w = rowWidth(s, i);
            drawRow(world, startP, w, t);
        }
    }

    /**
     * add a row
     * @param world world
     * @param p leftmost position
     * @param w width of row
     * @param t tile.
     */
    public static void drawRow(TETile[][] world, Position p, int w, TETile t) {
        for (int xi = 0; xi < w; xi++) {
            int xCoo = p.x + xi;
            world[xCoo][p.y] = TETile.colorVariant(t, 32, 32, 32, RANDOM);
        }
    }

    /**
     * Compute the width of row r for size s hexagon.
     * @param s size.
     * @param r row.
     * @return row width.
     */
    public static int rowWidth(int s, int r) {
        if (r >= s) {
            r = 2 * s - r - 1;
        }
        return s + 2 * r;
    }

    /**
     * x coordinate of the leftmost tile in rth row
     * bottom row has an x-coordinate of 0
     * @param s size
     * @param r row
     * @return leftmost x coordinate
     */
    public static int xOffset(int s, int r) {
        if (r >= s) {
            r = 2 * s - r - 1;
        }
        return -r;
    }
}
