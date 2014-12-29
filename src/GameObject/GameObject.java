/**
 * Created by danielmacario on 14-10-31.
 */
package GameObject;

import GamePlay.Coordinate;

import java.awt.*;
import javax.swing.ImageIcon;
import java.io.Serializable;

/**
 * Parent class to every object present in the game. Defines fundamental attributes present in every object
 * that is rendered on the game grid. Namely, position on the grid, visibility, the image object associated, and
 * the dimensions of the object.
 */
public class GameObject implements Serializable {

    //Actual coordinates on the grid
    protected String imagePath;
    protected int posX;
    protected int posY;

    //Coordinates where the object was rendered on the last frame
    protected int previousX;
    protected int previousY;

    //Width and height of the image object used to represent the Game Object.
    protected int width;
    protected int height;
    protected transient Image image;

    //Boolean that specifies whether the Game Object should be rendered on the grid or not.
    protected boolean visible;

    /**
     * Draws the GameObject instance on the Game Grid.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    public void draw(Graphics2D g) {
        if (image == null) {
            image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
            this.width = image.getWidth(null);
            this.height = image.getHeight(null);
        }
        g.drawImage(image, posX, posY, null);
    }

    /**
     * Get the bounds of the image that represents the Game Object.
     * Used for determining intersection between objects.
     * @return A Rectangle object containing the dimensions of the Game Object.
     */
    public Rectangle getBounds() {
        return new Rectangle(posX, posY, width, height);
    }

    /**
     * Get the x position of the Game Object on the grid.
     * @return An integer representing the x position on the grid.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Set the x position of the object on the grid. Where the object is rendered depends
     * directly on this position.
     * @param posX An integer representing the x position where the object will be rendered on the Game Grid.
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Get the y position of the Game Object on the grid.
     * @return An integer representing the y position on the grid.
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Set the y position of the object on the grid. Where the object is rendered depends
     * directly on this position.
     * @param posY An integer representing the y position where the object will be rendered on the Game Grid.
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Get the previous x coordinate where the object was rendered in the last frame.
     * Used to restore the position of an object after a collision.
     * @return An integer representing the x coordinate where the object was rendered in the previous frame.
     */
    public int getPreviousX() {
        return previousX;
    }

    /**
     * Set the previous x coordinate where the object was rendered in the last frame.
     * This method gets called every time the x coordinate of the object will be modified.
     * @param previousX The previous x coordinate where the object was rendered in the previous frame.
     */
    public void setPreviousX(int previousX) {
        this.previousX = previousX;
    }

    /**
     * Get the previous y coordinate where the object was rendered in the last frame.
     * Used to restore the position of an object after a collision.
     * @return An integer representing the y coordinate where the object was rendered in the previous frame.
     */
    public int getPreviousY() {
        return previousY;
    }

    /**
     * Set the previous y coordinate where the object was rendered in the last frame.
     * This method gets called every time the y coordinate of the object will be modified.
     * @param previousY The previous y coordinate where the object was rendered in the previous frame.
     */
    public void setPreviousY(int previousY) {
        this.previousY = previousY;
    }

    /**
     * Returns the width of the Image used to represent the Game Object on the game grid.
     * @return An integer specifying the width of the Game Object on the game grid.
     */
    public int getWidth() {
        return width;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the height of the Image used to represent the Game Object on the game grid.
     * @return An integer specifying the height of the Game Object on the game grid.
     */
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the Image object used to contain the image associated with the Game Object.
     * @return The Image object associated with the Game Object.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the Image object that will be rendered when the object is drawn.
     * @param image The Image object used to represent the GameObject on the Game Grid.
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Gets the visibility status of the Game Object.
     * @return A boolean representing whether the Game Object is visible on the grid or not.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set the visibility status of the Game Object.
     * @param visible A boolean representing whether the Game Object is visible on the grid or not.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get the position of the object as an instance of Coordinate
     * @return A coordinate object containing the x and y position of the enemy
     */
    public Coordinate getPositionAsCoordinate() {
        return new Coordinate(posX, posY);
    }
}
