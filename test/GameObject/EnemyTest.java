package GameObject;

import org.junit.Before;
import org.junit.Test;

import javax.management.StringValueExp;

import static org.junit.Assert.*;

public class EnemyTest {

    private Enemy enemy;

    @Before
    public void makeEnemy() {
        enemy = new Enemy(EnemyType.BALLOOM, 35, 35);
    }

    @Test
    public void testDeath() throws Exception {
        assertTrue("Enemy is visible upon existence", enemy.isVisible());
        enemy.death();
        assertFalse("Enemy is no longer visible after dying", enemy.isVisible());
    }
}
