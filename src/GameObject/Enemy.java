/**
 * Created by danielmacario on 14-11-12.
 */
package GameObject;

import GameObject.ArtificialIntelligence.*;
import GamePlay.Coordinate;

import javax.swing.*;
import java.io.Serializable;

/**
 * Used to represent the enemy objects present in the game. Note that there are 8 different enemy types in the game.
 * Instead of creating 8 different classes to represent each enemy, we have a single dynamic structure that creates
 * an enemy object based on the EnemyType passed to the constructor.
 *
 * Enemies can have 3 different speed attributes, the capability of passing through walls, and also 3 different
 * levels of intelligence.
 */
public class Enemy extends MovableObject implements Serializable {

    public static final int SPRITE_SIDE_LENGTH = 30;
    private EnemyType enemyType;
    private Direction directionOfMovement;
    private ArtificialIntelligence intelligence = null;
    private PathFinder pathFinder;
    private int difficultyRanking;
    private boolean hitByFlames = false;

    /**
     * Dynamically generate the enemy object based on the EnemyType passed as argument.
     * @param type EnemyType to be created
     * @param posX x position of the enemy on the grid
     * @param posY y position of the enemy on the grid
     */
    public Enemy(EnemyType type, int posX, int posY) {

        this.enemyType = type;
        this.posX = posX;
        this.posY = posY;
        this.previousX = posX;
        this.previousY = posY;
        this.visible = true;
        //Randomly select a direction of movement upon creation
        this.directionOfMovement = Direction.getRandomDirection();

        //Depending on the EnemyTyped passed, we select a score attribute, image, level of intelligence, speed,
        //and whether the enemy can pass through walls or not.
        switch (type) {
            case BALLOOM:
                this.imagePath = "/res/image/Enemies/balloom.png";
                this.score = 100;
                this.speed = MovableObject.SLOWSPEED;
                this.wallPass = false;
                this.intelligence = new LowIntelligence();
                this.difficultyRanking = 0;
                break;
            case ONEAL:
                this.imagePath = "/res/image/Enemies/oneal.png";
                this.score = 200;
                this.speed = MovableObject.NORMALSPEED;
                this.wallPass = false;
                this.intelligence = new MediumIntelligence();
                this.difficultyRanking = 1;
                break;
            case DOLL:
                this.imagePath = "/res/image/Enemies/doll.png";
                this.score = 400;
                this.speed = MovableObject.SLOWSPEED;
                this.wallPass = false;
                this.intelligence = new LowIntelligence();
                this.difficultyRanking = 2;
                break;
            case MINVO:
                this.imagePath = "/res/image/Enemies/minvo.png";
                this.score = 800;
                this.speed = MovableObject.NORMALSPEED;
                this.wallPass = false;
                this.intelligence = new MediumIntelligence();
                this.difficultyRanking = 3;
                break;
            case KONDORIA:
                this.imagePath = "/res/image/Enemies/kondoria.png";
                this.score = 1000;
                //TODO: define 'slowest' speed
                this.speed = MovableObject.SLOWSPEED;
                this.wallPass = true;
                this.intelligence = new HighIntelligence();
                this.difficultyRanking = 4;
                break;
            case OVAPI:
                this.imagePath = "/res/image/Enemies/ovapi.png";
                this.score = 2000;
                this.speed = MovableObject.SLOWSPEED;
                this.wallPass = true;
                this.intelligence = new MediumIntelligence();
                this.difficultyRanking = 5;
                break;
            case PASS:
                this.imagePath = "/res/image/Enemies/pass.png";
                this.score = 4000;
                this.speed = MovableObject.FASTSPEED;
                this.wallPass = false;
                this.intelligence = new HighIntelligence();
                this.difficultyRanking = 6;
                break;
            case PONTAN:
                this.imagePath = "/res/image/Enemies/pontan.png";
                this.score = 8000;
                this.speed = MovableObject.FASTSPEED;
                this.wallPass = true;
                this.intelligence = new HighIntelligence();
                this.difficultyRanking = 7;
                break;
        }
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    /**
     * This method moves the enemy based on its type of intelligence (low,medium and high).
     */
    public void move() {
        intelligence.move(this);
    }

    /**
     * This method reverses the direction of the enemy in terms of its cardinal
     * direction based on the enemys' intelligence.
     */
    public void reverseDirection() {
        intelligence.reverseDirection(this);
    }

    /**
     * This method gets called if the player is close to the enemy.
     * It is used by the enemy depending on its intelligence to chase the player, based on several parameters:
     * @param playerPosX x coordinate of the player object on the grid.
     * @param playerPosY y coordinate of the player object on the grid.
     * @param distanceBetweenEnemyAndPlayer Distance between the center of the player object
     *                                      and the center of the enemy object.
     */
    public void chasePlayer(int playerPosX, int playerPosY, int distanceBetweenEnemyAndPlayer) {
        intelligence.chasePlayer(playerPosX, playerPosY, distanceBetweenEnemyAndPlayer, this);
    }

    /**
     * Disable the visibility of the object. This method gets called once the enemy has been killed
     * by a bomb explosion.
     */
    public void death() {
        this.visible = false;
    }

    /**
     *Attains the cardinal direction that the enemy is travelling towards.
     * @return the cardinal direction of the enemy's direction of movement
     */
    public Direction getDirectionOfMovement() {
        return directionOfMovement;
    }

    /**
     * Changes the cardinal direction that the enemy is travelling towards, to an assigned direction.
     * @param directionOfMovement This assigned direction is now the enemy's new direction of movement.
     */
    public void setDirectionOfMovement(Direction directionOfMovement) {
        this.directionOfMovement = directionOfMovement;
    }

    /**
     *This method is utilised to retrieve the enemy/enemies present during game play
     * @return the type of enemy (whether Minvo or Balloom,etc) present during each stage of game play.
     */
    public EnemyType getEnemyType() {
        return enemyType;
    }

    /**
     * Retrieves the specific centered location of the enemy based on its current centeralized coordinate
     * @return the centralized coordinate location of the enemy.
     */
    public Coordinate getCenterOfEnemyAsCoordinate() {
        return new Coordinate(posX + SPRITE_SIDE_LENGTH / 2, posY + SPRITE_SIDE_LENGTH / 2);
    }

    /**
     * Checks whether or not the enemy is hit by the flame
     * @return a boolean- if enemy is hit by flame, it returns true. False if the enemy is not hit.
     */
    public boolean isHitByFlames() {
        return hitByFlames;
    }

    /**
     * Assigns whether or not the enemy is hit by the flame
     * @param hitByFlames this assigned parameter is now the decision of whether or not the enemy is hit by the flame
     */
    public void setHitByFlames(boolean hitByFlames) {
        this.hitByFlames = hitByFlames;
    }

    /**
     * Attains the ranking of how difficult the enemy is to kill (each enemy has a different difficulty ranking)
     * @return the integer corresponding to the enemys' difficulty ranking
     */
    public int getDifficultyRanking() {
        return difficultyRanking;
    }

    /**
     *Attains the type of intelligence the enemy contains- whether the enemy has low, medium or high intelligence
     * @return enemys' intelligence type
     */
    public ArtificialIntelligence getIntelligence() {
        return intelligence;
    }

    /**
     * If the enemy is defined to be of high intelligence, the shortest path between the player and the enemy is defined.
     * This method is called to attain this short path.
     * @return the shortest path between the player and enemy
     */
    public PathFinder getPathFinder() {
        return pathFinder;
    }

    /**
     * Assigns the shortest path between the player and enemy
     * @param pathFinder This assigned path is now the shortest path used when the getPathFinder method is called
     */
    public void setPathFinder(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

}

