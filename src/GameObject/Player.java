/**
 * Created by danielmacario on 14-10-31.
 */
package GameObject;

import GamePlay.GamePlayState;
import SystemController.SoundController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used to represent the Player object on the Game Grid. The Player class inherits most of its
 * functionality from MovableObject, but it also implements the logic that enables the user to
 * interact with GamePlay. Specifically, the player class defines the logic for: dropping bombs,
 * activating powerUps, and detonating bombs. It is also the final link in the keyPressed and
 * keyReleased chain during the GamePlay state, which starts with the GameStateManager.
 */
public class Player extends MovableObject implements Serializable {

    private GamePlayState currentState;
    private TileMap tileMap;
    ArrayList<Bomb> bombsPlaced;
    private int respawnCount = 0;
    private int livesRemaining;

    // PowerUp logic data.
    private int bombsAllowed;
    private boolean bombPass;
    private boolean flamePass;
    private boolean detonatorEnabled;
    private boolean invincibilityEnabled;
    // Invincibility last for 5 seconds = 150 frames.
    private int invincibilityDuration = 150;

    // Constants
    public static final int SPRITE_SIDE_LENGTH = 30;
    public static final int PLAYER_SPAWN_COORDINATE = 32;

    /**
     * Initialize a Player object representing the Bomberman character on the grid
     * @param posX int representing the x coordinate where the player will be drawn
     * @param posY int representing the y coordinate where the player will be drawn
     * @param visible boolean specifying whether the player object should be drawn
     * @param speed int representing the speed of movement of the player object on the map
     */
    public Player(int posX, int posY, boolean visible, int speed) {
        this.imagePath = "/res/image/bomberman.png";
        this.score = 0;
        this.livesRemaining = 3;
        this.currentState = GamePlayState.INGAME;
        this.deltaX = 0;
        this.deltaY = 0;
        this.posX = posX;
        this.posY = posY;
        this.previousX = posX;
        this.previousY = posY;
        this.visible = visible;
        this.speed = speed;
        this.image = new ImageIcon(Player.class.getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.bombsPlaced = new ArrayList<Bomb>();
        this.bombsAllowed = 3;
        this.wallPass = true;
        this.bombPass = true;
        this.flamePass = true;
        this.detonatorEnabled = true;
        this.invincibilityEnabled = false;
    }

    /**
     * Draws the player object on the grid during gameplay.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    public void draw(Graphics2D g) {

        if (invincibilityEnabled) {
            updateInvincibilityTimer();
        }

        //serialization does not save Image objects
        //If the image object is empty, we reload the sprite
        if (image == null) {
            this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        }

        if (visible /*not dead*/) {
            g.drawImage(image, posX, posY, null);
        } else {
            countDownToRespawn();
        }
    }

    /**
     * Draw the bomb objects on the grid during gameplay.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    public void drawBombs(Graphics2D g) {
        if (bombsPlaced.isEmpty()) return;
        int length = bombsPlaced.size();
        for (int i = 0; i < length; i ++) {
            Bomb bomb = bombsPlaced.get(i);

            //if the bomb is not visible it means it has detonated, which means we need to draw
            //flame objects where the bomb object used to be drawn
            if (!bomb.isVisible()) {
                tileMap.addFlames(bomb.getPosX(), bomb.getPosY());
                bombsPlaced.remove(bomb);
                length = bombsPlaced.size();
            }
            else bomb.draw(g);
        }
    }

    /**
     * Detonates the oldest bomb object placed by the player.
     */
    public void detonateLastBomb() {
        boolean detonated = false;
        int i = 0;
        //detonate the oldest visible bomb in the bombsPlaced array
        while (!detonated && i < bombsPlaced.size()) {
            if (bombsPlaced.get(i).isVisible() == false) {
                i++;
            } else {
                SoundController.BOMBEXPLODE.play();
                bombsPlaced.get(i).setVisible(false);
                detonated = true;
            }
        }
    }


    /**
     * Place a bomb object on the current tile the player object is standing on.
     */
    private void placeBomb() {
        if (bombsPlaced.size() < bombsAllowed) {
            //The % allows the bombs to snap to the center of the tiles where they are placed
            int bombX;
            int bombY;

            if (posX % TileMap.TILE_SIDE_LENGTH >= 17) {
                bombX = posX - posX % TileMap.TILE_SIDE_LENGTH + TileMap.TILE_SIDE_LENGTH;
            } else {
                bombX = posX - posX % TileMap.TILE_SIDE_LENGTH;
            }

            if (posY % TileMap.TILE_SIDE_LENGTH >= 17) {
                bombY = posY - posY % TileMap.TILE_SIDE_LENGTH + TileMap.TILE_SIDE_LENGTH;
            } else {
                bombY = posY - posY % TileMap.TILE_SIDE_LENGTH;
            }

            Bomb bomb = new Bomb(bombX, bombY);
            bombsPlaced.add(bomb);
        }
    }

    /**
     * Increment the speed of the player from its default
     * of 'NORMALSPEED' to 'FASTSPEED'. Called upon picking up
     * the Speed PowerUp.
     */
    public void incrementSpeed() {
        if (speed == NORMALSPEED) {
            speed = FASTSPEED;
        }
    }

    /**
     * Disable the visibility of the player so that it is not
     * drawn on the grid. Decrease the number of lives remaining
     * by one.
     */
    public void death() {
        disablePowerUpsOnDeath();
        this.visible = false;
        decrementLivesRemaining();
        this.deltaX = 0;
        this.deltaY = 0;
    }

    /**
     * Count down 60 frames before displaying
     * the player object again. Reset the position
     * of the player object to the top-left corner
     * of the tilemap.
     */
    public void countDownToRespawn() {
        if (this.respawnCount == 60) {
            visible = true;
            posX = PLAYER_SPAWN_COORDINATE;
            posY = PLAYER_SPAWN_COORDINATE;
            respawnCount = 0;
        } else {
            respawnCount++;
        }
    }

    /**
     * Reduces the number of lives the player has remaining by 1.
     */
    public void decrementLivesRemaining() {
        this.livesRemaining--;
        if (livesRemaining < 0) {
            currentState = GamePlayState.GAMEOVER;
            SoundController.THEME.stop();
            SoundController.GAMEOVER.play();
            return;
        }
        SoundController.DEATH.play();

    }

    /**
     * Timer used to keep track of the duration of the invincibility powerUp.
     * Upon coming in contact with the powerUp, the player becomes immune to the
     * collisions with enemies and flames.
     */
    public void updateInvincibilityTimer() {
        invincibilityDuration--;
        if (invincibilityDuration == 0) {
            invincibilityEnabled = false;
            invincibilityDuration = 150;
        }
    }

    /**
     * Based on the PowerUpType passed to this method, we modify gameplay logic according to the specific
     * functionality of the corresponding powerUp.
     * @param powerUpType
     */
    public void enablePowerUp(PowerUpType powerUpType) {
        switch (powerUpType) {
            case BOMBPASS:
                bombPass = true;
                break;
            case BOMBS:
                incrementBombsAllowed();
                break;
            case DETONATOR:
                detonatorEnabled = true;
                break;
            case FLAMEPASS:
                flamePass = true;
                break;
            case FLAMES:
                tileMap.incrementBombRadius();
                break;
            case MYSTERY:
                invincibilityEnabled = true;
                break;
            case SPEED:
                incrementSpeed();
                break;
            case WALLPASS:
                wallPass = true;
                break;
        }
    }

    /**
     * Disable the powerUps on the player object that are lost upon death.
     */
    public void disablePowerUpsOnDeath() {
        bombPass = false;
        flamePass = false;
        wallPass = false;
        detonatorEnabled = false;
    }

    /**
     * Reset the position of the player to the top-left corner of the map.
     * Called upon advancing to the next stage (killing all the enemies and
     * touching the door).
     */
    public void nextStage() {
        previousX = PLAYER_SPAWN_COORDINATE;
        previousY = PLAYER_SPAWN_COORDINATE;
        posX = PLAYER_SPAWN_COORDINATE;
        posY = PLAYER_SPAWN_COORDINATE;
        //Erase all the existing bombs placed on the completed stage.
        bombsPlaced = new ArrayList<Bomb>();
        if (tileMap != null) {
            tileMap.nextStage();
        }
    }

    /**
     * Move the player object if the arrow keys are pressed.
     * Place a bomb if the x key is pressed. Detonate a bomb if the z key is pressed.
     * Enter the in-game menu if the space bar is pressed.
     * @param key KeyCode used to represent the key pressed on the keyboard.
     */
    public void keyPressed(int key) {
        //disable the controls if the player is dead
        if (visible) {
            if (key == KeyEvent.VK_UP) {
                deltaY = -speed;
            } else if (key == KeyEvent.VK_DOWN) {
                deltaY = speed;
            } else if (key == KeyEvent.VK_LEFT) {
                deltaX = -speed;
            } else if (key == KeyEvent.VK_RIGHT) {
                deltaX = speed;
            } else if (key == KeyEvent.VK_SPACE) {
                SoundController.PAUSE.play();
                currentState = GamePlayState.PAUSE;
            } else if (key == KeyEvent.VK_X) {
                if (detonatorEnabled && !bombsPlaced.isEmpty()) {
                    detonateLastBomb();
                }
            } else if (key == KeyEvent.VK_Z) {
                placeBomb();
            }
        }
    }

    /**
     * Stop moving the player object upon releasing
     * the arrow keys.
     * @param key
     */
    public void keyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) {
            deltaX = 0;
        } else if (key == KeyEvent.VK_RIGHT) {
            deltaX = 0;
        } else if (key == KeyEvent.VK_UP) {
            deltaY = 0;
        } else if (key == KeyEvent.VK_DOWN) {
            deltaY = 0;
        }
    }

    /**
     * Increments the number of bombs the player is allowed to place on the grid by one.
     * The maximum number of bombs the player can put at a time is 10. Used by the Bombs
     * powerUp.
     */
    public void incrementBombsAllowed() {
        if (bombsAllowed < 10) {
            bombsAllowed++;
        }
    }

    /**
     * Get the TileMap object associated to the player.
     * @return
     */
    public TileMap getTileMap() {
        return tileMap;
    }

    /**
     * Set the TileMap object associated to the player.
     * @param tileMap An instance of the TileMap object.
     */
    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    /**
     * Get the bombs the active bombs placed on the grid by the player.
     * @return
     */
    public ArrayList<Bomb> getBombsPlaced() {
        return bombsPlaced;
    }

    /**
     * Set the active bombs associated to the player object.
     * @param bombsPlaced An arraylist of bomb objects that have been placed by
     *                    the player.
     */
    public void setBombsPlaced(ArrayList<Bomb> bombsPlaced) {
        this.bombsPlaced = bombsPlaced;
    }

    /**
     * Get the GamePlayState object associated to the player object.
     * @return The current gamePlay state of the application.
     */
    public GamePlayState getCurrentGamePlayState() {
        return this.currentState;
    }

    /**
     * Set the current gamePlay state of the application to one of the four options outlined
     * in the GamePlayState enum.
     * @param newState The new gamePlay state of the application.
     */
    public void setCurrentGamePlayState(GamePlayState newState) {
        this.currentState = newState;
    }

    /**
     * Get the number of bombs the player is allowed to placed on the grid during gamePlay.
     * @return An integer representing the number of bombs the player is allowed to plant during gamePlay.
     */
    public int getBombsAllowed() {
        return bombsAllowed;
    }

    /**
     * Put a limit to the number of bombs the player is allowed to place on the grid.
     * @param bombsAllowed The number of bombs the player can place on the grid at a time.
     */
    public void setBombsAllowed(int bombsAllowed) {
        this.bombsAllowed = bombsAllowed;
    }

    /**
     * Determine whether the player is able to walk over bombs or not.
     * @return A boolean specifying whether the player has the bombPass powerUp.
     */
    public boolean hasBombPass() {
        return bombPass;
    }

    /**
     * Enable or disable the bombPass powerUp.
     * @param bombPass A boolean specifying whether the player has the bombPass powerUp enabled or not.
     */
    public void setBombPass(boolean bombPass) {
        this.bombPass = bombPass;
    }

    /**
     * Determine whether the player is able to detonate bombs or not.
     * @return A boolean specifying whether the player has the detonator powerUp.
     */
    public boolean isDetonatorEnabled() {
        return detonatorEnabled;
    }

    /**
     * Enable or disable the detonator powerUp.
     * @param detonatorEnabled A boolean specifying whether the player has the detonator powerUp enabled or not.
     */
    public void setDetonatorEnabled(boolean detonatorEnabled) {
        this.detonatorEnabled = detonatorEnabled;
    }

    /**
     * Enable or disable the flamePass powerUp.
     * @return A boolean specifying whether the player has the flamePass powerUp.
     */
    public boolean hasFlamePass() {
        return flamePass;
    }

    /**
     * Enable or disable the flamePass powerUp.
     * @param flamePass A boolean specifying whether the player has the flamePass powerUp enabled or not.
     */
    public void setFlamePass(boolean flamePass) {
        this.flamePass = flamePass;
    }

    /**
     * Determine whether the player is invincible (cannot die from bombs or enemies) or not.
     * @return A boolean specifying whether the player is invincible or not.
     */
    public boolean isInvincibilityEnabled() {
        return invincibilityEnabled;
    }

    /**
     * Add a specified amount of points to the score attribute of the player.
     * Specifically used inside the ScoreManager Class.
     * @param enemyScore Integer representing the score increment to be added to the player score.
     */
    public void addToScore(int enemyScore) {
        this.score += enemyScore;
    }

    /**
     * Get the number of lives the player instance has remaining.
     * @return An integer representing the number of lives the player has remaining.
     */
    public int getLivesRemaining() {
        return livesRemaining;
    }

    /**
     * Specify whether the player is invincible (cannot be killed by enemies or flames).
     * @param invincibilityEnabled A boolean specifying whether the player is invincible.
     */
    public void setInvincibilityEnabled(boolean invincibilityEnabled) {
        this.invincibilityEnabled = invincibilityEnabled;
    }
}
