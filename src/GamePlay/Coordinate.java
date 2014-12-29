/**
 * Created by danielmacario on 14-11-12.
 */
package GamePlay;

import java.io.Serializable;

/**
 * The Coordinate class is used to keep track of the positions of the objects
 * drawn on the grid.
 */
public class Coordinate implements Serializable  {

    private int row;
    private int col;

    /**
     * Intialize a Coordinate object.
     * @param row An integer representing a row on the grid.
     * @param col An integer representing a column on the grid.
     */
    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * We override the equality function of the coordinate class to check if the
     * contents of two different coordinate instances are the same. Used throughout
     * the application to test if two objects are located at the same location on the
     * game grid.
     * @param coordinate The coordinate object to be tested for equality.
     * @return Returns a boolean specifying whether the coordinate instance passed
     * is equal to the one one contained in this instance.
     */
    @Override
    public boolean equals(Object coordinate) {

        // Check if the two references point to the same coordinate.
        if (this == coordinate) {
            return true;
        } else if (!(coordinate instanceof Coordinate)) {
            return false;
        }

        Coordinate modifiedOther = (Coordinate) coordinate;
        if (row == modifiedOther.getRow() && col == modifiedOther.getCol()) return true;
        else return false;
    }

    /**
     * We override the hashCode definition of the coordinate class so that
     * we can hash coordinates with the same contents into equal positions
     * inside a hash.
     * @return An integer specifying the unique hashcode of a coordinate object.
     */
    @Override
    public int hashCode() {
        return row * col + row - 4;
    }

    /**
     * @return Returns the row where position of the object.
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row of an object.
     * @param row An integer representing a row on the grid.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return Returns the column position of the object
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the column of an object
     * @param col An integer representing a column on the grid.
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Calculates thee distance between two coordinate object.
     * @param coordinate The end coordinate that we are trying to
     *                   find the distance to.
     * @return Returns An integer representing the distance between
     * two coordinates.
     */
    public int distanceTo(Coordinate coordinate) {
        return (int) Math.sqrt( (row - coordinate.getRow()) *  (row - coordinate.getRow()) + (col - coordinate.getCol()) *  (col - coordinate.getCol()));
    }

}
