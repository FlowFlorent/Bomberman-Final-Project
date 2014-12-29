/**
 * Created by danielmacario on 14-11-01.
 */
package GamePlay;

import GameObject.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class used to populate the game grid with bot static and dynamic objects
 * used during the gameplay state of the game.
 */
public class Spawner implements Serializable {

    private Random randomGenerator = new Random();
    private ArrayList<Coordinate> possibleEnemyCoordinates;
    private ArrayList<Coordinate> possiblePowerUpAndDoorCoordinates;
    private StageData stageData;

    // Two dimensional array used to represent the 403 tiles of the game grid.
    private GameObject[][] gridLayout;

    /**
     * Initializes a spawner object used to populate the game grid
     * when starting a new game, and advancing stages.
     * @param stageData Object specifying the types of enemies present
     *                  on the current stage and whether the current
     *                  stage is a bonus stage or not.
     */
    public Spawner(StageData stageData) {
        int numRows = 13;
        int numCols = 31;
        gridLayout = new GameObject[numCols][numRows];
        possibleEnemyCoordinates = new ArrayList<Coordinate>();
        possiblePowerUpAndDoorCoordinates = new ArrayList<Coordinate>();
        this.stageData = stageData;
    }

    /**
     * @return Returns the gridLayout with brick and concrete walls generated.
     */
    public GameObject[][] generateWalls() {

        generateConcreteWalls();
        generateBrickWalls();
        return gridLayout;
    }

    /**
     * Populates the grid with the constant and fixed concrete walls
     * that surround the edges of the map and interleave some of the rows.
     */
    public void generateConcreteWalls() {
        for (int col = 0; col < TileMap.NUM_OF_COLS; col++) {

            for (int row = 0; row < TileMap.NUM_OF_ROWS; row++) {

                //Generate top row of tiles
                if (row == 0) gridLayout[col][row] = new ConcreteWall(col * TileMap.TILE_SIDE_LENGTH, 0);

                //Generate bottom row of tiles
                if (row == 12) gridLayout[col][row] = new ConcreteWall(col * TileMap.TILE_SIDE_LENGTH,
                        TileMap.TILE_SIDE_LENGTH * (row));

                //Generate first column of tiles
                if (col == 0 && row >= 1 && row < 12)
                    gridLayout[col][row] = new ConcreteWall(col, row * TileMap.TILE_SIDE_LENGTH);

                //Generate last column of tiles
                if (col == 30 && row >= 1 && row < 12)
                    gridLayout[col][row] = new ConcreteWall(col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH);

                //Generate alternating tiles present inside the grid
                if (row >= 2 && row <= 10 && (row % 2) == 0 && col >= 2 && col <= 28 && (col % 2) == 0) {

                    gridLayout[col][row] = new ConcreteWall(col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH);
                }
            }
        }
    }

    /**
     * Populates the game grid with brick walls. Walls are generated with a 20%
     * chance on tiles that are not occupied by concrete walls.
     */
    public void generateBrickWalls() {

        for (int col = 0; col < TileMap.NUM_OF_COLS; col++) {

            for (int row = 0; row < TileMap.NUM_OF_ROWS; row++) {

                boolean isPositionNull = (gridLayout[col][row] == null);
                if (!stageData.isBonusStage() && isPositionNull && getRandomBoolean() && isNotInSpawnArea(row, col)) {
                    gridLayout[col][row] = new BrickWall(col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH, true, false);
                    possiblePowerUpAndDoorCoordinates.add(new Coordinate(row, col));

                } else if (isPositionNull) {
                    // The positions that are not selected to be populated with a
                    // brick wall are marked as possible spawn locations for enemies.
                    possibleEnemyCoordinates.add(new Coordinate(row, col));
                }
            }
        }
    }

