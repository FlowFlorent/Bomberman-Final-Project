/**
 *
 */
package Menu;

import SystemController.GameStateManager;
import SystemController.TopLevelState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * This class is used to represent the options available to the player when the game is paused. This menu is to appear
 * when the player presses the space bar during game play. The InGameMenu class inherits most of its functionality from
 * the MenuTemplate class. Specifically, the InGameMenu class defines the logic for: continuing the same game, starting
 * a new game, quitting the game entirely, redirecting to the main menu and viewing the leader board.
 */
public class InGameMenu extends MenuTemplate{

    // Options present in the in-game menu as specified in the SRS
    private String[] options = {"Resume Gameplay","Save Game", "Start New Game", "Quit Game",
                                "Exit to Main Menu", "View Leaderboard"};

    // Used to keep track of the option selected by the user.
    private int currentChoice = 0;
    private Color titleColor = MenuTemplate.TITLE_COLOR;
    private Font titleFont = MenuTemplate.TITLE_FONT;
    private Font font = MenuTemplate.BODY_FONT;
    private GameStateManager gsm;

    /**
     * Constructor for the InGameMenu class. Creates a InGameMenu object which takes both the menuManager and gsm
     * as a parameter.
     * @param menuManager Object used to navigate between the different menus
     * depending on the option selected by the player.
     * @param gsm Object used to navigate between the different game-states
     * depending on the menu option selected by the user.
     */
    public InGameMenu(MenuManager menuManager, GameStateManager gsm) {
	    this.menuManager = menuManager;
        this.gsm = gsm;
	}

    /**
     * Draws the InGameMenu when the game is paused
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
	@Override
	public void draw(Graphics2D g) { // method to ensure same formatting for every menu
        g.setColor(titleColor);
        g.setFont(titleFont);
        g.setPaint(titleColor);
        g.drawString("In-Game Menu", 80, 70);

        //draw menu options
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(MenuTemplate.BODY_COLOR);
            } else {
                g.setColor(MenuTemplate.BODY_SELECTED_COLOR);
            }

            g.drawString(options[i], X_OFFSET, Y_OFFSET + i * 15);
        }

    }

    /**
     * Implements the functionality that enables the user to scroll through the options with the
     * arrow keys on the keyboard.
     * @param k Integer code used to represent the key pressed on the keyboard.
     */
	@Override
	public void keyPressed(int k) {
        if (k == KeyEvent.VK_ENTER) {
            select();
        }
        if (k == KeyEvent.VK_UP) {
            currentChoice--;
            // Wrap around if we have reached the top end of the menu
            if (currentChoice == -1) currentChoice  = options.length - 1;
        }
        if (k == KeyEvent.VK_DOWN) {
            currentChoice++;
            // Wrap around if we have reached an lower end of the menu
            if (currentChoice == options.length) currentChoice = 0;
        }
    }

    /**
     * Enables the functionality of the current menu option selected by the user.
     */
	public void select() {

		if (currentChoice == 0) {
            //resume game play
            gsm.setState(TopLevelState.GAMEPLAYSTATE, null);
	    }
	    if (currentChoice == 1) {
            //save game
            SaveGamePopUp svg = new SaveGamePopUp(menuManager);
            svg.setVisible(true);
        }
        if (currentChoice == 2) {
            //start new game
            LevelSelectionPopUp lsp = new LevelSelectionPopUp(menuManager);
            lsp.setVisible(true);
        }
        if (currentChoice == 3) {
            //quit game
            System.exit(0);
        }
        if (currentChoice == 4) {
            //exit to main menu
            menuManager.setMenuState(MenuState.MAIN);
        }
        if (currentChoice == 5) {
            //view leaderboard
            menuManager.setPreviousLeaderboardMenuState(MenuState.INGAME);
            menuManager.setMenuState(MenuState.LEADERBOARD);
        }
	}

}
