package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        //Modify memorygame constructor to create a Random object
        //uses first program argument as the seed


        this.rand = new Random(seed);

    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        //complete so that it produces a random
        // string using rand object that is len specified by input n


        String result = "";
        for(int i = 0; i < n; i++){
            result += RandomUtils.uniform(this.rand, CHARACTERS.length);

        }
        return result;

    }

    public void drawFrame(String s) {
        //clears the canvas
        // sets the font to be large and bold size 30
        //draws input string centered on canvas
        //shows canvas on screen


        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width/2, this.height/2, s);
        StdDraw.show();

    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        //it takes the input  string and displays one character at a time on screen
        //time centered on screen
        //each charac visible for 1 sec, free 0.5 sec
        //break bet characs screen is blank

        for(char c: letters.toCharArray()){
            this.drawFrame(Character.toString(c));
            StdDraw.pause(1000);
            this.drawFrame("");
            StdDraw.pause(500);

        }




    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        //reads n keystrokes  using StdDraw and returns the
        //string corresponding to those keystrokes
        //string built up so far should appear centered on screen
        //as keys typed by user so user can see what done so far

        String result = "";
        for(int i = 0; i < n; i++){
            if(StdDraw.hasNextKeyTyped()){
                char c = StdDraw.nextKeyTyped();
                result += c;
                this.drawFrame(result);
            }
        }


        return result;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts

        //TODO: Establish Engine loop
        //start game at round 1
        //Display the message “Round: “ followed by the round number in the center of the screen
        //Generate a random string of length equal to the current round number
        //Display the random string one letter at a time
        //Wait for the player to type in a string the same length as the target string
        //Check to see if the player got it correct
        //If they got it correct, repeat from step 2 after increasing the round by 1
        //If they got it wrong, end the game and display the message “Game Over! You made it to round
        // :” followed by the round number they failed in the center of the screen


        int round = 1;
        boolean gameOver = false;

        while(!gameOver){
            this.drawFrame("Round: " + Integer.toString(round));
            String rando = this.generateRandomString(round);
            this.flashSequence(rando);
            StdDraw.pause(2000);
            String userInput = this.solicitNCharsInput(round);
            if(rando.equals(userInput)){
                gameOver = false;
                round++;
            } else {
                gameOver = true;
            }

        }
        this.drawFrame("Game Over! You made it to the round: " + round);

    }

}