    /**
     * Generates the set of enemies present on the game grid at valid locations
     * previously determined during brick wall generation.
     * @return ArrayList containing the enemy objects that are present on
     * the grid following the specifications of the current stage.
     */
    public ArrayList<Enemy> generateEnemies() {
        Coordinate positionOnGrid;
        EnemySet[] enemiesPresent = stageData.getEnemiesPresent();
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();

        // If the stage is a bonus stage, we generate
        // 8 enemies of the only type passed in the StageData object.
        if (stageData.isBonusStage()) {
            EnemyType onlyTypePresent = enemiesPresent[0].getEnemyType();

            for (int i = 0; i < 8; i++) {
                positionOnGrid = getRandomCoordinateFromSet(possibleEnemyCoordinates);
                int row = positionOnGrid.getRow();
                int col = positionOnGrid.getCol();

                enemies.add(new Enemy(onlyTypePresent, col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH));
            }
            return enemies;
        }

        // If the stage data is not a bonus stage,
        // we spawn the enemy sets specified inside StageData.
        for (EnemySet enemySet : enemiesPresent) {

            EnemyType currentSetType = enemySet.getEnemyType();
            int setSize = enemySet.getNumberPresent();

            for (int i = 0; i < setSize; i++) {
                positionOnGrid = getRandomCoordinateFromSet(possibleEnemyCoordinates);
                int row = positionOnGrid.getRow();
                int col = positionOnGrid.getCol();

                enemies.add(new Enemy(currentSetType, col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH));
            }

        }

        return enemies;
    }

    /**
     * Creates a set of harder enemies to be spawned on the grid.
     * This methods gets called when flames collide with a PowerUp
     * or Door.
     * @param harderEnemyType The enemy type that will be used
     *                        to generate the harder set of enemies.
     * @param posX Integer specifying the x coordinate where the enemies
     *             should be located (Position of the door or powerUp)
     * @param posY Integer specifying the y coordinate where the enemies
     *             should be located (Position of the door or powerUp)
     * @return An ArrayList<Enemy> containing the harder set of enemies.
     */
    public ArrayList<Enemy> createSetOfHarderEnemies(EnemyType harderEnemyType, int posX, int posY) {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();

        // We spawn 8 harder enemies than the hardest type present on the grid.
        for (int i = 0; i < 8; i++) {
            enemies.add(new Enemy(harderEnemyType, posX, posY));
        }

        return enemies;
    }

    /**
     * Creates a set of harder enemies at random valid locations
     * on the game grid. This method gets called once the 200 second
     * timer runs out in a non-bonus stage.
     * @param harderEnemyType The enemy type that will be used
     *                        to generate the harder set of enemies.
     * @return Returns an ArrayList<Enemy> containing the harder enemies.
     */
    public ArrayList<Enemy> createSetOfHardEnemiesAtRandomPositions(EnemyType harderEnemyType) {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        Coordinate spawnCoordinate;

        for (int i = 0; i < 8; i++) {

            // If there are not enough valid coordinates to spawn
            // the 8 enemies, we break;
            if (possibleEnemyCoordinates.size() == 0) break;

            spawnCoordinate = getRandomCoordinateFromSet(possibleEnemyCoordinates);
            int row = spawnCoordinate.getRow();
            int col = spawnCoordinate.getCol();
            enemies.add(new Enemy(harderEnemyType, col * TileMap.TILE_SIDE_LENGTH + 1, row * TileMap.TILE_SIDE_LENGTH + 1));

        }
        return enemies;
    }

    /**
     * Used to generate a new enemy of the unique type present in
     * a bonus stage. We generate a new enemy every second during
     * a bonus stage.
     * @return A new enemy object to be placed on the grid.
     */
    public Enemy createBonusStageEnemy() {
        Coordinate positionOnGrid = getRandomCoordinateFromSet(possibleEnemyCoordinates);
        int row = positionOnGrid.getRow();
        int col = positionOnGrid.getCol();

        //In the case of a bonus stage, the enemiesPresent array will only contain one enemySet
        //We retrieve the only type to create new enemies of that same type.
        EnemyType type = stageData.getEnemiesPresent()[0].getEnemyType();
        Enemy bonusEnemy = new Enemy(type, col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH);
        return bonusEnemy;
    }

