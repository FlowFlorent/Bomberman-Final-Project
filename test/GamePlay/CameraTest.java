package GamePlay;

import GameObject.Player; //import package name.class name
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class CameraTest {
    private Camera cam;

    @Before
    public void setup() throws Exception {
        Player player = new Player(0, 0, false, 0);
        cam = new Camera(player.getPosX(), player);
    }

    @Test
    public void testAdjustPosition() throws Exception {
        cam.adjustPosition();
        int result = cam.getPosX();
        assertEquals("The camera adjusts its position following the constants defined for the " +
                "thresholds", 222, result);
    }

}