/**
 * Created by danielmacario on 14-11-22.
 */
package Menu;

import Database.DatabaseController;
import Database.MongoDBController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Defines the logic and elements necessary to enable
 * the user to play an unlocked stage.
 */
public class LevelSelectionPopUp extends JFrame {

    private JScrollPane scrollpane;
    private MenuManager menuManager;

    /**
     * Initializes a LevelSelectionPopUp object.
     * @param menuManager A menuManager instance used to switch to the
     *                    InGame state once the player has selected a
     *                    stage.
     */
    public LevelSelectionPopUp(MenuManager menuManager) {
        super("Select Level Menu");
        this.menuManager = menuManager;
        setSize(200, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        displayUnlockedLevels();
        setVisible(true);
    }

    /**
     * Adds all the components necessary for level selection
     * to the window where the level is displayed. The menu
     * consists of a scrollable list of buttons each with a
     * number representing the stage to be played.
     */
    public void displayUnlockedLevels() {

        // For new accounts the number of unlocked levels is
        // set to a default of one.
        int numOfUnlockedLevels = getNumberOfUnlockedLevels();

        JPanel view = new JPanel(new GridLayout(0,1,0,10));
        view.setSize(100, 100);
        JButton exitButton = new JButton("Close");
        exitButton.setBorder( new LineBorder(Color.BLACK) );
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeWindow();
            }
        });

        view.add(exitButton);

        // Create all the buttons for scrollable list of the levels the user
        // has unlocked.
        for (int levelNumber = 1; levelNumber <= numOfUnlockedLevels; levelNumber++) {

            final JButton levelButton = new JButton(String.valueOf(levelNumber));
            final int finalLevelNumber = levelNumber;

            levelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //levelSelected(finalLevelNumber);
                    levelSelected(45);
                }
            });

            view.add(levelButton);
        }

        // Enable the the window scrolling functionality.
        scrollpane = new JScrollPane(
                view,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        getContentPane().add(scrollpane, BorderLayout.CENTER);

    }

    /**
     * Retrieve the number of levels the current user has unlocked.
     * @return An integer representing the number of levels the user
     * has unlocked by completing them in the past.
     */
    public int getNumberOfUnlockedLevels() {
        int levelUnlocked = 1;
        try {
            //levelUnlocked = DatabaseController.getLevelUnlocked(menuManager.getPlayerUserName());
            levelUnlocked = MongoDBController.getLevelUnlocked(menuManager.getPlayerUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return levelUnlocked;
    }

    /**
     * Passes the selected level to the MenuManager which will initiate
     * the GamePlayState of the game from that stage.
     * @param levelNumber Integer representing the number of the level
     *                    selected by the user.
     */
    public void levelSelected(int levelNumber) {
        System.out.println(levelNumber);
        menuManager.startGameFromSelectedStage(levelNumber);
        closeWindow();
    }

    /**
     * Closes the LevelSelection popUp window.
     */
    public void closeWindow() {
        setVisible(false);
        dispose();
    }
}
