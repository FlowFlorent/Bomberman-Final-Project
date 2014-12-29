/**
 * Created by danielmacario on 14-10-31.
 */
package GameObject;

import java.io.Serializable;

/**
 * Defines the common functionality shared by all the objects that can move
 * on the grid.
 */
public class MovableObject extends GameObject implements Serializable {

    public static final int SLOWSPEED = 2;
    public static final int NORMALSPEED = 3;
    public static final int FASTSPEED= 4;

    protected int speed;
    protected int deltaX;
    protected int deltaY;
    protected int score;
    protected boolean wallPass;

    /**
     * Displaces the object on the grid based on the values of the deltaX
     * and the deltaY variables.
     */
    public void move() {
        previousX = posX;
        previousY = posY;
        posX += deltaX;
        posY += deltaY;
    }

    /**
     * Displaces the object on the y axis based on the value of the posY variable.
     */
    public void moveVertically() {
        posY += deltaY;
        previousY = posY;
    }

    /**
     * Restores the position of the Movable Object to what it was in the previous frame.
     */
    public void restorePreviousPosition() {
        posX = previousX;
        posY = previousY;
    }

    /**
     * Restores the x position of the Movable Object to what it was in the previous frame.
     */
    public void restorePreviousXPosition() {
        posX = previousX;
    }

    /**
     * Restores the y position of the Movable Object to what it was in the previous frame.
     */
    public void restorePreviousYPosition() {
        posY = previousY;
    }

    /**
     * Restores the position of the object to a specific set of coordinates
     * @param x An integer representing the x coordinate on the grid where the MovableObject
     *          will be rendered.
     * @param y An integer representing the y coordinate on the grid where the MovableObject
     *          will be placed.
     */
    public void restorePositionTo(int x, int y) {
        posX = x;
        posY = y;
    }

    /**
     * Retrieve the speed attribute of the MovableObject
     * @return an integer representing the speed of the MovableObject
     */
    public int getSpeed() {
        return this.speed;
    }

    /**
     * Modify the speed attribute of the MovableObject
     * @param speed Set the speed of the MovableObject instance
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Retrieve the change in the x coordinate of the MovableObject
     * @return Integer deltaX representing change in x coordinate of MovableObject
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Modify the x coordinate of the MovableObject
     * @param deltaX Set the x coordinate of the MovableObject instance
     */
    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }
    /**
     * Retrieve the change in the y coordinate of the MovableObject
     * @return Integer deltaY representing change in y coordinate of MovableObject
     */
    public int getDeltaY() {
        return deltaY;
    }

    /**
     * Modify the y coordinate of the MovableObject
     * @param deltaY Set the y coordinate of the MovableObject instance
     */
    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    /**
     * Get the wallPass attribute associated with the MovableObject
     * @return A boolean specifying whether the object can pass through
     * brick walls or not.
     */
    public boolean hasWallPass() {
        return wallPass;
    }

    /**
     * Set the wallPass attribute associated with the MovableObject
     * @param wallPass A boolean specifying whether the object can pass
     *                 through brick walls or not.
     */
    public void setWallPass(boolean wallPass) {
        this.wallPass = wallPass;
    }

    /**
     * Get the score attribute associated with the MovableObject
     * @return Integer representing the score of the object
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the wallPass attribute associated with the MovableObject
     * @param score An integer representing the score of the object
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the x coordinate of the MovableObject
     * @return Integer representing x the coordinate of the MovableObject
     */
    public int getPosX(){
        return posX;
    }

    /**
     *  Sets the x coordinate of the MovableObject
     * @param posX An integer representing the x position where the object will be rendered on the Game Grid.
     */
    public void setPosX(int posX){
        this.posX = posX;
    }

    /**
     * Gets the y coordinate of the MovableObject
     * @return Integer representing y the coordinate of the MovableObject
     */
    public int getPosY(){
        return posY;
    }

    /**
     *  Sets the y coordinate of the MovableObject
     * @param posY An integer representing the y position where the object will be rendered on the Game Grid.
     */
    public void setPosY(int posY){
        this.posY = posY;
    }
}
