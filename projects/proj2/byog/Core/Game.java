package byog.Core;
import java.io.File;
import java.awt.Font;
import java.io.IOException;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import java.io.FileNotFoundException;
import byog.TileEngine.Tileset;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.io.FileInputStream;
import edu.princeton.cs.introcs.StdDraw;
import org.jcp.xml.dsig.internal.dom.DOMUtils;

import java.io.ObjectInputStream;
import java.awt.Color;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Random;


public class Game implements Serializable {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private static final long serialVersionUID = 45498234798734234L;
    private static TERenderer ter = new TERenderer();
    private TETile[][] finalWorldFrame;
    private TETile[][] forest;
    private int health;
    private boolean gameOver;
    private static Place player;
    private Place playerPosition;
    private int MaxHealth;

    private long seed = -1;
    private int level = 1;

    private static void showGame() {
        // canvas
        int w = WIDTH * 16;
        int h = HEIGHT * 16;
        StdDraw.setCanvasSize(w, h);
        StdDraw.setXscale(0, w);
        StdDraw.setYscale(0, h);
        StdDraw.clear(Color.BLACK);

        Font font = new Font("Chalkduster", Font.BOLD, 40);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.setFont(font);
        StdDraw.text(w / 2, 2 * h / 3, "CS61B: THE GAME");

        // options
        Font font1 = new Font("Chalkduster", Font.BOLD, 20);
        StdDraw.setFont(font1);
        StdDraw.text(w / 2, h / 2, "New Game (N)");
        StdDraw.text(w / 2, h / 2 - 40, "Load Game (L)");
        StdDraw.text(w / 2, h / 2 - 80, "Quit (Q)");

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    private static Game loadGame() {
        File f = new File("./game.ser");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Game loadgame = (Game) os.readObject();
                os.close();
                return loadgame;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        /* In the case no World has been saved yet, we return a new one. */
        return new Game();
    }

    private static void saveGame(Game game) {
        File f = new File("./game.ser");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(game);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }

    }

    private Monster rat;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */


    private void playWithMouse() {
        int w = WIDTH * 16;
        int h = HEIGHT * 16;
        StdDraw.setPenColor(Color.yellow);
        double mx = StdDraw.mouseX();
        double my = StdDraw.mouseY();
        StringBuilder input = new StringBuilder();
        if (mx > w / 2 - 12 * 6 && mx < w / 2 + 12 * 6
                && my > h / 2 - 2 * 8 && my < h / 2 + 2 * 8) {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.rectangle(w / 2, h / 2, 12 * 8, 2 * 8);
            StdDraw.show();
            if (StdDraw.isMousePressed()) {
                input.append('n');
                String g = newGame();
                input.append(g);
                finalWorldFrame = playWithInputString(input.toString());
                ter.renderFrame(finalWorldFrame);
                startGame(player);
            }
        } else {
            StdDraw.setPenColor(Color.black);
            StdDraw.rectangle(w / 2, h / 2, 12 * 8, 2 * 8);
            StdDraw.show();
        }
        if (mx > w / 2 - 12 * 6 && mx < w / 2 + 12 * 6
                && my > h / 2 - 40 - 2 * 8 && my < h / 2 - 40 + 2 * 8) {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.rectangle(w / 2, h / 2 - 40, 12 * 8, 2 * 8);
            StdDraw.show();

            if (StdDraw.isMousePressed()) {
                finalWorldFrame = playWithInputString("l");
                if (finalWorldFrame == null) {
                    input.append('n');
                    String g = newGame();
                    input.append(g);
                    finalWorldFrame = playWithInputString(input.toString());
                }
                ter.initialize(WIDTH, HEIGHT);
                ter.renderFrame(finalWorldFrame);
                startGame(player);
            }
        } else {
            StdDraw.setPenColor(Color.black);
            StdDraw.rectangle(w / 2, h / 2 - 40, 12 * 8, 2 * 8);
            StdDraw.show();
        }
        if (mx > w / 2 - 12 * 6 && mx < w / 2 + 12 * 6
                && my > h / 2 - 80 - 2 * 8 && my < h / 2 - 80 + 2 * 8) {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.rectangle(w / 2, h / 2 - 80, 12 * 8, 2 * 8);
            StdDraw.show();

            if (StdDraw.isMousePressed()) {
                System.exit(0);
            }
        } else {
            StdDraw.setPenColor(Color.black);
            StdDraw.rectangle(w / 2, h / 2 - 80, 12 * 8, 2 * 8);
            StdDraw.show();
        }
    }

