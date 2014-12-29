package GamePlay;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoordinateTest {
    Coordinate coordinate1;
    Coordinate coordinate2;
    Coordinate coordinate3;
    Object coordinate4;

    @Before
    public void setup() throws Exception {
        coordinate1 = new Coordinate(0, 0);
        coordinate2 = new Coordinate(1, 1);
        coordinate3 = new Coordinate(0, 0);
        coordinate4 = new Object();
    }


    @Test
    public void testEquals() throws Exception {
        assertTrue("The overridden equals method should return true when the same " +
                "instance of coordinate is passed", coordinate1.equals(coordinate1));
        assertTrue("The overridden equals method should return true for coordinates " +
                "that represent the same location on the grid", coordinate1.equals(coordinate3));
        assertFalse("The overridden equals should return false for coordinates that are not the " +
                "same instance and do not represent the same location on the board", coordinate1.equals(coordinate2));
    }

    @Test
    public void testHashCode() throws Exception {
        int firstHashCode = coordinate1.hashCode();

        assertEquals("the overriden hashcode method follows the specific logic outlined", -4, firstHashCode);

        Coordinate coordinateX = new Coordinate(1,2);
        Coordinate coordinateY = new Coordinate(2,1);
        int hashCodeX = coordinateX.hashCode();
        int hasCodeXMirror = coordinateY.hashCode();

        assertNotEquals("The hashcode for mirroring coordinates should not be the same", hashCodeX, hasCodeXMirror);

    }


    @Test
    public void testDistanceTo() throws Exception {
        int result = coordinate1.distanceTo(coordinate2);
        assertEquals("The distance returned follows the logic defined ", (int) Math.sqrt((0 - 1) * (0 - 1) + (0 - 1) * (0 - 1)), result);

        result = coordinate1.distanceTo(coordinate1);
        assertEquals("The distance from a coordinate to itself should be 0", 0, result);

    }
}