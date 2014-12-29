/**
 * Created by danielmacario on 14-11-02.
 */
package GamePlay;

import GameObject.Player;
import SystemController.GameController;
import java.io.Serializable;

/**
 * The Camera class manages the user's view of the game. It enables
 * the scrolling of the screen when the player has more than 15 columns
 * of tiles on either side.
 */
public class Camera implements Serializable {

    private int posX;
    private Player player;

    public Camera(int posX, Player player) {
        this.posX = posX;
        this.player = player;
    }

    /**
     * Adjust the position of the camera to follow the player.
     */
    public void adjustPosition() {
        posX = -player.getPosX() + GameController.WIDTH/2 - 18;
    }

    /**
     * @return Returns the x-position of an object in the form of an int.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Sets X position where the camera is looking.
     * @param posX An integer representing the x coordinate where the camera
     *             should focus on.
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

}
