package GameObject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BrickWallTest {

    private BrickWall brickwall;

    @Before
    public void makeBrickWall() {
        brickwall = new BrickWall(35, 35, true, false);
    }

    @Test
    public void testDisappear() throws Exception {
        assertTrue("Wall is visible before disappear function", brickwall.isVisible());
        brickwall.disappear();
        assertFalse("Wall is no longer visible after disappear function", brickwall.isVisible());

    }
}