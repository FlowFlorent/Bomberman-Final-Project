package GamePlay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import GameObject.Enemy;
import GameObject.EnemyType;
import GameObject.KillSet;


import static org.junit.Assert.*;

public class ScoreManagerTest {

    private ArrayList<KillSet> kills = new ArrayList<KillSet>();
    private ScoreManager scoreManager = new ScoreManager();

    @Before
    public void setup() throws Exception {
        kills.add(new KillSet(new Coordinate(0, 0), new Coordinate(0, 0), new Enemy(EnemyType.BALLOOM, 35, 35)));
        kills.add(new KillSet(new Coordinate(0, 0), new Coordinate(0, 0), new Enemy(EnemyType.BALLOOM, 35, 35)));
    }

    @Test
    public void testDetermineScoreFromKills() throws Exception {
        //Note that determineScoreFromKills is only called when at least one enemy has been killed.

        //Enemies killed by bomb at (64,64)
        // Oneal has score value of 200 - closest to bomb
        KillSet killA = new KillSet(new Coordinate(32, 32), new Coordinate(64, 64), new Enemy(EnemyType.ONEAL, 32, 32));
        // Kondoria has score value of 1000 - second closest to bomb
        KillSet killB = new KillSet(new Coordinate(0, 0), new Coordinate(64, 64), new Enemy(EnemyType.KONDORIA, 32, 32));
        // Balloom has score value of 100 - furthest from bomb
        KillSet killC = new KillSet(new Coordinate(300, 300), new Coordinate(64, 64), new Enemy(EnemyType.BALLOOM, 32, 32));

        //Enemies killed by bomb at (128,128)
        // Balloom has score value of 100 - furthest from bomb
        KillSet killD = new KillSet(new Coordinate(500, 500), new Coordinate(128, 128), new Enemy(EnemyType.BALLOOM, 32, 32));
        // Balloom has score value of 100 - closest to bomb
        KillSet killE = new KillSet(new Coordinate(300, 300), new Coordinate(128, 128), new Enemy(EnemyType.BALLOOM, 32, 32));
        // Balloom has score value of 100 - second furthest from bomb
        KillSet killF = new KillSet(new Coordinate(400, 400), new Coordinate(128, 128), new Enemy(EnemyType.BALLOOM, 32, 32));


        ArrayList<KillSet> kills = new ArrayList<KillSet>();
        kills.add(killA);
        kills.add(killB);
        kills.add(killC);
        kills.add(killD);
        kills.add(killE);
        kills.add(killF);

        int result = scoreManager.determineScoreFromKills(kills);
        assertEquals("DetermineScoreFromKills should handle the case where multiple bombs kill enemies", result, 3300);

        kills = new ArrayList<KillSet>();
        kills.add(killA); // Score 200
        kills.add(killB); // Score 1000
        kills.add(killC); // Score 100

        //200 + 2000 + 400
        result = scoreManager.calculateScore(kills);
        assertEquals("DetermineScoreFromKills should handle the case where only one bomb kills multiple enemies", result, 2600);

        kills = new ArrayList<KillSet>();
        kills.add(killA); // Oneal has score 200
        result = scoreManager.calculateScore(kills);
        assertEquals("DetermineScoreFromKills should only one enemy was killed", result, 200);

        boolean exceptionThrown = false;
        try {
            scoreManager.determineScoreFromKills(null);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue("Passing a null value to determineScoreFromKills results in an exception being thrown", exceptionThrown);

    }

    @Test
    public void testCalculateScore() throws Exception {

        // Oneal has score value of 200 - closest to bomb
        KillSet killA = new KillSet(new Coordinate(32, 32), new Coordinate(64, 64), new Enemy(EnemyType.ONEAL, 32, 32));
        // Kondoria has score value of 1000 - second closest to bomb
        KillSet killB = new KillSet(new Coordinate(0, 0), new Coordinate(64, 64), new Enemy(EnemyType.KONDORIA, 32, 32));
        // Balloom has score value of 100 - furthest from bomb
        KillSet killC = new KillSet(new Coordinate(300, 300), new Coordinate(64, 64), new Enemy(EnemyType.BALLOOM, 32, 32));

        ArrayList<KillSet> kills = new ArrayList<KillSet>();
        kills.add(killA);
        kills.add(killB);
        kills.add(killC);

        //200 + 2000 + 400
        int result = scoreManager.calculateScore(kills);
        assertEquals("For 3 or more enemies calculate score will sort the kills by distance to the bomb and " +
                "then double the double for each successive kill", result, 2600);

        //1000 + 200
        kills.remove(0);
        result = scoreManager.calculateScore(kills);
        assertEquals("For just 2 enemies calculate score will sort the kills by distance to the bomb and " +
                "then double the lowest value kill", result, 1200);

        boolean exceptionThrown = false;
        try {
            scoreManager.determineScoreForTwoKills(killA, null);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue("Passing a null value to calculateScore results in an exception being thrown", exceptionThrown);
    }

    @Test
    public void testDetermineScoreForManyKills() throws Exception {

        // Oneal has score value of 200 - closest to bomb
        KillSet killA = new KillSet(new Coordinate(32, 32), new Coordinate(64, 64), new Enemy(EnemyType.ONEAL, 32, 32));
        // Kondoria has score value of 1000 - second closest to bomb
        KillSet killB = new KillSet(new Coordinate(0, 0), new Coordinate(64, 64), new Enemy(EnemyType.KONDORIA, 32, 32));
        // Balloom has score value of 100 - furthest from bomb
        KillSet killC = new KillSet(new Coordinate(300, 300), new Coordinate(64, 64), new Enemy(EnemyType.BALLOOM, 32, 32));

        ArrayList<KillSet> kills = new ArrayList<KillSet>();
        kills.add(killA);
        kills.add(killB);
        kills.add(killC);

        //200 + 2000 + 400
        int result = scoreManager.calculateScore(kills);
        assertEquals("DetermineScoreForManyKills will double the double for each successive kill", result, 2600);

        kills = new ArrayList<KillSet>();
        result = scoreManager.calculateScore(kills);
        assertEquals("When 0 kills are passed, determineScoreForManyKills returns 0", result, 0);

        boolean exceptionThrown = false;
        try {
            scoreManager.determineScoreForManyKills(null);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue("Passing a null value to determineScoreForManyKills results in an exception being thrown", exceptionThrown);

    }

    @Test
    public void testDetermineScoreForTwoKills() throws Exception {

        // Oneal has score value of 200
        KillSet killA = new KillSet(new Coordinate(32, 32), new Coordinate(64, 64), new Enemy(EnemyType.ONEAL, 32, 32));
        // Balloom has score value of 1000
        KillSet killB = new KillSet(new Coordinate(32, 32), new Coordinate(64, 64), new Enemy(EnemyType.KONDORIA, 32, 32));

        int result = scoreManager.determineScoreForTwoKills(killA, killB);
        assertEquals("When two different enemy types are killed, the score of the kill with the lower " +
                "value get doubled", 1400, result);

        result = scoreManager.determineScoreForTwoKills(killA, killA);
        assertEquals("When two enemies of the same type are killed, the score of the second enemy passed" +
                "is doubled (it is arbitrary which one gets doubled)", 600, result);

        boolean exceptionThrown = false;
        try {
            scoreManager.determineScoreForTwoKills(killA, null);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue("Passing a null value to DetermineScoreForTwoKills results in an exception being thrown", exceptionThrown);
    }
}