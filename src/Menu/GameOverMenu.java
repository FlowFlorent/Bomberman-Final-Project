package Menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * This class is used to represent the options available to the player when the game is over or completed.
 * The GameOverMenu class inherits most of its functionality from the MenuTemplate class.
 * Specifically, the GameOverMenu class defines the logic for: starting a new game, loading a previously saved game,
 * viewing the leader boards menu and returning to the main menu.
 */
public class GameOverMenu extends MenuTemplate {
    private String[] options = {"Start New Game", "Load Saved Game", "View Leaderboard", "Return to Main Menu"};
    // Used to keep track of the option that should be highlighted on the menu.
    private int currentChoice = 0;
    private Color titleColor = MenuTemplate.TITLE_COLOR;
    private Font titleFont = MenuTemplate.TITLE_FONT;
    private Font font = MenuTemplate.BODY_FONT;

    /**
     * Initializes a GameOverMenu instance, which allows the rendering of the GameOver
     * options on the screen.
     * @param menuManager Object navigates between the different game states and menus
     *                    depending on the option selected by the user.
     */
    public GameOverMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * Draws the GameOverMenu on the screen once the game has ended.
     * @param g Graphics object corresponding to the JPanel where the menu will be rendered.
     */
    @Override
    public void draw(Graphics2D g) { //the code in this method is used in every menu for consistency

        g.setColor(titleColor);
        g.setFont(titleFont);
        g.setPaint(titleColor);
        g.drawString("Game Over Menu", 80, 70); //

        //draw menu options contained in the options Array
        g.setFont(font);
        for (int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(MenuTemplate.BODY_COLOR);
            } else {
                g.setColor(MenuTemplate.BODY_SELECTED_COLOR);
            }
            g.drawString(options[i], X_OFFSET, Y_OFFSET + i * 15);
        }
    }

    /**
     * Enables the user to scroll through the options in the menu using the arrow
     * keys on the keyboard.
     * @param k Integer used to represent the key pressed on the keyboard.
     */
    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_ENTER) {
            select();
        }
        if (k == KeyEvent.VK_UP) {
            currentChoice--;
            if (currentChoice == -1) currentChoice = options.length - 1;
        }
        if (k == KeyEvent.VK_DOWN) {
            currentChoice++;
            if (currentChoice == options.length) currentChoice = 0;
        }
    }


    /**
     * Enables the functionality of the current menu option selected by the user.
     */
    public void select() {
        if (currentChoice == 0) {
            //start new game
            LevelSelectionPopUp lsp = new LevelSelectionPopUp(menuManager); // level selection pops up
            lsp.setVisible(true);
        }
        if (currentChoice == 1) {
            //load saved game
            LoadGamePopUp acp = new LoadGamePopUp(menuManager); //options of saved game pops up
            acp.setVisible(true);
        }
        if (currentChoice == 2) {
            //view leaderboard
            menuManager.setPreviousLeaderboardMenuState(MenuState.GAMEOVER);
            menuManager.setMenuState(MenuState.LEADERBOARD);
        }
        if (currentChoice == 3) {
            //return to main menu
            menuManager.setMenuState(MenuState.MAIN);
        }
    }
}

