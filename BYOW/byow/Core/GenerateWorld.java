package byow.Core;

import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.util.Random;

import static byow.Core.RandomUtils.uniform;

public class GenerateWorld {
    private static final int WIDTH = 80; //Borders
    private static final int HEIGHT = 40; //Borders
    private static final int maxHeight = 15; //Max room height that can be created
    private static final int lowestHeight = 4; //Shortest room height
    private static final int maxLength = 20;  //Max length of a room
    private static final int lowestLength = 4; //Shortest length of room
    public static TETile[][] world; //world object
    private static Center[] center; //Center of object, gets center of rooms
    static roomNum[] rooms;  //Gets the x,y of our rooms and creates them


    public static void createWorld(TETile[][] world) {
        int height = world[0].length;
        int width = world.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }


    public static TETile randomBiome(int numberino) {
        int seeded = numberino;
        Random RANDOM = new Random(seeded);
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0:
                return Tileset.GRASS;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.SAND;
            case 3:
                return Tileset.MOUNTAIN;
            case 4:
                return Tileset.TREE;
            default:
                return Tileset.NOTHING;
        }
    }

    public GenerateWorld(TETile[][] world, long inputNum, int i) {
        long seed = inputNum;
        Random RANDOM = new Random(seed);
        int numRooms = (int) (inputNum % 8 + RANDOM.nextInt(7) + 4);
        rooms = new roomNum[(int) numRooms];
        center = new Center[(int) numRooms];
        int startUp = 1;
        while (i < numRooms) {
            int x = RANDOM.nextInt(WIDTH - maxLength);
            int y = RANDOM.nextInt(HEIGHT - maxHeight);
            int roomLength = Math.max(RANDOM.nextInt(10), lowestLength);
            int roomHeight = Math.max(RANDOM.nextInt(10), lowestHeight);
            int centerX = (int) Math.floor(x + roomLength / 2);
            int centerY = (int) Math.floor(y + roomHeight / 2);
            rooms[i] = new roomNum(x, y, roomLength, roomHeight);
            center[i] = new Center(centerX, centerY);

            if (startUp != 1 && i != 0) {
                if (spawnTogether(rooms, i)) {
                    rooms[i] = null;
                    center[i] = null;
                } else {
                    rooms[i].roomMaker(world);
                    i += 1;
                    startUp -= 1;
                }
            }
            if (i == 0) {
                rooms[i].roomMaker(world);
                i += 1;
                startUp -= 1;
            }
        }
        int t = 0;
        while (t < numRooms - 1) {
            int high = rooms[t].y + rooms[t].height;
            int high2 = rooms[t + 1].y + rooms[t + 1].height;
            int combined = rooms[t].x + rooms[t].length;
            int combined2 = rooms[t + 1].x + rooms[t + 1].length;
            if (rooms[t].x < rooms[t + 1].x) { //connect base room to new room on right
                for (int a = combined; a < combined2; a += 1) { //rooms generates hallway to right
                    world[a][rooms[t].y + 1] = Tileset.FLOOR;
                }
                if (rooms[t].y < rooms[t + 1].y) { //base room has lower height than the next one
                    for (int b = rooms[t + 1].y; b > rooms[t].y; b -= 1) {
                        world[combined2 - 1][b] = Tileset.FLOOR;

                    }
                }
                if (rooms[t].y > rooms[t + 1].y) {
                    for (int b = high2; b <= rooms[t].y; b += 1) {
                        world[combined2 - 1][b] = Tileset.FLOOR;
                    }
                }
            }
            if (rooms[t].x > rooms[t + 1].x) {
                for (int a = combined2; a < combined; a += 1) {
                    world[a][rooms[t + 1].y + 1] = Tileset.FLOOR;
                }
                if (rooms[t].y < rooms[t + 1].y) { //base room has lower height than the next one
                    for (int b = high; b <= rooms[t + 1].y; b += 1) {
                        world[combined - 1][b] = Tileset.FLOOR;
                    }
                }
                if (rooms[t].y > rooms[t + 1].y) {
                    for (int b = rooms[t].y; b > rooms[t + 1].y; b -= 1) {
                        world[combined - 1][b] = Tileset.FLOOR;
                    }
                }
            }
            t += 1;
        }
        int l = 0;
        while (l < numRooms - 1) {
            int high = rooms[l].y + rooms[l].height;
            int high2 = rooms[l + 1].y + rooms[l + 1].height;
            int combined = rooms[l].x + rooms[l].length;
            int combined2 = rooms[l + 1].x + rooms[l + 1].length;
            if (rooms[l].x < rooms[l + 1].x) { //connect base room to new room on right
                for (int a = combined; a <= combined2; a += 1) { //rooms generates hallway to right
                    if (world[a][rooms[l].y].equals(Tileset.NOTHING)) {
                        world[a][rooms[l].y] = Tileset.WALL;
                    }
                    if (world[a][rooms[l].y + 2].equals(Tileset.NOTHING)) {
                        world[a][rooms[l].y + 2] = Tileset.WALL;
                    }
                }
                if (rooms[l].y < rooms[l + 1].y) { //base room has lower height than the next one
                    for (int b = rooms[l + 1].y; b >= rooms[l].y; b -= 1) {
                        if (world[combined2][b].equals(Tileset.NOTHING)) {
                            world[combined2][b] = Tileset.WALL;
                        }
                        if (world[combined2 - 2][b].equals(Tileset.NOTHING)) {
                            world[combined2 - 2][b] = Tileset.WALL;
                        }
                    }
                }
                if (rooms[l].y > rooms[l + 1].y) {
                    for (int b = high2; b <= rooms[l].y + 1; b += 1) {
                        if (world[combined2][b].equals(Tileset.NOTHING)) {
                            world[combined2][b] = Tileset.WALL;
                        }
                        if (world[combined2 - 2][b].equals(Tileset.NOTHING)) {
                            world[combined2 - 2][b] = Tileset.WALL;
                        }
                    }
                }
            }
            if (rooms[l].x > rooms[l + 1].x) {
                for (int a = combined2; a <= combined; a += 1) {
                    if (world[a][rooms[l + 1].y].equals(Tileset.NOTHING)) {
                        world[a][rooms[l + 1].y] = Tileset.WALL;
                    }
                    if (world[a][rooms[l + 1].y + 2].equals(Tileset.NOTHING)) {
                        world[a][rooms[l + 1].y + 2] = Tileset.WALL;
                    }
                }
                if (rooms[l].y < rooms[l + 1].y) { //base room has lower height than the next one
                    for (int b = high; b <= rooms[l + 1].y + 2; b += 1) {
                        if (world[combined][b].equals(Tileset.NOTHING)) {
                            world[combined][b] = Tileset.WALL;
                        }
                        if (world[combined - 2][b].equals(Tileset.NOTHING)) {
                            world[combined - 2][b] = Tileset.WALL;
                        }
                    }
                }
                if (rooms[l].y > rooms[l + 1].y) {
                    for (int b = rooms[l].y; b >= rooms[l + 1].y; b -= 1) {
                        if (world[combined][b].equals(Tileset.NOTHING)) {
                            world[combined][b] = Tileset.WALL;
                        }
                        if (world[combined - 2][b].equals(Tileset.NOTHING)) {
                            world[combined - 2][b] = Tileset.WALL;
                        }
                    }
                }
            }
            l += 1;
        }
    }

    public static boolean spawnTogether(roomNum[] rooms, int i) {
        for (int previous = 0; previous < i; previous += 1)
            if (rooms[i].layOver(rooms[previous])) {
                return true;
            }
        return false;
    }

    public Avatar avatarSpawn(TETile[][] world, long number) {
        long seed = number;
        Random randomness = new Random(3453);
        int flowersMake = uniform(randomness,10,20);
        Random RANDOM = new Random(seed);
        Avatar newPlayer = new Avatar(RANDOM.nextInt(WIDTH - maxLength), RANDOM.nextInt(HEIGHT - maxHeight));
        while (world[newPlayer.x][newPlayer.y].equals(Tileset.WALL) ||
                world[newPlayer.x][newPlayer.y].equals(Tileset.NOTHING)) {
            newPlayer = new Avatar(RANDOM.nextInt(WIDTH - maxLength), RANDOM.nextInt(HEIGHT - maxHeight));
        }
        while (flowersMake > 0) {
            int flowerX = RANDOM.nextInt(WIDTH - maxLength);
            int flowerY = RANDOM.nextInt(HEIGHT - maxHeight);
            if (world[flowerX][flowerY].equals(Tileset.WALL) ||
                    world[flowerX][flowerY].equals(Tileset.NOTHING)) {
                flowersMake -= 0;
            } else {
                Color current = world[flowerX][flowerY].getBackColor();
                world[flowerX][flowerY] = new TETile(Tileset.FLOWER, current);
                flowersMake -= 1;
            }
        }
        world[newPlayer.x][newPlayer.y] = Tileset.AVATAR;
        return newPlayer;
        }
    }