/**
 * Created by danielmacario on 14-11-13.
 */
package GamePlay;

import Database.DatabaseController;
import Database.MongoDBController;
import GameObject.*;
import SystemController.SoundController;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used to control the logic behind all types of collision detection that
 * take place during game play, specifically collision between:
 * - Player and enemies, player and walls, player and powerUp, player and Door, player and flames, and player and bombs.
 * - Enemies and walls, player and flames, enemies and flames, and enemies and bombs.
 * It also control the logic required for resolving collisions.
 */
public class CollisionManager implements Serializable {

    private Player player;
    private TileMap tileMap;
    private ScoreManager scoreManager;
    private String userName;

    /**
     * Initializes a CollisionManager object, containing the entities to be tested for collisions.
     * @param player A player object used to determine the collisions between players and enemies.
     *               Also note that it contains the bombs that have placed on the grid.
     * @param tileMap A tileMap object containing the walls, powerUp, door, and flames present on the
     *                grid.
     * @param userName The username of the current player.
     */
    public CollisionManager(Player player, TileMap tileMap, String userName) {
        this.player = player;
        this.tileMap = tileMap;
        this.scoreManager = new ScoreManager();
        this.userName = userName;
    }

    /**
     * Fundamental function of the collision manager used to trigger the logic necessary to resolve all the collisions
     * between the objects present on the grid.
     * @param objects Two dimensional array containing the wall objects present on the grid.
     * @param playerRectangle The Rectangle object specifying the bounds of the player object.
     * @param enemies ArrayList containing the enemy objects present on the grid.
     * @param bombsPlaced An ArrayList containing the bombs objects placed on the grid.
     * @param flames An ArrayList containing the flame objects present on the grid.
     * @param powerUp The powerUp object present on the grid.
     * @param door The door object present on the grid.
     * @param isBonusStage A boolean specifying whether the current stage is a bonus stage or not.
     */
    public void handleCollisions(GameObject[][] objects,
                                 Rectangle playerRectangle, ArrayList<Enemy> enemies, ArrayList<Bomb> bombsPlaced,
                                 ArrayList<Flame> flames, PowerUp powerUp, Door door, boolean isBonusStage) {

        checkCollisionsWithWalls(objects, playerRectangle, enemies);
        checkCollisionsWithBombs(bombsPlaced, playerRectangle, enemies);
        checkCollisionsWithFlames(bombsPlaced, playerRectangle, enemies, flames, powerUp, door, isBonusStage);

        //If the current stage is a bonus stage, then we don't call collision detection between player/flames
        //and player/enemy. We also don't check for collision with powerUp or door, since these objects
        //are not present in a bonus stage.
        if (!isBonusStage) {
            checkCollisionWithPowerUp(playerRectangle, powerUp);
            checkCollisionWithDoor(playerRectangle, door, enemies);
            checkCollisionsWithEnemies(playerRectangle, enemies);
        }
    }

