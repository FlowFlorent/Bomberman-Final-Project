package SystemController;

import GamePlay.GamePlayManager;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;

public class GameFileManagerTest {

    @Test
    public void testSaveGameAndLoadGame() throws Exception {
        GameStateManager gameStateManager = new GameStateManager(5);
        GamePlayManager gamePlayManager = new GamePlayManager(gameStateManager, 5);
        GameFileManager.saveDirectory = "src/res/data/testSavedGames/";
        GameFileManager.setUserName("testUser1");
        GameFileManager.saveGame(gamePlayManager, "saveFileTest");

        File file = new File("src/res/data/testSavedGames/testUser1/saveFileTest.ser");
        assertTrue("The serialized save game file should exist", file.exists() && !file.isDirectory());

        GamePlayManager loadedGame = GameFileManager.loadGame("saveFileTest");
        // We test this by checking that the unique saveFileName assigned upon saving is the same
        // for both instances of GamePlayManager.
        assertTrue("The loaded GamePlayManager should be equal to the instance that " +
                "was previous saved", loadedGame.getSaveFileName().equals(gamePlayManager.getSaveFileName()));
    }

}