/**
 * Created by danielmacario on 14-10-29.
 */
package GameObject;

import Database.DatabaseController;
import Database.MongoDBController;
import GameObject.ArtificialIntelligence.HighIntelligence;
import GameObject.ArtificialIntelligence.PathFinder;
import GamePlay.Coordinate;
import GamePlay.GamePlayState;
import GamePlay.Spawner;

import java.awt.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * The TileMap class is used to represent the map or grid where GamePlay takes place.
 * It is a container for all the objects that are drawn on the grid during gamePlay,
 * including: walls, enemies, powerUps, and the door. This class is also in charge of
 * drawing all of these objects on the grid.
 */
public class TileMap implements Serializable {

    //Default constants for size of tiles and overall grid.
    public static final int TOTAL_WIDTH_OF_COLUMNS = 992;
    public static final int TOTAL_HEIGHT_OF_ROWS = 416;
    public static final int NUM_OF_COLS = 31;
    public static final int NUM_OF_ROWS = 13;
    public static final int TILE_SIDE_LENGTH = 32;
    public static final int CAMERA_MOVING_LIMIT_LEFT = 224;
    public static final int CAMERA_MOVING_LIMIT_RIGHT = 733;

    private Player player;
    private GameObject[][] walls;
    private ArrayList<Flame> flames;
    private ArrayList<Enemy> enemies;
    private PowerUp powerUp;
    private Door door;
    private Spawner spawner;
    private PathFinder pathFinder;
    private String userName;
    private int bombRadius;
    private boolean isBonusStage;
    private boolean harderSetAlreadyCreated;
    private boolean nextStageTransition;
    private boolean spawnHarderSet = false;
    private int timeToHarderSetSpawn = 6000; //equivalent to 200 seconds, but in frames
    private int timeToHarderSet = 20;
    private ArrayList<Enemy> newEnemySet;

    //keep track of the current stage
    private int currentStage;

    /**
     * Initialize a TileMap object to hold and interact with all the game objects that
     * are present in a specified stage.
     * @param player A player instance used to interact with various logic elements present
     *               in this class
     * @param selectedStage An integer specifying the stage number that will be used as the
     *                      blueprint for enemy generation and powerUp generation.
     * @param userName
     */
    public TileMap(Player player, int selectedStage, String userName) {
        this.player = player;
        this.userName = userName;
        this.currentStage = selectedStage;
        this.bombRadius = 5;
        this.spawner = new Spawner(getCurrentStage());
        this.flames = new ArrayList<Flame>();
        this.isBonusStage = getCurrentStage().isBonusStage();
        this.pathFinder = new PathFinder();
        this.harderSetAlreadyCreated = false;
        HighIntelligence.setPathFinder(pathFinder);
        populateGridWithBlocks();
        createEnemySet();
        generatePowerUp();
        generateDoor();
    }

    /**
     * Draws all the game objects contained within the TileMap instance on the grid.
     * We check if the spawn harder enemies flag is true to generate new enemies before drawing them.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    public void drawObjects(Graphics2D g) {
        determineIfShouldSpawnHarderEnemies();
        drawPowerUp(g);
        drawDoor(g);
        drawTiles(g);
        drawEnemies(g);
    }

    /**
     * Function called when a bomb explosion collides with a powerUp or the door.
     * If this is the case, we wait 20 frames for the flames to disappear and then
     * generate the harder set of enemies.
     */
    public void determineIfShouldSpawnHarderEnemies() {
        countDownToHarderEnemySetSpawn();
        if (spawnHarderSet) {
            timeToHarderSet--;
            if (timeToHarderSet == 0) {
                //Populate the enemies array with the new harder enemy set.
                enemies = newEnemySet;
                //reset the timer to its default of 20
                timeToHarderSet = 20;
                spawnHarderSet = false;
            }
        }
    }

