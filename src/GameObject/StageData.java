/**
 * Created by danielmacario on 14-11-12.
 */
package GameObject;

import java.io.Serializable;

/**
 * Defines a model used to specify how stages can be created, and the specific interface needed
 * to generate the enemy and powerup information on the Grid.
 */
public class StageData implements Serializable {

    private EnemySet[] enemiesPresent;
    private PowerUpType powerUpPresent;
    private boolean isBonusStage;

    /**
     * Initialize a StageData object which is used to define the Data elements present in a given stage,
     * specifically, the enemy types present and their numbers, the powerUp type present, and whether
     * the stage is a bonus stage or not.
     * @param enemiesPresent An array of EnemySet objects containing the enemyType and multiplicity
     *                       present on the game grid.
     * @param powerUpPresent A PowerUpType object specifying the powerUp present in the stage
     * @param isBonusStage A boolean specifying whether the stage is a Bonus stage or not.
     */
    public StageData(EnemySet[] enemiesPresent, PowerUpType powerUpPresent, boolean isBonusStage) {
        this.enemiesPresent = enemiesPresent;
        this.powerUpPresent = powerUpPresent;
        this.isBonusStage = isBonusStage;
    }

    /**
     * Get the data regarding which enemies are present in the stage.
     * @return An array of EnemySet objects specifying the enemy types present on
     * the stage and their multiplicity.
     */
    public EnemySet[] getEnemiesPresent() {
        return enemiesPresent;
    }

    /**
     * Set the types and multiplicity of the enemies present in a specific stage.
     * @param enemiesPresent An array of EnemySet objects containing the enemyType and multiplicity
     *                       of the enemies present on the game grid.
     */
    public void setEnemiesPresent(EnemySet[] enemiesPresent) {
        this.enemiesPresent = enemiesPresent;
    }

    /**
     * Get the powerUp type present in the stage.
     * @return A PowerUpType object specifying the kind of powerUp
     * present in the stage.
     */
    public PowerUpType getPowerUpPresent() {
        return powerUpPresent;
    }

    /**
     * Specify the type of powerUp present in the stage represented by this instance
     * of StageData.
     * @param powerUpPresent A PowerUpType object specifying the powerUp present in the stage.
     */
    public void setPowerUpPresent(PowerUpType powerUpPresent) {
        this.powerUpPresent = powerUpPresent;
    }

    /**
     * Get the boolean representing whether the stage represented by this StageData instance
     * is a bonus stage or not.
     * @return A boolean representing whether the stage represented by this StageData instance
     * is a bonus stage or not.
     */
    public boolean isBonusStage() {
        return this.isBonusStage;
    }

    /**
     * Specify whether the stage represented by this StageData object is a bonus stage or not.
     * @param isBonusStage A boolean specifying whether the stage represented by this stage data
     *                     object is a Bonus stage or not.
     */
    public void setBonusStage(boolean isBonusStage) {
        this.isBonusStage = isBonusStage;
    }
}
