/**
 * Created by danielmacario on 14-10-29.
 */
package SystemController;

import GamePlay.GamePlayManager;
import Menu.*;

import java.awt.*;

/**
 * Controller used to switch between the two states of the application:
 * Menu or GamePlay. Also acts as a channel of communication for telling
 * views to render new elements and passing user inputs.
 */
public class GameStateManager {

    private TopLevelState currentState;
    private MenuManager menuManager;
    private GamePlayManager gamePlayManager;
    private GameFileManager fileManager;
    private String playerUserName;

    /**
     * Class constructor. Creates an ArrayList used to contain instances of the
     * fundamental controllers of the game: the MenuManager and the GamePlayManager
     */
    public GameStateManager(int selectedStage) {
        menuManager = new MenuManager(this);
        gamePlayManager = new GamePlayManager(this, selectedStage);
        fileManager = new GameFileManager(playerUserName);
        currentState = TopLevelState.MENUSTATE;
    }

    /**
     * Toggle the state of the game between Menu and GamePlay
     * @param state
     */
    public void setState(TopLevelState state, MenuState menuState) {
        //current state is set to 0 by default
        if (state == TopLevelState.GAMEPLAYSTATE) {
            gamePlayManager.setGamePlayStateToInGame();
            currentState = state;
        } else {
            currentState = TopLevelState.MENUSTATE;
            menuManager.setMenuState(menuState);
        }
    }

    /**
     * Loads a serialized GamePlayManager instance, thus
     * resuming a previously saved GamePlayState.
     * @param loadedFile The GamePlayManager instance to be loaded.
     */
    public void loadGame(GamePlayManager loadedFile) {
        if (loadedFile != null) {
            loadedFile.setGamePlayStateToInGame();
            gamePlayManager = loadedFile;
            gamePlayManager.setGameStateManager(this);
            currentState = TopLevelState.GAMEPLAYSTATE;
        }
    }

    /**
     * Saves the current instance of GamePlayManager into a file
     * named after the passed String.
     * @param fileName The String to be used as the filename
     *                 for the serialized file.
     */
    public void saveGame(String fileName) {
        fileManager.saveGame(gamePlayManager, fileName);
    }

    /**
     * Call the draw method within the current game state Controller
     * at a given time
     * @param g The Graphics2D object where all the objects for
     *          both GamePlay and Menus are drawn.
     */
    public void draw(Graphics2D g) {
        if (currentState == TopLevelState.GAMEPLAYSTATE) {
            gamePlayManager.draw(g);
        } else {
            menuManager.draw(g);
        }
    }

    /**
     * Pass user input to the corresponding game state controller at a give
     * time
     * @param k Integer specifying the code of the key pressed by the user.
     */
    public void keyPressed(int k) {
        if (currentState == TopLevelState.GAMEPLAYSTATE) {
            gamePlayManager.keyPressed(k);
        } else {
            menuManager.keyPressed(k);
        }
    }

    /**
     * Pass user input to the corresponding game state controller at a give
     * time
     * @param k Integer specifying the code of the key released by the user.
     */
    public void keyReleased(int k) {
        if (currentState == TopLevelState.GAMEPLAYSTATE) {
            gamePlayManager.keyReleased(k);
        } else {
            menuManager.keyReleased(k);
        }
    }

    /**
     * Retrieve the username of the player currently logged in.
     * @return A String representing the username of the player currently
     * logged in.
     */
    public String getPlayerUserName() {
        return playerUserName;
    }

    /**
     * Set the username of the player current logged in.
     * @param playerUserName A string representing the username of
     *                       the player currently logged in.
     */
    public void setPlayerUserName(String playerUserName) {
        this.playerUserName = playerUserName;
    }
}