    /**
     * Draws the powerUp object on the grid.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    private void drawPowerUp(Graphics2D g) {
        //we check if the powerUp is null because during bonus stages there is no powerUp present.
        if (powerUp == null) return;
        if (powerUp.isVisible()) powerUp.draw(g);
    }

    /**
     * Draws the door on the grid.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    private void drawDoor(Graphics2D g) {
        //we check if the door is null because during bonus stages there is no powerUp present.
        if (powerUp == null) return;
        if (door.isVisible()) door.draw(g);
    }

    /**
     * Draws the wall and flame objects placed on the different tiles of the grid.
     * Note that this information is held by the two dimension walls array, which
     * represents the 12 by 31 map, and specifies which wall types are held by
     * different tiles.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    public void drawTiles(Graphics2D g) {
        //Draw the walls and the enemies
        for (GameObject[] row : walls) {
            for (GameObject object : row) {
                if (object != null && object.isVisible()) {
                    if (!(isBonusStage && (object instanceof BrickWall))) {
                        object.draw(g);
                    }
                }
            }
        }

        Flame flame;
        for (Iterator<Flame> iterator = flames.iterator(); iterator.hasNext();) {
            flame = iterator.next();
            flame.incrementTimeOnGrid();
            if (!flame.isVisible()) {
                //Remove the flames object from the flames ArrayList when they are
                //no longer visible.
                iterator.remove();
            } else {
                flame.draw(g);
            }
        }
    }

    /**
     * Draws the enemies on the grid.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    public void drawEnemies(Graphics2D g) {
        if (enemies.isEmpty()) return;

        int enemyCount = enemies.size();
        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isVisible()) {
                enemy.draw(g);
            } else {
                //if the enemy is not visible we remove it from the enemies ArrayList
                enemies.remove(enemy);
                enemyCount = enemies.size();
            }
        }
    }

    /**
     * Advances to the next stage in the progression of the game.
     * If the player has completed the last stage, it initiates the
     * finished game logic.
     */
    public void nextStage() {
        currentStage++;

        //player completed the game
        if (currentStage == 61) {
            player.setCurrentGamePlayState(GamePlayState.FINISHEDGAME);
            return;
        }

        //Update the unlocked stage column in the database for the user playing the game.
        updateUnlockedStage(currentStage, userName);

        //Load the new stage blueprint used for enemy generation.
        StageData newStage = Stages.gameStages[this.currentStage];
        this.timeToHarderSetSpawn = 6000;
        this.harderSetAlreadyCreated = false;
        this.nextStageTransition = true;
        this.isBonusStage = newStage.isBonusStage();
        this.flames = new ArrayList<Flame>();
        spawner.nextStage(newStage);
        populateGridWithBlocks();
        createEnemySet();
        generatePowerUp();
        generateDoor();
    }

