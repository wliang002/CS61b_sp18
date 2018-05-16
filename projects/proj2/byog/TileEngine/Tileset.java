package byog.TileEngine;

import java.awt.Color;
import java.io.Serializable;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset implements Serializable {
    private static final long serialVersionUID = 45498234798734234L;
    public static final TETile PLAYER = new TETile('@', Color.white, Color.black, "player");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static Color g2 = new Color(77, 255, 3);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('♨', g2, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "a curse is blocking the way out");
    public static Color brown = new Color(124, 68, 12);
    public static final TETile PATH = new TETile('☷', brown, Color.black,
            "path");
    public static final TETile SWORD = new TETile('ℐ', Color.yellow, Color.black,
            "a sword");
    public static final TETile RAT = new TETile('Ȑ', new Color(107, 0, 255), Color.black,
            "a rat: 1");
    public static final TETile STAIR = new TETile('▤', Color.YELLOW, Color.black, "stairs");
    public static final TETile BONUS = new TETile('♛', Color.magenta, Color.black, "a healing plant");
    public static final TETile POTION = new TETile('♟', Color.red, Color.black, "a healing potion");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static Color g = new Color(2, 98, 18);
    public static final TETile TREE = new TETile('♠', g, Color.black, "tree");
    public static final TETile GOBLIN = new TETile('Ǧ', Color.blue, Color.black, "a goblin: 5");
    public static final TETile AMULET = new TETile('❣', Color.red, Color.black, "the amulet");
    public static final TETile CURSE = new TETile('⟁', Color.yellow, Color.black,
            "retrieve the Amulet to lift the curse");
}


