package Database;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerScoreTest {

    @BeforeClass
    public static void setUp() throws Exception {
        PlayerScore testPlayer = new PlayerScore();
        testPlayer.username = "testUsername";
        testPlayer.realName = "testRealName";
        testPlayer.gamesPlayed = 0;
        testPlayer.score = 0;
    }
    @Test
    public void testCreatePlayer() throws Exception {
        PlayerScore testCreatePlayer =PlayerScore.createPlayer("testUsername",0,"testRealName",0);
        assertEquals("testPlayer username is the same as method returned object", "testUsername", testCreatePlayer.username);
        assertEquals("testPlayer real name is the same as method returned object", "testRealName", testCreatePlayer.realName);
        assertEquals("testPlayer score is the same as method returned object", 0, testCreatePlayer.score);
        assertEquals("testPlayer games played is the same as method returned object",0, testCreatePlayer.gamesPlayed);
    }
}