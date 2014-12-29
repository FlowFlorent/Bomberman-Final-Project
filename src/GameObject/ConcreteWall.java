/**
 * Created by danielmacario on 14-11-01.
 */
package GameObject;

import javax.swing.*;
import java.io.Serializable;

/**
 * Used to represent the ConcreteWall objects placed on the grid at fixed
 * positions during GamePlay. Note that this object falls in the category of static objects,
 * and it provides no other logic than the one needed for collision detection.
 */
public class ConcreteWall extends StaticObject implements Serializable {

    /**
     * Contains position attributes of the object.
     * @param posX x position of the ConcreteWall on the grid
     * @param posY y position of the ConcreteWall on the grid
     */
    public ConcreteWall(int posX, int posY) {
        this.imagePath = "/res/image/concreteBlock.png";
        this.posX = posX;
        this.posY = posY;
        this.previousX = posX;
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.visible = true;
    }

}
