/**
 * Created by danielmacario on 14-10-29.
 */
package SystemController;

import Menu.MenuManager;
import SystemController.GameStateManager;

import java.awt.*;

/**
 * Interface implemented by the two main Controllers of the game: MenuManager and GamePlayManager.
 * The common set of methods is used by the main game loop to interact with these two main
 * controllers.
 */
public abstract class GameState {

    protected GameStateManager gsm;
    protected MenuManager menuManager;

    public abstract void draw(Graphics2D g);
    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);

    public void setGameStateManager(GameStateManager gsm) {
        this.gsm = gsm;
    }
}
