package GameObject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BombTest {

    private Bomb bomb;

    @Before
    public void makeBomb() {
        bomb = new Bomb(35, 35);
    }

    @Test
    public void testTimeExplosion() throws Exception {
        //int currentTime = bomb.getFramesOnGrid();
        assertEquals("When the bomb is created, framesOnGrid is equal 0", 0, bomb.getFramesOnGrid());
        bomb.timeExplosion();
        assertEquals("After calling timeExplosion, framesOnGrid increases by 1", 1, bomb.getFramesOnGrid());
        bomb.setFramesOnGrid(90);
        bomb.timeExplosion();
        assertFalse("Bomb visibility is set to false after 90 frames", bomb.isVisible());
    }

    @Test
    public void testExplode() throws Exception {
        assertTrue("Bomb is visible when placed", bomb.isVisible());
        bomb.explode();
        assertFalse("Bomb is no longer visible after exploding", bomb.isVisible());
    }

}