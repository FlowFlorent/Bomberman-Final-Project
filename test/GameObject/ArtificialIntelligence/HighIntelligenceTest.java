package GameObject.ArtificialIntelligence;

import GameObject.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HighIntelligenceTest {

    private HighIntelligence highIntelligence;

    @Before
    public void setup(){
        highIntelligence = new HighIntelligence();
    }

    @Test
    public void testRandomTurnOnIntersection() throws Exception {
        assertTrue("Since all conditions are met, the return boolean should be true",
                highIntelligence.randomTurnOnIntersection(35, 35, true));

        assertFalse("Since probability condition boolean is false, the return boolean should be false",
                highIntelligence.randomTurnOnIntersection(35, 35, false));

        assertFalse("Since enemyAtXIntersection boolean is false, the return boolean should be false",
                highIntelligence.randomTurnOnIntersection(36, 35, true));

        assertFalse("Since enemyAtYIntersection boolean is false, the return boolean should be false",
                highIntelligence.randomTurnOnIntersection(35, 36, true));
    }
}