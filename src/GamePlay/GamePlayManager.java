/**
 * Created by danielmacario on 14-10-29.
 */
package GamePlay;

import java.awt.*;

import java.io.Serializable;
import java.util.ArrayList;

import Database.DatabaseController;
import Database.MongoDBController;
import GameObject.*;
import SystemController.GameState;
import SystemController.GameStateManager;
import Menu.MenuState;
import SystemController.SoundController;
import SystemController.TopLevelState;

/**
 * Control structure in charge of updating the object models present during
 * gameplay and also rendering them on the screen.
 */
public class GamePlayManager extends GameState implements Serializable {

    private TileMap tileMap;
    private Player player;
    private Camera camera;
    private CollisionManager collisionManager;
    private boolean cameraMoving;
    private boolean secondCameraRegion;

    private int notificationDuration = 70;
    public int bonusStageCountDown = 900;
    private int bonusStageNewEnemyCountDown = 30;
    public static final int framesPerSecond = 30;
    private String saveFileName;

    //TODO: remove after demo, these are for temporary pause feature
    private Color titleColor = new Color(255, 0, 21);
    private Color hudColor = new Color(255, 255, 255);
    private Font titleFont = new Font("Gill Sans Ultra Bold", Font.PLAIN, 42);
    private Font subTitleFont = new Font("Gill Sans Ultra Bold", Font.PLAIN, 22);
    private Font hudFont = new Font("Gill Sans Ultra Bold", Font.PLAIN, 12);

    /**
     * Initialize an instance of GamePlayManager, which will control and execute all
     * the logic needed for the gamePlay state of the game.
     * @param gsm An instance of the highest controller of the application, which allows
     *            the GamePlayManager to pass control to the MenuManager.
     * @param selectedStage An integer specifying the stage selected by the user to load
     *                      the stage data..
     */
    public GamePlayManager(GameStateManager gsm, int selectedStage) {
        this.gsm = gsm;
        this.player = new Player(33, 33, true, MovableObject.NORMALSPEED);
        this.cameraMoving = false;
        this.tileMap = new TileMap(player, selectedStage, gsm.getPlayerUserName());
        this.collisionManager = new CollisionManager(player, tileMap, gsm.getPlayerUserName());
        this.player.setTileMap(tileMap);
        this.camera = new Camera(player.getPosX(), player);
    }

    /**
     * Draws the specific GamePlayState that the user is currently
     * found in. The user can be in one of the following options during
     * gamePlay: GAMEOVER, PAUSE, INGAME, FINISHEDGAME.
     * @param g The Graphics2D object where all the objects are drawn.
     */
    @Override
    public void draw(Graphics2D g) {

        GamePlayState currentState = player.getCurrentGamePlayState();

        if (currentState == GamePlayState.GAMEOVER) {
            executeGameOverStateLogic(g);
        } else if (currentState == GamePlayState.PAUSE) {
            gsm.setState(TopLevelState.MENUSTATE, MenuState.INGAME);
        } else if (currentState == GamePlayState.INGAME) {
            executeInGameLogic(g);
        } else if (currentState == GamePlayState.FINISHEDGAME) {
            executeGameCompletedLogic(g);
        }
    }

    /**
     * This method gets called once the player has completed all 60 levels
     * of the game (50 stages + 10 bonus stages).
     * @param g The Graphics2D object where the endgame notification is drawn.
     */
    private void executeGameCompletedLogic(Graphics2D g) {
        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("Congratulations!", 80, 200);
        g.setColor(titleColor);
        g.setFont(subTitleFont);
        g.drawString("All your bases are belong to us.", 70, 250);

        boolean redirectToMainMenu = notificationDurationCountDown();
        if (redirectToMainMenu) {
            gsm.setState(TopLevelState.MENUSTATE, MenuState.GAMEOVER);
        }
    }

