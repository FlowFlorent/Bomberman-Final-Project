/**
 * Created by danielmacario on 14-11-01.
 */
package GameObject;

import java.io.Serializable;

/**
 * Defines the common functionality shared by all the objects that cannot move
 * on the grid.
 */
public class StaticObject extends GameObject implements Serializable {

    protected boolean firstCollision;

    /**
     * Determine whether the player is colliding with this StaticObject instance for the first time
     * or not.
     * @return A boolean representing whether this is the first time the player
     * is colliding with StaticObject.
     */
    public boolean isFirstCollision() {
        return firstCollision;
    }

    /**
     * Specify whether the player is colliding with this StaticObject instance for the first time
     * or not.
     * @param firstCollision boolean representing whether the player object has collided
     *                       with this StaticObject instance in the past or not.
     */
    public void setFirstCollision(boolean firstCollision) {
        this.firstCollision = firstCollision;
    }


}