    public void playWithKeyboard() {
        gameOver = false;
        showGame();

        StringBuilder input = new StringBuilder();
        boolean play = true;
        while (play) {
            playWithMouse();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'n' || c == 'N') {
                    input.append(c);
                    String g = newGame();
                    input.append(g);
                    finalWorldFrame = playWithInputString(input.toString());
                    ter.renderFrame(finalWorldFrame);
                    startGame(player);
                } else if (c == 'l' || c == 'L') {
                    input.append(c);
                    finalWorldFrame = playWithInputString(input.toString());
                    if (finalWorldFrame == null) {
                        input.append('n');
                        String g = newGame();
                        input.append(g);
                        finalWorldFrame = playWithInputString(input.toString());
                    }
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(finalWorldFrame);
                    startGame(player);
                } else if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }
            }

        }
    }

    //enter seed and 's' to begin a new game;
    private String newGame() {
        int w = WIDTH * 16;
        int h = HEIGHT * 16;
        Font font = new Font("Chalkduster", Font.BOLD, 20);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.setFont(font);
        StdDraw.text(w / 2, h / 2 - 120, "Enter Seed");
        StdDraw.show();
        //get seed
        StringBuilder seed = new StringBuilder();
        int x = (w - 16 * 7) / 2;
        boolean display = true;
        while (display) {
            if (StdDraw.hasNextKeyTyped()) {
                char s0 = StdDraw.nextKeyTyped();
                String s = Character.toString(s0);
                // check if input is a number
                if (s.matches("\\d")) {
                    StdDraw.text(x, h / 2 - 150, s);
                    StdDraw.enableDoubleBuffering();
                    StdDraw.show();
                    seed.append(s);
                    x += 16;
                }
                // start the game when player enters letter "s"
                if (!seed.toString().isEmpty() && (s0 == 's' || s0 == 'S')) {
                    seed.append(s);
                    display = false;
                }
            }
        }
        return seed.toString();
    }

    //heads up display
    private void displayTile() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        int xp = (int) x;
        int yp = (int) y;
        StdDraw.textLeft(3, HEIGHT - 1, "            ");
        Font font = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        if (xp < 0 || xp >= WIDTH - 1 || yp < 0 || yp >= HEIGHT - 1) {
            return;
        } else {
            if (!finalWorldFrame[xp][yp].description().equals("nothing")) {
                StdDraw.textLeft(3, HEIGHT - 1, finalWorldFrame[xp][yp].description());
                StdDraw.enableDoubleBuffering();
                StdDraw.show();
            }
        }

    }

    private void gameOverMenu() {
        level = 1;
        int w = WIDTH;
        int h = HEIGHT;
        Font font = new Font("Chalkduster", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.filledRectangle(w / 2, h / 2, 10, 5);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(w / 2, h / 2, 9.5, 4.5);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(w / 2, h / 2 + 3, "GAME OVER!");
        Font font3 = new Font("Chalkduster", Font.BOLD, 20);
        StdDraw.setFont(font3);
        StdDraw.text(w / 2, h / 2, "Restart (R)");
        StdDraw.text(w / 2, h / 2 - 2, "Quit (Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();

        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'r' || c == 'R') {
                    MapGenerator map = new MapGenerator();
                    finalWorldFrame = map.generate(seed + level, level);
                    player = map.getPlayer();
                    if (level < 6) {
                        health = level * 10;
                    } else {
                        health = 50 + level;
                    }
                    rat = new Monster(map.rat, 1);
                    ter.renderFrame(finalWorldFrame);
                    startGame(player);
                } else if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }
            }
        }

    }

    private void LevelMenu() {
        int w = WIDTH;
        int h = HEIGHT;
        Font font = new Font("Chalkduster", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.filledRectangle(w / 2, h / 2, 10, 5);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(w / 2, h / 2, 9.5, 4.5);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(w / 2, h / 2 + 3, "Level: " + level);
        Font font3 = new Font("Chalkduster", Font.BOLD, 20);
        StdDraw.setFont(font3);
        StdDraw.text(w / 2, h / 2, "Continue (C)");
        StdDraw.text(w / 2, h / 2 - 2, "Quit (Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        if (level < 6) {
            health = level * 10;
        } else {
            health = 50 + level;
        }

        if (level < 6) {
            MaxHealth = level * 10;
        } else {
            MaxHealth = 50 + level;
        }
        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'c' || c == 'C') {
                    MapGenerator map = new MapGenerator();
                    finalWorldFrame = map.generate(seed + level, level);
                    player = map.getPlayer();
                    if (level < 6) {
                        health = level * 10;
                    } else {
                        health = 50 + level;
                    }

                    if (level < 6) {
                        MaxHealth = level * 10;
                    } else {
                        MaxHealth = 50 + level;
                    }
                    rat = new Monster(map.rat, 1);
                    ter.renderFrame(finalWorldFrame);
                    startGame(player);
                } else if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }
            }
        }

    }

    private void menu() {
        int w = WIDTH;
        int h = HEIGHT;
        Font font = new Font("Chalkduster", Font.BOLD, 20);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.setFont(font);
        StdDraw.filledRectangle(w / 2, h / 2, 10, 5);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(w / 2, h / 2, 9.5, 4.5);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(w / 2, h / 2 + 2, "Restart (R)");
        StdDraw.text(w / 2, h / 2, "Cancel (C)");
        StdDraw.text(w / 2, h / 2 - 2, "Quit (Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();

        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'r' || c == 'R') {
                    MapGenerator map = new MapGenerator();
                    finalWorldFrame = map.generate(seed + level, level);
                    player = map.getPlayer();
                    if (level < 6) {
                        health = level * 10;
                    } else {
                        health = 50 + level;
                    }
                    rat = new Monster(map.rat, 1);
                    ter.renderFrame(finalWorldFrame);
                    startGame(player);
                }
                if (c == 'c' || c == 'C') {
                    finalWorldFrame = playWithInputString("l");
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(finalWorldFrame);
                    startGame(player);
                } else if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    private void showStats() {
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        StdDraw.textRight(WIDTH - 2, HEIGHT - 1, "health: " + health);
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }


    private void downStairs() {
        StdDraw.clear(Color.black);
        Forest map2 = new Forest();
        forest = map2.generateForest(seed + level, level);
        Place player2 = map2.getPlayer2();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(forest);
        forestGame(player2);
    }

    private void forestTile() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        int xp = (int) x;
        int yp = (int) y;
        StdDraw.textLeft(3, HEIGHT - 2, "            ");
        Font font = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        if (xp < 0 || xp >= WIDTH - 1 || yp < 0 || yp >= HEIGHT - 1) {
            return;
        } else {
            if (!forest[xp][yp].description().equals("nothing")) {
                StdDraw.textLeft(3, HEIGHT - 1, forest[xp][yp].description());
                StdDraw.enableDoubleBuffering();
                StdDraw.show();
            }
        }

    }

    private void forestGame(Place player2) {
        StringBuilder command = new StringBuilder();
        Place liftcurse = null;
        while (!gameOver) {
            showStats();
            displayScore();
            if (StdDraw.hasNextKeyTyped()) {
                char direction = StdDraw.nextKeyTyped();
                if (direction == 'd' || direction == 'D') {
                    command.append(direction);
                    TETile right = forest[player2.x + 1][player2.y];
                    if (right.equals(Tileset.AMULET)) {
                        liftcurse = MapGenerator.liftCurse();
                    }
                    if (right.equals(Tileset.BONUS)) {
                        if (health < MaxHealth) {
                            health += 2;
                        }
                    }
                    if (right.equals(Tileset.GOBLIN)) {
                        health -= 5;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (right.equals(Tileset.STAIR)) {
                        StdDraw.clear(Color.black);
                        finalWorldFrame = playWithInputString("l");
                        if (liftcurse != null) {
                            finalWorldFrame[liftcurse.x][liftcurse.y] = Tileset.FLOOR;
                        }
                        ter.initialize(WIDTH, HEIGHT);
                        ter.renderFrame(finalWorldFrame);
                        startGame(player);
                    }
                    if (!right.equals(Tileset.TREE) && !right.equals(Tileset.GRASS)) {
                        forest[player2.x][player2.y] = Tileset.PATH;
                        forest[player2.x + 1][player2.y] = Tileset.PLAYER;
                        player2 = new Place(player2.x + 1, player2.y);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(forest);
                    }
                }
                if (direction == 'a' || direction == 'A') {
                    command.append(direction);
                    TETile left = forest[player2.x - 1][player2.y];
                    if (left.equals(Tileset.AMULET)) {
                        liftcurse = MapGenerator.liftCurse();
                    }
                    if (left.equals(Tileset.BONUS)) {
                        if (health < MaxHealth) {
                            health += 2;
                        }
                    }
                    if (left.equals(Tileset.GOBLIN)) {
                        health -= 5;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (left.equals(Tileset.STAIR)) {
                        finalWorldFrame = playWithInputString("l");
                        if (liftcurse != null) {
                            finalWorldFrame[liftcurse.x][liftcurse.y] = Tileset.FLOOR;
                        }
                        ter.initialize(WIDTH, HEIGHT);
                        ter.renderFrame(finalWorldFrame);
                        startGame(player);
                    }
                    if (!left.equals(Tileset.TREE) && !left.equals(Tileset.GRASS)) {
                        forest[player2.x][player2.y] = Tileset.PATH;
                        forest[player2.x - 1][player2.y] = Tileset.PLAYER;
                        player2 = new Place(player2.x - 1, player2.y);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(forest);
                    }
                }
                if (direction == 'w' || direction == 'W') {
                    command.append(direction);
                    TETile up = forest[player2.x][player2.y + 1];
                    if (up.equals(Tileset.AMULET)) {
                        liftcurse = MapGenerator.liftCurse();
                    }
                    if (up.equals(Tileset.BONUS)) {
                        if (health < MaxHealth) {
                            health += 2;
                        }
                    }
                    if (up.equals(Tileset.GOBLIN)) {
                        health -= 5;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (up.equals(Tileset.STAIR)) {
                        finalWorldFrame = playWithInputString("l");
                        if (liftcurse != null) {
                            finalWorldFrame[liftcurse.x][liftcurse.y] = Tileset.FLOOR;
                        }
                        ter.initialize(WIDTH, HEIGHT);
                        ter.renderFrame(finalWorldFrame);
                        startGame(player);
                    }
                    if (!up.equals(Tileset.TREE) && !up.equals(Tileset.GRASS)) {
                        forest[player2.x][player2.y] = Tileset.PATH;
                        forest[player2.x][player2.y + 1] = Tileset.PLAYER;
                        player2 = new Place(player2.x, player2.y + 1);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(forest);
                    }
                }
                if (direction == 's' || direction == 'S') {
                    command.append(direction);
                    TETile down = forest[player2.x][player2.y - 1];
                    if (down.equals(Tileset.AMULET)) {
                        liftcurse = MapGenerator.liftCurse();
                    }
                    if (down.equals(Tileset.BONUS)) {
                        if (health < MaxHealth) {
                            health += 2;
                        }
                    }
                    if (down.equals(Tileset.GOBLIN)) {
                        health -= 5;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (down.equals(Tileset.STAIR)) {
                        finalWorldFrame = playWithInputString("l");
                        if (liftcurse != null) {
                            finalWorldFrame[liftcurse.x][liftcurse.y] = Tileset.FLOOR;
                        }
                        ter.initialize(WIDTH, HEIGHT);
                        ter.renderFrame(finalWorldFrame);
                        startGame(player);
                    }
                    if (!down.equals(Tileset.TREE) && !down.equals(Tileset.GRASS)) {
                        forest[player2.x][player2.y] = Tileset.PATH;
                        forest[player2.x][player2.y - 1] = Tileset.PLAYER;
                        player2 = new Place(player2.x, player2.y - 1);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(forest);
                    }
                }

                if (direction == ':') {
                    command.append(direction);
                    continue;
                }
                if (direction == 'q' || direction == 'Q') {
                    command.append(direction);
                    continue;
                }
            }
            if (command.substring(0).contains(":q")
                    || command.substring(0).contains(":Q")) {
                System.exit(0);
            }
            ter.renderFrame(forest);
            forestTile();
        }
    }


    public void displayScore() {
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT - 1, "Level: " + level);
        StdDraw.show();
    }
    //user can move up, down, left, and right using the W, A, S, and D keys,
    private void startGame(Place player) {
        StringBuilder command = new StringBuilder();
        while (!gameOver) {
            displayScore();
            showStats();
            displayTile();
            if (StdDraw.hasNextKeyTyped()) {
                char direction = StdDraw.nextKeyTyped();
                if (direction == 'd' || direction == 'D') {
                    command.append(direction);
                    TETile right = finalWorldFrame[player.x + 1][player.y];
                    // locked door
                    if (right.equals(Tileset.LOCKED_DOOR)) {
                        level += 1;
                        LevelMenu();
                    }

                    // other
                    if (right.equals(Tileset.POTION)) {
                        PlayAudio.play("byog/ExtraFiles/positive.wav");
                        if (health < MaxHealth) {
                            health += 1;
                        }
                    }
                    if (right.equals(Tileset.STAIR)) {
                        playerPosition = player;
                        saveGame(this);
                        downStairs();
                        break;
                    }
                    if (right.equals(Tileset.RAT)) {
                        health -= 1;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (!right.equals(Tileset.WALL) && !right.equals(Tileset.CURSE)) {
                        finalWorldFrame[player.x][player.y] = Tileset.FLOOR;
                        finalWorldFrame[player.x + 1][player.y] = Tileset.PLAYER;
                        player = new Place(player.x + 1, player.y);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(finalWorldFrame);
                    }
                    // rat
//                    if (rat.health > 0) {
//                        TETile l = finalWorldFrame[rat.place.x - 1][rat.place.y];
//                        if (l.equals(Tileset.PLAYER)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            rat.health = 0;
//                        }
//                        if (!l.equals(Tileset.WALL)
//                                && !l.equals(Tileset.POTION)
//                                && !l.equals(Tileset.RAT)
//                                && !l.equals(Tileset.STAIR)
//                                && !l.equals(Tileset.CURSE)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            finalWorldFrame[rat.place.x - 1][rat.place.y] = Tileset.RAT;
//                            rat.place = new Place(rat.place.x - 1, rat.place.y);
//                            StdDraw.clear(Color.black);
//                            ter.renderFrame(finalWorldFrame);
//                        }
//                    }

                }
                if (direction == 'a' || direction == 'A') {
                    command.append(direction);
                    TETile left = finalWorldFrame[player.x - 1][player.y];
                    // level up
                    if (left.equals(Tileset.LOCKED_DOOR)) {
                        level += 1;
                        LevelMenu();
                    }
                    //other
                    if (left.equals(Tileset.POTION)) {
                        PlayAudio.play("byog/ExtraFiles/positive.wav");
                        if (health < MaxHealth) {
                            health += 1;
                        }
                    }
                    if (left.equals(Tileset.RAT)) {
                        health -= 1;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (left.equals(Tileset.STAIR)) {
                        playerPosition = player;
                        saveGame(this);
                        downStairs();
                        break;
                    }
                    if (!left.equals(Tileset.WALL) && !left.equals(Tileset.CURSE)) {
                        finalWorldFrame[player.x][player.y] = Tileset.FLOOR;
                        finalWorldFrame[player.x - 1][player.y] = Tileset.PLAYER;
                        player = new Place(player.x - 1, player.y);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(finalWorldFrame);
                    }
                    //rat
//                    if (rat.health > 0) {
//                        TETile r = finalWorldFrame[rat.place.x + 1][rat.place.y];
//                        if (r.equals(Tileset.PLAYER)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            rat.health = 0;
//                            health -= 1;
//                        }
//                        if (!r.equals(Tileset.WALL)
//                                && !r.equals(Tileset.POTION)
//                                && !r.equals(Tileset.RAT)
//                                && !r.equals(Tileset.STAIR)
//                                && !r.equals(Tileset.CURSE)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            finalWorldFrame[rat.place.x + 1][rat.place.y] = Tileset.RAT;
//                            rat.place = new Place(rat.place.x + 1, rat.place.y);
//                            StdDraw.clear(Color.black);
//                            ter.renderFrame(finalWorldFrame);
//                        }
//                    }

                }
                if (direction == 'w' || direction == 'W') {
                    command.append(direction);
                    TETile up = finalWorldFrame[player.x][player.y + 1];
                    //level up
                    if (up.equals(Tileset.LOCKED_DOOR)) {
                        level += 1;
                        LevelMenu();
                    }

                    //other
                    if (up.equals(Tileset.STAIR)) {
                        playerPosition = player;
                        saveGame(this);
                        downStairs();
                        break;
                    }
                    if (up.equals(Tileset.POTION)) {
                        PlayAudio.play("byog/ExtraFiles/positive.wav");
                        if (health < MaxHealth) {
                            health += 1;

                        }
                    }
                    if (up.equals(Tileset.RAT)) {
                        health -= 1;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (!up.equals(Tileset.WALL) && !up.equals(Tileset.CURSE)) {
                        finalWorldFrame[player.x][player.y] = Tileset.FLOOR;
                        finalWorldFrame[player.x][player.y + 1] = Tileset.PLAYER;
                        player = new Place(player.x, player.y + 1);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(finalWorldFrame);
                    }

                    //rat
//                    if (rat.health > 0) {
//                        TETile d = finalWorldFrame[rat.place.x][rat.place.y - 1];
//                        if (d.equals(Tileset.PLAYER)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            rat.health = 0;
//                        }
//                        if (!d.equals(Tileset.WALL)
//                                && !d.equals(Tileset.POTION)
//                                && !d.equals(Tileset.RAT)
//                                && !d.equals(Tileset.STAIR)
//                                && !d.equals(Tileset.CURSE)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            finalWorldFrame[rat.place.x][rat.place.y - 1] = Tileset.RAT;
//                            rat.place = new Place(rat.place.x, rat.place.y - 1);
//                            StdDraw.clear(Color.black);
//                            ter.renderFrame(finalWorldFrame);
//                        }
//                    }

                }
                if (direction == 's' || direction == 'S') {
                    command.append(direction);
                    TETile down = finalWorldFrame[player.x][player.y - 1];
                    //level up
                    if (down.equals(Tileset.LOCKED_DOOR)) {
                        level += 1;
                        LevelMenu();
                    }
                    //other
                    if (down.equals(Tileset.POTION)) {
                        PlayAudio.play("byog/ExtraFiles/positive.wav");
                        if (health < MaxHealth) {
                            health += 1;
                        }
                    }
                    if (down.equals(Tileset.RAT)) {
                        health -= 1;
                        if (health <= 0) {
                            gameOverMenu();
                            gameOver = true;
                        }
                    }
                    if (down.equals(Tileset.STAIR)) {
                        playerPosition = player;
                        saveGame(this);
                        downStairs();
                        break;
                    }
                    if (!down.equals(Tileset.WALL) && !down.equals(Tileset.CURSE)) {
                        finalWorldFrame[player.x][player.y] = Tileset.FLOOR;
                        finalWorldFrame[player.x][player.y - 1] = Tileset.PLAYER;
                        player = new Place(player.x, player.y - 1);
                        StdDraw.clear(Color.black);
                        ter.renderFrame(finalWorldFrame);
                    }
                    //rat
//                    if (rat.health > 0) {
//                        TETile u = finalWorldFrame[rat.place.x][rat.place.y + 1];
//                        if (u.equals(Tileset.PLAYER)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            rat.health = 0;
//                        }
//                        if (!u.equals(Tileset.WALL)
//                                && !u.equals(Tileset.POTION)
//                                && !u.equals(Tileset.RAT)
//                                && !u.equals(Tileset.STAIR)
//                                && !u.equals(Tileset.CURSE)) {
//                            finalWorldFrame[rat.place.x][rat.place.y] = Tileset.FLOOR;
//                            finalWorldFrame[rat.place.x][rat.place.y + 1] = Tileset.RAT;
//                            rat.place = new Place(rat.place.x, rat.place.y + 1);
//                            StdDraw.clear(Color.black);
//                            ter.renderFrame(finalWorldFrame);
//                        }
//                    }

                }
                if (direction == 'm' || direction == 'M') {
                    playerPosition = player;
                    saveGame(this);
                    menu();
                    break;
                }
                if (direction == ':') {
                    command.append(direction);
                    continue;
                }
                if (direction == 'q' || direction == 'Q') {
                    command.append(direction);
                    continue;
                }
            }
            if (command.substring(0).contains(":q")
                    || command.substring(0).contains(":Q")) {
                playerPosition = player;
                saveGame(this);
                System.exit(0);
            }

            ter.renderFrame(finalWorldFrame);

        }
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        // String[] commands = Command.parseInput(input);

        char start = input.charAt(0);
        Game game = loadGame();

        if (start == 'n' || start == 'N') {
            String seedInput = input.substring(1).replaceAll("[^0-9]+", "");
            if(seedInput.length() == 0) {
                seed = game.seed;
            } else {
                seed = Long.parseLong(seedInput);
            }
            level = 1;
            if (level < 6) {
                health = level * 10;
            } else {
                health = 50 + level;
            }

            if (level < 6) {
                MaxHealth = level * 10;
            } else {
                MaxHealth = 50 + level;
            }
            MapGenerator map = new MapGenerator();
            finalWorldFrame = map.generate(seed + level, level);
            player = map.getPlayer();

            rat = new Monster(map.rat, 1);
        }
        if (start == 'l' || start == 'L') {
            finalWorldFrame = game.finalWorldFrame;
            player = game.playerPosition;
            level = game.level;
            seed = game.seed;
            health = game.health;
        }
        return finalWorldFrame;
    }


    //backup plan for playerPosition
    private Place backUp() {
        if (finalWorldFrame != null) {
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (finalWorldFrame[i][j].equals(Tileset.PLAYER)) {
                        return new Place(i, j);
                    }
                }
            }
        }
        return null;
    }

}