    /**
     * Determines whether the  enemies or the player are colliding with a wall or not. If a collision occurs,
     * it calls the necessary logic to resolve it.
     * @param objects A two dimensional array containing the walls present in the game.
     * @param playerRectangle The rectangle representing the collision box of the player object.
     * @param enemies An ArrayList containing the enemy objects present in the game.
     */
    public void checkCollisionsWithWalls(GameObject[][] objects, Rectangle playerRectangle, ArrayList<Enemy> enemies) {

        boolean playerHasWallPass = player.hasWallPass();

        //Iterate over all the walls present on the grid, and test if a collision occurs between them
        //and the player or the enemies.
        for (GameObject[] row : objects) {
            for (GameObject wall : row) {
                if (wall != null) {

                    Rectangle wallRectangle = wall.getBounds();

                    //We don't check for collisions between the player and the walls
                    //if the player has the wallPass powerUp enabled
                    if (!playerHasWallPass || (wall instanceof ConcreteWall)) {
                        if (playerRectangle.intersects(wallRectangle)) {
                            restoreToBeforeCollision(player, wallRectangle);
                        }
                    }

                    //Check for collisions of the enemies with the walls, unless the enemy
                    //has wallPass itself.
                    boolean enemyHasWallPass;
                    for (Enemy enemy : enemies) {
                        enemyHasWallPass = enemy.hasWallPass();
                        Rectangle enemyRectangle = enemy.getBounds();
                        if (!enemyHasWallPass || (wall instanceof ConcreteWall)) {
                            if (enemyRectangle.intersects(wallRectangle)) {
                                enemy.reverseDirection();
                                restoreToBeforeCollision(enemy, wallRectangle);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines whether the player is colliding with a powerUp. If this is the case,
     * it enables the powerUp attribute on the player object.
     * @param playerRectangle The rectangle representing the collision box of the player object.
     * @param powerUp The powerUp object present on the grid.
     */
    public void checkCollisionWithPowerUp(Rectangle playerRectangle, PowerUp powerUp) {
        if (powerUp == null) return;
        Rectangle powerUpRectangle = powerUp.getBounds();

        if (powerUp.isVisible() && !powerUp.isFirstCollision()) {
            if (playerRectangle.intersects(powerUpRectangle)) {
                player.enablePowerUp(powerUp.getPowerUpType());
                powerUp.setVisible(false);
                SoundController.POWERUP.play();
            }
        }
    }

    /**
     * Check collisions between the player and the door object. If a collision occurs, we advance
     * to the next stage.
     * @param playerRectangle The rectangle representing the collision box of the player object.
     * @param door The door object present on the grid.
     * @param enemies An ArrayList containing the enemy objects present in the game.
     */
    public void checkCollisionWithDoor(Rectangle playerRectangle, Door door, ArrayList<Enemy> enemies) {
        if (door == null) return;

        if (enemies.size() == 0) {
            Rectangle doorRectangle = door.getBounds();
            if (door.isVisible()) {
                if (playerRectangle.intersects(doorRectangle)){
                    System.out.println("advancing to next stage!");
                    player.nextStage();
                }
            }
        }
    }

    /**
     * Checks collisions between the player and the bomb objects, as well as enemies and the bomb
     * object. Note that by bomb we are not referring to the flames from the explosion, but rather
     * the bomb itself before it blows up.
     * @param bombsPlaced An ArrayList containing the bombs objects placed on the grid.
     * @param playerRectangle The rectangle representing the collision box of the player object.
     * @param enemies An ArrayList containing the enemy objects present in the game.
     */
    public void checkCollisionsWithBombs(ArrayList<Bomb> bombsPlaced, Rectangle playerRectangle, ArrayList<Enemy> enemies) {
        //Check for collisions of the player with the bombs
        for (Bomb bomb : bombsPlaced) {
            Rectangle bombRectangle = bomb.getBounds();

            if (!player.hasBombPass()) {
                if (playerRectangle.intersects(bombRectangle)) {
                    if (!bomb.isFirstCollision()) {
                        player.restorePreviousPosition();
                    }
                } else {
                    bomb.setFirstCollision(false);
                }
            }

            for (Enemy enemy : enemies) {
                Rectangle enemyRectangle = enemy.getBounds();
                if (enemyRectangle.intersects(bombRectangle)) {
                    enemy.reverseDirection();
                    restoreToBeforeCollision(enemy, bombRectangle);
                }
            }

        }
    }

    /**
     * Checks collision between the player, enemies, door and powerUp with the flames objects that result
     * from a bomb blast.
     * @param bombsPlaced An ArrayList containing the bombs objects placed on the grid.
     * @param playerRectangle The Rectangle object specifying the bounds of the player object.
     * @param enemies An ArrayList containing the enemy objects present in the game.
     * @param flames An ArrayList containing the flame objects present on the grid.
     * @param powerUp The powerUp object present on the grid.
     * @param door The door object present on the grid.
     * @param isBonusStage A boolean specifying whether the current stage is a bonus stage or not.
     */
    public void checkCollisionsWithFlames(ArrayList<Bomb> bombsPlaced, Rectangle playerRectangle, ArrayList<Enemy> enemies,
                                          ArrayList<Flame> flames, PowerUp powerUp, Door door, boolean isBonusStage) {

        Rectangle flameRectangle;
        ArrayList<KillSet> enemiesKilled = new ArrayList<KillSet>();

        //All the objects mentioned above are tested for collision with each flame object.
        //We pack everything into a single loop to avoid having to iterate over the same
        //flame object multiple times.
        for (Flame flame : flames) {
            flameRectangle = flame.getBounds();

            //we decouple this methods for testing purposes.
            checkCollisionsBetweenFlameAndPlayer(flameRectangle, playerRectangle, isBonusStage);
            checkCollisionsBetweenFlameAndBomb(flameRectangle, bombsPlaced);
            checkCollisionsBetweenFlameAndEnemies(flameRectangle, flame, enemies, enemiesKilled);
            checkCollisionsBetweenFlameAndPowerUp(flameRectangle, powerUp, isBonusStage);
            checkCollisionsBetweenFlameAndDoor(flameRectangle, door, isBonusStage);
        }

        calculateScoreFromKills(enemiesKilled);
    }

    /**
     * Resolves collisions between the player object and a flame object.
     * @param flameRectangle The rectangle representing the collision box of the flame object.
     * @param playerRectangle The rectangle representing the collision box of the player object.
     * @param isBonusStage A boolean specifying whether the current stage is a bonus stage or not.
     */
    public void checkCollisionsBetweenFlameAndPlayer(Rectangle flameRectangle, Rectangle playerRectangle,
                                                     boolean isBonusStage) {
        if (player.isVisible() && !player.isInvincibilityEnabled() && !player.hasFlamePass() && !isBonusStage ) {
            if (playerRectangle.intersects(flameRectangle)) {
                //If the player collides with a bomb object we call it's death method.
                player.death();
            }
        }
    }

    /**
     * Resolves the collisions between the bomb objects and a flame object.
     * @param flameRectangle The rectangle representing the collision box of the flame object.
     * @param bombsPlaced An ArrayList containing the bombs objects placed on the grid.
     */
    public void checkCollisionsBetweenFlameAndBomb(Rectangle flameRectangle, ArrayList<Bomb> bombsPlaced) {
        for (Bomb bomb : bombsPlaced) {
            Rectangle bombRectangle = bomb.getBounds();
            if (flameRectangle.intersects(bombRectangle)) {
                //Detonate bombs if they collide with the flame object
                SoundController.BOMBEXPLODE.play();
                bomb.setVisible(false);
            }
        }
    }

    /**
     * Resolves the collisions between the enemy objects and a flame object.
     * @param flameRectangle The rectangle representing the collision box of the flame object.
     * @param flame The flame object being tested for collision.
     * @param enemies ArrayList containing the enemy objects present on the grid.
     * @param enemiesKilled ArrayList used to keep track of the enemies killed. Used
     *                      for score management.
     */
    public void checkCollisionsBetweenFlameAndEnemies(Rectangle flameRectangle, Flame flame,
                                                      ArrayList<Enemy> enemies, ArrayList<KillSet> enemiesKilled) {
        for (Enemy enemy : enemies) {
            Rectangle enemyRectangle = enemy.getBounds();
            if (enemyRectangle.intersects(flameRectangle)) {
                enemy.death();
                Coordinate positionOfDeath = enemy.getCenterOfEnemyAsCoordinate();
                Coordinate locationOfBomb = flame.getExplosionOriginAsCoordinate();

                //If an enemy collides with a flame object, we create a KillSet object with
                //the data of the death so that we can then pass it to the score manager to
                //determine the score obtained.
                if (!enemy.isHitByFlames()) {
                    enemiesKilled.add(new KillSet(positionOfDeath, locationOfBomb, enemy));
                }
                enemy.setHitByFlames(true);
            }
        }
    }

    /**
     * Resolves the collisions between flame objects and a powerUp object.
     * @param flameRectangle The rectangle representing the collision box of the flame object.
     * @param powerUp The powerUp object present on the grid.
     * @param isBonusStage A boolean specifying whether the current stage is a bonus stage or not.
     */
    public void checkCollisionsBetweenFlameAndPowerUp(Rectangle flameRectangle, PowerUp powerUp, boolean isBonusStage) {
        if (!isBonusStage) {
            //check whether powerUp is visible on the map, and if we haven't already spawned
            //a harder set of enemies.
            if (powerUp.isVisible() && !powerUp.isFirstCollision()) {
                Rectangle powerUpRectangle = powerUp.getBounds();
                if (powerUpRectangle.intersects(flameRectangle)) {
                    powerUp.setVisible(false);
                    spawnSetOfHarderEnemies(powerUp.getPosX(), powerUp.getPosY());
                }
            }
        }
    }

    /**
     * Resolves the collision between the door object and a flame object.
     * @param flameRectangle The rectangle representing the collision box of the flame object.
     * @param door The door object present on the grid.
     * @param isBonusStage A boolean specifying whether the current stage is a bonus stage or not.
     */
    public void checkCollisionsBetweenFlameAndDoor(Rectangle flameRectangle, Door door, boolean isBonusStage) {
        if (!isBonusStage) {
            Rectangle doorRectangle = door.getBounds();
            if (doorRectangle.intersects(flameRectangle)) {
                if (!tileMap.isHarderSetAlreadyCreated()) {
                    spawnSetOfHarderEnemies(door.getPosX(), door.getPosY());
                }
            }
        }
    }

    /**
     * Checks collision between the player and the enemies present in the grid. If a collision
     * does occur, the player death method is called.
     * @param playerRectangle The rectangle representing the collision box of the player object.
     * @param enemies ArrayList containing the enemy objects present on the grid.
     */
    public void checkCollisionsWithEnemies(Rectangle playerRectangle, ArrayList<Enemy> enemies) {
        //check for collisions between the player and the enemies
        if (player.isVisible() && !player.isInvincibilityEnabled()) {
            for (Enemy enemy : enemies) {
                Rectangle enemyRectangle = enemy.getBounds();
                if (playerRectangle.intersects(enemyRectangle)) {
                    player.death();
                }
            }
        }
    }

    /**
     * Spawns a set of enemies one difficulty type higher than the hardest
     * enemy present on the grid.
     * @param posX x coordinate where the harder enemies will spawn.
     * @param posY y coordinate where the harder enemies will spawn.
     */
    public void spawnSetOfHarderEnemies(int posX, int posY) {
        if (tileMap.isHarderSetAlreadyCreated()) return;
        tileMap.spawnSetOfHarderEnemies(posX, posY);
    }

    /**
     * Increments the score attribute of the player by the amount specified by the
     * scoreManager class. We also update the database with the new score so that the
     * user can always see his total cumulative score when he accesses the leaderBoard.
     * @param enemiesKilled
     */
    private void calculateScoreFromKills(ArrayList<KillSet> enemiesKilled) {
        if (enemiesKilled.isEmpty()) return;
        int scoreObtained = scoreManager.determineScoreFromKills(enemiesKilled);

        try {
            //TODO: might want to remove this
            System.out.println("UPDATING SCORE IN DB");
            //DatabaseController.setScore(userName, scoreObtained);
            MongoDBController.setScore(userName, scoreObtained);
        } catch (Exception e){
            e.printStackTrace();
        }

        player.addToScore(scoreObtained);
    }

    /**
     * Restores the position of the object passed such that it doesn't collide with the
     * rectangle object passed. This method is called when the player or the enemies collide
     * with walls and bombs.
     * @param object The object that is in collision with the obstacle Rectangle.
     * @param obstacleRectangle The collision box of the obstacle the object is colliding with.
     */
    public void restoreToBeforeCollision(MovableObject object, Rectangle obstacleRectangle) {
        int x = object.getPosX();
        int y = object.getPosY();

        //Restore the previous x position of the movable object and then check if it still
        //collides with the rectangle. If no collision reoccurs, we resolved the problem and
        //return.
        object.restorePreviousXPosition();

        if (object.getBounds().intersects(obstacleRectangle)) {
            object.restorePositionTo(x,y);
        } else {
            return;
        }

        //Restore the previous y position of the movable object and then check if it still
        //collides with the rectangle. If no collision reoccurs, we resolved the problem and
        //return.
        object.restorePreviousYPosition();

        if (object.getBounds().intersects(obstacleRectangle)) {
            object.restorePositionTo(x,y);
        } else {
            return;
        }

        //In this last case we restore both the previous x and y positions, the game is built
        //so that this ensures the collision has been resolved.
        object.restorePreviousPosition();
        return;
    }

}
