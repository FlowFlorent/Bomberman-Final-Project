/**
 * Created by danielmacario on 14-10-31.
 */
package GameObject;

import javax.swing.*;
import java.io.Serializable;

/**
 * Used to represent the door object that allows the player to progress through the stages
 * of the game after having killed all the enemies present in a stage.
 */
public class Door extends StaticObject implements Serializable {

    boolean firstCollision;

    /**
     * Initialize a door object. Contains the image representing the door.
     * @param posX Position X of the door in the grid
     * @param posY Position Y of the door in the grid
     */
    public Door(int posX, int posY) {
        this.imagePath = "/res/image/door.png";
        this.posX = posX;
        this.posY = posY;
        this.visible = true;
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.firstCollision = true;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

}
