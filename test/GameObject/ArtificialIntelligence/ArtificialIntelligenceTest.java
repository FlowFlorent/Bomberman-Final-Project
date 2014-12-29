package GameObject.ArtificialIntelligence;

import GameObject.Direction;
import GameObject.Enemy;
import GameObject.EnemyType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArtificialIntelligenceTest {
    private ArtificialIntelligence lowIntelligence;
    // All 3 types of intelligence: low, medium and high all share artificial intelligence,
    // so testing just one type of intelligence is equivalent to testing all 3

    private Enemy enemy;

    @Before
    public void setup() {

        lowIntelligence = new LowIntelligence();
        enemy = new Enemy(EnemyType.BALLOOM, 35, 35);
    }

    @Test
    public void testMoveEnemyOnBoard() throws Exception {
        int posY = enemy.getPosY();
        int posX = enemy.getPosX();
        int speed = enemy.getSpeed();

        //east movement as expected
        enemy.setDirectionOfMovement(Direction.EAST);
        lowIntelligence.moveEnemyOnBoard(enemy);
        assertEquals("There should be no change in the y-coordinate", posY, enemy.getPosY());
        assertEquals(" Movements displaces the enemy to the right", posX + speed, enemy.getPosX());

        //west movement as expected
        enemy.setDirectionOfMovement(Direction.WEST);
        lowIntelligence.moveEnemyOnBoard(enemy);
        assertEquals("There should be no change in the y-coordinate",posY, enemy.getPosY());
        assertEquals("Movement displaces enemy to the left", posX, enemy.getPosX());

        //north movement as expected
        enemy.setDirectionOfMovement(Direction.NORTH);
        lowIntelligence.moveEnemyOnBoard(enemy);
        assertEquals("Movement displaces enemy up",posY - speed, enemy.getPosY());
        assertEquals("There should be no change in the x-coordinate",posX, enemy.getPosX());

        // south movement as expected
        enemy.setDirectionOfMovement(Direction.SOUTH);
        lowIntelligence.moveEnemyOnBoard(enemy);
        assertEquals("Movement displaces enemy down",posY, enemy.getPosY());
        assertEquals("There should be no change in the x-coordinate",posX, enemy.getPosX());

    }

    @Test
    public void testReverseDirection() throws Exception {

        //east movement as expected
        enemy.setDirectionOfMovement(Direction.EAST);
        lowIntelligence.reverseDirection(enemy);
        assertEquals("If EAST, reverse direction should be WEST", Direction.WEST, enemy.getDirectionOfMovement());

        //west movement as expected
        enemy.setDirectionOfMovement(Direction.WEST);
        lowIntelligence.reverseDirection(enemy);
        assertEquals("If WEST, reverse direction should be EAST", Direction.EAST, enemy.getDirectionOfMovement());

        //north movement as expected
        enemy.setDirectionOfMovement(Direction.NORTH);
        lowIntelligence.reverseDirection(enemy);
        assertEquals("If NORTH, reverse direction should be SOUTH", Direction.SOUTH, enemy.getDirectionOfMovement());

        // south movement as expected
        enemy.setDirectionOfMovement(Direction.SOUTH);
        lowIntelligence.reverseDirection(enemy);
        assertEquals("If SOUTH, reverse direction should be NORTH", Direction.NORTH, enemy.getDirectionOfMovement());

    }

}