    /**
     * Update the unlocked stage column in the database for the user playing the game.
     * @param currentStage The integer specifying the stage number completed by the user.
     * @param username The username of the player used to find the row in the database
     *                 to be updated.
     */
    private void updateUnlockedStage(int currentStage, String username) {

        try {
            //DatabaseController.setLevelUnlocked(username, currentStage);
            MongoDBController.setLevelUnlocked(username, currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the walls two dimensional array with the specific information about the walls
     * present in the current grid. This is done by means of the Spawner class.
     */
    public void populateGridWithBlocks() {
        walls = spawner.generateWalls();
        pathFinder.updateGraph(walls);
    }

    /**
     * Populates the enemies ArrayList with a new set of enemies that matches the blueprint given by
     * the current stage.
     */
    public void createEnemySet() {
        enemies = spawner.generateEnemies();
    }

    /**
     * Adds a new enemy to the enemies ArrayList. This function is used during bonus stages,
     * where the enemies keep spawning infinitely for 30 seconds.
     */
    public void addNewEnemy() {
        Enemy bonusEnemy = spawner.createBonusStageEnemy();
        enemies.add(bonusEnemy);
    }

    /**
     * Generate a powerUp following the blueprint given by the current stage.
     */
    public void generatePowerUp() {
        this.powerUp = spawner.generatePowerUp();
    }

    /**
     * Generate a door object to be placed randomly on the grid.
     */
    public void generateDoor() {
        this.door = spawner.generateDoor();
    }

    /**
     * Populates the flames ArrayList with a new set of flames. This function
     * is called when a bomb explodes.
     * @param bombPosX Integer representing the x coordinate where the bomb exploded.
     * @param bombPosY Integer representing the y coordinate where the bomb exploded.
     */
    public void addFlames(int bombPosX, int bombPosY) {

        int posXOfExplosion = bombPosX / TILE_SIDE_LENGTH;
        int posYOfExplosion = bombPosY / TILE_SIDE_LENGTH;

        //Place a flame object at the center of the explosion
        //TODO: INTRODUCE CONSTANTS EVERYWHERE!
        //We create a flame object directly on the location where the bomb exploded.
        flames.add(new Flame(posXOfExplosion * TILE_SIDE_LENGTH, posYOfExplosion * TILE_SIDE_LENGTH, true, bombPosX, bombPosY));

        boolean isConcreteWall;
        boolean isBrickWall;
        int posXofFlame;
        int posYofFlame;
        int posXofWall;
        int posYofWall;
        GameObject wall;
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        //We expand in the radius of explosion of the bomb going in the cardinal directions.
        //We add flame objects at each tile until we reach the limit of the radius or we hit
        //a wall.
        for (Direction direction : directions) {
            for (int i = 1; i < bombRadius + 1; i++) {

                if (direction == Direction.EAST) {
                    //expand flames in the East direction
                    posXofWall = posXOfExplosion + i;
                    posYofWall = posYOfExplosion;
                    posXofFlame = (posXofWall) * TILE_SIDE_LENGTH;
                    posYofFlame = (posYofWall) * TILE_SIDE_LENGTH;

                } else if (direction == Direction.WEST) {
                    //expand flames in the West direction
                    posXofWall = posXOfExplosion - i;
                    posYofWall = posYOfExplosion;
                    posXofFlame = (posXofWall) * TILE_SIDE_LENGTH;
                    posYofFlame = (posYofWall) * TILE_SIDE_LENGTH;

                } else if (direction == Direction.NORTH) {
                    //expand flames in the North direction
                    posXofWall = posXOfExplosion;
                    posYofWall = posYOfExplosion - i;
                    posXofFlame = (posXofWall) * TILE_SIDE_LENGTH;
                    posYofFlame = (posYofWall) * TILE_SIDE_LENGTH;

                } else {
                    //expand flames in the South direction
                    posXofWall = posXOfExplosion;
                    posYofWall = posYOfExplosion + i;
                    posXofFlame = (posXofWall) * TILE_SIDE_LENGTH;
                    posYofFlame = (posYofWall) * TILE_SIDE_LENGTH;
                }

                wall = walls[posXofWall][posYofWall];
                isConcreteWall = wall instanceof ConcreteWall;
                isBrickWall = wall instanceof BrickWall;

                if (isBrickWall || isConcreteWall) {
                    if (isBrickWall) {
                        if (wall.getBounds().intersects(powerUp.getBounds())) {
                            powerUp.setFirstCollision(false);
                        }
                        walls[posXofWall][posYofWall] = null;
                    }
                    break;
                } else {
                    flames.add(new Flame(posXofFlame, posYofFlame, true, bombPosX, bombPosY));
                }
            }
        }
    }

    /**
     * Countdown timer used to trigger the spawn of a harder set of enemies
     * once the 200 seconds that a stage lasts ends.
     */
    public void countDownToHarderEnemySetSpawn() {

        // timeToHarderSet refers to the 200 second timer countdown present
        // in regular stages. Once this timer hits 0, we start a second timer
        // that times 0.66 seconds before the enemy set is spawned. This timer
        // avoid having the enemies killed by the flames of the bomb that detonated
        // the door or powerUp.

        // Bomb or powerUp detonated by the player before the 200 second timer
        // ran out.
        if (harderSetAlreadyCreated) {
            if (timeToHarderSet > 0) timeToHarderSetSpawn--;
            return;
        }

        // Spawn new enemy set if the 200 second timer runs out
        if (timeToHarderSetSpawn == 0) {
            EnemyType harderEnemyType = determineHarderEnemyTypeToSpawn();
            newEnemySet = spawner.createSetOfHardEnemiesAtRandomPositions(harderEnemyType);
            spawnHarderSet = true;
            harderSetAlreadyCreated = true;
        }
        timeToHarderSetSpawn--;
    }

    /**
     * Creates a new set of enemies of one difficulty higher than the current
     * most difficult enemy present on the grid.
     * @param spawnPosX Integer representing the x coordinate where the new enemies
     *             will spawn.
     * @param spawnPosY Integer representing the y coordinate where the new enemies
     *             will spawn.
     */
    public void spawnSetOfHarderEnemies(int spawnPosX, int spawnPosY) {

        // If there are no enemies left on the grid, then we don't spawn any new enemies.
        // We follow this requirement from the NES version of the game that the wiki is based
        // on.
        if (enemies.size() == 0) return;
        EnemyType harderEnemyType = determineHarderEnemyTypeToSpawn();
        newEnemySet = spawner.createSetOfHarderEnemies(harderEnemyType, spawnPosX, spawnPosY);
        spawnHarderSet = true;
        harderSetAlreadyCreated = true;
    }

    /**
     * Displaces the enemies on the grid, and initiates their artificial intelligence chase mode
     * if the player is at a specified distance.
     * @param playerPosX Integer specifying the x position of the player on the grid.
     * @param playerPosY Integer specifying the y position of the player on the grid.
     * @param playerIsVisible Boolean specifying whether the player is visible or not.
     */
    public void moveEnemies(int playerPosX, int playerPosY, boolean playerIsVisible) {
        //We update the pathFinder timer to determine if we should refresh the graph
        //used to find the shortest path between the enemy and the player by the
        //high intelligence AI.
        pathFinder.updateGraphRefreshTimer();

        for (Enemy enemy : enemies) {
            enemy.move();

            //Calculate the distance between the player and the enemy to determine
            //whether the enemy should chase the player or not. We set the distance
            //to a default of 100 which would not result in a chase for any of
            //the intelligence types.
            int distanceBetweenPlayerAndEnemy = 100;
            if (playerIsVisible) {

                Coordinate centerOfPlayerObject = new Coordinate(playerPosX + Player.SPRITE_SIDE_LENGTH / 2,
                                                                 playerPosY + Player.SPRITE_SIDE_LENGTH / 2);

                Coordinate centerOfEnemyObject = new Coordinate(enemy.getPosX() + Player.SPRITE_SIDE_LENGTH / 2,
                                                                enemy.getPosY() + Player.SPRITE_SIDE_LENGTH / 2);

                distanceBetweenPlayerAndEnemy = centerOfPlayerObject.distanceTo(centerOfEnemyObject);
            }

            //Updating the graph is an expensive operation, we want to do it once for all the enemies.
            //Also, we only want to update it when a chase will take place, which only happens when the distance
            //between the player and the enemy is less than 85 pixels (2 tiles).
            if (enemy.getIntelligence() instanceof HighIntelligence && distanceBetweenPlayerAndEnemy < 85 && pathFinder.getRefreshGraph()) {
                pathFinder.updateGraph(walls);
                pathFinder.setRefreshGraph(false);
            }

            if (playerIsVisible && !player.isInvincibilityEnabled() && !isBonusStage){
                enemy.chasePlayer(playerPosX, playerPosY, distanceBetweenPlayerAndEnemy);
            }
        }
    }

    /**
     * Determines the hardest enemy type that follows the current hardest enemy type present on the grid.
     * @return An EnemyType object specifying an Enemy type harder than the hardest enemy alive on the grid.
     */
    public EnemyType determineHarderEnemyTypeToSpawn() {

        // Sort the enemies present on the grid based on their difficulty attribute
        Collections.sort(enemies, new Comparator<Enemy>() {
            @Override
            public int compare(Enemy enemyA, Enemy enemyB) {
                return enemyA.getDifficultyRanking() - enemyB.getDifficultyRanking(); // Ascending
            }

        });

        //Get the most difficult enemy ranking present in the game
        int hardestTypeDifficulty = enemies.get(enemies.size() - 1).getDifficultyRanking();

        switch (hardestTypeDifficulty) {
            case 0:
                return EnemyType.ONEAL;
            case 1:
                return EnemyType.DOLL;
            case 2:
                return EnemyType.MINVO;
            case 3:
                return EnemyType.KONDORIA;
            case 4:
                return EnemyType.OVAPI;
            case 5:
                return EnemyType.PASS;
            case 6:
                return EnemyType.PONTAN;
            case 7:
                return EnemyType.PONTAN;
        }
        return null;
    }

    /**
     * Sets the boolean for timetoHarderSetSpawn
     * @param timeToHarderSetSpawn Boolean is passed to set timeToHarderSetSpawn to True or False
     */
    public void setTimeToHarderSetSpawn(int timeToHarderSetSpawn) {
        this.timeToHarderSetSpawn = timeToHarderSetSpawn;
    }

    /**
     * Increments the bomb explosion radius by 1 tile.
     */
    public void incrementBombRadius() {
        bombRadius++;
    }

    /**
     * Retrieve the StageData object corresponding to the current stage difficulty.
     * @return A StageData object with the information of the current stage.
     */
    public StageData getCurrentStage() {
        return Stages.gameStages[this.currentStage];
    }

    /**
     * Retrieve the current stage number the user is playing.
     * @return An integer specfying the current stage number being played.
     */
    public int getCurrentStageNumber() {
        return currentStage;
    }

    /**
     * Specify the stage number that the TileMap instance represents.
     * @param currentStage Integer specifying the stage number that the TileMap
     *                     represents.
     */
    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Retrieve the enemies present on the grid.
     * @return An ArrayList containing the enemy objects present on the grid.
     */
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Set the enemies present on the grid.
     * @param enemies An ArrayList containing the enemy objects to be drawn on the grid.
     */
    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    /**
     * Get the Wall objects present on the grid.
     * @return An two dimensional array containing instances of ConcreteWall and BrickWall
     */
    public GameObject[][] getWalls() {
        return walls;
    }

    /**
     * Specify the walls present on the grid.
     * @param walls A two dimensional array representing the map. Each slot contains
     *              a wall or a null object specifying that there is no wall.
     */
    public void setWalls(GameObject[][] walls) {
        this.walls = walls;
    }

    /**
     * Get the flame objects present on the grid.
     * @return An ArrayList containing the flame objects currently visible on the grid.
     */
    public ArrayList<Flame> getFlames() {
        return flames;
    }

    /**
     * Set the flames objects present on the grid.
     * @param flames An ArrayList containing the flame objects to be drawn on the grid.
     */
    public void setFlames(ArrayList<Flame> flames) {
        this.flames = flames;
    }

    /**
     * Get the powerUp object contained in the grid.
     * @return The powerUp object present on the current stage.
     */
    public PowerUp getPowerUp() {
        return powerUp;
    }

    /**
     * Set the powerUp present in the grid.
     * @param powerUp A powerUp object to be drawn on the grid.
     */
    public void setPowerUp(PowerUp powerUp) {
        this.powerUp = powerUp;
    }

    /**
     * Get the door object present on the grid.
     * @return The Door object contained in the current stage.
     */
    public Door getDoor() {
        return door;
    }

    /**
     * Set the door object present on the grid.
     * @param door
     */
    public void setDoor(Door door) {
        this.door = door;
    }

    /**
     * Determine whether the current stage is a bonus stage or not.
     * @return A boolean specifying whether the current stage is a bonus stage or not.
     */
    public boolean isBonusStage() {
        return isBonusStage;
    }

    /**
     * Set whether the TileMap instance represents a bonus stage or not.
     * @param isBonusStage A boolean representing whether the TileMap current represents a bonus
     *                     Stage or not.
     */
    public void setBonusStage(boolean isBonusStage) {
        this.isBonusStage = isBonusStage;
    }

    /**
     * Determine whether a harder enemy set has been created or not. We can only generate a
     * harder set of enemies once per stage.
     * @return A boolean specifying whether a harder enemy set has been created or not.
     */
    public boolean isHarderSetAlreadyCreated() {
        return harderSetAlreadyCreated;
    }

    /**
     * Set whether a harder enemy set has been created or not.
     * @param harderSetAlreadyCreated A boolean specifying whether a harder enemy set has been
     *                                created or not.
     */
    public void setHarderSetAlreadyCreated(boolean harderSetAlreadyCreated) {
        this.harderSetAlreadyCreated = harderSetAlreadyCreated;
    }

    /**
     * Determine whether we are currently transitioning between stages or not.
     * @return A boolean specifying whether we are transitioning between stages or not.
     */
    public boolean isNextStageTransition() {
        return nextStageTransition;
    }

    /**
     * Specify whether we are transition between stages or not.
     * @param nextStageTransition A boolean representing whether we are in a stage transition or not.
     */
    public void setNextStageTransition(boolean nextStageTransition) {
        this.nextStageTransition = nextStageTransition;
    }

    public int getTimeToHarderSetSpawn() {
        return timeToHarderSetSpawn;
    }

    /**
     * Get the radius of explosion of a bomb.
     * @return An integer representing the radius of explosion of a bomb.
     */
    public int getBombRadius() {
        return bombRadius;
    }

    /**
     * Sets the radius of the bomb
     * @param bombRadius The radius of the bomb is passed
     */
    public void setBombRadius(int bombRadius) {
        this.bombRadius = bombRadius;
    }

    /**
     * Retrieve the new enemySet to be spawned once a bomb explodes
     * on a powerUp or door.
     * @return An ArrayList containing the new Enemy set to be spawned after
     * a bomb explodes on a powerUp or door.
     */
    public ArrayList<Enemy> getNewEnemySet() {
        return newEnemySet;
    }

    /**
     * Gets the boolean on whether to spawn harder enemies or not
     * @return Boolean whether to spawn harder set of enemies
     */
    public boolean isSpawnHarderSet() {
        return spawnHarderSet;
    }

    /**
     * Sets the boolean to true or false on whether to spawn harder enemies
     * @param spawnHarderSet Boolean is passed to set spawnHarderSet to true or false
     */
    public void setSpawnHarderSet(boolean spawnHarderSet) {
        this.spawnHarderSet = spawnHarderSet;
    }

    /**
     * Gets the value of timeToHarderSet
     * @return int timeToHarderSet representing the time regarding harder enemy spawn
     */
    public int getTimeToHarderSet() {
        return timeToHarderSet;
    }

    /**
     * Sets value of timeToHarderSet
     * @param timeToHarderSet int representing time regarding harder enemy spawn
     */
    public void setTimeToHarderSet(int timeToHarderSet) {
        this.timeToHarderSet = timeToHarderSet;
    }
}