    /**
     * This method gets called once the player has lost all the lives given
     * to him/her. It renders an endgame notification and then redirects to the
     * GameOver menu.
     * @param g The Graphics2D object where all the GameOver notification is drawn.
     */
    private void executeGameOverStateLogic(Graphics2D g) {

        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("Game Over", 100, 200);

        boolean redirectToGameOverMenu = notificationDurationCountDown();
        if (redirectToGameOverMenu) {

            //We update the number of games played.
            try {
                //DatabaseController.incrementGamesPlayed(gsm.getPlayerUserName());
                MongoDBController.incrementGamesPlayed(gsm.getPlayerUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SoundController.THEME.loop();
            gsm.setState(TopLevelState.MENUSTATE, MenuState.GAMEOVER);

        }
    }

    /**
     * Executes the logic tied to the current GamePlayState. It triggers
     * stage transitions, and notifications. And it also calls the methods
     * necessary to draw all the objects present during GamePlay.
     * @param g
     */
    public void executeInGameLogic(Graphics2D g) {

        if (tileMap.isNextStageTransition()) {
            inStageTransition(g);
            return;
        }

        if (tileMap.isBonusStage()) {
            initiateTimerToNextStageFromBonusStage();
            initiateTimeToSpawnEnemy();
        }

        // Fundamental calls necessary to update the models of the game
        player.move();
        tileMap.moveEnemies(player.getPosX(), player.getPosY(), player.isVisible());
        checkCollisions();
        updateCamera();
        camera.adjustPosition();

        // The camera is moving when the player has more than 15 tile columns
        // on his left and right sides.
        if (cameraMoving) {

            // The g.translate method shifts the JPanel so all the objects move with
            // the player. In this case, the camera is actively following the player.
            g.translate(camera.getPosX(), 0);
            player.drawBombs(g);
            tileMap.drawObjects(g);
            player.draw(g);
            g.translate(-camera.getPosX(), 0);

        } else {

            // The secondCameraRegion refers to when the player has less than 15 tile columns
            // on its right side, which means that the camera isn't moving.
            // In this case we snap the camera to a position encompassing all of the right
            // side of the board.
            if (secondCameraRegion) {
                g.translate(-(tileMap.TOTAL_WIDTH_OF_COLUMNS - 15 * tileMap.TILE_SIDE_LENGTH), 0);
                player.drawBombs(g);
                tileMap.drawObjects(g);
                player.draw(g);
                g.translate(tileMap.TOTAL_WIDTH_OF_COLUMNS - 15 * tileMap.TILE_SIDE_LENGTH, 0);
            } else {
                // The player has less than 15 tile columns on its left side. In this case we do
                // not need to adjust the camera position; it is fixed on the left side of the board.
                player.drawBombs(g);
                tileMap.drawObjects(g);
                player.draw(g);
            }

        }
        drawHUD(g, tileMap.isBonusStage(), tileMap.getTimeToHarderSetSpawn());
    }

    /**
     * This method gets called once the player has completed a stage. It draws a
     * transition screen notifying the player that they are advancing to the next
     * stage.
     * @param g The Graphics2D object where all the StageTransition notification is drawn.
     */
    public void inStageTransition(Graphics2D g) {
        boolean redirectToNextStage = notificationDurationCountDown();

        if (redirectToNextStage) {
            tileMap.setNextStageTransition(false);
        } else {
            g.setColor(titleColor);
            g.setFont(titleFont);
            g.drawString("Next Stage!", 100, 200);
        }
    }

    /**
     * Draws the heads-up display notifying the user of the time remaining in the stage,
     * the number of lives they have remaining, and their score so far in the current
     * game session.
     * @param g The Graphics2D object where GamePlay is drawn.
     * @param bonusStage A boolean specifying whether the current stage is a bonus stage.
     *                   If this is the case, the timer drawn starts at 30 seconds and not 200.
     * @param timeToHarderSetSpawn The time remaining before a harder set of enemy is spawned.
     *                             This is the default timer used in regular stages.
     */
    public void drawHUD(Graphics2D g, boolean bonusStage, int timeToHarderSetSpawn) {
        g.setColor(hudColor);
        g.setFont(hudFont);
        String hudInformation;
        hudInformation = "Lives Left: " + player.getLivesRemaining() + " | Score: " + player.getScore() +
                " | Time Remaining: ";

        if (bonusStage) {
            //display the number of seconds remaining in the bonus stage.
            hudInformation += bonusStageCountDown / framesPerSecond;
        } else {
            //display the number of seconds remaining in the regular stage.
            hudInformation += timeToHarderSetSpawn / framesPerSecond;
        }
        g.drawString(hudInformation, 160, 20);
    }

    /**
     * General timer used to control the duration of the following
     * notifications present during GamePlay: game completed, game over,
     * and stage transition. These notifications all last 1.66 seconds.
     * @return
     */
    public boolean notificationDurationCountDown() {
        notificationDuration--;
        if (notificationDuration < 0) {
            notificationDuration = 50;
            return true;
        }
        return false;
    }

    /**
     * Timer used to control the duration of a bonus stage, which
     * does not end with the player going through a door, but instead
     * has a limited duration of 30 seconds.
     */
    public void initiateTimerToNextStageFromBonusStage() {
        if (bonusStageCountDown == 0) {
            bonusStageCountDown = 900;
            player.nextStage();
        }
        bonusStageCountDown--;
    }

    /**
     * Timer used to control the spawn rate of new enemies
     * during a bonus stage. During a bonus stage, a single
     * type of enemy spawns an every second for 30 seconds.
     */
    public void initiateTimeToSpawnEnemy() {
        if (bonusStageNewEnemyCountDown == 0) {
            bonusStageNewEnemyCountDown = 30;
            tileMap.addNewEnemy();
        }
        bonusStageNewEnemyCountDown--;
    }

    /**
     * Updates the position of the camera so that it follows
     * the player once it has crossed the 15 tile threshold.
     */
    public void updateCamera() {
        int playerPosX = player.getPosX();
        if (playerPosX > tileMap.CAMERA_MOVING_LIMIT_LEFT && playerPosX < tileMap.CAMERA_MOVING_LIMIT_RIGHT) {
            cameraMoving = true;
        } else {
            cameraMoving = false;
            secondCameraRegion = false;
            if (playerPosX >= tileMap.CAMERA_MOVING_LIMIT_RIGHT) {
                secondCameraRegion = true;
            }
        }
    }

    /**
     * Passes all the data used for collision detection to the
     * collision manager. We check for collisions every time a
     * new frame is drawn.
     */
    public void checkCollisions() {
        Rectangle playerRectangle = player.getBounds();
        GameObject[][] objects = tileMap.getWalls();
        ArrayList<Bomb> bombsPlaced = player.getBombsPlaced();
        ArrayList<Enemy> enemies = tileMap.getEnemies();
        ArrayList<Flame> flames = tileMap.getFlames();
        PowerUp powerUp = tileMap.getPowerUp();
        Door door = tileMap.getDoor();
        collisionManager.handleCollisions(
                objects,
                playerRectangle,
                enemies,
                bombsPlaced,
                flames,
                powerUp, door, this.tileMap.isBonusStage()
        );

    }

    /**
     * Pass user input to the Player object contained in this instance
     * of GamePlayManager. The Player object is the terminal receiver
     * of the input signals from the user.
     * time
     * @param k Integer specifying the code of the key pressed by the user.
     */
    @Override
    public void keyPressed(int k) {
        this.player.keyPressed(k);
    }

    /**
     * Pass user input to the Player object contained in this instance
     * of GamePlayManager. The Player object is the terminal receiver
     * of the input signals from the user.
     * time
     * @param k Integer specifying the code of the key released by the user.
     */
    @Override
    public void keyReleased(int k) {
        this.player.keyReleased(k);
    }

    /**
     * Sets the current GamePlayState of the player object contained in this
     * instance of GamePlayManager to INGAME.
     */
    public void setGamePlayStateToInGame() {
        this.player.setCurrentGamePlayState(GamePlayState.INGAME);
    }

    /**
     * Retrieves the String associated with this instance of GamePlayManager that
     * is assigned right before serialization.
     * @return A String specifying the save file name associated with this
     * instance of GamePlayManager.
     */
    public String getSaveFileName() {
        return saveFileName;
    }

    /**
     * Set the saveFileName associated with this instance of GamePlayManager
     * @param saveFileName A String specifying the name of the file in
     *                     which this instance of GamePlayManager is being
     *                     serialized.
     */
    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }
}