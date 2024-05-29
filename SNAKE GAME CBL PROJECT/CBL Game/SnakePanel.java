import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

/*
 * @author: Horia-George Dună
 * @id: 1949284
 * @author: Radu-Cristian Sarău
 * @id: 1939149 
 */

/**
 * This is the panel where the main game will be played.
 */
public class SnakePanel extends JPanel implements ActionListener {
    
    //Window Characteristics
    static final int WINDOW_WIDTH = 700;
    static final int WINDOW_HEIGHT = 700;
    static final int CELL_SIZE = 25;
    static final int WINDOW_CELLS = (WINDOW_HEIGHT * WINDOW_WIDTH) / CELL_SIZE;

    //Coordinate System
    private final int[] x = new int[WINDOW_CELLS];
    private final int[] y = new int[WINDOW_CELLS];

    //Snake Characteristics
    private int bodyParts = 3;
    private int applesEaten;
    private boolean running = false;
    private char runDirection = 'R';
    
    //Miscellaneous Variables
    private Timer timer;
    Random random;
    static final int DELAY = 175;
    private int currentDelay = DELAY;
    private static int stage = 0;
    private int titleScreenCommandNo = 0;
    private int optionsScreenCommandNo = 0;
    private boolean mutedMusic = false;
    private int lemonsEaten = 0;
    static final int DELETE_COORDINATE = -69;

    //Fruits as objects 
    private Fruit apple = new Fruit();
    private Fruit orange = new Fruit();
    private Fruit rottenApple = new Fruit();
    private Fruit blueberry = new Fruit();
    private Fruit lemon = new Fruit();
    
    /**
     * This array is used to store some colors for the background.
     */
    private Color[] stageBackgroundColor = {
        Color.black,
        Color.green,
        Color.lightGray,
        Color.darkGray,
        Color.gray
    };

    //Variables used for picking the background image
    private ImageIcon stage1Background = new ImageIcon("stage1.png");
    private ImageIcon stage2Background = new ImageIcon("stage2.png");
    private ImageIcon stage3Background = new ImageIcon("stage3.png");
    private ImageIcon stage4Background = new ImageIcon("stage4.png");
    private ImageIcon stage5Background = new ImageIcon("stage5.png");

    //Variables used for fruit sprites
    private ImageIcon lemonSprite = new ImageIcon("lemonSprite.png");
    private ImageIcon appleSprite = new ImageIcon("appleSprite.png");
    private ImageIcon orangeSprite = new ImageIcon("orangeSprite.png");
    private ImageIcon rottenAppleSprite = new ImageIcon("rottenAppleSprite.png");
    private ImageIcon blueberrySprite = new ImageIcon("blueberrySprite.png");

    //Game State Variables
    private int gameState;
    private final int playState = 1;
    private final int pauseState = 2;
    private final int gameOverState = -1;
    private final int winState = 100;
    private final int titleState = 0;
    private final int optionsState = 69;
    

