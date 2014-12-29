/**
 * Created by danielmacario on 14-11-02.
 */
package GameObject;

import SystemController.SoundController;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * This class creates an object used to represent the bombs placed by the player
 * during gameplay. It includes the logic relevant to triggering an explosion, and the timing of
 * that same event.
 */
public class Bomb extends StaticObject implements Serializable {

    //Used to allow the player to walk over the bomb
    //only when he is standing over it during the initial
    //placement
    private boolean firstCollision;
    private int framesOnGrid;
    public static final int SPRITE_SIDE_LENGTH = 15;

    /**
     * Contains the image that represents the structure
     * and it defines the logic that is modified after placement.
     * @param posX Position X of the robot on the grid
     * @param posY Position Y of the robot on the grid
     */
    public Bomb(int posX, int posY) {
        this.imagePath = "/res/image/bomb.png";
        this.posX = posX;
        this.posY = posY;
        this.visible = true;
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.firstCollision = true;
        this.framesOnGrid = 0;
    }

    /**
     * Counts up to 90 frames after the bomb is placed and explodes after it crosses 90 frames
     */
    public void timeExplosion() {
        framesOnGrid++;
        if (framesOnGrid > 90) {
            explode();
        }
    }

    /**
     * Toggle the visibility of the object to false.
     * The bomb will be deleted in the next refresh of the screen.
     */
    public synchronized void explode() {
        SoundController.BOMBEXPLODE.play();
        this.visible = false;
    }

    /**
     * draw the bomb on the game grid.
     * @param g Graphics object used to render the image
     */
    public void draw(Graphics2D g) {
        timeExplosion();
        if (image == null) {
            this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        }
        if (visible) g.drawImage(image, posX, posY, null);
    }

    /**
     * Determine whether the player is colliding with the bomb for the first time
     * or not.
     * @return A boolean representing whether this is the first time the player
     * is colliding with the the Bomb
     */
    public boolean isFirstCollision() {
        return firstCollision;
    }

    /**
     * Set the first collision attribute of the bomb object.
     * @param firstCollision boolean representing whether the player object has collided
     *                       with this bomb instance in the past.
     */
    public void setFirstCollision(boolean firstCollision) {
        this.firstCollision = firstCollision;
    }

    /**
     * Get the number of frames that has passed since the bomb has been placed
     * @return Integer framesOnGrid representing how many frames have been passed since bomb placement
     */
    public int getFramesOnGrid() {
        return framesOnGrid;
    }

    /**
     *Sets the number of frames since the bomb has been placed
     * @param framesOnGrid Integer framesOnGrid representing number of frames that has passed since place of bomb
     */
    public void setFramesOnGrid(int framesOnGrid) {
        this.framesOnGrid = framesOnGrid;
    }
}
