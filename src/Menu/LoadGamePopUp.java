package Menu;

import GamePlay.GamePlayManager;
import SystemController.GameFileManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * The LoadGamePopUp is accessed through the main menu and allows the user to load a previously saved game.
 * This popup allows the user to scroll through the list of previously saved games and choose which saved game
 * to start up. Methods from JFrame are inherited to be used in the popup.
 */
public class LoadGamePopUp extends JFrame {

    private JScrollPane scrollpane;
    private MenuManager menuManager;

    /**
     * Constructor for LoadGamePopUp.
     * @param menuManager Object menuManager is passed to navigate between the different game states and menus.
     */
    public LoadGamePopUp(MenuManager menuManager) {
        super("Load Game Menu");
        this.menuManager = menuManager;

        //Sets up window attributes
        setSize(200, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        displayFiles();
        setVisible(true);
    }

    /**
     * Displays the existing saved game files.
     */
    public void displayFiles() {

        ArrayList<String> userSavedFiles = getFileNames(menuManager.getPlayerUserName());
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

        if (userSavedFiles == null) {
            JLabel label = new JLabel("No saved Files exist");
            view.add(label);
        } else {
            for (final String fileName : userSavedFiles) {
                final JButton fileButton = new JButton(String.valueOf(fileName));
                fileButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        fileButtonClicked(fileName);
                    }
                });

                view.add(fileButton);
            }
        }

        scrollpane = new JScrollPane(view,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollpane, BorderLayout.CENTER);
    }


    /**
     * Loads the selected game.
     * @param fileName The name of the saved game file chosen is passed, so that it can be loaded.
     */
    public void fileButtonClicked(String fileName) {
        GamePlayManager loadedGame = GameFileManager.loadGame(fileName);
        menuManager.setUpLoadedGame(loadedGame);
        closeWindow();
    }

    /**
     * Adds functionality to close the popup.
     */
    public void closeWindow() {
        setVisible(false);
        dispose();
    }

    /**
     * Gets the array of saved files for the current user.
     * @param username String is passed, so that it can match it with the files in the database for the current user.
     * @return Arraylist of save games is returned after matching it with the username.
     */
    public static ArrayList<String> getFileNames(String username) {

        //Access the directory where the the user files are saved
        String folderPath = "src/res/data/savedgames/" + username + "/";
        File f = new File(folderPath);

        if (!f.exists()) return null;
        File[] files = f.listFiles();
        ArrayList<String> filePaths = new ArrayList<String>();

        String fileName;
        for (File file : files) {
            try {
                fileName = file.getPath().replace("\\", "/").replace(folderPath, "").replace(".ser", "");
                filePaths.add(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filePaths;
    }

}