    /**
     * The constructor for the class SnakePanel.
     */
    SnakePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new SnakeKeyAdapter());
        startGame();
        gameState = titleState;
    }

    

    /**
     * This method draws a black rectangle the size of the window, and the message "PAUSED" 
     * when the player pauses the game.
     * @param g represents all the components drawn sequentially
     * @param gameState is an integer used to determine whether the game is running, paused, won, 
     *      or not yet running.
     */
    public void drawPaused(Graphics g, int gameState) {
        if (gameState == pauseState) {
            this.setBackground(stageBackgroundColor[0]);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Agency FB", Font.BOLD, 50));
            String pausedMessage = "PAUSED";
            int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(pausedMessage)) / 2;
            int y = WINDOW_HEIGHT / 2;
            g.drawString(pausedMessage, x, y);
        }
    }

    /**
     * This method draws the message "YOU WIN!" when the player wins.
     * @param g represents all the components drawn sequentially
     * @param gameState is an integer used to determine whether the game is running, paused, won, 
     *      or not yet running.
     */
    public void drawWinScreen(Graphics g, int gameState) {
        if (gameState == winState) {
            this.setBackground(stageBackgroundColor[0]);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Agency FB", Font.BOLD, 50));
            String youWin = "YOU WIN!";
            int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(youWin)) / 2;
            int y = WINDOW_HEIGHT / 2;
            g.drawString(youWin, x, y);
            g.setFont(new Font("Agency FB", Font.BOLD, 30));
            String winExitText = "PRESS 'ESC' TO EXIT THE GAME";
            FontMetrics winExitTextMetrics = getFontMetrics(g.getFont());
            int xExitText = (WINDOW_WIDTH - winExitTextMetrics.stringWidth(winExitText)) / 2;
            int yExitText = WINDOW_HEIGHT / 2 + winExitTextMetrics.getHeight() + 50; 
            g.drawString(winExitText, xExitText, yExitText);
        } 
    }
    
    /**
     * This method draws an apple on the grid.
     * @param g represents all the components drawn sequentially
     */
    public void drawApple(Graphics g) {
        g.drawImage(appleSprite.getImage(), apple.xFruit, apple.yFruit, this);
    }
    
    /**
     * This method draws an orange on the grid.
     * @param g represents all the components drawn sequentially
     */
    public void drawOrange(Graphics g) {
        if (applesEaten % 15 == 0) {
            g.drawImage(orangeSprite.getImage(), orange.xFruit, orange.yFruit, this);
        }
    }

    /**
     * This method draws a rotten apple on the grid.
     * @param g represents all the components drawn sequentially
     */
    public void drawRottenApple(Graphics g) {
        if (applesEaten % 3 == 0) {
            g.drawImage(rottenAppleSprite.getImage(), rottenApple.xFruit, rottenApple.yFruit, this);
        }
    }

    /**
     * This method draws a lemon on the grid.
     * @param g represents all the components drawn sequentially
     */
    public void drawLemon(Graphics g) {
        if (applesEaten % 50 == 0 && applesEaten != 0 && lemonsEaten == 0) {
            g.drawImage(lemonSprite.getImage(), lemon.xFruit, lemon.yFruit, this);
        }
    }

    /**
     * This method draws a lemon on the grid.
     * @param g represents all the components drawn sequentially
     */
    public void drawBlueberry(Graphics g) {
        if (applesEaten % 26 == 0 && applesEaten != 0) {
            g.drawImage(blueberrySprite.getImage(), blueberry.xFruit, blueberry.yFruit, this);
        }
    }

    /**
     * This method draws the snake on the screen.
     * @param g represents all the components drawn sequentially
     */
    public void drawSnake(Graphics g) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                //Head
                g.setColor(Color.YELLOW);
            } else {
                //Body
                g.setColor(new Color(181, 172, 2));
            }
                
            g.fillRect(x[i], y[i], CELL_SIZE, CELL_SIZE);
        }
    }

    /**
     * This method draws the grid lines on the screen.
     * @param g represents all the components drawn sequentially
     */
    public void drawGridLines(Graphics g) {
        for (int i = 0; i < WINDOW_HEIGHT / CELL_SIZE; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, WINDOW_HEIGHT);
            g.drawLine(0, i * CELL_SIZE, WINDOW_WIDTH, i * CELL_SIZE);
        }

    }

    /**
     * This method draws the text "STAGE: " followed by the stage number.
     * @param g represents all the components drawn sequentially
     */
    public void drawStageNumber(Graphics g) {
        g.setColor(Color.blue);
        g.setFont(new Font("Agency FB", Font.BOLD, 45));
        FontMetrics stageMetrics = getFontMetrics(g.getFont());
        g.drawString("STAGE: " + stage,
                    (WINDOW_WIDTH - stageMetrics.stringWidth("STAGE: " + stage)) / 2,
                    WINDOW_HEIGHT - stageMetrics.getHeight() + 40);
    }

    /**
     * This method draws the text "SCORE: " followed by the player's score.
     * @param g represents all the components drawn sequentially
     */
    public void drawScore(Graphics g) {
        g.setColor(Color.BLUE);
        g.setFont(new Font("Agency FB", Font.BOLD, 45));
        FontMetrics fontSettings = getFontMetrics(g.getFont());
        g.drawString("SCORE: " + applesEaten, (WINDOW_WIDTH  
                    - fontSettings.stringWidth("SCORE: " + applesEaten)) / 2,
                    g.getFont().getSize());
    }

    /**
     * This method is used to draw the title screen.
     * @param g represents all the components drawn sequentially
     */
    public void drawTitleScreen(Graphics g) {
        g.setFont(new Font("Agency FB", Font.BOLD, 70));
        String gameTitle = "SNAKE GAME RPG";
        int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(gameTitle)) / 2;
        int y = WINDOW_HEIGHT / 2 - 200;
        g.setColor(Color.YELLOW);
        g.drawString(gameTitle, x, y);
        
        
        g.setFont(new Font("Agency FB", Font.BOLD, 40));
        String playGame = "PLAY";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(playGame)) / 2;
        y = WINDOW_HEIGHT / 2 + 50;
        g.drawString(playGame, x, y);
        if (titleScreenCommandNo == 0) {
            g.setColor(Color.BLUE);
            g.drawString(">", x - 2 * CELL_SIZE, y);
            g.setColor(Color.YELLOW);
        }
        
        String gameOptions = "OPTIONS";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(gameOptions)) / 2;
        y = WINDOW_HEIGHT / 2 + 100;
        g.drawString(gameOptions, x, y);
        if (titleScreenCommandNo == 1) {
            g.setColor(Color.BLUE);
            g.drawString(">", x - 2 * CELL_SIZE, y);
            g.setColor(Color.YELLOW);
        }

        String gameExit = "EXIT";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(gameExit)) / 2;
        y = WINDOW_HEIGHT / 2 + 150;
        g.drawString(gameExit, x, y); 
        if (titleScreenCommandNo == 2) {
            g.setColor(Color.BLUE);
            g.drawString(">", x - 2 * CELL_SIZE, y);
            g.setColor(Color.YELLOW);
        }
    }

    /**
     * This method draws the playing field, the snake and the apple at their respective positions.
     * @param g represents all the components drawn sequentially
     */
    public void draw(Graphics g) {
        if (gameState == titleState) {
            drawTitleScreen(g);
            this.setBackground(stageBackgroundColor[0]);
        } else  if (gameState == optionsState) {
            drawOptionsScreen(g);
            this.setBackground(stageBackgroundColor[0]);
        } else {
            if (stage < 1) {
                stage++;
            }
            if (running) {

                //Draws the grid lines
                drawGridLines(g);
            
                //Draws the apple
                drawApple(g);

                //Draws the orange
                drawOrange(g);

                //Draws the rotten apple
                drawRottenApple(g);

                //Draws the lemon
                drawLemon(g);

                //Draws the blueberry
                drawBlueberry(g);

                //Draws the snake
                drawSnake(g);
                
                //Stage Number
                drawStageNumber(g);    
                    
                //Score
                drawScore(g);    
                
                //Draws the message "PAUSED" when the game is paused
                drawPaused(g, gameState);
                
                //Draws the message "YOU WIN!" when the player wins the game
                drawWinScreen(g, gameState);
            } else {
                gameOver(g);
            }
        }
        
        
    }

    /**
     * This method draws the text "GAME OVER" when the player loses.
     * @param g represents all the components drawn sequentially
     */
    public void drawGameOverText(Graphics g) {
        this.setBackground(stageBackgroundColor[0]);
        g.setColor(Color.RED);
        g.setFont(new Font("Agency FB", Font.BOLD, 90));
        FontMetrics gameOverMetrics = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (WINDOW_WIDTH - gameOverMetrics.stringWidth("GAME OVER")) / 2,
            WINDOW_HEIGHT / 2);
        g.setFont(new Font("Agency FB", Font.BOLD, 30));
        String exitText = "PRESS 'ESC' TO EXIT THE GAME";
        FontMetrics exitTextMetrics = getFontMetrics(g.getFont());
        int xExitText = (WINDOW_WIDTH - exitTextMetrics.stringWidth(exitText)) / 2;
        int yExitText = WINDOW_HEIGHT / 2 + exitTextMetrics.getHeight() + 50; 
        g.drawString(exitText, xExitText, yExitText);
    }


    /**
     * This method is called when the snake runs out of bounds, or collides with itself.
     * It draws the text "GAME OVER" in the center of the screen, and show the score.
     * @param g represents all the components drawn sequentially.
     */
    public void gameOver(Graphics g) {
        //"GAME OVER" text
        drawGameOverText(g);
        
        //Score
        drawScore(g);

        //Stage Number
        drawStageNumber(g);

        gameState = gameOverState;

    }

    
    /**
     * Method that creates an apple at a random position.
     */
    public void spawnApple() {
        apple.spawnFruit();
    }

    /**
     * Method that creates an orange at a random position.
     */
    public void spawnOrange() {
        if (applesEaten % 15 == 0) {
            orange.spawnFruit();
        }
    }

    /**
     * Method that creates a rotten apple at a random position.
     */
    public void spawnRottenApple() {
        if (applesEaten % 3 == 0) {
            rottenApple.spawnFruit();
        }
    }
    
    /**
     * Method that creates a lemon at a random position.
     */
    public void spawnLemon() {
        if (applesEaten % 50 == 0 && applesEaten != 0 && lemonsEaten == 0) {
            lemon.spawnFruit();
        }
    }

    /**
     * Method that creates a blueberyy at a random position.
     */
    public void spawnBlueberry() {
        if (applesEaten % 26 == 0 && applesEaten != 0) {
            blueberry.spawnFruit();
        }
    }

    /**
     * Remake of the paintComponent method from JPanel. This method sets the background image
     * and draws everything.
     * @param g represents all the components drawn sequentially.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == playState) {
            switch (stage) {
                case 1:
                    g.drawImage(stage1Background.getImage(), 0, 0, this);
                    break;
                case 2:
                    g.drawImage(stage2Background.getImage(), 0, 0, this);
                    break;
                case 3:
                    g.drawImage(stage3Background.getImage(), 0, 0, this);
                    break;
                case 4:
                    g.drawImage(stage4Background.getImage(), 0, 0, this);
                    break;
                case 5:
                    g.drawImage(stage5Background.getImage(), 0, 0, this);
                    break;
                default:
                    break;
            }
        }
        draw(g);
    }

    /**
     * This method is used to delete fruits from the grid when the conditions to spawn them
     * are not met.
     */
    public void checkFruitConditions() {
        if (applesEaten % 3 != 0) {
            rottenApple.deleteFruit();
        }
        if (applesEaten % 15 != 0) {
            orange.deleteFruit();
        }
        if (!(applesEaten % 50 == 0 && applesEaten != 0 && lemonsEaten == 0)) {
            lemon.deleteFruit();
        }
        if (!(applesEaten % 26 == 0 && applesEaten != 0)) {
            blueberry.deleteFruit();
        }
    }

    /**
     * This method spawns a new apple, makes the snake start slithering with a delay, 
     * spawns the fruits (if the conditions to spawn them are met), and starts the
     * stage calculator.
     */
    public void startGame() {
        spawnApple();
        spawnOrange();
        spawnRottenApple();
        spawnLemon();
        spawnBlueberry();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        gameState = playState;
        stageCalculator();
    }

    /**
     * This method is used to move the snake's body.
     * @param x is an array used to draw the snake.
     * @param y is another array used to draw the snake.
     */
    public void moveSnakeBody(int[] x, int[] y) {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
    }

    /**
     * This method is used to move eths snake's head.
     * @param x is an array used to draw the snake.
     * @param y is another array used to draw the snake.
     * @param runDirection determines the snake's direction.
     */
    public void moveSnakeHead(int[] x, int[] y, char runDirection) {
        switch (runDirection) {
            case 'U':
                y[0] = y[0] - CELL_SIZE;
                break;
            case 'D':
                y[0] = y[0] + CELL_SIZE;
                break;
            case 'L':
                x[0] = x[0] - CELL_SIZE;
                break;
            case 'R':
                x[0] = x[0] + CELL_SIZE;
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to move the snake.
     */
    public void slither() {
        //Moves the body
        moveSnakeBody(x, y);

        //Moves the head
        moveSnakeHead(x, y, runDirection);
    }

    private String[] stageMusicPaths = {
        "",
        "Snake Sewers(The Demon is Dead).wav",
        "Snake Slums.wav",
        "snake middle class.wav",
        "Snake Royalty.wav",
        "REAL Main Menu.wav",
        "Castle Front.wav"
    };
    private SnakeMusic musicObject = new SnakeMusic();

    /**
     * This method changes the music based on the stage.
     */
    public void changeMusic() {
        if (!mutedMusic) {
            switch (gameState) {
                case titleState:
                    musicObject.playMusic(stageMusicPaths[0]);
                    break;
                case playState:
                    musicObject.playMusic(stageMusicPaths[stage]);
                    break;
                case winState:
                    musicObject.playMusic(stageMusicPaths[6]);
                    break;
                default:
                    break;
            }
        }
    }

    
    /**
     * This method calculates the stage based on the player's score.
     */
    public void stageCalculator() {
        
        int newStage = 0;

        if (applesEaten >= 25 && applesEaten < 50) {
            newStage = 2;
        } else if (applesEaten >= 50 && applesEaten < 75) {
            newStage = 3;
        } else if (applesEaten >= 75 && applesEaten < 100) {
            newStage = 4;
        } else if (applesEaten >= 100 && applesEaten < 125) {
            newStage = 5;
        } else if (applesEaten == 125) {
            newStage = 6;
            gameState = winState;
        } else {
            newStage = 1;
        }

        if (newStage != stage) {
            stage = newStage;
            changeMusic();
        }

    }

    /**
     * This method checks whether the snake has eaten an apple or not.
     * The snake eats an apple if the coordinates of the snake's head and those of the apple are 
     * the same. It also calculates the coordinates for the other fruits.
     * After the snake eats an apple, its size increases, the variable applesEaten increases,
     * and a new apple is spawned.
     */
    public void eatApple() {
        if ((x[0] == apple.xFruit) && (y[0] == apple.yFruit)) {
            applesEaten++;
            bodyParts++;
            spawnApple();
            lemon.spawnFruit();
            rottenApple.spawnFruit();
            blueberry.spawnFruit();
            orange.spawnFruit();
        }
    }

    /**
     * This method checks whether the snake has eaten an orange or not.
     * The snake eats an orange if the coordinates of the snake's head and those of the orange are 
     * the same.
     * After the snake eats an orange, its size increases by +2, the variable applesEaten increases
     * by +2, and a new orange is spawned.
     */
    public void eatOrange() {
        if ((x[0] == orange.xFruit) && (y[0] == orange.yFruit)) {
            applesEaten += 2;
            bodyParts += 2;
            spawnOrange();
        }
    }



    /**
     * This method checks whether the snake has eaten a rotten apple or not.
     * The snake eats a rotten apple if the coordinates of the snake's head and those of the 
     * rotten apple are the same.
     * After the snake eats a rotten apple, its size decreases by 1, the variable applesEaten 
     * decreases by 1, its speed decreases, and a new rotten apple is spawned.
     */
    public void eatRottenApple() {
        if ((x[0] == rottenApple.xFruit) && (y[0] == rottenApple.yFruit)) {
            applesEaten--;
            bodyParts--;
            currentDelay = currentDelay / 10 * 15;
            timer.setDelay(currentDelay);
            spawnRottenApple();
        }
    }

    /**
     * This method checks whether the snake has eaten a blueberry or not.
     * The snake eats a blueberry if the coordinates of the snake's head and those of the 
     * blueberry are the same.
     * After the snake eats a blueberry, its size increases by 1, the variable applesEaten 
     * increases by 1, its speed increases, and a new blueberry is spawned.
     */
    public void eatBlueberry() {
        if ((x[0] == blueberry.xFruit) && (y[0] == blueberry.yFruit)) {
            applesEaten++;
            bodyParts++;
            currentDelay = currentDelay * 95 / 100;
            timer.setDelay(currentDelay);
            spawnBlueberry();
            
        }
    }

    /**
     * This method checks whether the snake has eaten a lemon or not.
     * The snake eats a lemon if the coordinates of the snake's head and those of the lemon
     * are the same.
     * After the snake eats a lemon, its size increases by 1, the variable applesEaten 
     * increases by 1, the variable lemonsEaten increases by 1, 
     * thus giving the snake the ability to slither over itself,
     * and a new rotten apple is spawned.
     */
    public void eatLemon() {
        if ((x[0] == lemon.xFruit) && (y[0] == lemon.yFruit)) {
            applesEaten++;
            lemonsEaten++;
            bodyParts++;
            spawnLemon();
        }
    }

    /**
     * This method checks if the snake's head collides with its body.
     * @param x is an array used to draw the snake.
     * @param y is another array used to draw the snake.
     */
    public void checkHeadToBody(int[] x, int[] y) {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
    }

    /**
     * This method checks if the snake's head collides with the borders.
     * @param x is an array used to draw the snake.
     * @param y is another array used to draw the snake.
     */
    public void checkHeadToBorder(int[] x, int[] y) {
        if (x[0] < 0) {
            running = false;
        }
        if (x[0] >= WINDOW_WIDTH) {
            running = false;
        }
        if (y[0] < 0) {
            running = false;
        }
        if (y[0] >= WINDOW_HEIGHT) {
            running = false;
        }
    }

    /**
     * This method checks if the snake runs out of bounds, or if it collides with itself.
     */
    public void checkCollision() {
        //checks if the head touches the body
        if (lemonsEaten == 0) {
            checkHeadToBody(x, y);
        }
        
        //checks if the head touches any of the borders
        checkHeadToBorder(x, y);
    }
    
    /**
     * This method checks for the user's input.
     * @param event contains the data of the user's input on the keyboard.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (running && gameState == playState) {
            slither();
            eatApple();
            checkCollision();
            stageCalculator();
            eatOrange();
            eatRottenApple();
            eatBlueberry();
            eatLemon();
            checkFruitConditions();
        }
        repaint();
    }

    /**
     * This method draws the options screen.
     * @param g represents all the components drawn sequentially.
     */
    public void drawOptionsScreen(Graphics g) {
        this.setBackground(stageBackgroundColor[0]);
        g.setFont(new Font("Agency FB", Font.BOLD, 30));
        g.setColor(Color.YELLOW);
        String muteMusic = "MUTE (SELECT, THEN PRESS 'M')";
        int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(muteMusic)) / 2;
        int y = WINDOW_HEIGHT / 2 + 50;
        g.drawString(muteMusic, x, y); 
        if (optionsScreenCommandNo == 0) {
            g.setColor(Color.BLUE);
            g.drawString(">", x - 2 * CELL_SIZE, y);
            g.setColor(Color.YELLOW);
        }
        
        String gameOptionsExit = "EXIT";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(gameOptionsExit)) / 2;
        y = WINDOW_HEIGHT / 2 + 150;
        g.drawString(gameOptionsExit, x, y); 
        if (optionsScreenCommandNo == 2) {
            g.setColor(Color.BLUE);
            g.drawString(">", x - 2 * CELL_SIZE, y);
            g.setColor(Color.YELLOW);
        }
        
        String backCommand = "BACK (SELECT, THEN PRESS 'B')";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(backCommand)) / 2;
        y = WINDOW_HEIGHT / 2 + 100;
        g.drawString(backCommand, x, y); 
        if (optionsScreenCommandNo == 1) {
            g.setColor(Color.BLUE);
            g.drawString(">", x - 2 * CELL_SIZE, y);
            g.setColor(Color.YELLOW);
        }
    }

    /**
     * This class reads the event according to each keyboard press used to control the snake.
     */
    public class SnakeKeyAdapter extends KeyAdapter {
        
        @Override
        public void keyPressed(KeyEvent event) {
            if (gameState == titleState) {
                if (event.getKeyCode() == KeyEvent.VK_UP) {
                    titleScreenCommandNo--;
                    if (titleScreenCommandNo < 0) {
                        //makes the cursor go back down, and not disappear
                        titleScreenCommandNo = 2;
                    }
                }
                if  (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    titleScreenCommandNo++;
                    if (titleScreenCommandNo > 2) {
                        //makes the cursor go back up, and not disappear
                        titleScreenCommandNo = 0;
                    }
                }
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (titleScreenCommandNo == 0) {
                        //starts the game
                        gameState = playState;
                    } else if (titleScreenCommandNo == 1) {
                        //opens the options screen
                        gameState = optionsState;
                    } else {
                        //exits the game
                        System.exit(0);
                    }
                }
            }
            
            if (gameState == optionsState) {
                drawOptionsScreen(getGraphics());
                if (event.getKeyCode() == KeyEvent.VK_UP) {
                    optionsScreenCommandNo--;
                    if (optionsScreenCommandNo < 0) {
                        //makes the cursor go back down, and not disappear
                        optionsScreenCommandNo = 2;
                    }
                }
                if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    optionsScreenCommandNo++;
                    if (optionsScreenCommandNo > 2) {
                        //makes the cursor go back up, and not disappear
                        optionsScreenCommandNo = 0;
                    }
                }

                if (optionsScreenCommandNo == 0) {
                    if (event.getKeyCode() == KeyEvent.VK_M) {
                        //mutes the music
                        musicObject.muteMusic();
                        mutedMusic = true;
                    }
                } else if (optionsScreenCommandNo == 1) {
                    if (event.getKeyCode() == KeyEvent.VK_B) {
                        //goes back to the title screen
                        gameState = titleState;
                    }
                } else {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        //exits the game
                        System.exit(0);
                    }
                }

            }

            if (gameState == gameOverState) {
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    //exits the game
                    System.exit(0);
                }
            }

            if (gameState == winState) {
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    //exits the game
                    System.exit(0);
                }
            }

            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    //makes the snake turn left
                    if (runDirection != 'R' && gameState == playState) {
                        runDirection = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    //makes the snake turn right
                    if (runDirection != 'L' && gameState == playState) {
                        runDirection = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    //makes the snake slither up
                    if (runDirection != 'D' && gameState == playState) {
                        runDirection = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    //makes the snake slither down
                    if (runDirection != 'U' && gameState == playState) {
                        runDirection = 'D';
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (gameState == playState) {
                        //pauses the game
                        gameState = pauseState;
                    } else if (gameState == pauseState) {
                        //resumes the game
                        gameState = playState;
                    }
                    break;
                default:
                    break;
            }
        }


    }
    
}