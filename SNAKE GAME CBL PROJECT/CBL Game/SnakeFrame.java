import javax.swing.*;

/*
 * @author: Horia-George Dună
 * @id: 1949284
 * @author: Radu-Cristian Sarău
 * @id: 1939149 
 */

/**
 * "SnakeFrame" class.
 */
public class SnakeFrame extends JFrame {
    
    //Game Icon
    private ImageIcon snakeIcon = new ImageIcon("snakeAppIcon.png");

    /**
     * Constructor for the frame of the game.
     */
    SnakeFrame() {
        this.add(new SnakePanel());
        this.setTitle("Snake Game");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setIconImage(snakeIcon.getImage());
        this.setLocationRelativeTo(null); //place it in the middle of the screen
    }

}