/**
 * Created by danielmacario on 14-10-31.
 */
package GameObject;

import java.io.Serializable;
import java.util.Random;

/**
 * Defines the motions that a movable object can perform in the game.
 */
public enum Direction implements Serializable {
    NORTH, SOUTH, EAST, WEST;
    public static Random random = new Random();

    public static final Direction[] DIRECTIONS = {NORTH, SOUTH, EAST, WEST};
    public static final Direction[] NSDIRECTIONS = {NORTH, SOUTH};
    public static final Direction[] EWDIRECTIONS = {EAST, WEST};

    /**
     * Get a random Direction. Used to determine the motion of enemies upon spawning,
     * and also when their artificial intelligence requirements are met.
     * @return A randomly selected Direction object representing one of the four cardinal directions.
     */
    public static Direction getRandomDirection() {
        int index = random.nextInt(DIRECTIONS.length);
        return DIRECTIONS[index];
    }

    /**
     * Return a random perpendicular direction object to the one passed to the function.
     * For example, a call to the function with Direction being set to EAST would randomly
     * selected between the perpendicular alternatives of NORTH or SOUTH.
     * @param direction The direction specifying the perpendicular set that will be considered
     *                  in the selection process.
     * @return A Direction object that is perpendicular to the one passed to the function.
     */
    public static Direction getRandomPerpendicularDirection(Direction direction) {
        int index = random.nextInt(2);
        if (direction == NORTH || direction == SOUTH) {
            return EWDIRECTIONS[index];
        } else {
            return NSDIRECTIONS[index];
        }
    }


}
