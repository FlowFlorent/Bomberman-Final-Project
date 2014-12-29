package Menu;

import SystemController.GameStateManager;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * This class is used to represent the options available after the user logs in. The MainMenu class
 * inherits most of its functionality from the MenuTemplate class. Specifically, the MainMenu class defines the logic
 * for: starting a new game, loading a previously saved game, view their score on the
 * leaderboard, changing their account information, logging out, and quitting the game entirely.
 */
public class MainMenu extends MenuTemplate {

    private String[] options = {"Start Game",
            "Load Game",
            "View Leaderboard",
            "Modify Account Information",
            "Logout",
            "Quit"};
    private int currentChoice = 0;
    private Color titleColor = MenuTemplate.TITLE_COLOR;
    private Font titleFont = MenuTemplate.TITLE_FONT;
    private Font font = MenuTemplate.BODY_FONT;

    /**
     * Constructor for the MainMenu class. Creates a MainMenu object which takes both the menuManager and gsm
     * as a parameter.
     * @param menuManager Object navigates between the different menus
     * depending on the currentChoice as chosen by the player.
     * @param gsm Object navigates between the different game-states
     * depending on the currentChoice as chosen by the user.
     */
    public MainMenu (MenuManager menuManager, GameStateManager gsm) {
        this.menuManager = menuManager;
        this.gsm = gsm;
    }

    /**
     * Draws the InGameMenu with the options when the game is paused
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    @Override
    public void draw(Graphics2D g) {

        //draw the title
        g.setColor(titleColor);
        g.setFont(titleFont);
        //g.setPaint(titleColor);
        g.drawString("BOMBERMAN", 80, 70);
        g.drawString("Main Menu", 80, 100);

        //draw menu options
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(MenuTemplate.BODY_COLOR);
            } else {
                g.setColor(MenuTemplate.BODY_SELECTED_COLOR);
            }
            // pass horizontal distance, then vertical distance
            g.drawString(options[i], X_OFFSET, Y_OFFSET + i * 15);
        }

    }

    /**
     * Implements the functionality of each choice on the screen
     */
    private void select() {
        if (currentChoice == 0) {
            LevelSelectionPopUp lsp = new LevelSelectionPopUp(menuManager);
            lsp.setVisible(true);
        }
        if (currentChoice == 1) {
            LoadGamePopUp acp = new LoadGamePopUp(menuManager);
            acp.setVisible(true);
        }
        if (currentChoice == 2) {
            //view leaderboards
            menuManager.setPreviousLeaderboardMenuState(MenuState.MAIN);
            menuManager.setMenuState(MenuState.LEADERBOARD);
        }
        if (currentChoice == 3) {
            //Modify account options
            AccountOptionsPopUp modAccPop = new AccountOptionsPopUp(menuManager);
            modAccPop.setVisible(true);
        }
        if (currentChoice == 4) {
            //logout
            menuManager.setMenuState(MenuState.LOGIN);
        }
        if (currentChoice == 5) {
            //terminate the game
            System.exit(0);
        }
    }

    /**
     * Implements the functionality for the user to scroll through the options with the direction
     * buttons on the keyboard.
     * @param k KeyCode used to represent the key pressed on the keyboard.
     */
    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_ENTER) {
            select();
        }
        if (k == KeyEvent.VK_UP) {
            currentChoice--;
            if (currentChoice == -1) currentChoice  = options.length - 1;
        }
        if (k == KeyEvent.VK_DOWN) {
            currentChoice++;
            if (currentChoice == options.length) currentChoice = 0;
        }
    }

}