    /**
     * Spawns the powerUp present on the stage based on the
     * requirements given by the StageData object.
     * @return Returns the powerUp object present on the grid.
     */
    public PowerUp generatePowerUp() {

        // Bonus stages do not have powerUps.
        if (stageData.isBonusStage()) return null;

        PowerUpType powerUpType = stageData.getPowerUpPresent();
        Coordinate positionOnGrid = getRandomCoordinateFromSet(possiblePowerUpAndDoorCoordinates);
        int row = positionOnGrid.getRow();
        int col = positionOnGrid.getCol();

        PowerUp powerUp = new PowerUp(powerUpType, col * TileMap.TILE_SIDE_LENGTH, row * TileMap.TILE_SIDE_LENGTH);
        return powerUp;
    }

    /**
     * Spawns the door object that allows the object
     * to advance to the next stage in a non-bonus stage.
     * @return Returns the door object present on the grid.
     */
    public Door generateDoor() {

        // There is no door in a bonus stage.
        if (stageData.isBonusStage()) return null;

        Coordinate positionOnGrid = getRandomCoordinateFromSet(possiblePowerUpAndDoorCoordinates);
        int row = positionOnGrid.getRow();
        int col = positionOnGrid.getCol();
        Door door = new Door(col * TileMap.TILE_SIDE_LENGTH + 1, row * TileMap.TILE_SIDE_LENGTH + 1);
        return door;
    }

    /**
     * Load the StageData specifications for the
     * next stage.
     * @param stageData Object specifying the types of enemies present
     *                  on the current stage and whether the current
     *                  stage is a bonus stage or not.
     */
    public void nextStage(StageData stageData) {
        this.stageData = stageData;
        clearPossibleCoordinates();
    }

    /**
     * Retrieves a random coordinate from the passed ArrayList. Used to
     * determine the spawn locations of new enemies on the grid.
     * @param coordinates
     * @return Returns random coordinates from ArrayList<Coordinate>.
     */
    public Coordinate getRandomCoordinateFromSet(ArrayList<Coordinate> coordinates) {
        int index = randomGenerator.nextInt(coordinates.size());
        Coordinate coordinate = coordinates.remove(index);
        return coordinate;
    }

    /**
     * Clears the structures containing possible spawn locations
     * for enemies, powerUps and doors.
     */
    public void clearPossibleCoordinates() {
        possibleEnemyCoordinates = new ArrayList<Coordinate>();
        possiblePowerUpAndDoorCoordinates = new ArrayList<Coordinate>();
    }

    /**
     * Determines whether the selected location to spawn a brick wall
     * is in the three tile area where the player spawns after dying. No
     * brick walls should appear at this location.
     * @param row Integer specifying the row selected to spawn a brick wall.
     * @param col Integer specifying the col selected to spawn a brick wall.
     * @return Returns true if the passed coordinate is not in the
     * area where the player spawns.
     */
    public boolean isNotInSpawnArea(int row, int col) {
        return (row + col != 3 && row + col != 2);
    }

    /**
     * Returns a true boolean with a 20% chance.
     * @return A randomly generated boolean with a 20%
     * chance of being true.
     */
    public boolean getRandomBoolean() {
        return randomGenerator.nextFloat() <= 0.2;
    }


    /**
     * Retrieve the two dimensional array encoding where the
     * walls are present on the grid.
     * @return A two dimensional array specifying the location
     * of walls on the grid.
     */
    public GameObject[][] getGridLayout() {
        return gridLayout;
    }

    /**
     * Retrieve the possible enemy spawn locations.
     * @return An ArrayList containing the possible spawn locations of
     * enemies on the grid.
     */
    public ArrayList<Coordinate> getPossibleEnemyCoordinates() {
        return possibleEnemyCoordinates;
    }

    /**
     * Retrieve the possible powerUp and Door spawn locations.
     * @return An ArrayList containing the possible spawn locations for
     * the powerUp and Door objects.
     */
    public ArrayList<Coordinate> getPossiblePowerUpAndDoorCoordinates() {
        return possiblePowerUpAndDoorCoordinates;
    }
}
