/**
 * Created by danielmacario on 14-11-06.
 */
package GameObject;

import GamePlay.Coordinate;

import javax.swing.*;
import java.io.Serializable;

/**
 * Used to represent the flame object that is displayed once a bomb explodes. The collision manager class
 * relies on the flame object to enable most of the gameplay logic, specifically: when the player should die,
 * when a bomb should detonate another bomb, when the enemies should die, when the powerUp should disappear,
 * and when a harder set of enemies should be spawned (flame collision with powerUp or door).
 */
public class Flame extends StaticObject implements Serializable {

    private int timeOnGrid;
    private int explosionOriginX;
    private int explosionOriginY;

    /**
     * A Flame object is defined by a position on the grid, and a boolean representing whether it is
     * visible or not. We keep track of the time it will remain visible in terms of the number of frames
     * it should be rendered on the screen.
     * @param posX x coordinate of the flame on the grid.
     * @param posY y coordinate of the flame on the grid.
     * @param visible boolean representing whether the flame can be seen or not.
     * @param explosionOriginX x coordinate where the bomb exploded.
     * @param explosionOriginY y coordinate where the bomb exploded.
     */
    public Flame(int posX, int posY, boolean visible, int explosionOriginX, int explosionOriginY) {
        this.imagePath = "/res/image/flames.png";
        this.posX = posX;
        this.posY = posY;
        this.previousX = posX;
        this.visible = visible;
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.timeOnGrid = 0;
        this.explosionOriginX = explosionOriginX;
        this.explosionOriginY = explosionOriginY;
    }

    /**
     * Increment the number of frames the object has been rendered on the grid. This function
     * keeps track of the number of frames that remain until the Flame object is removed from the grid.
     */
    public void incrementTimeOnGrid() {
        this.timeOnGrid++;
        if (timeOnGrid > 20) {
            this.visible = false;
        }
    }

    /**
     * Retrieves the x coordinate specifying where the bomb that generated this flame exploded.
     * @return The x coordinate specifying where the bomb the bomb that generated this flame exploded.
     */
    public int getExplosionOriginX() {
        return explosionOriginX;
    }

    /**
     * Sets the x coordinate where the the bomb that generated this flame exploded.
     * @param explosionOriginX Integer representing the x coordinate where the bomb that generated
     *                         this flame occurred.
     */
    public void setExplosionOriginX(int explosionOriginX) {
        this.explosionOriginX = explosionOriginX;
    }

    /**
     * Retrieve the y coordinate of where the explosion occurred.
     * @return Integer representing the y coordinate where the explosion occurred.
     */
    public int getExplosionOriginY() {
        return explosionOriginY;
    }

    /**
     * Sets the y-coordinate where the bomb that generated this flame exploded.
     * @param explosionOriginY Integer representing the y coordinate where the bomb
     *                         that generated this flame exploded.
     */
    public void setExplosionOriginY(int explosionOriginY) {
        this.explosionOriginY = explosionOriginY;
    }

    /**
     * Retrieves the coordinate where the bomb exploded.
     * @return A coordinate representing the center of the bomb sprite that generated
     * this flame object.
     */
    public Coordinate getExplosionOriginAsCoordinate() {
        return new Coordinate(explosionOriginX + Bomb.SPRITE_SIDE_LENGTH, explosionOriginY + Bomb.SPRITE_SIDE_LENGTH);
    }
}
