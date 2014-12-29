package GamePlay;

import GameObject.*;
import GameObject.Player;
import GameObject.TileMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CollisionManagerTest {

    private CollisionManager collisionManager;
    private Player player;
    private TileMap tileMap;

    @Before
    public void createNecessaryObjectsForTesting() {
        player = new Player(32, 32, true, MovableObject.NORMALSPEED);
        tileMap = new TileMap(player, 1, "test username");
        collisionManager = new CollisionManager(player, tileMap, "test username");
    }

    @Test
    public void testCheckCollisionsWithWalls() throws Exception {
        player.setPreviousX(33);
        player.setPreviousY(33);
        GameObject[][] walls = tileMap.getWalls();
        walls[1][2] = new BrickWall(64,32, true, false);
        tileMap.setEnemies(new ArrayList<Enemy>());

        // Test collision handling when no collision occurs.
        collisionManager.checkCollisionsWithWalls(walls, player.getBounds(), tileMap.getEnemies());
        assertEquals("If player does not collide, we do not restore the previous X coordinate", 32, player.getPosX());
        assertEquals("If player does not collide, we do not restore the previous Y coordinate", 32, player.getPosY());
        player.setPosX(36);

        // Test collision resolved through X shifting.
        collisionManager.checkCollisionsWithWalls(walls, player.getBounds(), tileMap.getEnemies());
        assertEquals("If a collision occurs, player's position is restored to the previous " +
                "X value if doing so revolves the collision", 33, player.getPosX());
        player.setPosY(31);

        // Test collision resolved through Y shifting.
        collisionManager.checkCollisionsWithWalls(walls, player.getBounds(), tileMap.getEnemies());
        assertEquals("If a collision occurs, player's position is restored to the previous " +
                "Y value if doing so revolves the collision", 33, player.getPosY());
        player.setPosX(36);
        player.setWallPass(true);

        //Test collision with brick walls when wallpass is enabled
        collisionManager.checkCollisionsWithWalls(walls, player.getBounds(), tileMap.getEnemies());
        assertEquals("If the player has WallPass enabled, collisions with brick walls do not " +
                "restore its position", 36, player.getPosX());
    }

    @Test
    public void testCheckCollisionWithPowerUp() throws Exception {
        //powerUp is visible and powerUp is not covered by a brickWall (firstCollision = false)
        PowerUp powerUp = new PowerUp(PowerUpType.BOMBPASS, 32, 32);
        powerUp.setFirstCollision(false);
        collisionManager.checkCollisionWithPowerUp(player.getBounds(), powerUp);
        assertTrue("If player collides with powerUp and the powerUp is not covered by a brickWall, then the powerUp effect " +
                "is activated on the player object", player.hasBombPass());

        //powerUp is visible but it is covered by a wall (firstCollision = true)
        powerUp = new PowerUp(PowerUpType.BOMBPASS, 32, 32);
        powerUp.setFirstCollision(true);
        collisionManager.checkCollisionWithPowerUp(player.getBounds(), powerUp);
        assertTrue("If player collides with powerUp, and the powerUp is covered by a brickWall the powerUp effect " +
                "is activated on the player object", player.hasBombPass());

        //powerUp is null
        powerUp = null;
        player.setBombPass(false);
        collisionManager.checkCollisionWithPowerUp(player.getBounds(), powerUp);
        assertFalse("If the powerUp is null, player attributes are not modified", player.hasBombPass());

        //powerUp is not visible
        powerUp = new PowerUp(PowerUpType.BOMBPASS, 32, 32);
        powerUp.setVisible(false);
        assertFalse("If player collides with powerUp but the PowerUp is not visible, then the attribute " +
                "is not enabled on the player object", player.hasBombPass());
    }

    @Test
    public void testCheckCollisionWithDoor() {
        //door is null
        Door door = null;
        collisionManager.checkCollisionWithDoor(player.getBounds(), door, tileMap.getEnemies());
        assertEquals("If the door is null, player will not advance to the next stage ", 32, player.getPosX());

        //door exists, and there are still enemies on the map
        collisionManager.checkCollisionWithDoor(player.getBounds(), door, tileMap.getEnemies());
        assertEquals("if the door exists and there are enemies on the map, a collision with " +
                "the door doesn't advance the player to the next stage, which means it position" +
                "remains the unchanged", 32, player.getPosX());

        //door exists, and there are no enemies on the map.
        door = new Door(32, 32);
        tileMap.setEnemies(new ArrayList<Enemy>());
        collisionManager.checkCollisionWithDoor(player.getBounds(), door, tileMap.getEnemies());
        assertEquals("if the door exists and there are no enemies on the map, a collision with " +
                "the door advances the player to the next stage which resets its x position to 32", 32, player.getPosX());
    }

    @Test
    public void testCheckCollisionsWithBombs() throws Exception {
        Bomb bomb = new Bomb(64, 32);
        ArrayList<Bomb> bombs = new ArrayList<Bomb>();
        bombs.add(bomb);
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        tileMap.setEnemies(enemies);
        player.setBombsPlaced(bombs);
        player.setPreviousX(32);
        player.setPosX(35);
        player.setBombPass(false);

        //first player/bomb collision.
        collisionManager.checkCollisionsWithBombs(bombs, player.getBounds(), enemies);
        assertEquals("Player's first collision with a bomb will not restore its position", 35, player.getPosX());

        //Player steps off bomb, enables collision detection
        player.setPosX(32);
        collisionManager.checkCollisionsWithBombs(bombs, player.getBounds(), enemies);
        assertFalse("Bombs' first collision attribute is set to false after player steps off it", bomb.isFirstCollision());

        //Player tries to walk over bomb again
        player.setPosX(35);
        collisionManager.checkCollisionsWithBombs(bombs, player.getBounds(), enemies);
        assertEquals("Player's new collision after stepping off bomb will result in its position" +
                "being reset", 32, player.getPosX());

        //collision between enemy and bomb
        Enemy enemy = new Enemy(EnemyType.BALLOOM, 35, 32);
        enemy.setPreviousX(32);
        enemy.setDirectionOfMovement(Direction.EAST);
        enemies.add(enemy);
        collisionManager.checkCollisionsWithBombs(bombs, player.getBounds(), enemies);
        assertEquals("Enemy's direction is reversed upon colliding with a bomb", Direction.WEST, enemy.getDirectionOfMovement());
        assertEquals("Enemy's position is reset upon colliding with a bomb", 32, enemy.getPosX());
    }

    @Test
    public void testCheckCollisionBetweenFlameAndPlayer() throws Exception {

        Flame flame = new Flame(32, 32, true, 32, 32);
        player.setVisible(false);
        int livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsBetweenFlameAndPlayer(flame.getBounds(), player.getBounds(), false);
        assertEquals("If the player is not visible when colliding with a flame object, " +
                "he will not die", livesBeforeCollision, player.getLivesRemaining());

        player.setVisible(true);
        player.setInvincibilityEnabled(true);
        livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsBetweenFlameAndPlayer(flame.getBounds(), player.getBounds(), false);
        assertEquals("If the player is invincible when colliding with a flame object, " +
                "he will not die", livesBeforeCollision, player.getLivesRemaining());

        player.setFlamePass(true);
        player.setInvincibilityEnabled(false);
        livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsBetweenFlameAndPlayer(flame.getBounds(), player.getBounds(), false);
        assertEquals("If the player does have FlamePass when colliding with a flame object, " +
                "he will not die", livesBeforeCollision, player.getLivesRemaining());


        player.setInvincibilityEnabled(false);
        player.setFlamePass(false);
        livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsBetweenFlameAndPlayer(flame.getBounds(), player.getBounds(), true);
        assertEquals("If the current stage is a bonus stage, then a collision with a flame object " +
                "will not kill the player", livesBeforeCollision, player.getLivesRemaining());

        player.setVisible(true);
        player.setInvincibilityEnabled(false);
        player.setFlamePass(false);
        livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsBetweenFlameAndPlayer(flame.getBounds(), player.getBounds(), false);
        assertEquals("If the current stage is not a bonus stage, and the player does not have flamesPass/invincibility and " +
                "he is visible, then a collision with a flame object will kill the player", livesBeforeCollision - 1, player.getLivesRemaining());

    }

    @Test
    public void testCheckCollisionBetweenFlameAndBomb() throws Exception {

        ArrayList<Bomb> bombs = new ArrayList<Bomb>();
        bombs.add(new Bomb(32,32));
        Flame flame = new Flame(32, 32, true, 32, 32);
        collisionManager.checkCollisionsBetweenFlameAndBomb(flame.getBounds(), bombs);
        assertFalse("If a bomb collides with a flame object, its visibility will be set to false", bombs.get(0).isVisible());

        bombs.set(0, new Bomb(128, 128));
        collisionManager.checkCollisionsBetweenFlameAndBomb(flame.getBounds(), bombs);
        assertTrue("If a bomb does not collide with a flame object, it will remain visible", bombs.get(0).isVisible());

    }

    @Test
    public void testCheckCollisionBetweenFlameAndEnemies() throws Exception {

        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        Flame flame = new Flame(32, 32, true, 32, 32);
        ArrayList<KillSet> killSets = new ArrayList<KillSet>();
        collisionManager.checkCollisionsBetweenFlameAndEnemies(flame.getBounds(), flame, enemies, killSets);
        assertFalse("If an enemy collides with a flame object, its visibility will be set to false ", enemies.get(0).isVisible());
        assertTrue("If an enemy collides with a flame object, a KillSet instance will be created", killSets.size() > 0);

        enemies.set(0, new Enemy(EnemyType.DOLL, 128, 128));
        int killSetSize = killSets.size();
        collisionManager.checkCollisionsBetweenFlameAndEnemies(flame.getBounds(), flame, enemies, killSets);
        assertTrue("If a flame does not collide with a an enemy, it will remain visible", enemies.get(0).isVisible());
        assertTrue("If a flame does not collide with a an enemy, no KillSet instance will be created", killSets.size() == killSetSize);

    }

    @Test
    public void testCheckCollisionBetweenFlameAndPowerUp() throws Exception {

        Flame flame = new Flame(32, 32, true, 32, 32);
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        tileMap.setEnemies(enemies);
        PowerUp powerUp = new PowerUp(PowerUpType.BOMBPASS, 32, 32);
        powerUp.setFirstCollision(false);
        collisionManager.checkCollisionsBetweenFlameAndPowerUp(flame.getBounds(), powerUp, false);
        assertFalse("If a powerUp collides with a flame object in a stage that is not bonus more than once, its visibility " +
                "will be set to false", powerUp.isVisible());
        assertTrue("If a powerUp collides with a flame object in a stage that is not bonus more than once, a new set of enemies " +
                        "will be created", tileMap.isHarderSetAlreadyCreated());

        tileMap.setEnemies(new ArrayList<Enemy>());
        powerUp = new PowerUp(PowerUpType.BOMBPASS, 32, 32);
        powerUp.setFirstCollision(true);
        collisionManager.checkCollisionsBetweenFlameAndPowerUp(flame.getBounds(), powerUp, false);
        assertTrue("If a powerUp collides with a flame object in a stage that is not bonus for the first time, " +
                "its visibility will not be set to false", powerUp.isVisible());

        tileMap.setEnemies(new ArrayList<Enemy>());
        tileMap.setHarderSetAlreadyCreated(false);
        powerUp = new PowerUp(PowerUpType.BOMBPASS, 32, 32);
        collisionManager.checkCollisionsBetweenFlameAndPowerUp(flame.getBounds(), powerUp, true);
        assertTrue("If a powerUp collides with a flame object in a stage that is bonus, its visibility " +
                "will not change", powerUp.isVisible());
        assertFalse("If a powerUp collides with a flame object in a stage that is bonus, a new set of enemies " +
                "will not be created", tileMap.isHarderSetAlreadyCreated());

    }

    @Test
    public void testCheckCollisionBetweenFlameAndDoor() throws Exception {

        Flame flame = new Flame(32, 32, true, 32, 32);
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        tileMap.setEnemies(enemies);
        Door door = new Door(32, 32);
        collisionManager.checkCollisionsBetweenFlameAndDoor(flame.getBounds(), door, false);
        assertTrue("If a door collides with a flame object in a stage that is not bonus, " +
                "a new set of enemies will be created", tileMap.isHarderSetAlreadyCreated());

        enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        tileMap.setEnemies(enemies);
        tileMap.setHarderSetAlreadyCreated(false);
        door = new Door(32, 32);
        collisionManager.checkCollisionsBetweenFlameAndDoor(flame.getBounds(), door, true);
        assertFalse("If a door collides with a flame object in a stage that is bonus, " +
                "a harder set of enemies will not be spawned", tileMap.isHarderSetAlreadyCreated());

        enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.BALLOOM, 32, 32));
        tileMap.setEnemies(enemies);
        tileMap.setHarderSetAlreadyCreated(false);
        door = new Door(128, 128);
        collisionManager.checkCollisionsBetweenFlameAndDoor(flame.getBounds(), door, true);
        assertFalse("If a door does not collide with a flame object in a stage that is not bonus more than once, "
                + "a new set of enemies will not be spawned", tileMap.isHarderSetAlreadyCreated());

    }

    @Test
    public void testCheckCollisionsWithEnemies() throws Exception {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        Enemy enemy = new Enemy(EnemyType.BALLOOM, 35, 32);
        enemy.setPreviousX(32);
        enemy.setDirectionOfMovement(Direction.EAST);
        enemies.add(enemy);

        //if the player is already dead, a collision does not kill him again
        player.setVisible(false);
        int livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsWithEnemies(player.getBounds(), enemies);
        assertEquals("If the player is not visible when it collides with an enemy, his/her number of lives should not " +
                "be reduced", livesBeforeCollision, player.getLivesRemaining());

        player.setVisible(true);
        player.setInvincibilityEnabled(true);
        livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsWithEnemies(player.getBounds(), enemies);
        assertEquals("If the player is invincible when it collides with an enemy, his/her number of lives should not " +
                "be reduced", livesBeforeCollision, player.getLivesRemaining());

        player.setInvincibilityEnabled(false);
        livesBeforeCollision = player.getLivesRemaining();
        collisionManager.checkCollisionsWithEnemies(player.getBounds(), enemies);
        assertEquals("If the player is visible and it is not invincible when it collides with an enemy, his/her number of lives should " +
                "be reduced", livesBeforeCollision - 1, player.getLivesRemaining());

    }

    @Test
    public void testSpawnSetOfHarderEnemies() throws Exception {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(EnemyType.DOLL, 32, 32));
        tileMap.setEnemies(enemies);
        tileMap.setHarderSetAlreadyCreated(true);

        collisionManager.spawnSetOfHarderEnemies(32, 32);
        assertEquals("If a harder set of enemies has already been created, then the enemies arrayList of tileMap will " +
                "not be modified", enemies, tileMap.getEnemies());

        tileMap.setHarderSetAlreadyCreated(false);
        collisionManager.spawnSetOfHarderEnemies(32, 32);
        assertTrue("If a harder set of enemies has not been created before, then the enemies arrayList of tileMap will " +
                "be repopulated", tileMap.isHarderSetAlreadyCreated());
    }

    @Test
    public void testRestoreToBeforeCollision() throws Exception {

        player.setPreviousX(22);
        BrickWall wall = new BrickWall(58, 32, true, false);

        //Collision resolved by shifting in the x axis
        collisionManager.restoreToBeforeCollision(player, wall.getBounds());
        assertEquals("Collisions in the x axis are resolved by restoring the previous x coordinate ", 22, player.getPosX());
        assertEquals("Collisions in the x axis do not restore the y coordinate to its previous value", 32, player.getPosY());

        //Collision resolved by shifting in the y axis
        wall.setPosX(32);
        wall.setPosY(64);
        player.setPosX(32);
        player.setPosY(38);
        player.setPreviousY(31);
        collisionManager.restoreToBeforeCollision(player, wall.getBounds());
        assertEquals("Collisions in the y axis are resolved by restoring restoring the previous y coordinate", 31, player.getPosY());
        assertEquals("Collisions in the y axis do not restore the x coordinate to its previous value", 32, player.getPosX());

    }
}