package GameObject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MovableObjectTest {

    private MovableObject movableObject;

    @Before
    public void makeMovableObject(){
        movableObject = new MovableObject();
    }

    @Test
    public void testMove() throws Exception {
        movableObject.setPosX(30);
        movableObject.setPosY(30);
        movableObject.setDeltaX(5);
        movableObject.setDeltaY(5);
        assertEquals("X coordinate of movableObject should be 30 before move() is used", 30, movableObject.getPosX());
        assertEquals("Y coordinate of movableObject should be 30 before move() is used", 30, movableObject.getPosY());
        movableObject.move();
        assertEquals("X coordinate of movableObject should be 35 after move() is used", 35, movableObject.getPosX());
        assertEquals("Y coordinate of movableObject should be 35 after move() is used", 35, movableObject.getPosY());

    }

    @Test
    public void testMoveVertically() throws Exception {
        movableObject.setPosY(30);
        movableObject.setDeltaY(5);
        assertEquals("Y coordinate of movableObject should be 30 before moveVertically() is used", 30, movableObject.getPosY());
        movableObject.moveVertically();
        assertEquals("Y coordinate of movableObject should be 35 after moveVertically() is used", 35, movableObject.getPosY());

    }

    @Test
    public void testRestorePreviousPosition() throws Exception {
        movableObject.setPosX(30);
        movableObject.setPosY(30);
        movableObject.setDeltaX(5);
        movableObject.setDeltaY(5);
        assertEquals("X coordinate of movableObject should be 30 before restorePreviousPosition() is used", 30, movableObject.getPosX());
        assertEquals("Y coordinate of movableObject should be 30 before restorePreviousPosition() is used", 30, movableObject.getPosY());
        movableObject.move();
        movableObject.restorePreviousPosition();
        assertEquals("X coordinate of movableObject should be 30 after restorePreviousPosition()is used", 30, movableObject.getPosX());
        assertEquals("Y coordinate of movableObject should be 30 after restorePreviousPosition() is used", 30, movableObject.getPosY());
    }

    @Test
    public void testRestorePreviousXPosition() throws Exception {
        movableObject.setPosX(30);
        movableObject.setDeltaX(5);
        assertEquals("X coordinate of movableObject should be 30 before restorePreviousXPosition() is used", 30, movableObject.getPosX());
        movableObject.move();
        movableObject.restorePreviousXPosition();
        assertEquals("X coordinate of movableObject should be 30 after restorePreviousXPosition()is used", 30, movableObject.getPosX());

    }

    @Test
    public void testRestorePreviousYPosition() throws Exception {
        movableObject.setPosY(30);
        movableObject.setDeltaY(5);
        assertEquals("Y coordinate of movableObject should be 30 before restorePreviousYPosition() is used", 30, movableObject.getPosY());
        movableObject.move();
        movableObject.restorePreviousYPosition();
        assertEquals("Y coordinate of movableObject should be 30 after restorePreviousYPosition()is used", 30, movableObject.getPosY());
    }

    @Test
    public void testRestorePositionTo() throws Exception {
        movableObject.setPosX(30);
        movableObject.setPosY(30);
        movableObject.setDeltaX(5);
        movableObject.setDeltaY(5);
        assertEquals("X coordinate of movableObject should be 30 before restorePositionTo() is used", 30, movableObject.getPosX());
        assertEquals("Y coordinate of movableObject should be 30 before restorePositionTo() is used", 30, movableObject.getPosY());
        movableObject.move();
        movableObject.restorePositionTo(30, 30);
        assertEquals("X coordinate of movableObject should be 30 after restorePositionTo() is used", 30, movableObject.getPosX());
        assertEquals("Y coordinate of movableObject should be 30 after restorePositionTo() is used", 30, movableObject.getPosY());

    }
}