package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, int seed) {
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
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder rands = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int c = rand.nextInt(CHARACTERS.length);
            rands.append(CHARACTERS[c]);
        }
        return rands.toString();
    }


    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.black);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.rectangle(0, height - 1, width, 1);
        Font topfont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(topfont);
        StdDraw.text(3, height - 1, "Round " + 1);
        if (playerTurn) {
            StdDraw.text(15, height - 1, "Type!");
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
        }
        if (!playerTurn) {
            StdDraw.text(15, height - 1, "Watch!");
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
        }
        if (!gameOver) {
            int i = rand.nextInt(ENCOURAGEMENT.length);
            StdDraw.text(30, height - 1, ENCOURAGEMENT[i]);
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.show();

    }

    public void flashSequence(String letters) {
        String [] strArray = letters.split("");
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        for (String s: strArray) {
            StdDraw.text(width / 2, height / 2, s);
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
            StdDraw.pause(1000);
            StdDraw.clear(Color.black);
            StdDraw.show();
            StdDraw.pause(500);
        }
        StdDraw.clear(Color.black);
        StdDraw.show();
    }

    public String solicitNCharsInput(int n) {
        StringBuilder str = new StringBuilder();
        int x = (width - n) / 2;
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        int i = 0;
        while(i < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char l = StdDraw.nextKeyTyped();
                String letter = Character.toString(l);
                StdDraw.text(x, height / 2, letter);
                StdDraw.enableDoubleBuffering();
                StdDraw.show();
                str.append(l);
                i++;
                x++;
            }
        }
        return str.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts

        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        round = 1;
        gameOver = false;
        while (!gameOver) {
            playerTurn = false;
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
            StdDraw.clear(Color.black);
            String gameStr = generateRandomString(round);
            drawFrame("");
            playerTurn = true;
            flashSequence(gameStr);
            String player = solicitNCharsInput(round);
            StdDraw.pause(1000);
            StdDraw.clear(Color.black);
            if (player.equals(gameStr)) {
                round += 1;
            } else {
                StdDraw.clear(Color.black);
                StdDraw.text(width / 2, height / 2, "Game Over! You made it to round: " + round);
                StdDraw.show();
                gameOver = true;
            }
        }
    }

}
