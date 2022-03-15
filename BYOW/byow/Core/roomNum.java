package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;

import static byow.Core.RandomUtils.uniform;
import static byow.TileEngine.TETile.colorDecrement;
import static byow.TileEngine.TETile.colorVariant;

public class roomNum {
    public int x;
    public int y;
    public int height;
    public int length;
    private static final int WIDTH = 80; //Borders
    private static final int HEIGHT = 40; //Borders
    private static final int maxHeight = 15; //Max room height that can be created
    private static final int maxLength = 20;  //Max length of a room

    public roomNum(int x,int y,int length,int height) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.height = height;
    }


    public void roomMaker(TETile[][] world) {
        TETile surface = Tileset.FLOOR;
        Random RANDOM = new Random(3453);
        for (int a = 0; a < length; a += 1) {
            for (int b = 0; b < height; b += 1) {
                surface = colorDecrement(surface, -10,  - 10 , - 2);
                world[a + this.x][b + this.y] = surface;
                if((a == length - 1) && (b == 1)){
                    Color bright = colorDecrement(surface, 100, 100, 140).getBackColor();
                    world[a + this.x][b + this.y] = new TETile(Tileset.LIGHT, bright);
                }
            }
            surface = colorDecrement(surface, 37, 37, 35);
        }
        for (int a = 0; a < length; a += 1) {
            world[a + this.x][y] = Tileset.WALL;
            world[a + x][y + height] = Tileset.WALL;
        }
        for (int b = 0; b <= height; b += 1) {
            world[x][y + b] = Tileset.WALL;
            world[x + length][y + b] = Tileset.WALL;
        }

    }


    public boolean layOver(roomNum rooms) {
        int rightBottom = rooms.x + rooms.length;
        int leftTop = rooms.y+rooms.height;
        int newLength = this.x+this.length;
        int newHeight = this.y+this.height;
        if ((this.x < rightBottom && rightBottom <= newLength+1) &&
                (this.y < leftTop && this.y <= newHeight)) {
            return true;
        }
        if ((this.x < rightBottom && rightBottom <= newLength+1) &&
                (rooms.y < newHeight && newHeight <= leftTop)) {
            return true;
        }
        if ((rooms.x < newLength && newLength <= rightBottom+1) &&
                (this.y < leftTop && this.y <= newHeight)) {
            return true;
        }
        if ((rooms.x < newLength && newLength <= rightBottom+1) &&
                (rooms.y < newHeight && newHeight <= leftTop)) {
            return true;
        }
        return false;
    }
}
