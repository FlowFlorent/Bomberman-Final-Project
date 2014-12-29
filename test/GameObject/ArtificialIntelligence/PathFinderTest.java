package GameObject.ArtificialIntelligence;

import GameObject.GameObject;
import GameObject.MovableObject;
import GameObject.Player;
import GameObject.BrickWall;
import GameObject.TileMap;
import GamePlay.Coordinate;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PathFinderTest {

    private PathFinder pathFinder;

    @Before
    public void setUp() {
        pathFinder = new PathFinder();
    }

    @Test
    public void testUpdateGraphTimer() throws Exception {
        int currentTimeBeforeRefresh = pathFinder.getTimeToRefreshGraph();
        assertEquals("Upon creating a PathFinder instance, the timeToRefreshGraph should equal " +
                "45", currentTimeBeforeRefresh, pathFinder.getTimeToRefreshGraph());

        pathFinder.updateGraphRefreshTimer();
        assertEquals("Calling update updateGraphRefreshTimer should decrease the timeToRefreshGraph by 1 if " +
                "said variable is not equal to 0", currentTimeBeforeRefresh - 1, pathFinder.getTimeToRefreshGraph());

        pathFinder.setTimeToRefreshGraph(1);
        pathFinder.updateGraphRefreshTimer();
        assertTrue("When timeToRefreshGraph equals 0, the refreshGraph flag should be set to true ", pathFinder.getRefreshGraph());
        assertEquals("When timeToRefreshGraph equals 0, it should be reset to 45", 45, pathFinder.getTimeToRefreshGraph());

    }

    @Test
    public void testFindPath() throws Exception {
        Node[][] graph = pathFinder.graph;

        //test find path has a direct dependency on A*; we only test for the unexpected behaviour.
        ArrayList<Coordinate> pathBetweenPlayerAndEnemy = pathFinder.findPath(-1000, 160, -200, 96, false);
        assertNull("If the nodes requested do not exits, findPath should return null", pathBetweenPlayerAndEnemy);

        graph[0][0].setObstacle(false);
        graph[1][0].setObstacle(false);
        graph[2][0].setObstacle(false);

        pathBetweenPlayerAndEnemy = pathFinder.findPath(32, 96, 32, 32, false);
        ArrayList<Coordinate> expectedResult = new ArrayList<Coordinate>();
        expectedResult.add(new Coordinate(0, 0));
        expectedResult.add(new Coordinate(2, 0));
        boolean pathIsCorrect = determineIfPathMatches(pathBetweenPlayerAndEnemy, expectedResult, graph);

        assertTrue("If the nodes exist, findPath should return the path connecting the enemy and the player",
                pathIsCorrect);
    }

    @Test
    public void testAStar() throws Exception {
        Node[][] graph = pathFinder.graph;
        graph[0][0].setObstacle(false);
        graph[1][0].setObstacle(false);
        graph[2][0].setObstacle(false);

        ArrayList<Coordinate> resultFromAStar = pathFinder.aStar(graph[0][0], graph[0][0], false);
        assertTrue("A* should return one coordinate for a path between a node and itself", resultFromAStar.size() == 1);

        resultFromAStar = pathFinder.aStar(null, null, false);
        assertNull("A* should return null when the nodes passed are null", resultFromAStar);

        resultFromAStar = pathFinder.aStar(graph[0][0], graph[2][0], false);
        ArrayList<Coordinate> expectedResult = new ArrayList<Coordinate>();
        expectedResult.add(new Coordinate(0, 0));
        expectedResult.add(new Coordinate(2, 0));

        boolean isThePathCorrect = determineIfPathMatches(resultFromAStar, expectedResult, graph);

        assertTrue("The path from coordinate (0,0) to (0,2) should be exactly those two ends, " +
                "without the 'chain link' (0,1)", isThePathCorrect);

        // We expect the same result as above if the enemy is capable of walking through walls
        graph[1][0].setObstacle(true);
        resultFromAStar = pathFinder.aStar(graph[0][0], graph[2][0], true);

        isThePathCorrect = determineIfPathMatches(resultFromAStar, expectedResult, graph);

        assertTrue("The path from coordinate (0,0) to (0,2) with a wall at coordinate (0,1) should be exactly those two ends, " +
                "without the 'chain link' (0,1) if the enemy is able to walk through walls", isThePathCorrect);

        graph[1][0].setObstacle(true);
        graph[0][1].setObstacle(true);
        resultFromAStar = pathFinder.aStar(graph[0][0], graph[2][0], false);

        assertTrue("If A* can't find a path between the two selected nodes, it should return the starting node",
                resultFromAStar.size() == 1);

    }

    public boolean determineIfPathMatches(ArrayList<Coordinate> resultFromAStar, ArrayList<Coordinate> expectedResult,
                                       Node[][] graph) {

        boolean isThePathCorrect = true;
        for (int i = 0; i < resultFromAStar.size(); i++) {
            Coordinate resultCoordinate = resultFromAStar.get(i);
            Coordinate expectedCoordiante = expectedResult.get(i);

            boolean rowMatches = resultCoordinate.getRow() == expectedCoordiante.getRow();
            boolean colMatches = resultCoordinate.getCol() == expectedCoordiante.getCol();

            if (!rowMatches || !colMatches) isThePathCorrect = false;

        }

        return isThePathCorrect;
    }

    @Test
    public void testEstimateDistance() throws Exception {
        Node n1 = new Node(1, 1);
        Node n2 = new Node(2,2);
        Node n3 = new Node(5,1);

        int distance = pathFinder.estimateDistance(n1, n1);
        assertEquals("The between a node and itself should be 0", 0, distance);

        distance = pathFinder.estimateDistance(n1, n2);
        assertEquals("The distance from (1,1) to (2,2) should be 2", 2, distance);

        distance = pathFinder.estimateDistance(n1, n3);
        assertEquals("The distance from (1,1) to (5,1) should be 4", 4, distance);

    }

    @Test
    public void testUpdateGraph() throws Exception {
        Player player = new Player(32, 32, true, MovableObject.NORMALSPEED);
        TileMap tileMap = new TileMap(player, 1, "testuser");
        GameObject[][] walls = tileMap.getWalls();

        //We create brick walls at specific positions of the TileMap
        walls[1][1] = new BrickWall(32, 32, true, false);
        walls[1][2] = new BrickWall(32, 32, true, false);
        walls[1][3] = new BrickWall(32, 32, true, false);
        walls[2][1] = new BrickWall(32, 32, true, false);
        walls[3][1] = new BrickWall(32, 32, true, false);

        pathFinder.updateGraph(walls);
        Node[][] graph = pathFinder.graph;

        // The graph only represents the 29x11 inner tiles of the map, which is why the coordinates
        // used to target the walls shown above are different.
        boolean allGraphPositionsAreObstacles = graph[0][0].isObstacle() && graph[0][1].isObstacle()
                && graph[0][2].isObstacle() && graph[1][0].isObstacle() && graph[2][0].isObstacle();

        assertTrue("Updating the graph should set the nodes that contains brick walls as " +
                "obstacles", allGraphPositionsAreObstacles);

        walls[1][1] = null;
        walls[1][2] = null;
        walls[1][3] = null;
        walls[2][1] = null;
        walls[3][1] = null;

        pathFinder.updateGraph(walls);
        graph = pathFinder.graph;

        boolean previousObstaclesAreNowClear = !graph[0][0].isObstacle() && !graph[0][1].isObstacle()
                && !graph[0][2].isObstacle() && !graph[1][0].isObstacle() && !graph[2][0].isObstacle();

        assertTrue("Updating the graph should set the nodes that previously contain walls as not " +
                "obstacles", previousObstaclesAreNowClear);


    }

    @Test
    public void testCreateMap() throws Exception {
        assertNotNull("The graph returned should not be null", pathFinder.graph);
    }
}