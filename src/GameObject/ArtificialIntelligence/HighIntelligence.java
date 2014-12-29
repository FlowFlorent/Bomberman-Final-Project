/**
 * Created by danielmacario on 14-11-13.
 */
package GameObject.ArtificialIntelligence;

import GameObject.Direction;
import GameObject.Enemy;
import GameObject.TileMap;
import GamePlay.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to establish the logic implemented by high intelligence enemies. High intelligence enemies
 * are capable of finding the shortest path from their location to the player if the player is within
 * a two tile radius. For this purpose we use the A* path finding algorithm. It also specifies
 * how the high intelligence enemies move on the grid.
 */
public class HighIntelligence extends ArtificialIntelligence implements Serializable {

    // Maximum distance at which the player will be chased.
    public static final int CHASE_THRESHOLD = 85;

    public static PathFinder pathFinder;
    //List of tiles that connect the current position of the enemy to the position of the player.
    private ArrayList<Coordinate> pathToPlayer;
    private boolean chaseEnabled;
    private Coordinate nextDestination;

    /**
     * The chasePlayer method determines whether the enemy should initiate
     * movement in the direction of the enemy.
     * @param playerPosX x coordinate of the player object on the grid.
     * @param playerPosY y coordinate of the player object on the grid.
     * @param distanceFromEnemyToPlayer Integer representing the distance from
     *                                  the center of the enemy object to the
     *                                  center of the player object.
     * @param enemy Enemy object that will start chasing the player.
     */
    @Override
    public void chasePlayer(int playerPosX, int playerPosY, int distanceFromEnemyToPlayer, Enemy enemy) {

        boolean enemyAtCenterOfTile =
                enemy.getPosX() % TileMap.TILE_SIDE_LENGTH == 0 && enemy.getPosY() % TileMap.TILE_SIDE_LENGTH == 0;

        // Initiate a chase only if the enemy is at the center of a tile and the distance
        // to the player is less than 85 (the diagonal length of two tiles combined).
        // Here we meet the requirement that the enemy should only recalculate the path
        //to the player once it has traversed one tile.
        if (enemyAtCenterOfTile && distanceFromEnemyToPlayer < CHASE_THRESHOLD) {

            pathToPlayer = pathFinder.findPath(playerPosX, playerPosY, enemy.getPosX(), enemy.getPosY(), enemy.hasWallPass());
            if (pathToPlayer != null) {
                pathToPlayer.remove(0);
                setNextDestination();
                chaseEnabled = true;
            }
        }
    }

    /**
     * This method sets the next destination the enemy needs to go to in order
     * to arrive at the location of the player. It is called every time the enemy
     * arrives at a destination specified by the A* algorithm.
     * @return A boolean specifying whether the next destination was set successfully,
     * or not.
     */
    public boolean setNextDestination() {
        if (pathToPlayer == null || pathToPlayer.isEmpty()) {
            return false;
        }

        // Set the next destination to the first coordinate found
        // in the path to player ArrayList.
        Coordinate nextPositionOnGrid = pathToPlayer.remove(0);
        int nextRow = nextPositionOnGrid.getRow();
        int nextCol = nextPositionOnGrid.getCol();
        nextDestination = new Coordinate((nextCol + 1) * TileMap.TILE_SIDE_LENGTH, (nextRow + 1) * TileMap.TILE_SIDE_LENGTH);
        return true;
    }

    /**
     * If a chase is not enabled, then the enemy traverses the grid
     * with a 50% of turning at every intersection. If a chase is enabled,
     * the enemy moves towards its next destination specified by the path
     * calculated using the A* algorithm.
     * @param enemy Enemy object to be moved on the grid.
     */
    @Override
    public void move(Enemy enemy) {
        int enemyPosX = enemy.getPosX();
        int enemyPosY = enemy.getPosY();

        // If the chase is enabled, move toward the next destination in the path towards the player.
        if (chaseEnabled) {

            if (nextDestination == null) {
                chaseEnabled = false;
                return;
            }

            int nextX = nextDestination.getRow();
            int nextY = nextDestination.getCol();
            boolean enemyIsAtNextRow = (Math.abs(enemyPosY - nextY) <= 1);
            boolean enemyIsAtNextCol = (Math.abs(enemyPosX - nextX) <= 1);
            if (enemyIsAtNextRow && enemyIsAtNextCol) {
                // Snap the enemy to the next destination tile if it has arrived
                // this is done to avoid having the enemy get stuck on a turn.
                enemy.setPosY(nextY);
                enemy.setPosX(nextX);

                boolean nextDestinationExists = setNextDestination();
                if (!nextDestinationExists) {
                    // If there is no next destination, then we have reached the end of the chase.
                    // So we move the enemy using its standard movement method inherited from AI.
                    moveEnemyOnBoard(enemy);
                    chaseEnabled = false;
                    return;
                }
            }

            if (enemyIsAtNextRow) {
                // Turn the direction of movement towards the next column
                if (enemyPosX < nextX) {
                    enemy.setDirectionOfMovement(Direction.EAST);
                } else {
                    enemy.setDirectionOfMovement(Direction.WEST);
                }

            } else {
                // Turn the direction of movement towards the next row
                if (enemyPosY < nextY) {
                    enemy.setDirectionOfMovement(Direction.SOUTH);
                } else {
                    enemy.setDirectionOfMovement(Direction.NORTH);
                }

            }

        } else {
            // if chase is not enabled and enemy is at an intersection,
            // then determine if we should turn in a random direction.
            Direction directionOfMovement = enemy.getDirectionOfMovement();

            boolean shouldTurn = random.nextFloat() <= 0.1f;
            if (randomTurnOnIntersection(enemyPosX, enemyPosY, shouldTurn)) {
                enemy.setDirectionOfMovement(Direction.getRandomPerpendicularDirection(directionOfMovement));
                enemy.setPosX(enemyPosX - enemyPosX % TileMap.TILE_SIDE_LENGTH);
                enemy.setPosY(enemyPosY - enemyPosY % TileMap.TILE_SIDE_LENGTH);
            }
        }
        moveEnemyOnBoard(enemy);
    }

    /**
     * Determines if the enemy should turn in a direction with a 50% chance if the enemy is standing at an intersection.
     * @param posX The x coordinate where the enemy is located.
     * @param posY The y coordinate where the enemy is located.
     * @return A boolean specifying whether a random turn should be performed.
     */
    public boolean randomTurnOnIntersection(int posX, int posY, boolean shouldTurn) {
        boolean enemyAtXIntersection = (posX) % TileMap.TILE_SIDE_LENGTH <= 3 && (posX/TileMap.TILE_SIDE_LENGTH) % 2 == 1;
        boolean enemyAtYIntersection = (posY) % TileMap.TILE_SIDE_LENGTH <= 3 && (posY/TileMap.TILE_SIDE_LENGTH) % 2 == 1;
        if (enemyAtXIntersection && enemyAtYIntersection && shouldTurn) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the path Finder object used by the high intelligence enemy to calculate the path shortest
     * path to the player.
     * @param pathFinder
     */
    public static void setPathFinder(PathFinder pathFinder) {
        HighIntelligence.pathFinder = pathFinder;
    }

}
