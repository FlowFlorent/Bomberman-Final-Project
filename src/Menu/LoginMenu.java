package Menu;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The very first menu that loads up when the program is started that gives the user the option to either login or
 * create a new account. It inherits some functionality from MenuTemplate. It loads up LoginPopup and
 * AccountCreationMenuPopUp for logging in or creating a new account. It also has the exit functionality which
 * stops the program entirely.
 */
public class LoginMenu extends MenuTemplate {

    private String[] options = {"Login","Create Account","Exit"};
    private int currentChoice = 0;
    private MenuManager menuManager;
    private Color titleColor = MenuTemplate.TITLE_COLOR;
    private Font titleFont = MenuTemplate.TITLE_FONT;
    private Font font = MenuTemplate.BODY_FONT;


    /**
     * Constructor for the Login Menu
     * @param menuManager Object menuManager is passed to navigate between the different game states and menus.
     */
    public LoginMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * Draws the login menu with the options when the program is first started.
     * @param g Graphics object corresponding to the JPanel where the game play state is rendered.
     */
    @Override
    public void draw(Graphics2D g) {


        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("BOMBERMAN", 80, 70);
        g.drawString("Login Menu", 80, 100);

        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(MenuTemplate.BODY_COLOR);
            } else {
                g.setColor(MenuTemplate.BODY_SELECTED_COLOR);
            }

            g.drawString(options[i], 95, 140 + i * 15);

        }
    }

    /**
     * Implements the functionality of each choice on the screen
     */
    private void select() {
        if (currentChoice == 0) {
            LoginPopup loginPopUp = new LoginPopup(menuManager);
            loginPopUp.setVisible(true);
        }
        if (currentChoice == 1) {
            AccountCreationPopUp acp = new AccountCreationPopUp(menuManager);
            acp.setVisible(true);
        }
        if (currentChoice == 2) {
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

    /**
     * Redirects the user to the MainMenu
     */
    public void redirectToMainMenu() {
        menuManager.setMenuState(MenuState.MAIN);
    }

}

