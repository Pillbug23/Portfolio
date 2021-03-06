package byow.TileEngine;

import java.awt.Color;

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

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you"/*,"tu"*/);
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall"/*,"la pared"*/);
    public static final TETile FLOOR = new TETile('·', new Color(128, 0, 0), new Color(0, 0, 0),
            "floor"/*,"el piso"*/);
    public static final TETile LIGHT = new TETile('*', Color.black, Color.black, "light"/*,"la luz"*/);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing"/*, "vacio"*/);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");

    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.gray, "flower"/*, "la flor"*/);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "door"/*,"la puerta"*/);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile SPIKE = new TETile('X',Color.red,Color.black,"spike");

    public static final TETile GIFDEM = new TETile('?',Color.orange,Color.pink,"test", "/Users/alexadunn/Desktop/doge.jpeg" /*"/Users/alexadunn/demo/giphy.gif"*/);


}


