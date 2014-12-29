package Menu;

import GamePlay.GamePlayManager;
import SystemController.GameFileManager;
import SystemController.GameState;
import SystemController.GameStateManager;

import java.awt.*;
import java.util.Hashtable;

/**
 * The MenuManager class is used primarily to navigate between different menu states. It also deals with saving and
 * loading games. It also tells the program who the current logged in user is. The MenuManager inherits methods from
 * GameState.
 */
public class MenuManager extends GameState {

    private Hashtable<MenuState, MenuTemplate> menuStates;
    private LeaderboardMenu leaderboardMenu;
    private MenuState currentMenu;

    /**
     * Constructor for MenuManager
     * @param gsm GameStateManager is passed, so that the menu manager can navigate between game states.
     */
    public MenuManager(GameStateManager gsm) {
        this.gsm = gsm;
        menuStates = new Hashtable<MenuState, MenuTemplate>();
        menuStates.put(MenuState.MAIN, new MainMenu(this, gsm));
        menuStates.put(MenuState.LOGIN, new LoginMenu(this));
        menuStates.put(MenuState.INGAME, new InGameMenu(this, gsm));
        menuStates.put(MenuState.GAMEOVER, new GameOverMenu(this));
        leaderboardMenu = new LeaderboardMenu(this);
        menuStates.put(MenuState.LEADERBOARD, leaderboardMenu);
        currentMenu = MenuState.LOGIN;
    }

    /**
     * Sets the current MenuState.
     * @param state Variable representing the menu state to be switched to is passed.
     */
    public void setMenuState(MenuState state) {
        currentMenu = state;
    }

    /**
     * Saves the current progress of the game with the given file name.
     * @param fileName Name of the saved file is passed.
     */
    public void saveGame(String fileName) {
        gsm.saveGame(fileName);
    }

    /**
     * Sets up the required saved game with the help of the gamePlayManager.
     * @param gamePlayManager GamePlayManager is passed as it is required for the game to be loaded.
     */
    public void setUpLoadedGame(GamePlayManager gamePlayManager) {
        gsm.loadGame(gamePlayManager);
    }

    /**
     * Starts the game from the stage selected by the user.
     * @param stageSelected Passes the stage selected by the user to be loaded.
     */
    public void startGameFromSelectedStage(int stageSelected) {
        GamePlayManager newGame = new GamePlayManager(gsm, stageSelected);
        gsm.loadGame(newGame);
    }

    /**
     * Associates the program with the current logged in user.
     * @param username Username is passed as it loads up the information regarding the user.
     */
    public void associatePlayerUserName(String username) {
        GameFileManager.setUserName(username);
        gsm.setPlayerUserName(username);
    }

    /**
     * Gets the username of the current user
     * @return Username is returned to be used by the program when retrieving information about the player
     */
    public String getPlayerUserName(){
        return gsm.getPlayerUserName();
    }

    /**
     * Allows the user to go back to the previous menu before the leader board menu.
     * @param state The state at which the user was at before loading the leader board is passed.
     */
    public void setPreviousLeaderboardMenuState(MenuState state) {
        leaderboardMenu.setPreviousMenuState(state);
    }

    /**
     * Draws the current menu
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered
     */
    @Override
    public void draw(Graphics2D g) {
        menuStates.get(currentMenu).draw(g);
    }

    /**
     * Pass the user input to the corresponding menu state at a given time.
     * @param k User input is passed.
     */
    @Override
    public void keyPressed(int k) {
        menuStates.get(currentMenu).keyPressed(k);
    }

    @Override
    public void keyReleased(int k) {

    }


}
