import java.util.Random;

/*
 * @author: Horia-George Dună
 * @id: 1949284
 * @author: Radu-Cristian Sarău
 * @id: 1939149 
 */

/**
 * This class defines the fruits of this game.
 */
public class Fruit {
    
    Random random = new Random();
    int xFruit;
    int yFruit;
    static final int WINDOW_WIDTH = 700;
    static final int WINDOW_HEIGHT = 700;
    static final int CELL_SIZE = 25;
    static final int WINDOW_CELLS = (WINDOW_HEIGHT * WINDOW_WIDTH) / CELL_SIZE;
    static final int DELETE_COORDINATE = -69;
    
    /**
     * This method is used to spawn fruits on the grid.
     */
    public void spawnFruit() {
        xFruit = random.nextInt((int) (WINDOW_WIDTH / CELL_SIZE)) * CELL_SIZE;
        yFruit = random.nextInt((int) (WINDOW_HEIGHT / CELL_SIZE)) * CELL_SIZE;
    }

    /**
     * This method is used to delete fruits from the grid.
     */
    public void deleteFruit() {
        xFruit = DELETE_COORDINATE;
        yFruit = DELETE_COORDINATE;
    }
}