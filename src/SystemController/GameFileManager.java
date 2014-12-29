/**
 * Created by danielmacario on 14-11-19.
 */
package SystemController;

import GamePlay.GamePlayManager;
import java.io.*;

/**
 * Interface in charge of saving and loading gamePlay files from the
 * system.
 */
public class GameFileManager {

    private static String userName;
    public static String saveDirectory = "src/res/data/savedgames/";

    /**
     * Initialize an instance of GameFileManager. It receives a string specifying the
     * username of the currently logged in user. This is the only key necessary to
     * access the corresponding directory where the user's files are stored.
     * @param username
     */
    public GameFileManager(String username) {
        userName = username;
    }

    /**
     * Serializes an instance of the GamePlayManager object the user decides to save.
     * Then stores the .ser file to a directory specifically created for the user that
     * is generated when his/her account is created using the account creation system.
     * @param gamePlayManager The GamePlayManager instance containing all the information
     *                        regarding the current state of the game. Including all enemy
     *                        objects, the current score, the level progression, etc.
     * @param fileName A String selected by the user to distinguish the save file.
     */
    public static void saveGame(GamePlayManager gamePlayManager, String fileName) {
        // If a previous saved file already exists with the same name, then the
        // old file is replaced by the new one.
        gamePlayManager.setSaveFileName(fileName);
        try {
            System.out.println(userName);
            FileOutputStream fileOut = new FileOutputStream(saveDirectory + userName + "/" + fileName +".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gamePlayManager);
            out.close();
            fileOut.close();
            System.out.println("Successfully saved the game");
        } catch (IOException i) {
            System.out.println("Save Game Failed");
            i.printStackTrace();
        }
    }

    /**
     * Loads a state of the game previously saved by the user.
     * @param fileName The name of the file containing the instance of
     *                 GamePlayManager to be loaded.
     * @return The GamePlayManager instance that was stored in the
     * saved file.
     */
    public static GamePlayManager loadGame(String fileName) {
        GamePlayManager loadedGame;
        try {
            FileInputStream fileIn = new FileInputStream(saveDirectory + userName + "/" + fileName + ".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            loadedGame = (GamePlayManager) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Game loaded successfully!");
        } catch(IOException i) {
            i.printStackTrace();
            return null;
        } catch(ClassNotFoundException c) {
            System.out.println("Load Game Failed");
            c.printStackTrace();
            return null;
        }

        return loadedGame;
    }

    /**
     * Associates an username with this instance of the GameFileManager.
     * @param userName A String specifying the username of the player to associate
     *                 with the GameFileManager.
     */
    public static void setUserName(String userName) {
        GameFileManager.userName = userName;
    }
}
