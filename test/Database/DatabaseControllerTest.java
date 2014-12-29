package Database;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseControllerTest {

    @BeforeClass
    public static void InitializeDatabase() throws Exception {
        DatabaseController.database_id = "jdbc:sqlite:test_data.db";
        DatabaseController.initializeDatabase();
        DatabaseController.createNewUser("testUser1", "testPassword1","testRealName1");
        DatabaseController.createNewUser("testUser2", "testPassword2","testRealName2");
        DatabaseController.createNewUser("testUser3", "testPassword3","testRealName3");
        DatabaseController.createNewUser("testUser4", "testPassword4","testRealName4");
        DatabaseController.createNewUser("testUser5", "testPassword5","testRealName5");
        DatabaseController.createNewUser("testUser6", "testPassword6","testRealName6");
        DatabaseController.createNewUser("testUser7", "testPassword7","testRealName7");
        DatabaseController.createNewUser("testUser8", "testPassword8","testRealName8");
        DatabaseController.createNewUser("testUser9", "testPassword9","testRealName9");
        DatabaseController.createNewUser("testUser10", "testPassword10","testRealName10");
        DatabaseController.setScore("testUser1", 16000);
        DatabaseController.setScore("testUser2", 16000);
        DatabaseController.setScore("testUser3", 16000);
        DatabaseController.setScore("testUser4", 16000);
        DatabaseController.setScore("testUser5", 16000);
        DatabaseController.setScore("testUser6", 16000);
        DatabaseController.setScore("testUser7", 16000);
        DatabaseController.setScore("testUser8", 16000);
        DatabaseController.setScore("testUser9", 16000);
        DatabaseController.setScore("testUser10",16000);
        DatabaseController.setLevelUnlocked("testUser1", 11);
        DatabaseController.setLevelUnlocked("testUser2", 11);
        DatabaseController.setLevelUnlocked("testUser3", 13);
        DatabaseController.setLevelUnlocked("testUser4", 14);
        DatabaseController.setLevelUnlocked("testUser5", 15);
        DatabaseController.setLevelUnlocked("testUser6", 17);
        DatabaseController.setLevelUnlocked("testUser7", 19);
        DatabaseController.setLevelUnlocked("testUser8", 21);
        DatabaseController.setLevelUnlocked("testUser9", 25);
        DatabaseController.setLevelUnlocked("testUser10", 34);
    }
    @AfterClass
    public static void tearDown() throws Exception {
        DatabaseController.dropDatabaseTable();
    }
    @Test
    public void createNewUser() throws Exception {
        assertEquals("The level is set to the specified level upon initialization",11, DatabaseController.getLevelUnlocked("testUser1"));
        assertEquals("The # of games played is set to 0 upon user creation",0, DatabaseController.getGamesPlayed("testUser1"));
        int originalHighScore = DatabaseController.getScore("testUser1");
        assertEquals("Highscore is set to the specified score upon user creation",originalHighScore,DatabaseController.getScore("testUser1"));
        assertTrue("The user exists in the database", DatabaseController.authenticateUser("testUser1", "testPassword1"));
    }
    @Test
    public void testCreateDirectoryForUserSavedFiles() throws Exception {
        DatabaseController.saveDirectory = "src/res/data/testSavedGames/";
        DatabaseController.createDirectoryForUserSavedFiles("testUser1");
        File dir = new File("src/res/data/testSavedGames/testUser1");
        assertTrue("User folder exists", dir.exists());
    }
    @Test
    public void testGetPassword() throws Exception {
        assertEquals("The user's password is equal to the one set during user creation", "testPassword1", DatabaseController.getPassword("testUser1"));
    }
    @Test
    public void testSetLevelUnlocked() throws Exception {
        DatabaseController.setLevelUnlocked("testUser2", 20);
        assertEquals("The level unlocked has been set to 20",20, DatabaseController.getLevelUnlocked("testUser2"));
        DatabaseController.setLevelUnlocked("userdoesnotexist", 34);
        assertEquals("The method terminates and no change are made",20, DatabaseController.getLevelUnlocked("testUser2"));
        DatabaseController.setLevelUnlocked("testUser2", -5);
        assertEquals("Method returns and no changes are made",20, DatabaseController.getLevelUnlocked("testUser2"));
        DatabaseController.setLevelUnlocked("testUser2", 65);
        assertEquals("Method returns and no changes are made", 20, DatabaseController.getLevelUnlocked("testUser2"));
    }
    @Test
    public void testGetLevelUnlocked() throws Exception {
        assertEquals("User's level unlocked is equal to the set level unlocked",11 , DatabaseController.getLevelUnlocked("testUser2"));
    }
    @Test
    public void testAuthenticateUser() throws Exception {
        assertTrue("User exists", DatabaseController.authenticateUser("testUser1", "testPassword1"));
        assertFalse("User does not exist", DatabaseController.authenticateUser("not exists", "not exists"));
    }
    @Test
    public void testUpdatePassword() throws Exception {
        DatabaseController.updatePassword("testNewPass2","testUser2");
        assertEquals("testNewPass2",DatabaseController.getPassword("testUser2"));
        DatabaseController.updatePassword("","testUser2");
        assertEquals("When no new password is entered, method terminates, no changes made", "testNewPass2", DatabaseController.getPassword("testUser2"));
        DatabaseController.updatePassword("testNewPass2","userdoesnotexist");
        assertEquals("When an invalid user is entered, method terminates, no changes made", "testNewPass2", DatabaseController.getPassword("testUser2"));
    }
    @Test
    public void testUpdateRealName() throws Exception {
        DatabaseController.updateRealName("testNewRealName","testUser2");
        assertEquals("testNewRealName",DatabaseController.getRealName("testUser2"));
        DatabaseController.updateRealName("testNewRealName", "userdoesnotexist");
        assertEquals("When an invalid user is entered, method terminates, no changes made", "testNewRealName", DatabaseController.getRealName("testUser2"));
        DatabaseController.updateRealName("testRealName2","testUser2");
    }
    @Test
    public void testDeleteAccount() throws Exception {
        DatabaseController.deleteAccount("username");
        assertFalse("User account no longer exists", DatabaseController.authenticateUser("username","password"));
    }
    @Test
    public void testSetScore() throws Exception {
        int originalScore = DatabaseController.getScore("testUser2");
        DatabaseController.setScore("testUser2", 2000);
        assertEquals("The score has been incremented by 2000",originalScore+2000, DatabaseController.getScore("testUser2"));
        int newScore = DatabaseController.getScore("testUser2");
        DatabaseController.setScore("userdoesnotexist", 3400);
        assertEquals("The method terminates and no changes are made",newScore, DatabaseController.getScore("testUser2"));
        DatabaseController.setScore("testUser2", -5000);
        assertEquals("Negative invalid, method returns, no changes made",newScore, DatabaseController.getScore("testUser2"));
    }
    @Test
    public void testGetTopScoresSet() throws Exception {
        ArrayList<PlayerScore> testTopScoresSet;
        testTopScoresSet = DatabaseController.getTopScoresSet();
        for (int i = 1; i <= 10; i++) {
            assertEquals("getTopScoresSet PlayerScore objects' user names' match expected username ","testUser" + i, testTopScoresSet.get(i - 1).username);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("getTopScoresSet returns the PlayerScore objects sorted by score descending",16000, testTopScoresSet.get(i).score);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("getTopScoresSet PlayerScore objects' games played match expected games played",0, testTopScoresSet.get(i).gamesPlayed);

        }
        for (int i = 1; i<=10; i++) {
            assertEquals("getTopScoresSet PlayerScore objects' real names' match expected real names","testRealName"+i, testTopScoresSet.get(i-1).realName);
        }
    }
    @Test
    public void testIncrementGamesPlayed() throws Exception {
        int gamesPlayed = DatabaseController.getGamesPlayed("testUser2");
        assertEquals("Number of games played is set to 0 upon user creation",gamesPlayed,DatabaseController.getGamesPlayed("testUser2"));
        DatabaseController.incrementGamesPlayed("testUser2");
        assertEquals("Games played is incremented by 1 when method is called", gamesPlayed+1 , DatabaseController.getGamesPlayed("testUser2"));
        DatabaseController.decrementGamesPlayed("testUser2");
    }
    @Test
    public void testDecrementGamesPlayed() throws Exception {
        int gamesPlayed = DatabaseController.getGamesPlayed("testUser2");
        assertEquals("Number of games played is set to 0 upon user creation",gamesPlayed,DatabaseController.getGamesPlayed("testUser2"));
        DatabaseController.decrementGamesPlayed("testUser2");
        assertEquals("Games played is decremented by 1 when method is called", gamesPlayed-
                1 , DatabaseController.getGamesPlayed("testUser2"));
        DatabaseController.incrementGamesPlayed("testUser2");
    }
    @Test
    public void testGetPlayerObject() throws Exception {
        PlayerScore testPlayer = PlayerScore.createPlayer("testUser6", DatabaseController.getScore("testUser6"), "testRealName6", 0);
        assertEquals("testPlayer username is the same as method returned object", testPlayer.username, DatabaseController.getPlayerObject("testUser6").username);
        assertEquals("testPlayer real name is the same as method returned object", testPlayer.realName, DatabaseController.getPlayerObject("testUser6").realName);
        assertEquals("testPlayer score is the same as method returned object", testPlayer.score, DatabaseController.getPlayerObject("testUser6").score);
        assertEquals("testPlayer games played is the same as method returned object", testPlayer.gamesPlayed, DatabaseController.getPlayerObject("testUser6").gamesPlayed);
    }
}