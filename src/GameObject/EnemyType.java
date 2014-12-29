/**
 * Created by danielmacario on 14-11-12.
 */
package GameObject;

import java.io.Serializable;

/**
 * Enum containing the constants used to represent the Enemy types present in the game. This structure is used
 * to dynamically create enemies within the enemy class, keep track of the hardest enemyType present in a map,
 * and also specify the enemy sets present in each stage.
 */
public enum EnemyType implements Serializable {

    BALLOOM, DOLL, MINVO, OVAPI, ONEAL, PASS, PONTAN, KONDORIA

}
