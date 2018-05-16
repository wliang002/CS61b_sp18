package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Forest implements Serializable {
    private final int WIDTH = 80;
    private final int HEIGHT = 30;
    private final int MAXW = 2;
    private final int MAXH = 3;
    private final int MAX_OFFSET = 3;
    private final TETile Path = Tileset.PATH;
    private Random random;
    private TETile[][] world;
    private TERenderer ter = new TERenderer();
    private Stack<Room> rooms;
    private TETile wall;
    private static Place player2;
    private static final long serialVersionUID = 45498234798734234L;


    public Place getPlayer2() {
        return player2;
    }


    public TETile[][] generateForest(long seed, int level) {
        random = new Random(seed);
        initialize();
        rooms = new Stack<>();
        wall = Tileset.GRASS;

        Room firstRoom = generateRoom();
        addRoom(firstRoom);
        rooms.push(firstRoom);

        Place entrence = firstRoom.getBottomLeft();
        world[entrence.x + 2][entrence.y + 2] = Tileset.STAIR;
        world[entrence.x + 2][entrence.y + 3] = Tileset.PLAYER;
        player2 = new Place(entrence.x + 2, entrence.y + 3);

        while (rooms.size() > 0) {
            firstRoom = rooms.peek();
            if (!firstRoom.haveExit()) {
                rooms.pop();
            }
            extendRoom(firstRoom);
        }

        for (int i = 0; i < 8 - level; i++) {
            addTreasure();
        }


        for (int i = 0; i < 8 + level; i++) {
            addGoblin();
        }
        addAmulet();
        return world;
    }

    //generate a room within WIDTH and HEIGHT from exit point EP;
    public Room roomGenerator(Enter ep) {
        //left
        if (ep.side == 0) {
            int x = ep.place.x - 1;
            int y = ep.place.y - 1;
            int h = random.nextInt(MAXH) + 2;
            int w = random.nextInt(MAXW) + 2;
            while (x - w <= 0 || y + h >= HEIGHT - 3) {
                h = random.nextInt(MAXH) + 1;
                w = random.nextInt(MAXW) + 1;
            }
            int blx = x - w - 1;
            return new Room(w, h, new Place(blx, y));
        }
        //right
        if (ep.side == 1) {
            int x = ep.place.x + 1;
            int y = ep.place.y - 1;
            int h = random.nextInt(MAXH) + 2;
            int w = random.nextInt(MAXW) + 2;
            while (x + w >= WIDTH - 2 || y + h >= HEIGHT - 3) {
                h = random.nextInt(MAXH) + 1;
                w = random.nextInt(MAXW) + 1;

            }

            return new Room(w, h, new Place(x, y));
        }
        // up
        if (ep.side == 2) {
            int x = ep.place.x - 1;
            int y = ep.place.y + 1;
            int h = random.nextInt(MAXH) + 2;
            int w = random.nextInt(MAXW) + 2;
            while (x + w >= WIDTH - 2 || y + h >= HEIGHT - 3) {
                h = random.nextInt(MAXH) + 1;
                w = random.nextInt(MAXW) + 1;
            }
            return new Room(w, h, new Place(x, y));
        }
        //down
        if (ep.side == 3) {
            int x = ep.place.x - 1;
            int y = ep.place.y - 1;
            int h = random.nextInt(MAXH) + 2;
            int w = random.nextInt(MAXW) + 2;
            while (x + w >= WIDTH - 2 || y - h <= 0) {
                h = random.nextInt(MAXH) + 1;
                w = random.nextInt(MAXW) + 1;
            }
            int bly = y - h - 1;
            return new Room(w, h, new Place(x, bly));
        }
        return null;
    }

    //Dead end, not enough space for a room;
    public boolean deadEnd(Enter ep) {
        if (ep.side == 0) {
            if (ep.place.x - MAX_OFFSET <= 0 || ep.place.y + MAX_OFFSET >= HEIGHT - 3) {
                world[ep.place.x][ep.place.y] = wall;
                return true;
            }
        }
        if (ep.side == 1) {
            if (ep.place.x + MAX_OFFSET >= WIDTH - 2 || ep.place.y + MAX_OFFSET >= HEIGHT - 3) {
                world[ep.place.x][ep.place.y] = wall;
                return true;
            }
        }
        if (ep.side == 2) {
            if (ep.place.x + MAX_OFFSET >= WIDTH - 2 || ep.place.y + MAX_OFFSET >= HEIGHT - 5) {
                world[ep.place.x][ep.place.y] = wall;
                return true;
            }
        }
        if (ep.side == 3) {
            if (ep.place.x + MAX_OFFSET >= WIDTH - 2 || ep.place.y - MAX_OFFSET <= 0) {
                world[ep.place.x][ep.place.y] = wall;
                return true;
            }
        }
        return false;
    }


    //
    private void extendRoom(Room r) {
        Enter ep = extendHall(r);
        // no extended hall;
        if (ep == null) {
            return;
        }
        while (ep != null) {
            if (deadEnd(ep)) {
                ep = extendHall(r);
            } else {
                Room extRoom = roomGenerator(ep);
                if (extRoom.noConflicts2(world)) {
                    addRoom(extRoom);
                    if (ep.side == 0) {
                        extRoom.getHalls()[1] = false;
                        world[ep.place.x - 1][ep.place.y] = Path;
                    } else if (ep.side == 1) {
                        extRoom.getHalls()[0] = false;
                        world[ep.place.x + 1][ep.place.y] = Path;
                    } else if (ep.side == 2) {
                        extRoom.getHalls()[3] = false;
                        world[ep.place.x][ep.place.y + 1] = Path;
                    } else if (ep.side == 3) {
                        extRoom.getHalls()[2] = false;
                        world[ep.place.x][ep.place.y - 1] = Path;
                    }
                    rooms.push(extRoom);
                    return;
                } else {
                    world[ep.place.x][ep.place.y] = wall;
                    ep = extendHall(r);
                }
            }
        }
    }

    //returns an Exit point form a hall extended
    //or null if there's no hall;
    private Enter addHall(Enter exh) {
        Place exit = exh.place;
        if (exh.side == 0) {
            Room hall = new Room(MAX_OFFSET - 1, 1,
                    new Place(exit.x - MAX_OFFSET, exit.y - 1));
            if (hall.noConflicts2(world)) {
                world[exit.x][exit.y] = Path;
                extendLeft(exit);
                Place enter = new Place(exit.x - MAX_OFFSET, exit.y);
                return new Enter(enter, 0);
            }
        }
        if (exh.side == 1) {
            Room hall = new Room(MAX_OFFSET, 1,
                    new Place(exit.x + 1, exit.y - 1));
            if (hall.noConflicts2(world)) {
                world[exit.x][exit.y] = Path;
                extendRight(exit);
                Place enter = new Place(exit.x + MAX_OFFSET, exit.y);
                //exit right 1;
                return new Enter(enter, 1);
            }
        }
        if (exh.side == 2) {
            Room hall = new Room(1, MAX_OFFSET,
                    new Place(exit.x - 1, exit.y + 1));
            if (hall.noConflicts2(world)) {
                world[exit.x][exit.y] = Path;
                extendUp(exit);
                Place enter = new Place(exit.x, exit.y + MAX_OFFSET);
                return new Enter(enter, 2);
            }
        }
        if (exh.side == 3) {
            Room hall = new Room(1, MAX_OFFSET - 1,
                    new Place(exit.x - 1, exit.y - MAX_OFFSET));
            if (hall.noConflicts2(world)) {
                world[exit.x][exit.y] = Path;
                extendDown(exit);
                Place enter = new Place(exit.x, exit.y - MAX_OFFSET);
                return new Enter(enter, 3);
            }
        }
        return null;
    }

    //extend hall from a room, return an exit point or null;
    private Enter extendHall(Room r1) {
        r1.avoidWalls();
        while (r1.haveExit()) {
            //available collection of exits' sides
            ArrayList<Integer> okExt = new ArrayList<>();
            for (int i = 0; i < r1.getHalls().length; i++) {
                if (r1.getHalls()[i]) {
                    okExt.add(i);
                }
            }
            int s = okExt.get(random.nextInt(okExt.size()));
            //leftwall = 0;
            if (s == 0) {
                r1.getHalls()[0] = false;
                int e = random.nextInt(r1.leftsize());
                Place exit = r1.leftWall().get(e);
                Enter exhall = new Enter(exit, 0);
                Enter exitHall = addHall(exhall);
                if (exitHall != null) {
                    return exitHall;
                }
            }
            //rightwall = 1;
            if (s == 1) {
                r1.getHalls()[1] = false;
                int e = random.nextInt(r1.leftsize());
                Place exit = r1.rightWall().get(e);
                Enter exhall = new Enter(exit, 1);
                Enter exitHall = addHall(exhall);
                if (exitHall != null) {
                    return exitHall;
                }
            }

            //topwall = 2
            if (s == 2) {
                r1.getHalls()[2] = false;
                int e = random.nextInt(r1.bottomsize());
                Place exit = r1.topWall().get(e);
                Enter exhall = new Enter(exit, 2);
                Enter exitHall = addHall(exhall);
                if (exitHall != null) {
                    return exitHall;
                }
            }
            //bottomwall = 3
            if (s == 3) {
                r1.getHalls()[3] = false;
                int e = random.nextInt(r1.bottomsize());
                Place exit = r1.bottomWall().get(e);
                Enter exhall = new Enter(exit, 3);
                Enter exitHall = addHall(exhall);

                if (exitHall != null) {
                    return exitHall;
                }
            }
        }
        return null;
    }


    private void extendLeft(Place p) {
        int x1 = p.x;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[x1 - i][p.y] = Path;
            world[x1 - i][p.y + 1] = wall;
            world[x1 - i][p.y - 1] = wall;
        }
    }

    private void extendRight(Place p) {
        int x1 = p.x;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[x1 + i][p.y] = Path;
            world[x1 + i][p.y + 1] = wall;
            world[x1 + i][p.y - 1] = wall;
        }
    }

    private void extendUp(Place p) {
        int y1 = p.y;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[p.x][y1 + i] = Path;
            world[p.x + 1][y1 + i] = wall;
            world[p.x - 1][y1 + i] = wall;
        }
    }

    private void extendDown(Place p) {
        int y1 = p.y;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[p.x][y1 - i] = Path;
            world[p.x + 1][y1 - i] = wall;
            world[p.x - 1][y1 - i] = wall;
        }
    }

