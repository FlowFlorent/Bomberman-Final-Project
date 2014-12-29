/**
 * Created by danielmacario on 14-11-12.
 */
package GameObject;

import java.io.Serializable;

/**
 * Used to represent the enemy sets present in each stage of the game. An EnemySet object
 * is defined by an EnemyType and their multiplicity in a given level. For example, stage 1 of
 * the game has a single EnemySet composed of 6 Balloom type enemies. The stages class uses this
 * representation to encode the 60 levels present in the game.
 */
public class EnemySet implements Serializable {

    private EnemyType enemyType;
    private int numberPresent;

    /**
     * Initializes an EnemySet object, which is specified by an EnemyType and
     * its multiplicity.
     * @param enemyType EnemyType object specifying what kind of enemy is contained in the set.
     * @param numberPresent Integer representing the multiplicity of the enemy in the set.
     */
    public EnemySet(EnemyType enemyType, int numberPresent) {
        this.enemyType = enemyType;
        this.numberPresent = numberPresent;
    }

    /**
     * Get the EnemyType contained by an EnemySet instance.
     * @return An EnemyType object specifying the enemy type contained in this instance.
     */
    public EnemyType getEnemyType() {
        return enemyType;
    }

    /**
     * Set the EnemyType that will be used to define the type of enemy contained in
     * a particular instance of EnemySet.
     * @param enemyType An EnemyType object specifying the type of enemy that this instance
     *                  of EnemySet should contain.
     */
    public void setEnemyType(EnemyType enemyType) {
        this.enemyType = enemyType;
    }

    /**
     * Get the number of enemies present of the type specified by an EnemySet.
     * @return An integer representing the number of enemies present of the type specified
     * by an EnemySet.
     */
    public int getNumberPresent() {
        return numberPresent;
    }

    /**
     * Set the number of enemies present in an EnemySet of the type specified by said object.
     * @param numberPresent Integer representing the multiplicity of the EnemyType contained in
     *                      the set.
     */
    public void setNumberPresent(int numberPresent) {
        this.numberPresent = numberPresent;
    }
}
