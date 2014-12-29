/**
 * Created by danielmacario on 14-10-29.
 */
package SystemController;

import Database.DatabaseController;
import javax.swing.JFrame;

import Database.MongoDBController;
import Database.PlayerScore;

/**
 * Class in charge of initializing the game and creating the JFrame object
 * where all the elements for both the MenuState and the GamePlayState will
 * be rendered.
 */
public class Game {

    /**
     * Creates the JFrame object where all the views will be rendered
     * and where the user will interact with the application.
     * @param args
     */
    public static void main(String[] args) {

        MongoDBController dbController = new MongoDBController();
        dbController.initializeDataBase();

        SoundController.THEME.loop();
        try {
            DatabaseController.database_id = "jdbc:sqlite:user_data.db";
            DatabaseController.initializeDatabase();
            /**
            DatabaseController.createNewUser("Demo01","Dem@Us3R01", "John Doe 1");
            DatabaseController.createNewUser("Demo02","Dem@Us3R02", "John Doe 2");
            DatabaseController.createNewUser("Demo03","Dem@Us3R03", "John Doe 3");
            DatabaseController.createNewUser("Demo04","Dem@Us3R04", "John Doe 4");
            DatabaseController.createNewUser("Demo05","Dem@Us3R05", "John Doe 5");
            DatabaseController.createNewUser("Demo06","Dem@Us3R06", "John Doe 6");
            DatabaseController.createNewUser("Demo07","Dem@Us3R07", "John Doe 7");
            DatabaseController.createNewUser("Demo08","Dem@Us3R08", "John Doe 8");
            DatabaseController.createNewUser("Demo09","Dem@Us3R09", "John Doe 9");
            DatabaseController.createNewUser("Demo10","Dem@Us3R10", "John Doe 10");
            DatabaseController.setLevelUnlocked("Demo01", 11);
            DatabaseController.setLevelUnlocked("Demo02", 11);
            DatabaseController.setLevelUnlocked("Demo03", 13);
            DatabaseController.setLevelUnlocked("Demo04", 14);
            DatabaseController.setLevelUnlocked("Demo05", 15);
            DatabaseController.setLevelUnlocked("Demo06", 16);
            DatabaseController.setLevelUnlocked("Demo07", 15);
            DatabaseController.setLevelUnlocked("Demo08", 10);
            DatabaseController.setLevelUnlocked("Demo09", 11);
            DatabaseController.setLevelUnlocked("Demo10", 12);
            DatabaseController.setScore("Demo01", 10000);
            DatabaseController.setScore("Demo02", 10000);
            DatabaseController.setScore("Demo03", 13000);
            DatabaseController.setScore("Demo04", 14000);
            DatabaseController.setScore("Demo05", 16000);
            DatabaseController.setScore("Demo06", 16000);
            DatabaseController.setScore("Demo07", 16000);
           DatabaseController.setScore("Demo08", 9000);
            DatabaseController.setScore("Demo09", 11000);
            DatabaseController.setScore("Demo10", 12000);
             **/
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        JFrame window = new JFrame("Bomberman");
        //Start the single thread of the application.
        window.setContentPane(new GameController());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);

    }

}
