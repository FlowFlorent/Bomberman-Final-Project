package Menu;

import SystemController.GameStateManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by danielmacario on 14-10-31.
 */

/**
 * The MenuTemplate class is used by other menus for their appearance. Methods from JFrame are inherited to be used in
 * this class.
 */
public abstract class MenuTemplate extends JFrame {

    protected MenuManager menuManager;
    protected GameStateManager gsm;

    public static final int FIELD_WIDTH = 150;
    public static final int FIELD_HEIGHT = 30;
    public static final int X_OFFSET = 95;
    public static final int Y_OFFSET = 140;


    /*
    Use these global variables for the menus, it makes the game homogeneous
     */
    //Font Manager
    //Title
    public static final Font TITLE_FONT = new Font("Century Gothic", Font.PLAIN, 28);
    public static final Color TITLE_COLOR = new Color(230, 200, 0);
    //Body
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 18);
    public static final Color BODY_COLOR = new Color(0, 35, 230);
    public static final Color BODY_SELECTED_COLOR = new Color (198, 76, 0);

    //Background
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0);

    //Methods
    public abstract void draw(Graphics2D g);
    public abstract void keyPressed(int k);

}
