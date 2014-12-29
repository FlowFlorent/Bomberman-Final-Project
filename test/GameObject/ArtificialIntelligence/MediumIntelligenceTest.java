package GameObject.ArtificialIntelligence;

import GameObject.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MediumIntelligenceTest {

    private MediumIntelligence mediumIntelligence;
    private Enemy enemy;

    @Before
    public void setup(){
        mediumIntelligence = new MediumIntelligence();
        enemy = new Enemy(EnemyType.ONEAL, 35, 35);
    }


    @Test
    public void testChasePlayer() throws Exception {
        enemy.setDirectionOfMovement(Direction.EAST);
        enemy.setPosX(64);
        enemy.setPosY(64);
        mediumIntelligence.chasePlayer(0, 0, 60, enemy);
        assertEquals("The enemy will not chase the player and head west as they are not in the same row or column",
                        Direction.EAST, enemy.getDirectionOfMovement());

        enemy.setDirectionOfMovement(Direction.EAST);
        enemy.setPosX(32);
        enemy.setPosY(0);
        mediumIntelligence.chasePlayer(0, 0, 66, enemy);
        assertEquals("The enemy will not chase the player and head west as they are more than 65 units of distance " +
                    "apart", Direction.EAST, enemy.getDirectionOfMovement());

        enemy.setDirectionOfMovement(Direction.WEST);
        enemy.setPosX(0);
        enemy.setPosY(0);
        mediumIntelligence.chasePlayer(32, 0, 64, enemy);
        assertEquals("The enemy will chase the player and head east as they are in the same row and less than 65 units" +
                    "of distance apart", Direction.EAST, enemy.getDirectionOfMovement());

        enemy.setDirectionOfMovement(Direction.EAST);
        enemy.setPosX(32);
        enemy.setPosY(0);
        mediumIntelligence.chasePlayer(0, 0, 64, enemy);
        assertEquals("The enemy will chase the player and head west as they are in the same row and less than 65 units" +
                    "of distance apart", Direction.WEST, enemy.getDirectionOfMovement());

        enemy.setDirectionOfMovement(Direction.EAST);
        enemy.setPosX(0);
        enemy.setPosY(0);
        mediumIntelligence.chasePlayer(0, 32, 64, enemy);
        assertEquals("The enemy will chase the player and head south as they are in the same column and less than 65 " +
                "units of distance apart", Direction.SOUTH, enemy.getDirectionOfMovement());

        enemy.setDirectionOfMovement(Direction.EAST);
        enemy.setPosX(0);
        enemy.setPosY(32);
        mediumIntelligence.chasePlayer(0, 0, 64, enemy);
        assertEquals("The enemy will chase the player and head north as they are in the same column and less than 65 " +
                "units of distance apart", Direction.NORTH, enemy.getDirectionOfMovement());
    }

    @Test
    public void testRandomTurnOnIntersection() throws Exception {
        assertTrue("Since all conditions are met, the return boolean should be true",
               mediumIntelligence.randomTurnOnIntersection(35, 35, true));

        assertFalse("Since probability condition boolean is false, the return boolean should be false",
                mediumIntelligence.randomTurnOnIntersection(35, 35, false));

        assertFalse("Since enemyAtXIntersection boolean is false, the return boolean should be false",
                mediumIntelligence.randomTurnOnIntersection(36, 35, true));

        assertFalse("Since enemyAtYIntersection boolean is false, the return boolean should be false",
                mediumIntelligence.randomTurnOnIntersection(35, 36, true));

    }
}