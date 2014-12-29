/**
 * Created by danielmacario on 14-11-13.
 */
package GameObject.ArtificialIntelligence;

import GameObject.Direction;
import GameObject.Enemy;

import java.io.Serializable;
import java.util.Random;

/**
 * Used as a template for the 3 different levels of artificial intelligence present in the game,
 * which are composed by 2 fundamental methods: chasePlayer which tracks and follows the player, and
 * move which specifies how the enemy behaves whether it is engaged in a chase or not.
 */
public abstract class ArtificialIntelligence implements Serializable {

    public static final Random random = new Random();

    /**
     * Abstract method implemented by the three levels of intelligence present in the game used
     * to control the movement of the enemy on the grid.
     * @param enemy Enemy object to be moved on the grid.
     */
    public abstract void move(Enemy enemy);

    /**
     * Abstract method implemented by the three intelligence levels present in the game.
     * @param playerPosX x coordinate of the player object on the grid.
     * @param playerPosY y coordinate of the player object on the grid.
     * @param distanceBetweenEnemyAndPlayer Distance between the center of the player object
     *                                      and the center of the enemy object.
     * @param enemy Enemy object that will start chasing the player.
     */
    public abstract void chasePlayer(int playerPosX, int playerPosY, int distanceBetweenEnemyAndPlayer, Enemy enemy);

    /**
     * Displaced the object in one of the four cardinal directions based on the directionOfMovement attribute
     */
    public void moveEnemyOnBoard(Enemy enemy) {
        int posY = enemy.getPosY();
        int posX = enemy.getPosX();
        int speed = enemy.getSpeed();

        // Note that the (0,0) coordinate is at the top-left corner of the grid,
        // and that the y increases going South.
        switch (enemy.getDirectionOfMovement()) {
            case NORTH:
                enemy.setPreviousY(posY);
                enemy.setPosY(posY - speed);
                break;
            case SOUTH:
                enemy.setPreviousY(posY);
                enemy.setPosY(posY + speed);
                break;
            case EAST:
                enemy.setPreviousX(posX);
                enemy.setPosX(posX + speed);
                break;
            case WEST:
                enemy.setPreviousX(posX);
                enemy.setPosX(posX - speed);
                break;
        }
    }

    /**
     * Sets the direction of movement of the passed enemy to the opposite in terms of cardinal direction.
     * For example, North would become South, East would become West, etc.
     */
    public void reverseDirection(Enemy enemy) {
        Direction directionOfMovement = enemy.getDirectionOfMovement();
        switch (directionOfMovement) {
            case NORTH:
                enemy.setDirectionOfMovement(Direction.SOUTH);
                break;
            case SOUTH:
                enemy.setDirectionOfMovement(Direction.NORTH);
                break;
            case EAST:
                enemy.setDirectionOfMovement(Direction.WEST);
                break;
            case WEST:
                enemy.setDirectionOfMovement(Direction.EAST);
                break;
        }
    }
}
