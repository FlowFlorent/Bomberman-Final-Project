package GameObject.ArtificialIntelligence;

import GameObject.Enemy;

import java.io.Serializable;

/**
 * Class used to establish the logic implemented by low intelligence enemies. Low intelligence enemies move in one
 * direction until they hit an obstacle, which reverses their direction of movement. Low intelligence enemies
 * do not have any logic defined for chasing the player through the grid.
 */
public class LowIntelligence extends ArtificialIntelligence implements Serializable{

    /**
     * The movement of low intelligence enemies consists of the standard logic inherited
     * from the Artificial Intelligence class.
     * @param enemy Enemy object to be moved on the grid.
     */
    @Override
    public void move(Enemy enemy) {
        moveEnemyOnBoard(enemy);
    }

    @Override
    public void chasePlayer(int playerPosX, int playerPosY, int distanceFromEnemyToPlayer, Enemy enemy) {

    }

}