//TETile.colorVariant(Tileset.TREE, 30, 200, 70, random);
    private void initialize() {
        world = new TETile[WIDTH][HEIGHT];
        ter.initialize(WIDTH, HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT - 1; j++) {
                world[i][j] = TETile.colorVariant(Tileset.TREE, 32, 200, 32, random);
            }
        }
        for (int i = 0; i < WIDTH; i++) {
            for (int j = HEIGHT - 2; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }


    private Room generateRoom() {
        int h = random.nextInt(MAXH) + 3;
        int w = random.nextInt(MAXW) + 3;
        //Random starting place startx, starty with room for walls;
        int startX = random.nextInt(WIDTH - 6) + 3;
        int startY = random.nextInt(HEIGHT - 6) + 3;
        while (startX + w + 1 >= WIDTH - 3) {
            startX = random.nextInt(WIDTH - 6) + 3;
        }
        while (startY + h + 1 >= HEIGHT - 3) {
            startY = random.nextInt(HEIGHT - 6) + 3;
        }
        return new Room(w, h, new Place(startX, startY));

    }

    private void addTreasure() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[x][y] != Path || world[x][y] == Tileset.PLAYER
                        || world[x][y] == Tileset.STAIR) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.BONUS;
    }


    private void addSword() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[x][y] != Path || world[x][y] == Tileset.PLAYER
                        || world[x][y] == Tileset.STAIR || world[x][y] == Tileset.BONUS) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.SWORD;
    }

    private void addAmulet() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[x][y] != Path || world[x][y] == Tileset.PLAYER
                        || world[x][y] == Tileset.STAIR
                        || world[x][y] == Tileset.BONUS
                        || world[x][y] == Tileset.SWORD) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.AMULET;
    }

    private void addGoblin() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[x][y] != Path || world[x][y] == Tileset.PLAYER
                        || world[x][y] == Tileset.STAIR
                        || world[x][y] == Tileset.BONUS || world[x][y] == Tileset.SWORD) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.GOBLIN;
    }

//TETile.colorVariant(Tileset.GRASS, 32, 32, 32, random);
    private void addRoom(Room r) {
        int w = r.getWidth();
        int h = r.getHeight();
        int startX = r.getBottomLeft().x;
        int starty = r.getBottomLeft().y;
        for (int i = 0; i < w + 2; i++) {
            for (int j = 0; j < h + 2; j++) {
                if (i == 0 || j == 0 || i == w + 1 || j == h + 1) {
                    world[i + startX][j + starty]
                            = Tileset.TREE;
                } else {
                    world[i + startX][j + starty] = Path;
                }
            }
        }
    }


}

