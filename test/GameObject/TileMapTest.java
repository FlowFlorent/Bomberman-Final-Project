package GameObject;

import GamePlay.GamePlayState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TileMapTest {
    private TileMap tileMap;
    private Player player;
    private String userName;

    @Before
    public void setUp(){
        player = new Player(35, 35, true, MovableObject.NORMALSPEED);
        tileMap = new TileMap(player, 1, userName);
    }

    @Test
    public void testNextStage() {

        int currentStage = tileMap.getCurrentStageNumber();
        tileMap.setHarderSetAlreadyCreated(true);
        tileMap.setTimeToHarderSetSpawn(4000);
        tileMap.nextStage();
        assertEquals("Calling next stage increases the currentStage value by 1",
                currentStage + 1, tileMap.getCurrentStageNumber());

        assertEquals("The 200 second timer should be reset to its default of 6000 (unit in frames)",
                6000, tileMap.getTimeToHarderSetSpawn());

        assertFalse("harderSetAlreadyCreated should be set to false.", tileMap.isHarderSetAlreadyCreated());

        assertTrue("nextStage transition should be set to true", tileMap.isNextStageTransition());

        tileMap.setCurrentStage(60);
        tileMap.nextStage();
        assertEquals("Calling next stage when the current stage number is 60 changes the current " +
                "gamePlay state to FINISHEDGAME", GamePlayState.FINISHEDGAME, player.getCurrentGamePlayState());

    }

    @Test
    public void testAddFlames() {
        GameObject[][] walls = tileMap.getWalls();
        tileMap.setBombRadius(4);
        tileMap.setPowerUp(new PowerUp(PowerUpType.BOMBPASS, 200,200));

        // col then row      |     row then col
        walls[2][1] = new BrickWall(1, 2, true, false);
        walls[1][2] = new BrickWall(2, 1, true, false);

        tileMap.addFlames(32, 32);
        assertTrue("We expect brick walls within the radius of explosion to be replaced by null in the two-dimensional " +
                "walls array", walls[2][1] == null && walls[1][2] == null);
        walls[1][2] = null;

        // This brick wall is created behind a concrete wall which faces the origin of the explosion.
        // The brick wall should not disappear because it is protected by the concrete wall.
        player = new Player(35, 35, true, MovableObject.NORMALSPEED);
        tileMap = new TileMap(player, 1, userName);
        walls = tileMap.getWalls();
        walls[2][3] = new BrickWall(3, 2, true, false);
        // get the powerUp far from the explosion so that it doesn't alter the result.
        tileMap.setPowerUp(new PowerUp(PowerUpType.BOMBPASS, 200,200));
        tileMap.addFlames(64, 32);
        assertTrue("Concrete walls should stop the expansion of the explosion", walls[2][3] instanceof BrickWall);


        player = new Player(35, 35, true, MovableObject.NORMALSPEED);
        tileMap = new TileMap(player, 1, userName);
        // PowerUp is placed under the brick wall created below

        PowerUp powerUp = new PowerUp(PowerUpType.BOMBS, 32, 64);
        tileMap.setPowerUp(powerUp);
        tileMap.setBombRadius(8);
        walls = tileMap.getWalls();
        walls[2][1] = new BrickWall(1, 2, true, true);

        tileMap.addFlames(32,32);

        assertNull("If a brick wall hiding a powerUp is hit by flames, then its place in the two-dimensional walls array " +
                "should become null", walls[2][1]);
    }

    @Test
    public void testDetermineIfShouldSpawnHarderEnemies() throws Exception {

        // Time to spawn new set of enemies is greater than 0
        tileMap.setSpawnHarderSet(true);
        int timeToHarderSet = tileMap.getTimeToHarderSet();
        tileMap.determineIfShouldSpawnHarderEnemies();
        assertEquals("timeToHarderSet should decrease by 1 after determineIfShouldSpawnHarderEnemies() is called ",
                timeToHarderSet - 1, tileMap.getTimeToHarderSet());

        // Time to spawn new set of enemies equals 0
        tileMap.setSpawnHarderSet(true);
        tileMap.setTimeToHarderSet(1);
        tileMap.determineIfShouldSpawnHarderEnemies();
        assertEquals("timeToHarderSet should be rolled back to 20 since timeToHarderSet equals 0 after " +
                "determineIfShouldSpawnHarderEnemies() is called ", 20,tileMap.getTimeToHarderSet());
        assertFalse("spawnHarderSet is set to false after determineIfShouldSpawnHarderEnemies() is called since " +
                "timeToHarderSet hits 0  ", tileMap.isSpawnHarderSet());

        // Harder set of enemies should not be created.
        tileMap.setSpawnHarderSet(false);
        timeToHarderSet = tileMap.getTimeToHarderSet();
        assertEquals("if spawnHarderSet is set to false, then timeToHarderSet should not change its values",
                timeToHarderSet, tileMap.getTimeToHarderSet());
    }

    @Test
    public void testCountDownToHarderEnemySetSpawn() throws Exception {

        tileMap.setHarderSetAlreadyCreated(true);
        tileMap.setTimeToHarderSet(1);
        tileMap.setTimeToHarderSetSpawn(1);
        tileMap.countDownToHarderEnemySetSpawn();
        assertEquals("If a harder set of enemies has already been created, and timeToHarderSet is greater than 0 " +
                " then the timeToHarderSetSpawn decreases by 1", 0, tileMap.getTimeToHarderSetSpawn());
        tileMap.setTimeToHarderSet(0);
        tileMap.countDownToHarderEnemySetSpawn();
        assertEquals("If a harder set of enemies has already been created and the timeToHarderSet is 0, then the its " +
                " value should not change from 0", 0, tileMap.getTimeToHarderSetSpawn());

        tileMap.setTimeToHarderSetSpawn(0);
        tileMap.setHarderSetAlreadyCreated(false);
        tileMap.setSpawnHarderSet(false);
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        tileMap.countDownToHarderEnemySetSpawn();
        assertTrue("If timeToHarderSet is equal to 0, a new enemy set should be generated", tileMap.getNewEnemySet().size() > 0);
        assertTrue("If timeToHarderSet is equal to 0, a new enemy set should be generated, spawnHarderSet and " +
                "harderSetAlreadyCreated should be set to true", tileMap.isHarderSetAlreadyCreated() && tileMap.isSpawnHarderSet());

        // 200 second timer is still running, harder enemy set has not been spawned.
        tileMap.setHarderSetAlreadyCreated(false);
        tileMap.setTimeToHarderSetSpawn(200);
        int timeToHarderSetSpawn = tileMap.getTimeToHarderSetSpawn();
        tileMap.countDownToHarderEnemySetSpawn();
        assertEquals("If the timeToHarderSetSpawn is not equal to 0 and a harder enemy set has not been spawned, " +
                "them we decrease the timer by 1", timeToHarderSetSpawn - 1, tileMap.getTimeToHarderSetSpawn());


    }

    @Test
    public void testSpawnSetOfHarderEnemies() throws Exception {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        tileMap.setEnemies(enemies);
        tileMap.spawnSetOfHarderEnemies(32,32);
        assertNull("If there are no enemies left in the grid, then no new ones should be spawned ", tileMap.getNewEnemySet());

        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        tileMap.spawnSetOfHarderEnemies(32,32);
        assertNotNull("If there are enemies left in the grid, a new harder set should be generated ", tileMap.getNewEnemySet());
        assertTrue("The booleans spawnHarderSet and harderSetAlreadyCreated should be set to true",
                tileMap.isHarderSetAlreadyCreated() && tileMap.isSpawnHarderSet());


    }

    @Test
    public void testIncrementBombRadius() throws Exception {
        tileMap.setBombRadius(5);
        tileMap.incrementBombRadius();
        assertEquals("Bomb radius should be 6 after calling incrementBombRadius()", 6, tileMap.getBombRadius());
    }

    @Test
    public void testDetermineHarderEnemyTypeToSpawn() {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        tileMap.setEnemies(enemies);

        boolean exceptionThrown = false;
        try {
            tileMap.determineHarderEnemyTypeToSpawn();
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue("If there are no enemies present on the tileMap, we expect the function to throw an " +
                "exception", exceptionThrown);

        //Oneal is followed in difficulty by Doll.
        enemies.add(new Enemy(EnemyType.ONEAL, 32, 32));
        EnemyType harderEnemy = tileMap.determineHarderEnemyTypeToSpawn();
        assertEquals("When there is one enemy on the grid, we expect to get a type one harder " +
                "than the one we passed in (except for a Pontan)", harderEnemy, EnemyType.DOLL);

        //Minvo is followed in difficulty by Kondoria
        enemies.add(new Enemy(EnemyType.MINVO, 32, 32));
        harderEnemy = tileMap.determineHarderEnemyTypeToSpawn();
        assertEquals("When there is more than one enemy on the grid, we expect to get a type one harder " +
                "than the hardest one present(except for a Pontan)", harderEnemy, EnemyType.KONDORIA);

        //Pontan is the hardest enemy type. We return the Pontan in this case.
        enemies.add(new Enemy(EnemyType.PONTAN, 32, 32));
        harderEnemy = tileMap.determineHarderEnemyTypeToSpawn();
        assertEquals("When a Pontan is present on the grid, we expect to a Pontan back since there is no " +
                "harder enemy type", harderEnemy, EnemyType.PONTAN);
    }

}
