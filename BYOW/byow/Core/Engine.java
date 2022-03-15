package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;

import static byow.Core.GenerateWorld.*;
import static byow.Core.Utils.*;
import static java.lang.Long.parseLong;

public class Engine implements Serializable {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    public static int flower = 10;
    private static long SEED;
    private static boolean spanishChange = false;
    private Avatar newPlayer;
    public static boolean lightsOn = true;
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        if (!spanishChange) {
            mainMenu();
            StdDraw.enableDoubleBuffering();
        } else {
            mainSpanish();
            StdDraw.enableDoubleBuffering();
        }
        while (spanishChange) {
            while (true) {
                char keyboard;
                InputSource keyboardInput = new KeyboardInputSource();
                keyboard = keyboardInput.getNextKey();
                StringBuilder result = new StringBuilder();
                if (keyboard == 'N') {
                    while (true) {
                        menuPrincipal(result);
                        if (StdDraw.hasNextKeyTyped()) {
                            char language = StdDraw.nextKeyTyped();

                            if (language == 'S' || language == 's') {
                                String convert = result.toString();
                                boolean check = true;
                                SEED = parseLong(convert);
                                TERenderer ter = new TERenderer();
                                ter.initialize(WIDTH, HEIGHT);
                                TETile[][] world = new TETile[WIDTH][HEIGHT];
                                createWorld(world);
                                GenerateWorld worldMaker = new GenerateWorld(world, SEED, 0);
                                newPlayer = worldMaker.avatarSpawn(world, SEED);
                                ter.renderFrame(world);
                                moveAvatar(world,check);
                                return;
                            }
                            result.append(language);
                        }
                    }
                }
                if (keyboard == 'L') {
                    loadState();
                }
                if (keyboard == ':') {//:
                    saveState();
                    System.exit(0);
                }
                if (keyboard == 'V') {
                    while (true) {
                        spanishMenu();
                        if (StdDraw.hasNextKeyTyped()) {
                            char spanish = StdDraw.nextKeyTyped();
                            if (spanish == '1') {
                                spanishChange = true;
                                interactWithKeyboard();
                            }
                            if (spanish == '2') {
                                spanishChange = false;
                                interactWithKeyboard();
                            }
                        }
                    }
                }
            }
        }
        while (!spanishChange) {
            char keyboard;
            InputSource keyboardInput = new KeyboardInputSource();
            keyboard = keyboardInput.getNextKey();
            StringBuilder result = new StringBuilder();
            //menuMain(result);
            if (keyboard == 'N') {
                //StringBuilder result = new StringBuilder();
                while (true) {
                    menuMain(result);
                    if (StdDraw.hasNextKeyTyped()) {
                        char language = StdDraw.nextKeyTyped();
                        if (language == 'S' || language == 's') {
                            boolean check = false;
                            String convert = result.toString();
                            SEED = Long.parseLong(convert);
                            TERenderer ter = new TERenderer();
                            ter.initialize(WIDTH, HEIGHT);
                            TETile[][] world = new TETile[WIDTH][HEIGHT];
                            createWorld(world);
                            GenerateWorld worldMaker = new GenerateWorld(world, SEED, 0);
                            newPlayer = worldMaker.avatarSpawn(world, SEED);
                            ter.renderFrame(world);
                            moveAvatar(world,check);
                            return;
                        }
                        result.append(language);
                    }
                }
            }
            if (keyboard == 'L') {
                loadState();
            }
            if (keyboard == 'Q') {
                saveState();
                System.exit(0);
            }
            if (keyboard == 'V') {
                while (true) {
                    spanishLanguage();
                    if (StdDraw.hasNextKeyTyped()) {
                        char spanish = StdDraw.nextKeyTyped();
                        if (spanish == '1') {
                            spanishChange = true;
                            interactWithKeyboard();
                        }
                        if (spanish == '2') {
                            spanishChange = false;
                            interactWithKeyboard();
                        }
                    }
                }
            }
        }
    }


    private void spanishLanguage() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(0.5, 0.8, "Select Language");
        StdDraw.text(0.5, 0.6, "-Spanish (1)");
        StdDraw.text(0.5, 0.5, "-English (2)");
        StdDraw.text(0.5, 0.4, "---------------");
        StdDraw.text(0.5, 0.2, "Other Languages available soon");
        StdDraw.show();
    }

    private void spanishMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(0.5, 0.8, "Seleccione el Idioma");
        StdDraw.text(0.5, 0.6, "-Espanol (1)");
        StdDraw.text(0.5, 0.5, "-Inglés (2)");
        StdDraw.text(0.5, 0.4, "---------------");
        StdDraw.text(0.5, 0.2, "Otros Idiomas Disponibles Pronto");
        StdDraw.show();
    }

    /**
     * Same menu but in Spanish
     */


    public void mainSpanish() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(0.5, 0.8, "CS61B: El poder de los flores");
        StdDraw.text(0.5, 0.5, "Nuevo Juego (N)");
        StdDraw.text(0.5, 0.4, "Cargar Juego (L)");
        StdDraw.text(0.5, 0.3, "Idioma (V)");
        StdDraw.text(0.5, 0.2, "Dejar (:Q)");
        StdDraw.text(0.5, 0.1, "Luz (J)");

        StdDraw.show();
    }

    /**
     * Same seed menu but in Spanish
     */


    public void menuPrincipal(StringBuilder result) {
        Font font3 = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font3);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(0.5, 0.8, "CS61B: El poder de los flores");
        StdDraw.text(0.5, 0.4, "Escribes un seed(Numero):" + result.toString());
        StdDraw.text(0.5, 0.2, "Cuando terminas");
        StdDraw.text(0.5, 0.1, "Presiona S para continuar");
        StdDraw.show();

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        String stdInput = input.toUpperCase();

        char firstLetter = stdInput.charAt(0);
        //char isFinished = input.charAt(input.length() - 1);


        //commented for autograder
        //TERenderer ter = new TERenderer();
        // ter.initialize(WIDTH, HEIGHT);

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        createWorld(finalWorldFrame);


        if (firstLetter == 'N') {
            char L = ' ';
            if (stdInput.contains("S")) {
                int index = 1;
                for (int i = 1; i < stdInput.length(); i++) {
                    if (stdInput.charAt(i) == 'S') {
                        index = i;
                        break;
                    }
                }

                /*if(index < stdInput.length()) {
                    L = stdInput.charAt(index + 1);
                }*/

                long UserRand = Long.parseLong(stdInput.substring(1, index));
                //TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];


                GenerateWorld madeWorld = new GenerateWorld(finalWorldFrame, UserRand, 0);
                newPlayer = madeWorld.avatarSpawn(finalWorldFrame, SEED);
                while (index < stdInput.length() - 1) {
                    L = stdInput.charAt(index + 1);
                    switch (L) {
                        case 'W':
                            moveUpAG(finalWorldFrame);
                            index++;
                            break;
                        case 'A':
                            moveLeftAG(finalWorldFrame);
                            index++;
                            break;
                        case 'S':
                            moveDownAG(finalWorldFrame);
                            index++;
                            break;
                        case 'D':
                            moveRightAG(finalWorldFrame);
                            index++;
                            break;
                        case ':':
                            saveWorld(finalWorldFrame);
                            System.exit(0);
                            index++;

                            break;

                        case 'L':
                            loadState();

                        default:
                            break;
                    }
                }

                //ter.renderFrame(finalWorldFrame);

            }

        }

        return finalWorldFrame;
    }


    /**
     * Main Menu
     */
   public void mainMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(0.5, 0.8, "CS61B: Flower Power");
        StdDraw.text(0.5, 0.5, "New Game (N)");
        StdDraw.text(0.5, 0.4, "Load Game (L)");
        StdDraw.text(0.5, 0.3, "Language (V)");
        StdDraw.text(0.5, 0.2, "Quit (:Q)");
        StdDraw.text(0.5, 0.1, "Lights (J)");

        StdDraw.show();
    }

    /**
     * After selecting a new game, enter a seed and press S to generate the world.
     */
   public void menuMain(StringBuilder result) {
        Font font2 = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font2);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(0.5, 0.8, "CS61B: Flower Power");
        StdDraw.text(0.5, 0.4, "Enter Seed(Num):" + result.toString());
        StdDraw.text(0.5, 0.2, "Once the Seed is Entered");
        StdDraw.text(0.5, 0.1, "Press S to Continue");
        StdDraw.show();
    }

    public void moveAvatar(TETile[][] world,boolean check) {
        TERenderer ter = new TERenderer();
        ter.renderFrame(world);
        while (!check) {
            hudDisplay(world);
            if (StdDraw.hasNextKeyTyped()) {
                char keyboard = StdDraw.nextKeyTyped();
                if (keyboard == 'W' || keyboard == 'w') {
                    moveUp(ter, world);
                }
                if (keyboard == 'D' || keyboard == 'd') {
                    moveRight(ter, world);
                }
                if (keyboard == 'S' || keyboard == 's') {
                    moveDown(ter, world);
                }
                if (keyboard == 'A' || keyboard == 'a') {
                    moveLeft(ter, world);
                }
                if (keyboard == 'J' || keyboard == 'j') {

                    turnOff(ter, world);

                }
                if (keyboard == ';' || keyboard == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char keyboard2 = StdDraw.nextKeyTyped();
                            if (keyboard2 == 'Q' || keyboard2 == 'q') {
                                saveState();
                                System.exit(0);
                            }
                        }
                    }
                }
            }
        }
        while (check) {
            hudSpanish(world);
            if (StdDraw.hasNextKeyTyped()) {
                char keyboard = StdDraw.nextKeyTyped();
                if (keyboard == 'W' || keyboard == 'w') {
                    moveUp2(ter, world);
                }
                if (keyboard == 'D' || keyboard == 'd') {
                    moveRight2(ter, world);
                }
                if (keyboard == 'S' || keyboard == 's') {
                    moveDown2(ter, world);
                }
                if (keyboard == 'A' || keyboard == 'a') {
                    moveLeft2(ter, world);
                }
                if (keyboard == 'J' || keyboard == 'j') {
                    turnOff(ter, world);

                }
                if (keyboard == ';' || keyboard == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char keyboard2 = StdDraw.nextKeyTyped();
                            if (keyboard2 == 'Q' || keyboard2 == 'q') {
                                saveState();
                                System.exit(0);
                            }
                        }
                    }
                }



            }
        }
    }

    private void turnOff(TERenderer ter, TETile[][] world) {

        int l = rooms[2].length;
        int h = rooms[2].height;
        for (int y = rooms[2].y; y < h; y+=1) {
            for (int x = rooms[2].x; x < l; x+=1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        ter.renderFrame(world);

    }

    private void moveUp2(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x][newPlayer.y + 1].getBackColor();
        Color current3 = world[newPlayer.x][newPlayer.y + 1].textColor;
        if (world[newPlayer.x][newPlayer.y + 1].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR, current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winSpanish();
            }
            findSpanish(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y + 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y += 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y + 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y += 1;
            ter.renderFrame(world);
        }
    }

    private void moveRight2(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x+1][newPlayer.y].getBackColor();
        Color current3 = world[newPlayer.x+1][newPlayer.y].textColor;
        if (world[newPlayer.x + 1][newPlayer.y].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winSpanish();
            }
            findSpanish(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x + 1][newPlayer.y] =  new TETile(Tileset.AVATAR, current2);
            newPlayer.x += 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x+1][newPlayer.y] = new TETile(Tileset.AVATAR, current2);
            newPlayer.x += 1;
            ter.renderFrame(world);
        }
    }

    private void moveDown2(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x][newPlayer.y-1].getBackColor();
        Color current3 = world[newPlayer.x][newPlayer.y-1].textColor;
        if (world[newPlayer.x][newPlayer.y - 1].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winSpanish();
            }
            findSpanish(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y - 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y -= 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y-1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y -= 1;
            ter.renderFrame(world);
        }
    }

    private void moveLeft2(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x-1][newPlayer.y].getBackColor();
        Color current3 = world[newPlayer.x-1][newPlayer.y].textColor;
        if (world[newPlayer.x - 1][newPlayer.y].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winSpanish();
            }
            findSpanish(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x-1][newPlayer.y] = new TETile(Tileset.AVATAR, current);
            newPlayer.x -= 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x-1][newPlayer.y] = new TETile(Tileset.AVATAR, current2);
            newPlayer.x -= 1;
            ter.renderFrame(world);
        }
    }

    private void moveUp(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x][newPlayer.y + 1].getBackColor();
        Color current3 = world[newPlayer.x][newPlayer.y + 1].textColor;
        if (world[newPlayer.x][newPlayer.y + 1].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR, current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y + 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y += 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y + 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y += 1;
            ter.renderFrame(world);
        }
    }

    private void moveRight(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x+1][newPlayer.y].getBackColor();
        Color current3 = world[newPlayer.x+1][newPlayer.y].textColor;
        if (world[newPlayer.x + 1][newPlayer.y].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x + 1][newPlayer.y] =  new TETile(Tileset.AVATAR, current2);
            newPlayer.x += 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x+1][newPlayer.y] = new TETile(Tileset.AVATAR, current2);
            newPlayer.x += 1;
            ter.renderFrame(world);
        }
    }

    private void moveDown(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x][newPlayer.y-1].getBackColor();
        Color current3 = world[newPlayer.x][newPlayer.y-1].textColor;
        if (world[newPlayer.x][newPlayer.y - 1].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y - 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y -= 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y-1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y -= 1;
            ter.renderFrame(world);
        }
    }

    private void moveLeft(TERenderer ter,TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x-1][newPlayer.y].getBackColor();
        Color current3 = world[newPlayer.x-1][newPlayer.y].textColor;
        if (world[newPlayer.x - 1][newPlayer.y].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);
        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x-1][newPlayer.y] = new TETile(Tileset.AVATAR, current);
            newPlayer.x -= 1;
            ter.renderFrame(world);
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x-1][newPlayer.y] = new TETile(Tileset.AVATAR, current2);
            newPlayer.x -= 1;
            ter.renderFrame(world);
        }
    }


    ///autograder component/////
    private void moveUpAG(TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x][newPlayer.y + 1].getBackColor();
        Color current3 = world[newPlayer.x][newPlayer.y + 1].textColor;
        if (world[newPlayer.x][newPlayer.y + 1].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR, current);

        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y + 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y += 1;
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y + 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y += 1;
        }
    }

    private void moveRightAG(TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x+1][newPlayer.y].getBackColor();
        Color current3 = world[newPlayer.x+1][newPlayer.y].textColor;
        if (world[newPlayer.x + 1][newPlayer.y].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);

        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x + 1][newPlayer.y] =  new TETile(Tileset.AVATAR, current2);
            newPlayer.x += 1;
        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x+1][newPlayer.y] = new TETile(Tileset.AVATAR, current2);
            newPlayer.x += 1;
        }
    }

    private void moveDownAG(TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x][newPlayer.y-1].getBackColor();
        Color current3 = world[newPlayer.x][newPlayer.y-1].textColor;
        if (world[newPlayer.x][newPlayer.y - 1].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);

        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y - 1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y -= 1;

        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x][newPlayer.y-1] = new TETile(Tileset.AVATAR, current2);
            newPlayer.y -= 1;

        }
    }

    private void moveLeftAG(TETile[][] world) {
        Color current = world[newPlayer.x][newPlayer.y].getBackColor();
        Color current2 = world[newPlayer.x-1][newPlayer.y].getBackColor();
        Color current3 = world[newPlayer.x-1][newPlayer.y].textColor;
        if (world[newPlayer.x - 1][newPlayer.y].equals(Tileset.WALL)) {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.AVATAR,current);

        } else if (current3.equals(Color.magenta)) {
            flower -= 1;
            if (flower == 0) {
                winGame();
            }
            findFlower(flower);
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x-1][newPlayer.y] = new TETile(Tileset.AVATAR, current);
            newPlayer.x -= 1;

        } else {
            world[newPlayer.x][newPlayer.y] = new TETile(Tileset.FLOOR, current);
            world[newPlayer.x-1][newPlayer.y] = new TETile(Tileset.AVATAR, current2);
            newPlayer.x -= 1;
        }
    }


    private void winGame() {
        Font font2 = new Font("Monaco", Font.BOLD, 12);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH / 2, HEIGHT - 4, "You've collected enough flowers! You win!");
        StdDraw.text(WIDTH / 2, HEIGHT - 6, "Game will exit automatically");
        StdDraw.show();
        StdDraw.pause(5000);
        System.exit(0);
    }


    private void winSpanish() {
        Font font2 = new Font("Monaco", Font.BOLD, 12);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH / 2, HEIGHT - 4, "Has Recolectado Suficientes Flores! Tú Ganas!");
        StdDraw.text(WIDTH / 2, HEIGHT - 6, "El Juego Saldrá Automáticamente");
        StdDraw.show();
        StdDraw.pause(5000);
        System.exit(0);
    }


            /**
             * Saves current state, file must have .txt for autograder

             */
    private static void saveState() {
        File saveFile = new File("world.txt");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
                FileOutputStream save = new FileOutputStream(saveFile);
                ObjectOutputStream save2 = new ObjectOutputStream(save);
                save2.writeObject(world);
                save2.close();
                save.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveWorld(TETile[][] world) {
        File saveFile = new File("savefile.txt");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeObject(saveFile, world);

    }


    /**
     * Loads file if saved from the main menu, if file doesnt exist print
     * exception error message
     */
    public void loadState() {
        File saveFile = new File("savefile.txt");
        if (saveFile.exists()) {
            Utils.readObject(saveFile, TETile.class);
            //GenerateWorld savedWorld = ((TETile[][])readObject(saveFile, TETile.class));
        } else {
            System.exit(0);
            Font font2 = new Font("Monaco", Font.BOLD, 12);
            StdDraw.setPenColor(Color.PINK);
            StdDraw.setFont(font2);
            StdDraw.text(0.5, 0.5, "No File Saved");
        }
    }

    /**
     * Displays the description of the tile, shown in the GUI on hovering over the tile.
     * From TETile class;
     * Large tile size fixed  with ED Post @5312
     */
            public void hudDisplay (TETile[][]world){
                StdDraw.enableDoubleBuffering();
                TERenderer ter = new TERenderer();
                ter.renderFrame(world);
                int x = (int) StdDraw.mouseX(); /*Mouse is double*/
                int y = (int) StdDraw.mouseY();
                if (y < 40) {
                    if (world[x][y].equals(Tileset.WALL)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "Wall");
                        StdDraw.show();
                    } else if (world[x][y].equals(Tileset.FLOOR)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "Floor");
                        StdDraw.show();
                    } else if (world[x][y].equals(Tileset.NOTHING)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "Nothing");
                        StdDraw.show();
                    } else {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, world[x][y].description());
                        StdDraw.show();
                    }
                }
            }

            public void hudSpanish (TETile[][]world){
                StdDraw.enableDoubleBuffering();
                TERenderer ter = new TERenderer();
                ter.renderFrame(world);
                int x = (int) StdDraw.mouseX(); /*Mouse is double*/
                int y = (int) StdDraw.mouseY();
                Color pink = world[newPlayer.x][newPlayer.y].textColor;
                if (y < 40) {
                    if (world[x][y].equals(Tileset.WALL)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "La Pared");
                        StdDraw.show();
                    } else if (world[x][y].equals(Tileset.FLOOR)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "El Piso");
                        StdDraw.show();
                    } else if (world[x][y].equals(Tileset.NOTHING)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "Vacío");
                        StdDraw.show();
                    } else if (world[x][y].equals(Tileset.AVATAR)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "Usted");
                        StdDraw.show();
                    } else if (world[x][y].equals(Color.magenta)) {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "La Flor");
                        StdDraw.show();
                    } else {
                        Font font2 = new Font("Monaco", Font.BOLD, 12);
                        StdDraw.setPenColor(Color.PINK);
                        StdDraw.setFont(font2);
                        StdDraw.text(WIDTH / 2, HEIGHT - 2, "La Flor");
                        StdDraw.show();
                    }
                }
            }

            private void findFlower ( int flower){
                Font font2 = new Font("Monaco", Font.BOLD, 12);
                StdDraw.setPenColor(Color.PINK);
                StdDraw.setFont(font2);
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "Flower Found! " + flower + " left!");
                StdDraw.show();
                StdDraw.pause(500);

            }

            private void findSpanish ( int flower){
                flower -= 1;
                Font font2 = new Font("Monaco", Font.BOLD, 12);
                StdDraw.setPenColor(Color.PINK);
                StdDraw.setFont(font2);
                StdDraw.text(WIDTH / 2, HEIGHT - 4, "Flor Encontrada! " + flower + " Izquierdo!");
                StdDraw.show();
                StdDraw.pause(500);
            }





   private void saveState(TETile[][] world) {
        File saveFile = new File("savefile.txt");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeObject(saveFile, world);




        //write seed
        //write newPlayer.x, newPlayer.y
        //writeObject newPlayer

        //String saveAvatar = readContentsAsString(newPlayer)


        //writeObject(saveFile, saveAvatar);
        //writeObject(saveFile, seed);


        //writeObject(interactWithInputString(long seed)); //this returns a frame object
        // writeObject(interactWithInputString(seed));
    }

    /**
    /**
     * Loads file if saved from the main menu, if file doesnt exist print
     * exception error message
    private void loadState () {
        File saveFile = new File("savefile.txt");
        if (!saveFile.exists()) {
            System.exit(0);
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
        }
        GenerateWorld savedWorld = ((TETile[][])readObject(saveFile, TETile.class));
        //Avatar A = ((Avatar)readObject(saveFile, Avatar.class));
        //passing in numbers + wasd key presses
        Long S = parseLong(readContentsAsString(saveFile));
        return;
        }
    }
     */
}