package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MapGenerator implements Serializable {
    private final int WIDTH = 80;
    private final int HEIGHT = 30;
    private final int MAXW = 6;
    private final int MAXH = 6;
    private final int MAX_OFFSET = 2;
    private final TETile ENTRY_TILE = Tileset.LOCKED_DOOR;
    private final TETile EXIT_TILE = Tileset.FLOOR;
    private Random random;
    private TETile[][] world;
    private TERenderer ter = new TERenderer();
    private Stack<Room> rooms;
    private TETile wall;
    private static Place player;
    private static final long serialVersionUID = 45498234798734234L;
    private static Place curse;

    public Place rat;
    public Place rat2;


    public Place getPlayer() {
        return player;
    }


    public TETile[][] generate(long seed, int level) {
        random = new Random(seed);
        initialize();
        wall = TETile.colorVariant(Tileset.WALL, 32, 32, 32, random);
        rooms = new Stack<>();
        // create the first room
        Room firstRoom = generateRoom();
        addRoom(firstRoom);
        rooms.push(firstRoom);

        while (rooms.size() > 0) {
            firstRoom = rooms.peek();
            if (!firstRoom.haveExit()) {
                rooms.pop();
            }
            extendRoom(firstRoom);
        }

        addLockedDoor(firstRoom);
        player = addPlayer();
        for (int i = 0; i < 10 - level; i++) {
            addPotions();
        }

        addRat();

        for (int i = 0; i < 10 + level; i++) {
            addRats();
        }
        addStairs();
        return world;
    }


    public static Place liftCurse() {
        return curse;
    }

    public Place addPlayer() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        while (!world[x][y].equals(EXIT_TILE)) {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        }
        world[x][y] = Tileset.PLAYER;
        return new Place(x, y);
    }

    private Place addStairs() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if(world[x][y] != Tileset.FLOOR
                        || world[x][y] == Tileset.POTION
                        || world[x][y] == Tileset.RAT
                        || world[x][y] == Tileset.PLAYER) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.STAIR;
        return new Place(x,y);
    }

    private void addLockedDoor(Room room) {
        ArrayList<Place> doorSpots = new ArrayList<>();
        for (int i = 0; i < room.leftWall().size(); i++) {
            Place p = room.leftWall().get(i);
            if (p.x - 1 >= 0) {
                if (world[p.x][p.y].description().equals("wall")
                        && (!world[p.x][p.y + 1].description().equals("floor")
                        || !world[p.x][p.y - 1].description().equals("floor"))
                        && world[p.x - 1][p.y].description().equals("nothing")) {
                    doorSpots.add(p);
                }
            }
        }
        for (int i = 0; i < room.rightWall().size(); i++) {
            Place p = room.rightWall().get(i);
            if (p.x + 1 < WIDTH) {
                if (world[p.x][p.y].description().equals("wall")
                        && (!world[p.x][p.y + 1].description().equals("floor")
                        || !world[p.x][p.y - 1].description().equals("floor"))
                        && world[p.x + 1][p.y].description().equals("nothing")) {
                    doorSpots.add(p);
                }
            }
        }
        for (int i = 0; i < room.topWall().size(); i++) {
            Place p = room.topWall().get(i);
            if (p.y + 1 < HEIGHT) {
                if (world[p.x][p.y].description().equals("wall")
                        && (!world[p.x + 1][p.y].description().equals("floor")
                        || !world[p.x - 1][p.y].description().equals("floor"))
                        && world[p.x][p.y + 1].description().equals("nothing")) {
                    doorSpots.add(p);
                }
            }
        }

        for (int i = 0; i < room.bottomWall().size(); i++) {
            Place p = room.bottomWall().get(i);
            if (p.y - 1 >= 0) {
                if (world[p.x][p.y].description().equals("wall")
                        && (!world[p.x + 1][p.y].description().equals("floor")
                        || !world[p.x - 1][p.y].description().equals("floor"))
                        && world[p.x][p.y - 1].description().equals("nothing")) {
                    doorSpots.add(p);
                }
            }
        }
        if (!doorSpots.isEmpty()) {
            int door = random.nextInt(doorSpots.size());
            Place exit = doorSpots.get(door);
            world[exit.x][exit.y] = ENTRY_TILE;
            if (world[exit.x + 1][exit.y].equals(Tileset.FLOOR)) {
                world[exit.x + 1][exit.y] = Tileset.CURSE;
                curse = new Place(exit.x + 1, exit.y);
            } else if (world[exit.x - 1][exit.y].equals(Tileset.FLOOR)) {
                world[exit.x - 1][exit.y] = Tileset.CURSE;
                curse = new Place(exit.x - 1, exit.y);
            } else if (world[exit.x][exit.y + 1].equals(Tileset.FLOOR)) {
                world[exit.x][exit.y + 1] = Tileset.CURSE;
                curse = new Place(exit.x, exit.y + 1);
            } else if (world[exit.x][exit.y - 1].equals(Tileset.FLOOR)) {
                world[exit.x][exit.y - 1] = Tileset.CURSE;
                curse = new Place(exit.x, exit.y - 1);
            }

        } else {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            for (int i = 0; i < WIDTH - 1; i++) {
                for (int j = 0; j < HEIGHT - 1; j++) {
                    if(world[x][y] != Tileset.WALL) {
                        x = random.nextInt(WIDTH);
                        y = random.nextInt(HEIGHT);
                    }
                }
            }
            world[x][y] = Tileset.LOCKED_DOOR;
        }

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


    // add additional rooms
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
                if (extRoom.noConflicts(world)) {
                    addRoom(extRoom);
                    if (ep.side == 0) {
                        extRoom.getHalls()[1] = false;
                        world[ep.place.x - 1][ep.place.y] = Tileset.FLOOR;
                    } else if (ep.side == 1) {
                        extRoom.getHalls()[0] = false;
                        world[ep.place.x + 1][ep.place.y] = Tileset.FLOOR;
                    } else if (ep.side == 2) {
                        extRoom.getHalls()[3] = false;
                        world[ep.place.x][ep.place.y + 1] = Tileset.FLOOR;
                    } else if (ep.side == 3) {
                        extRoom.getHalls()[2] = false;
                        world[ep.place.x][ep.place.y - 1] = Tileset.FLOOR;
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
            if (hall.noConflicts(world)) {
                world[exit.x][exit.y] = EXIT_TILE;
                extendLeft(exit);
                Place enter = new Place(exit.x - MAX_OFFSET, exit.y);
                return new Enter(enter, 0);
            }
        }
        if (exh.side == 1) {
            Room hall = new Room(MAX_OFFSET, 1,
                    new Place(exit.x + 1, exit.y - 1));
            if (hall.noConflicts(world)) {
                world[exit.x][exit.y] = EXIT_TILE;
                extendRight(exit);
                Place enter = new Place(exit.x + MAX_OFFSET, exit.y);
                //exit right 1;
                return new Enter(enter, 1);
            }
        }
        if (exh.side == 2) {
            Room hall = new Room(1, MAX_OFFSET,
                    new Place(exit.x - 1, exit.y + 1));
            if (hall.noConflicts(world)) {
                world[exit.x][exit.y] = EXIT_TILE;
                extendUp(exit);
                Place enter = new Place(exit.x, exit.y + MAX_OFFSET);
                return new Enter(enter, 2);
            }
        }
        if (exh.side == 3) {
            Room hall = new Room(1, MAX_OFFSET - 1,
                    new Place(exit.x - 1, exit.y - MAX_OFFSET));
            if (hall.noConflicts(world)) {
                world[exit.x][exit.y] = EXIT_TILE;
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
            world[x1 - i][p.y] = Tileset.FLOOR;
            world[x1 - i][p.y + 1] = wall;
            world[x1 - i][p.y - 1] = wall;
        }
    }

    private void extendRight(Place p) {
        int x1 = p.x;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[x1 + i][p.y] = Tileset.FLOOR;
            world[x1 + i][p.y + 1] = wall;
            world[x1 + i][p.y - 1] = wall;
        }
    }

    private void extendUp(Place p) {
        int y1 = p.y;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[p.x][y1 + i] = Tileset.FLOOR;
            world[p.x + 1][y1 + i] = wall;
            world[p.x - 1][y1 + i] = wall;
        }
    }

    private void extendDown(Place p) {
        int y1 = p.y;
        for (int i = 1; i <= MAX_OFFSET; i++) {
            world[p.x][y1 - i] = Tileset.FLOOR;
            world[p.x + 1][y1 - i] = wall;
            world[p.x - 1][y1 - i] = wall;
        }
    }

    private void initialize() {
        world = new TETile[WIDTH][HEIGHT];
        ter.initialize(WIDTH, HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }


    private Room generateRoom() {
        int h = random.nextInt(MAXH) + 2;
        int w = random.nextInt(MAXW) + 2;
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

    private void addPotions() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if(world[x][y] != Tileset.FLOOR || world[x][y] == Tileset.PLAYER) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.POTION;
    }

    private void addRat() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if(world[x][y] != Tileset.FLOOR
                        || world[x][y] == Tileset.PLAYER
                        || world[x][y] == Tileset.POTION) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.RAT;
        rat = new Place(x,y);
    }

    private void addRats() {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if(world[x][y] != Tileset.FLOOR
                        || world[x][y] == Tileset.PLAYER
                        || world[x][y] == Tileset.POTION
                        || world[x][y] == Tileset.RAT) {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                }
            }
        }
        world[x][y] = Tileset.RAT;
    }

    // add room on map;
    private void addRoom(Room r) {
        int w = r.getWidth();
        int h = r.getHeight();
        int startX = r.getBottomLeft().x;
        int starty = r.getBottomLeft().y;
        for (int i = 0; i < w + 2; i++) {
            for (int j = 0; j < h + 2; j++) {
                if (i == 0 || j == 0 || i == w + 1 || j == h + 1) {
                    world[i + startX][j + starty]
                            = TETile.colorVariant(Tileset.WALL, 32, 32, 32, random);
                } else {
                    world[i + startX][j + starty] = Tileset.FLOOR;
                }
            }
        }
    }


}
