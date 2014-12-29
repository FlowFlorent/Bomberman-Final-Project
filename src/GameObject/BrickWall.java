/**
 * Created by danielmacario on 14-11-02.
 */
package GameObject;

import javax.swing.*;
import java.io.Serializable;

/**
 * Used to represent the BrickWall objects placed on the grid at fixed
 * positions during GamePlay. Note that this object falls in the category of static objects,
 * and it provides no other logic than the one needed for collision detection and disappearance once
 * it is hit by a bomb explosion.
 */
public class BrickWall extends StaticObject implements Serializable {

    private boolean hasPowerUp;

    /**
     * BrickWall constructor. Contains position attributes of the object, and two booleans
     * used to control the logic behind its disappearance.
     * @param posX x position of the BrickWall on the grid
     * @param posY y position of the BrickWall on the grid
     * @param visible boolean representing whether the wall is visible to the player or not
     * @param hasPowerUp boolean representing whether the wall has a powerup hidden under its image
     *                   or not
     */
    public BrickWall(int posX, int posY, boolean visible, boolean hasPowerUp) {
        this.imagePath = "/res/image/brickBlock.png";
        this.posX = posX;
        this.posY = posY;
        this.previousX = posX;
        this.visible = visible;
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.hasPowerUp = hasPowerUp;
    }

    /**
     * Toggle the visibility of the tile to false.
     */
    public void disappear() {
        this.visible = false;
    }

}
