/**
 * Created by danielmacario on 14-11-13.
 */
package GameObject.ArtificialIntelligence;

import GameObject.Direction;
import GameObject.Enemy;
import GameObject.TileMap;
import GamePlay.Coordinate;

import java.io.Serializable;

/**
 * Class used to establish the logic implemented by medium intelligence enemies. Medium intelligence enemies
 * are capable of chasing the player if they are within a one tile radius. They also have a 10% chance of
 * performing random turns at intersections.
 */
public class MediumIntelligence extends ArtificialIntelligence implements Serializable {

    private static final int CHASE_THRESHOLD = 65;

    /**
     * Medium intelligence enemies have a 10% chance of making a random turn at an intersection. If the
     * turn is set to happen, then we pick a direction that is perpendicular to the movement of the
     * enemy. For example, if the enemy is moving North, the possible turns are East and West.
     * @param enemy Enemy object to be moved on the grid.
     */
    @Override
    public void move(Enemy enemy) {
        moveEnemyOnBoard(enemy);

        int posX = enemy.getPosX();
        int posY = enemy.getPosY();
        Direction directionOfMovement = enemy.getDirectionOfMovement();

        boolean shouldTurn = random.nextFloat() <= 0.1f;
        if (randomTurnOnIntersection(posX, posY, shouldTurn)) {
            enemy.setDirectionOfMovement(Direction.getRandomPerpendicularDirection(directionOfMovement));
            enemy.setPosX(posX - posX % TileMap.TILE_SIDE_LENGTH);
            enemy.setPosY(posY - posY % TileMap.TILE_SIDE_LENGTH);

        }
    }

    /**
     * This methods implements the functionality of the enemy following the player if certain conditions are met.
     * The conditions that have to be met are the following: If the enemy and the player are on the same row or the
     * same column. Also the enemy object must not be more than 65 units away from the player.
     * @param playerPosX x coordinate of the player object on the grid.
     * @param playerPosY y coordinate of the player object on the grid.
     * @param distanceFromEnemyToPlayer Integer representing distance of the enemy from the player.
     * @param enemy Enemy object that will start chasing the player.
     */
    @Override
    public void chasePlayer(int playerPosX, int playerPosY, int distanceFromEnemyToPlayer, Enemy enemy) {

        boolean playerAndEnemyOnSameRow = playerPosY - enemy.getPosY() < 3 && playerPosY - enemy.getPosY() > -3;
        boolean playerAndEnemyOnSameCol = playerPosX - enemy.getPosX() < 3 && playerPosX - enemy.getPosX() > -3;

        if (!(playerAndEnemyOnSameCol || playerAndEnemyOnSameRow)) {
            return;
        }

        int enemyPosX = enemy.getPosX();
        int enemyPosY = enemy.getPosY();

        if (distanceFromEnemyToPlayer < CHASE_THRESHOLD) {
            if (playerAndEnemyOnSameRow) {
                enemy.setPosY(enemyPosY - enemyPosY % TileMap.TILE_SIDE_LENGTH);
                if (playerPosX > enemyPosX) {
                    enemy.setDirectionOfMovement(Direction.EAST);
                    System.out.println("going east");
                } else {
                    enemy.setDirectionOfMovement(Direction.WEST);
                    System.out.println("going west");
                }
            } else {
                enemy.setPosX(enemyPosX - enemyPosX % TileMap.TILE_SIDE_LENGTH);
                if (playerPosY > enemyPosY) {
                    enemy.setDirectionOfMovement(Direction.SOUTH);
                    System.out.println("going south");
                } else {
                    enemy.setDirectionOfMovement(Direction.NORTH);
                    System.out.println("going north");
                }
            }
        }
    }

    /**
     * This method determines whether a medium intelligence enemy should perform
     * a random turn at an intersection. There is a 10% chance that the turn will
     * be taken.
     * @param posX Integer representing x coordinate of the enemy.
     * @param posY  Integer representing y coordinate of the enemy.
     * @return A boolean specifying whether the random turn should be carried out
     * or not.
     */
    public boolean randomTurnOnIntersection(int posX, int posY, boolean shouldTurn) {
        boolean enemyAtXIntersection = (posX) % TileMap.TILE_SIDE_LENGTH <= 3 && (posX/ TileMap.TILE_SIDE_LENGTH) % 2 == 1;
        boolean enemyAtYIntersection = (posY) % TileMap.TILE_SIDE_LENGTH <= 3 && (posY/ TileMap.TILE_SIDE_LENGTH) % 2 == 1;
        if (enemyAtXIntersection && enemyAtYIntersection && shouldTurn) {
            return true;
        } else {
            return false;
        }
    }
}
