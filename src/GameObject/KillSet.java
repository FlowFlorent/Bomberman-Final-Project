/**
 * Created by danielmacario on 14-11-17.
 */
package GameObject;

import GamePlay.Coordinate;
import java.io.Serializable;

/**
 * Class used as a container to store the data needed to calculate the score
 * obtained from a bomb blast. We keep track of the location of the bomb that
 * killed the passed enemy, as well as the location of the death. With this information
 * We can calculate the distance between the bomb blast and the enemy, which we use to
 * determine which enemies died first from a bomb blast.
 */
public class KillSet implements Serializable {

    private Coordinate locationOfDeath;
    private Coordinate locationOfBomb;
    private Enemy enemy;
    private int score;
    private int distanceToBombOrigin;

    /**
     * Initialize a KillSet object containing the information regarding the death of an enemy.
     * @param locationOfDeath A Coordinate object representing where the enemy died.
     * @param locationOfBomb A Coordinate object representing where the bomb exploded.
     * @param enemy The enemy object that was killed by the bomb blast.
     */
    public KillSet(Coordinate locationOfDeath, Coordinate locationOfBomb, Enemy enemy) {
        this.locationOfDeath = locationOfDeath;
        this.locationOfBomb = locationOfBomb;
        this.enemy = enemy;
        this.distanceToBombOrigin = locationOfBomb.distanceTo(locationOfDeath);
        this.score = enemy.getScore();
    }

    /**
     * Get the coordinate object specifying where the enemy died.
     * @return Coordinate where the death occurred.
     */
    public Coordinate getLocationOfDeath() {
        return locationOfDeath;
    }

    /**
     * Set the coordinate where the enemy died.
     * @param locationOfDeath A coordinate specifying where the enemy died on the grid.
     */
    public void setLocationOfDeath(Coordinate locationOfDeath) {
        this.locationOfDeath = locationOfDeath;
    }

    /**
     * Get the location where the bomb exploded.
     * @return A coordinate specifying where the bomb exploded.
     */
    public Coordinate getLocationOfBomb() {
        return locationOfBomb;
    }

    /**
     * Set the location where the bomb exploded
     * @param locationOfBomb A coordinate specifying where the bomb exploded.
     */
    public void setLocationOfBomb(Coordinate locationOfBomb) {
        this.locationOfBomb = locationOfBomb;
    }

    /**
     * Get the enemy object that died from the bomb blast.
     * @return The enemy object that died from the bomb blast.
     */
    public Enemy getEnemy() {
        return enemy;
    }

    /**
     * Set the Enemy obect that died from the bomb blast.
     * @param enemy The enemy object that died from the bomb blast.
     */
    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    /**
     * Get the distance from the bomb blast to the location where the enemy died.
     * @return An integer representing the distance from the bomb blast to the location where the enemy died.
     */
    public int getDistanceToBombOrigin() {
        return distanceToBombOrigin;
    }

    /**
     * Get the score associated with the dead enemy object.
     * @return An Integer representing the score value of the dead enemy.
     */
    public int getScore() {
        return score;
    }
}
