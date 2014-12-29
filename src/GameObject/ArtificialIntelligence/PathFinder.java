/**
 * Created by danielmacario on 14-11-22.
 */
package GameObject.ArtificialIntelligence;

import GameObject.GameObject;
import GameObject.BrickWall;
import GameObject.TileMap;
import GamePlay.Coordinate;

import java.io.Serializable;
import java.util.*;

/**
 * System used by the high intelligence enemies to calculate the shortest path between
 * their location and the location of the player. It also holds the graph representation of
 * the grid and the logic necessary to update the graph once obstacles like brick walls
 * are destroyed.
 */
public class PathFinder implements Serializable {

    private boolean refreshGraph = false;
    // This time variable represents frames to be rendered. Recall that 30 frames is equivalent
    // to 1 second in our implementation of the game. 45 frames represents 1.5 seconds.
    private int timeToRefreshGraph = 45;
    public Node[][] graph = createMap();

    /**
     * Timer used to refresh the graph representation of the grid every
     * 1.5 seconds.
     */
    public void updateGraphRefreshTimer() {
        timeToRefreshGraph--;
        if (timeToRefreshGraph == 0) {
            refreshGraph = true;
            timeToRefreshGraph = 45;
        }
    }

    /**
     * Finds the smallest set of tiles that need to traversed in order to navigate the enemy from its location
     * tod the location of the player.
     * @param playerPosX The x coordinate where the player is located.
     * @param playerPosY The y coordinate where the player is located.
     * @param enemyPosX The x coordinate where the enemy is located.
     * @param enemyPosY The y coordinate where the enemy is located.
     * @param enemyHasWallPass Boolean specifying whether the enemy can go walk through brick walls.
     * @return An ArrayList containing the coordinates representing the chain of movements to go from
     * the location of the enemy to the location of the player.
     */
    public ArrayList<Coordinate> findPath(int playerPosX, int playerPosY, int enemyPosX, int enemyPosY, boolean enemyHasWallPass) {

        int playerRow = ((playerPosY - playerPosY % TileMap.TILE_SIDE_LENGTH) / TileMap.TILE_SIDE_LENGTH) - 1;
        int playerCol = ((playerPosX - playerPosX % TileMap.TILE_SIDE_LENGTH) / TileMap.TILE_SIDE_LENGTH) - 1;
        int enemyRow = ((enemyPosY - enemyPosY % TileMap.TILE_SIDE_LENGTH) / TileMap.TILE_SIDE_LENGTH) - 1;
        int enemyCol = ((enemyPosX - enemyPosX % TileMap.TILE_SIDE_LENGTH) / TileMap.TILE_SIDE_LENGTH) - 1;

        Node start = null;
        Node destination = null;
        boolean nodesExist = true;
        try {
            //retrieve the starting node and the ending node from the graph representation of
            //the grid.
            start = graph[enemyRow][enemyCol];
            destination = graph[playerRow][playerCol];
        } catch (Exception e) {
            e.printStackTrace();
            nodesExist = false;
            System.out.println("Nodes for path finding do not exist");
        }

        ArrayList<Coordinate> pathToPlayer = null;
        if (nodesExist) {
            //Find the shortest path from the starting node to the ending node.
            pathToPlayer = aStar(start, destination, enemyHasWallPass);
        }
        return pathToPlayer;
    }

    /**
     * The A* algorithm calculates the shortest path between two specified nodes in the graph,
     * it is similar to a Dijkstra's algorithm but with certain heuristics to optimize it in
     * a number of ways.
     * @param start The starting node in the graph.
     * @param goal The ending destination node in the graph.
     * @param enemyHasWallPass Boolean specifying whether the enemy can go walk through brick walls.
     * @return An ArrayList containing the set of coordinates that we need to traverse in order to go
     * from the starting node to the destination node.
     */
    public ArrayList<Coordinate> aStar(Node start, Node goal, boolean enemyHasWallPass) {

        if (start == null || goal == null) return null;
        Set<Node> open = new HashSet<Node>();
        Set<Node> closed = new HashSet<Node>();

        start.setCostToReachNodeFromStart(0);
        start.setEstimatedCostToReachDestination(estimateDistance(start, goal));
        start.setOverallCost(start.getEstimatedCostToReachDestination());

        open.add(start);
        while (true) {
            Node current = null;

            // In certain scenarios the enemy won't be able to find a path to the player
            // because they are cutoff by the presence of brick walls.
            if (open.size() == 0) {
                System.out.println("Could not find a path connecting the player and the enemy");
                break;
            }

            for (Node node : open) {
                if (current == null || node.getOverallCost() < current.getOverallCost()) {
                    current = node;
                }
            }

            // If the current node equals the destination, we have found a path
            // connecting the two nodes.
            if (current == goal) {
                break;
            }

            open.remove(current);
            closed.add(current);

            for (Node neighbor : current.getNeighbors()) {
                if (neighbor == null || (neighbor.isObstacle() && !enemyHasWallPass)) {
                    continue;
                }

                int nextG = current.getCostToReachNodeFromStart() + neighbor.getCost();

                if (nextG < neighbor.getCostToReachNodeFromStart()) {
                    open.remove(neighbor);
                    closed.remove(neighbor);
                }

                if (!open.contains(neighbor) && !closed.contains(neighbor)) {
                    neighbor.setCostToReachNodeFromStart(nextG);
                    neighbor.setEstimatedCostToReachDestination(estimateDistance(neighbor, goal));
                    neighbor.setOverallCost(neighbor.getCostToReachNodeFromStart() + neighbor.getEstimatedCostToReachDestination());
                    neighbor.setParent(current);
                    open.add(neighbor);
                }
            }
        }

        List<Node> nodes = new ArrayList<Node>();
        Node current = goal;
        // We follow the parent references to construct the path.
        while (current.getParent() != null) {
            nodes.add(current);
            current = current.getParent();
        }
        nodes.add(start);

        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        // We invert the path to go from the starting node to the destination.
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node selectedNode = nodes.get(i);
            coordinates.add(new Coordinate(selectedNode.getX(), selectedNode.getY()));
        }

        removeChains(coordinates);

        for (Node[] rowOfNodes : graph) {
            for (Node node : rowOfNodes) {
                if (node != null) {
                    node.setParent(null);
                }
            }
        }

        return coordinates;
    }

    /**
     * Removes contiguous links in the path from the enemy to the player. For example,
     * if the path traverses the coordinates (0,1) -> (0,2) -> (0,3), this function will eliminate
     * the middle link which is not needed for the traversal algorithm. So, the chain mentioned above
     * becomes (0,1) -> (0,3).
     * @param coordinates
     */
    private void removeChains(ArrayList<Coordinate> coordinates) {

        for (int i = 0; i < coordinates.size() - 2; i ++) {
            if ((coordinates.get(i).getRow() == coordinates.get(i + 1).getRow() && coordinates.get(i).getRow() == coordinates.get(i + 2).getRow())
                    || (coordinates.get(i).getCol() == coordinates.get(i + 1).getCol() && coordinates.get(i).getCol() == coordinates.get(i + 2).getCol())) {
                coordinates.remove(i+1);
                i--;
            }
        }

    }

    /**
     * Estimates the distance between two nodes in manhattan distance,
     * which is more efficient than computing the actual distance using trigonometry.
     * @param node1 First endpoint of edge distance to calculate.
     * @param node2 Second endpoint of the edge distance to calculate.
     * @return An integer representing the distance between the two passed nodes.
     */
    public int estimateDistance(Node node1, Node node2) {
        return Math.abs(node1.getX() - node2.getX()) + Math.abs(node1.getY() - node2.getY());
    }

    /**
     * Updates the graph representation of the grid with new information regarding the walls present.
     * @param walls A two dimensional array containing the wall objects (both concrete and brick) that
     *              are present on the grid at a given time.
     */
    public void updateGraph(GameObject[][] walls) {
        // Walls is 31x13 tiles. We only want to iterate over the
        // inner square: 29x11 tiles. We keep track of two separate
        // indices for the object array and the graph array.
        int graphRow = 0;
        int graphCol = 0;
        for (int col = 1; col < TileMap.NUM_OF_COLS - 1; col++) {
            for (int row = 1; row < TileMap.NUM_OF_ROWS - 1; row++) {

                // update which of the graph nodes are no longer obstacles,
                // which results from the destruction of brick walls.
                GameObject wall = walls[col][row];
                if (wall instanceof BrickWall && wall.isVisible()) {
                    graph[graphRow][graphCol].setObstacle(true);
                } else if (wall == null) {
                    graph[graphRow][graphCol].setObstacle(false);
                }
                graphRow++;

            }
            graphRow = 0;
            graphCol++;
        }
    }

    /**
     * Determine whether the graph representation of the grid should be refreshed.
     * @return A boolean specifying whether the graph representation of the grid should be refreshed.
     */
    public boolean getRefreshGraph() {
        return refreshGraph;
    }

    /**
     * Specify whether the graph representation of the grid should be refreshed.
     * @param refreshGraph A boolean specifying whether the graph representation of the grid should be refreshed.
     */
    public void setRefreshGraph(boolean refreshGraph) {
        this.refreshGraph = refreshGraph;
    }

    /**
     * Get the time remaining before the refresh flag is set to true.
     * @return An integer representing the time remaining before the graph is refreshed.
     */
    public int getTimeToRefreshGraph() {
        return timeToRefreshGraph;
    }

    /**
     * Specify the time remaining before thr refresh flag is set top true.
     * @param timeToRefreshGraph An integer representing the time remaining before the graph is refreshed.
     */
    public void setTimeToRefreshGraph(int timeToRefreshGraph) {
        this.timeToRefreshGraph = timeToRefreshGraph;
    }

    /**
     * Creates a graph representation of the grid in term of node objects. Note that
     * we do not represent the outer concrete walls that surround the map, only the 29x11
     * inner tiles.
     * @return
     */
    public Node[][] createMap() {

        /*
        Important information regarding this architecture:
            - We only keep one instance of this graph during gamepaly; all the enemies
              with high intelligence share this instance.
            - We don't encode the nodes that are concrete walls; by ignoring them
              them from the graph they are never considered in path finding.
            - The 500 lines of code below were generated using a bash script for the nodes,
              and a python script for the array. They were not written by hand!
         */

        // We don't encode the nodes that are concrete walls in the inner map.
        // By removing from the graph we save memory and slightly optimize the algorithm
        Node r0c0 = new Node(0, 0);
        Node r0c1 = new Node(0, 1);
        Node r0c2 = new Node(0, 2);
        Node r0c3 = new Node(0, 3);
        Node r0c4 = new Node(0, 4);
        Node r0c5 = new Node(0, 5);
        Node r0c6 = new Node(0, 6);
        Node r0c7 = new Node(0, 7);
        Node r0c8 = new Node(0, 8);
        Node r0c9 = new Node(0, 9);
        Node r0c10 = new Node(0, 10);
        Node r0c11 = new Node(0, 11);
        Node r0c12 = new Node(0, 12);
        Node r0c13 = new Node(0, 13);
        Node r0c14 = new Node(0, 14);
        Node r0c15 = new Node(0, 15);
        Node r0c16 = new Node(0, 16);
        Node r0c17 = new Node(0, 17);
        Node r0c18 = new Node(0, 18);
        Node r0c19 = new Node(0, 19);
        Node r0c20 = new Node(0, 20);
        Node r0c21 = new Node(0, 21);
        Node r0c22 = new Node(0, 22);
        Node r0c23 = new Node(0, 23);
        Node r0c24 = new Node(0, 24);
        Node r0c25 = new Node(0, 25);
        Node r0c26 = new Node(0, 26);
        Node r0c27 = new Node(0, 27);
        Node r0c28 = new Node(0, 28);
        Node r1c0 = new Node(1, 0);
        Node r1c2 = new Node(1, 2);
        Node r1c4 = new Node(1, 4);
        Node r1c6 = new Node(1, 6);
        Node r1c8 = new Node(1, 8);
        Node r1c10 = new Node(1, 10);
        Node r1c12 = new Node(1, 12);
        Node r1c14 = new Node(1, 14);
        Node r1c16 = new Node(1, 16);
        Node r1c18 = new Node(1, 18);
        Node r1c20 = new Node(1, 20);
        Node r1c22 = new Node(1, 22);
        Node r1c24 = new Node(1, 24);
        Node r1c26 = new Node(1, 26);
        Node r1c28 = new Node(1, 28);
        Node r2c0 = new Node(2, 0);
        Node r2c1 = new Node(2, 1);
        Node r2c2 = new Node(2, 2);
        Node r2c3 = new Node(2, 3);
        Node r2c4 = new Node(2, 4);
        Node r2c5 = new Node(2, 5);
        Node r2c6 = new Node(2, 6);
        Node r2c7 = new Node(2, 7);
        Node r2c8 = new Node(2, 8);
        Node r2c9 = new Node(2, 9);
        Node r2c10 = new Node(2, 10);
        Node r2c11 = new Node(2, 11);
        Node r2c12 = new Node(2, 12);
        Node r2c13 = new Node(2, 13);
        Node r2c14 = new Node(2, 14);
        Node r2c15 = new Node(2, 15);
        Node r2c16 = new Node(2, 16);
        Node r2c17 = new Node(2, 17);
        Node r2c18 = new Node(2, 18);
        Node r2c19 = new Node(2, 19);
        Node r2c20 = new Node(2, 20);
        Node r2c21 = new Node(2, 21);
        Node r2c22 = new Node(2, 22);
        Node r2c23 = new Node(2, 23);
        Node r2c24 = new Node(2, 24);
        Node r2c25 = new Node(2, 25);
        Node r2c26 = new Node(2, 26);
        Node r2c27 = new Node(2, 27);
        Node r2c28 = new Node(2, 28);
        Node r3c0 = new Node(3, 0);
        Node r3c2 = new Node(3, 2);
        Node r3c4 = new Node(3, 4);
        Node r3c6 = new Node(3, 6);
        Node r3c8 = new Node(3, 8);
        Node r3c10 = new Node(3, 10);
        Node r3c12 = new Node(3, 12);
        Node r3c14 = new Node(3, 14);
        Node r3c16 = new Node(3, 16);
        Node r3c18 = new Node(3, 18);
        Node r3c20 = new Node(3, 20);
        Node r3c22 = new Node(3, 22);
        Node r3c24 = new Node(3, 24);
        Node r3c26 = new Node(3, 26);
        Node r3c28 = new Node(3, 28);
        Node r4c0 = new Node(4, 0);
        Node r4c1 = new Node(4, 1);
        Node r4c2 = new Node(4, 2);
        Node r4c3 = new Node(4, 3);
        Node r4c4 = new Node(4, 4);
        Node r4c5 = new Node(4, 5);
        Node r4c6 = new Node(4, 6);
        Node r4c7 = new Node(4, 7);
        Node r4c8 = new Node(4, 8);
        Node r4c9 = new Node(4, 9);
        Node r4c10 = new Node(4, 10);
        Node r4c11 = new Node(4, 11);
        Node r4c12 = new Node(4, 12);
        Node r4c13 = new Node(4, 13);
        Node r4c14 = new Node(4, 14);
        Node r4c15 = new Node(4, 15);
        Node r4c16 = new Node(4, 16);
        Node r4c17 = new Node(4, 17);
        Node r4c18 = new Node(4, 18);
        Node r4c19 = new Node(4, 19);
        Node r4c20 = new Node(4, 20);
        Node r4c21 = new Node(4, 21);
        Node r4c22 = new Node(4, 22);
        Node r4c23 = new Node(4, 23);
        Node r4c24 = new Node(4, 24);
        Node r4c25 = new Node(4, 25);
        Node r4c26 = new Node(4, 26);
        Node r4c27 = new Node(4, 27);
        Node r4c28 = new Node(4, 28);
        Node r5c0 = new Node(5, 0);
        Node r5c2 = new Node(5, 2);
        Node r5c4 = new Node(5, 4);
        Node r5c6 = new Node(5, 6);
        Node r5c8 = new Node(5, 8);
        Node r5c10 = new Node(5, 10);
        Node r5c12 = new Node(5, 12);
        Node r5c14 = new Node(5, 14);
        Node r5c16 = new Node(5, 16);
        Node r5c18 = new Node(5, 18);
        Node r5c20 = new Node(5, 20);
        Node r5c22 = new Node(5, 22);
        Node r5c24 = new Node(5, 24);
        Node r5c26 = new Node(5, 26);
        Node r5c28 = new Node(5, 28);
        Node r6c0 = new Node(6, 0);
        Node r6c1 = new Node(6, 1);
        Node r6c2 = new Node(6, 2);
        Node r6c3 = new Node(6, 3);
        Node r6c4 = new Node(6, 4);
        Node r6c5 = new Node(6, 5);
        Node r6c6 = new Node(6, 6);
        Node r6c7 = new Node(6, 7);
        Node r6c8 = new Node(6, 8);
        Node r6c9 = new Node(6, 9);
        Node r6c10 = new Node(6, 10);
        Node r6c11 = new Node(6, 11);
        Node r6c12 = new Node(6, 12);
        Node r6c13 = new Node(6, 13);
        Node r6c14 = new Node(6, 14);
        Node r6c15 = new Node(6, 15);
        Node r6c16 = new Node(6, 16);
        Node r6c17 = new Node(6, 17);
        Node r6c18 = new Node(6, 18);
        Node r6c19 = new Node(6, 19);
        Node r6c20 = new Node(6, 20);
        Node r6c21 = new Node(6, 21);
        Node r6c22 = new Node(6, 22);
        Node r6c23 = new Node(6, 23);
        Node r6c24 = new Node(6, 24);
        Node r6c25 = new Node(6, 25);
        Node r6c26 = new Node(6, 26);
        Node r6c27 = new Node(6, 27);
        Node r6c28 = new Node(6, 28);
        Node r7c0 = new Node(7, 0);
        Node r7c2 = new Node(7, 2);
        Node r7c4 = new Node(7, 4);
        Node r7c6 = new Node(7, 6);
        Node r7c8 = new Node(7, 8);
        Node r7c10 = new Node(7, 10);
        Node r7c12 = new Node(7, 12);
        Node r7c14 = new Node(7, 14);
        Node r7c16 = new Node(7, 16);
        Node r7c18 = new Node(7, 18);
        Node r7c20 = new Node(7, 20);
        Node r7c22 = new Node(7, 22);
        Node r7c24 = new Node(7, 24);
        Node r7c26 = new Node(7, 26);
        Node r7c28 = new Node(7, 28);
        Node r8c0 = new Node(8, 0);
        Node r8c1 = new Node(8, 1);
        Node r8c2 = new Node(8, 2);
        Node r8c3 = new Node(8, 3);
        Node r8c4 = new Node(8, 4);
        Node r8c5 = new Node(8, 5);
        Node r8c6 = new Node(8, 6);
        Node r8c7 = new Node(8, 7);
        Node r8c8 = new Node(8, 8);
        Node r8c9 = new Node(8, 9);
        Node r8c10 = new Node(8, 10);
        Node r8c11 = new Node(8, 11);
        Node r8c12 = new Node(8, 12);
        Node r8c13 = new Node(8, 13);
        Node r8c14 = new Node(8, 14);
        Node r8c15 = new Node(8, 15);
        Node r8c16 = new Node(8, 16);
        Node r8c17 = new Node(8, 17);
        Node r8c18 = new Node(8, 18);
        Node r8c19 = new Node(8, 19);
        Node r8c20 = new Node(8, 20);
        Node r8c21 = new Node(8, 21);
        Node r8c22 = new Node(8, 22);
        Node r8c23 = new Node(8, 23);
        Node r8c24 = new Node(8, 24);
        Node r8c25 = new Node(8, 25);
        Node r8c26 = new Node(8, 26);
        Node r8c27 = new Node(8, 27);
        Node r8c28 = new Node(8, 28);
        Node r9c0 = new Node(9, 0);
        Node r9c2 = new Node(9, 2);
        Node r9c4 = new Node(9, 4);
        Node r9c6 = new Node(9, 6);
        Node r9c8 = new Node(9, 8);
        Node r9c10 = new Node(9, 10);
        Node r9c12 = new Node(9, 12);
        Node r9c14 = new Node(9, 14);
        Node r9c16 = new Node(9, 16);
        Node r9c18 = new Node(9, 18);
        Node r9c20 = new Node(9, 20);
        Node r9c22 = new Node(9, 22);
        Node r9c24 = new Node(9, 24);
        Node r9c26 = new Node(9, 26);
        Node r9c28 = new Node(9, 28);
        Node r10c0 = new Node(10, 0);
        Node r10c1 = new Node(10, 1);
        Node r10c2 = new Node(10, 2);
        Node r10c3 = new Node(10, 3);
        Node r10c4 = new Node(10, 4);
        Node r10c5 = new Node(10, 5);
        Node r10c6 = new Node(10, 6);
        Node r10c7 = new Node(10, 7);
        Node r10c8 = new Node(10, 8);
        Node r10c9 = new Node(10, 9);
        Node r10c10 = new Node(10, 10);
        Node r10c11 = new Node(10, 11);
        Node r10c12 = new Node(10, 12);
        Node r10c13 = new Node(10, 13);
        Node r10c14 = new Node(10, 14);
        Node r10c15 = new Node(10, 15);
        Node r10c16 = new Node(10, 16);
        Node r10c17 = new Node(10, 17);
        Node r10c18 = new Node(10, 18);
        Node r10c19 = new Node(10, 19);
        Node r10c20 = new Node(10, 20);
        Node r10c21 = new Node(10, 21);
        Node r10c22 = new Node(10, 22);
        Node r10c23 = new Node(10, 23);
        Node r10c24 = new Node(10, 24);
        Node r10c25 = new Node(10, 25);
        Node r10c26 = new Node(10, 26);
        Node r10c27 = new Node(10, 27);
        Node r10c28 = new Node(10, 28);

        //Add corresponding neighbors
        //We ignore the nodes that represent concrete walls
        //Doing so reduces reduces the size of the graph by 70 nodes
        r0c0.addNeighbors(new Node[] {r1c0, r0c1});
        r0c1.addNeighbors(new Node[] {r0c0, r0c2});
        r0c2.addNeighbors(new Node[] {r0c1, r1c2, r0c3});
        r0c3.addNeighbors(new Node[] {r0c2, r0c4});
        r0c4.addNeighbors(new Node[] {r0c3, r1c4, r0c5});
        r0c5.addNeighbors(new Node[] {r0c4, r0c6});
        r0c6.addNeighbors(new Node[] {r0c5, r1c6, r0c7});
        r0c7.addNeighbors(new Node[] {r0c6, r0c8});
        r0c8.addNeighbors(new Node[] {r0c7, r1c8, r0c9});
        r0c9.addNeighbors(new Node[] {r0c8, r0c10});
        r0c10.addNeighbors(new Node[] {r0c9, r1c10, r0c11});
        r0c11.addNeighbors(new Node[] {r0c10, r0c12});
        r0c12.addNeighbors(new Node[] {r0c11, r1c12, r0c13});
        r0c13.addNeighbors(new Node[] {r0c12, r0c14});
        r0c14.addNeighbors(new Node[] {r0c13, r1c14, r0c15});
        r0c15.addNeighbors(new Node[] {r0c14, r0c16});
        r0c16.addNeighbors(new Node[] {r0c15, r1c16, r0c17});
        r0c17.addNeighbors(new Node[] {r0c16, r0c18});
        r0c18.addNeighbors(new Node[] {r0c17, r1c18, r0c19});
        r0c19.addNeighbors(new Node[] {r0c18, r0c20});
        r0c20.addNeighbors(new Node[] {r0c19, r1c20, r0c21});
        r0c21.addNeighbors(new Node[] {r0c20, r0c22});
        r0c22.addNeighbors(new Node[] {r0c21, r1c22, r0c23});
        r0c23.addNeighbors(new Node[] {r0c22, r0c24});
        r0c24.addNeighbors(new Node[] {r0c23, r1c24, r0c25});
        r0c25.addNeighbors(new Node[] {r0c24, r0c26});
        r0c26.addNeighbors(new Node[] {r0c25, r1c26, r0c27});
        r0c27.addNeighbors(new Node[] {r0c26, r0c28});
        r0c28.addNeighbors(new Node[] {r0c27, r1c28});
        r1c0.addNeighbors(new Node[] {r0c0, r2c0});
        //r1c1
        r1c2.addNeighbors(new Node[] {r0c2, r2c2});
        //r1c3
        r1c4.addNeighbors(new Node[] {r0c4, r2c4});
        //r1c5
        r1c6.addNeighbors(new Node[] {r0c6, r2c6});
        //r1c7
        r1c8.addNeighbors(new Node[] {r0c8, r2c8});
        //r1c9
        r1c10.addNeighbors(new Node[] {r0c10, r2c10});
        //r1c11
        r1c12.addNeighbors(new Node[] {r0c12, r2c12});
        //r1c13
        r1c14.addNeighbors(new Node[] {r0c14, r2c14});
        //r1c15
        r1c16.addNeighbors(new Node[] {r0c16, r2c16});
        //r1c17
        r1c18.addNeighbors(new Node[] {r0c18, r2c18});
        //r1c19
        r1c20.addNeighbors(new Node[] {r0c20, r2c20});
        //r1c21
        r1c22.addNeighbors(new Node[] {r0c22, r2c22});
        //r1c23
        r1c24.addNeighbors(new Node[] {r0c24, r2c24});
        //r1c25
        r1c26.addNeighbors(new Node[] {r0c26, r2c26});
        //r1c27
        r1c28.addNeighbors(new Node[] {r0c28, r2c28});
        r2c0.addNeighbors(new Node[] {r1c0, r3c0, r2c1});
        r2c1.addNeighbors(new Node[] {r2c0, r2c2});
        r2c2.addNeighbors(new Node[] {r2c1, r1c2, r3c2, r2c3});
        r2c3.addNeighbors(new Node[] {r2c2, r2c4});
        r2c4.addNeighbors(new Node[] {r2c3, r1c4, r3c4, r2c5});
        r2c5.addNeighbors(new Node[] {r2c4, r2c6});
        r2c6.addNeighbors(new Node[] {r2c5, r1c6, r3c6, r2c7});
        r2c7.addNeighbors(new Node[] {r2c6, r2c8});
        r2c8.addNeighbors(new Node[] {r2c7, r1c8, r3c8, r2c9});
        r2c9.addNeighbors(new Node[] {r2c8, r2c10});
        r2c10.addNeighbors(new Node[] {r2c9, r1c10, r3c10, r2c11});
        r2c11.addNeighbors(new Node[] {r2c10, r2c12});
        r2c12.addNeighbors(new Node[] {r2c11, r1c12, r3c12, r2c13});
        r2c13.addNeighbors(new Node[] {r2c12, r2c14});
        r2c14.addNeighbors(new Node[] {r2c13, r1c14, r3c14, r2c15});
        r2c15.addNeighbors(new Node[] {r2c14, r2c16});
        r2c16.addNeighbors(new Node[] {r2c15, r1c16, r3c16, r2c17});
        r2c17.addNeighbors(new Node[] {r2c16, r2c18});
        r2c18.addNeighbors(new Node[] {r2c17, r1c18, r3c18, r2c19});
        r2c19.addNeighbors(new Node[] {r2c18, r2c20});
        r2c20.addNeighbors(new Node[] {r2c19, r1c20, r3c20, r2c21});
        r2c21.addNeighbors(new Node[] {r2c20, r2c22});
        r2c22.addNeighbors(new Node[] {r2c21, r1c22, r3c22, r2c23});
        r2c23.addNeighbors(new Node[] {r2c22, r2c24});
        r2c24.addNeighbors(new Node[] {r2c23, r1c24, r3c24, r2c25});
        r2c25.addNeighbors(new Node[] {r2c24, r2c26});
        r2c26.addNeighbors(new Node[] {r2c25, r1c26, r3c26, r2c27});
        r2c27.addNeighbors(new Node[] {r2c26, r2c28});
        r2c28.addNeighbors(new Node[] {r2c27, r1c28, r3c28});
        r3c0.addNeighbors(new Node[] {r2c0, r4c0});
        //r3c1
        r3c2.addNeighbors(new Node[] {r2c2, r4c2});
        //r3c3
        r3c4.addNeighbors(new Node[] {r2c4, r4c4});
        //r3c5
        r3c6.addNeighbors(new Node[] {r2c6, r4c6});
        //r3c7
        r3c8.addNeighbors(new Node[] {r2c8, r4c8});
        //r3c9
        r3c10.addNeighbors(new Node[] {r2c10, r4c10});
        //r3c11
        r3c12.addNeighbors(new Node[] {r2c12, r4c12});
        //r3c13
        r3c14.addNeighbors(new Node[] {r2c14, r4c14});
        //r3c15
        r3c16.addNeighbors(new Node[] {r2c16, r4c16});
        //r3c17
        r3c18.addNeighbors(new Node[] {r2c18, r4c18});
        //r3c19
        r3c20.addNeighbors(new Node[] {r2c20, r4c20});
        //r3c21
        r3c22.addNeighbors(new Node[] {r2c22, r4c22});
        //r3c23
        r3c24.addNeighbors(new Node[] {r2c24, r4c24});
        //r3c25
        r3c26.addNeighbors(new Node[] {r2c26, r4c26});
        //r3c27
        r3c28.addNeighbors(new Node[] {r2c28, r4c28});
        r4c0.addNeighbors(new Node[] {r3c0, r5c0, r4c1});
        r4c1.addNeighbors(new Node[] {r4c0, r4c2});
        r4c2.addNeighbors(new Node[] {r4c1, r3c2, r5c2, r4c3});
        r4c3.addNeighbors(new Node[] {r4c2, r4c4});
        r4c4.addNeighbors(new Node[] {r4c3, r3c4, r5c4, r4c5});
        r4c5.addNeighbors(new Node[] {r4c4, r4c6});
        r4c6.addNeighbors(new Node[] {r4c5, r3c6, r5c6, r4c7});
        r4c7.addNeighbors(new Node[] {r4c6, r4c8});
        r4c8.addNeighbors(new Node[] {r4c7, r3c8, r5c8, r4c9});
        r4c9.addNeighbors(new Node[] {r4c8, r4c10});
        r4c10.addNeighbors(new Node[] {r4c9, r3c10, r5c10, r4c11});
        r4c11.addNeighbors(new Node[] {r4c10, r4c12});
        r4c12.addNeighbors(new Node[] {r4c11, r3c12, r5c12, r4c13});
        r4c13.addNeighbors(new Node[] {r4c12, r4c14});
        r4c14.addNeighbors(new Node[] {r4c13, r3c14, r5c14, r4c15});
        r4c15.addNeighbors(new Node[] {r4c14, r4c16});
        r4c16.addNeighbors(new Node[] {r4c15, r3c16, r5c16, r4c17});
        r4c17.addNeighbors(new Node[] {r4c16, r4c18});
        r4c18.addNeighbors(new Node[] {r4c17, r3c18, r5c18, r4c19});
        r4c19.addNeighbors(new Node[] {r4c18, r4c20});
        r4c20.addNeighbors(new Node[] {r4c19, r3c20, r5c20, r4c21});
        r4c21.addNeighbors(new Node[] {r4c20, r4c22});
        r4c22.addNeighbors(new Node[] {r4c21, r3c22, r5c22, r4c23});
        r4c23.addNeighbors(new Node[] {r4c22, r4c24});
        r4c24.addNeighbors(new Node[] {r4c23, r3c24, r5c24, r4c25});
        r4c25.addNeighbors(new Node[] {r4c24, r4c26});
        r4c26.addNeighbors(new Node[] {r4c25, r3c26, r5c26, r4c27});
        r4c27.addNeighbors(new Node[] {r4c26, r4c28});
        r4c28.addNeighbors(new Node[] {r4c27, r3c28, r5c28});
        r5c0.addNeighbors(new Node[] {r4c0, r6c0});
        //r5c1
        r5c2.addNeighbors(new Node[] {r4c2, r6c2});
        //r5c3
        r5c4.addNeighbors(new Node[] {r4c4, r6c4});
        //r5c5
        r5c6.addNeighbors(new Node[] {r4c6, r6c6});
        //r5c7
        r5c8.addNeighbors(new Node[] {r4c8, r6c8});
        //r5c9
        r5c10.addNeighbors(new Node[] {r4c10, r6c10});
        //r5c11
        r5c12.addNeighbors(new Node[] {r4c12, r6c12});
        //r5c13
        r5c14.addNeighbors(new Node[] {r4c14, r6c14});
        //r5c15
        r5c16.addNeighbors(new Node[] {r4c16, r6c16});
        //r5c17
        r5c18.addNeighbors(new Node[] {r4c18, r6c18});
        //r5c19
        r5c20.addNeighbors(new Node[] {r4c20, r6c20});
        //r5c21
        r5c22.addNeighbors(new Node[] {r4c22, r6c22});
        //r5c23
        r5c24.addNeighbors(new Node[] {r4c24, r6c24});
        //r5c25
        r5c26.addNeighbors(new Node[] {r4c26, r6c26});
        //r5c27
        r5c28.addNeighbors(new Node[] {r4c28, r6c28});
        r6c0.addNeighbors(new Node[] {r5c0, r7c0, r6c1});
        r6c1.addNeighbors(new Node[] {r6c0, r6c2});
        r6c2.addNeighbors(new Node[] {r6c1, r5c2, r7c2, r6c3});
        r6c3.addNeighbors(new Node[] {r6c2, r6c4});
        r6c4.addNeighbors(new Node[] {r6c3, r5c4, r7c4, r6c5});
        r6c5.addNeighbors(new Node[] {r6c4, r6c6});
        r6c6.addNeighbors(new Node[] {r6c5, r5c6, r7c6, r6c7});
        r6c7.addNeighbors(new Node[] {r6c6, r6c8});
        r6c8.addNeighbors(new Node[] {r6c7, r5c8, r7c8, r6c9});
        r6c9.addNeighbors(new Node[] {r6c8, r6c10});
        r6c10.addNeighbors(new Node[] {r6c9, r5c10, r7c10, r6c11});
        r6c11.addNeighbors(new Node[] {r6c10, r6c12});
        r6c12.addNeighbors(new Node[] {r6c11, r5c12, r7c12, r6c13});
        r6c13.addNeighbors(new Node[] {r6c12, r6c14});
        r6c14.addNeighbors(new Node[] {r6c13, r5c14, r7c14, r6c15});
        r6c15.addNeighbors(new Node[] {r6c14, r6c16});
        r6c16.addNeighbors(new Node[] {r6c15, r5c16, r7c16, r6c17});
        r6c17.addNeighbors(new Node[] {r6c16, r6c18});
        r6c18.addNeighbors(new Node[] {r6c17, r5c18, r7c18, r6c19});
        r6c19.addNeighbors(new Node[] {r6c18, r6c20});
        r6c20.addNeighbors(new Node[] {r6c19, r5c20, r7c20, r6c21});
        r6c21.addNeighbors(new Node[] {r6c20, r6c22});
        r6c22.addNeighbors(new Node[] {r6c21, r5c22, r7c22, r6c23});
        r6c23.addNeighbors(new Node[] {r6c22, r6c24});
        r6c24.addNeighbors(new Node[] {r6c23, r5c24, r7c24, r6c25});
        r6c25.addNeighbors(new Node[] {r6c24, r6c26});
        r6c26.addNeighbors(new Node[] {r6c25, r5c26, r7c26, r6c27});
        r6c27.addNeighbors(new Node[] {r6c26, r6c28});
        r6c28.addNeighbors(new Node[] {r6c27, r5c28, r7c28});
        r7c0.addNeighbors(new Node[] {r6c0, r8c0});
        //r7c1
        r7c2.addNeighbors(new Node[] {r6c2, r8c2});
        //r7c3
        r7c4.addNeighbors(new Node[] {r6c4, r8c4});
        //r7c5
        r7c6.addNeighbors(new Node[] {r6c6, r8c6});
        //r7c7
        r7c8.addNeighbors(new Node[] {r6c8, r8c8});
        //r7c9
        r7c10.addNeighbors(new Node[] {r6c10, r8c10});
        //r7c11
        r7c12.addNeighbors(new Node[] {r6c12, r8c12});
        //r7c13
        r7c14.addNeighbors(new Node[] {r6c14, r8c14});
        //r7c15
        r7c16.addNeighbors(new Node[] {r6c16, r8c16});
        //r7c17
        r7c18.addNeighbors(new Node[] {r6c18, r8c18});
        //r7c19
        r7c20.addNeighbors(new Node[] {r6c20, r8c20});
        //r7c21
        r7c22.addNeighbors(new Node[] {r6c22, r8c22});
        //r7c23
        r7c24.addNeighbors(new Node[] {r6c24, r8c24});
        //r7c25
        r7c26.addNeighbors(new Node[] {r6c26, r8c26});
        //r7c27
        r7c28.addNeighbors(new Node[] {r6c28, r8c28});
        r8c0.addNeighbors(new Node[] {r7c0, r9c0, r8c1});
        r8c1.addNeighbors(new Node[] {r8c0, r8c2});
        r8c2.addNeighbors(new Node[] {r8c1, r7c2, r9c2, r8c3});
        r8c3.addNeighbors(new Node[] {r8c2, r8c4});
        r8c4.addNeighbors(new Node[] {r8c3, r7c4, r9c4, r8c5});
        r8c5.addNeighbors(new Node[] {r8c4, r8c6});
        r8c6.addNeighbors(new Node[] {r8c5, r7c6, r9c6, r8c7});
        r8c7.addNeighbors(new Node[] {r8c6, r8c8});
        r8c8.addNeighbors(new Node[] {r8c7, r7c8, r9c8, r8c9});
        r8c9.addNeighbors(new Node[] {r8c8, r8c10});
        r8c10.addNeighbors(new Node[] {r8c9, r7c10, r9c10, r8c11});
        r8c11.addNeighbors(new Node[] {r8c10, r8c12});
        r8c12.addNeighbors(new Node[] {r8c11, r7c12, r9c12, r8c13});
        r8c13.addNeighbors(new Node[] {r8c12, r8c14});
        r8c14.addNeighbors(new Node[] {r8c13, r7c14, r9c14, r8c15});
        r8c15.addNeighbors(new Node[] {r8c14, r8c16});
        r8c16.addNeighbors(new Node[] {r8c15, r7c16, r9c16, r8c17});
        r8c17.addNeighbors(new Node[] {r8c16, r8c18});
        r8c18.addNeighbors(new Node[] {r8c17, r7c18, r9c18, r8c19});
        r8c19.addNeighbors(new Node[] {r8c18, r8c20});
        r8c20.addNeighbors(new Node[] {r8c19, r7c20, r9c20, r8c21});
        r8c21.addNeighbors(new Node[] {r8c20, r8c22});
        r8c22.addNeighbors(new Node[] {r8c21, r7c22, r9c22, r8c23});
        r8c23.addNeighbors(new Node[] {r8c22, r8c24});
        r8c24.addNeighbors(new Node[] {r8c23, r7c24, r9c24, r8c25});
        r8c25.addNeighbors(new Node[] {r8c24, r8c26});
        r8c26.addNeighbors(new Node[] {r8c25, r7c26, r9c26, r8c27});
        r8c27.addNeighbors(new Node[] {r8c26, r8c28});
        r8c28.addNeighbors(new Node[] {r8c27, r7c28, r9c28});
        r9c0.addNeighbors(new Node[] {r8c0, r10c0});
        //r9c1
        r9c2.addNeighbors(new Node[] {r8c2, r10c2});
        //r9c3
        r9c4.addNeighbors(new Node[] {r8c4, r10c4});
        //r9c5
        r9c6.addNeighbors(new Node[] {r8c6, r10c6});
        //r9c7
        r9c8.addNeighbors(new Node[] {r8c8, r10c8});
        //r9c9
        r9c10.addNeighbors(new Node[] {r8c10, r10c10});
        //r9c11
        r9c12.addNeighbors(new Node[] {r8c12, r10c12});
        //r9c13
        r9c14.addNeighbors(new Node[] {r8c14, r10c14});
        //r9c15
        r9c16.addNeighbors(new Node[] {r8c16, r10c16});
        //r9c17
        r9c18.addNeighbors(new Node[] {r8c18, r10c18});
        //r9c19
        r9c20.addNeighbors(new Node[] {r8c20, r10c20});
        //r9c21
        r9c22.addNeighbors(new Node[] {r8c22, r10c22});
        //r9c23
        r9c24.addNeighbors(new Node[] {r8c24, r10c24});
        //r9c25
        r9c26.addNeighbors(new Node[] {r8c26, r10c26});
        //r9c27
        r9c28.addNeighbors(new Node[] {r8c28, r10c28});
        r10c0.addNeighbors(new Node[] {r9c0, r10c1});
        r10c1.addNeighbors(new Node[] {r10c0, r10c2});
        r10c2.addNeighbors(new Node[] {r10c1, r9c2, r10c3});
        r10c3.addNeighbors(new Node[] {r10c2, r10c4});
        r10c4.addNeighbors(new Node[] {r10c3, r9c4, r10c5});
        r10c5.addNeighbors(new Node[] {r10c4, r10c6});
        r10c6.addNeighbors(new Node[] {r10c5, r9c6, r10c7});
        r10c7.addNeighbors(new Node[] {r10c6, r10c8});
        r10c8.addNeighbors(new Node[] {r10c7, r9c8, r10c9});
        r10c9.addNeighbors(new Node[] {r10c8, r10c10});
        r10c10.addNeighbors(new Node[] {r10c9, r9c10, r10c11});
        r10c11.addNeighbors(new Node[] {r10c10, r10c12});
        r10c12.addNeighbors(new Node[] {r10c11, r9c12, r10c13});
        r10c13.addNeighbors(new Node[] {r10c12, r10c14});
        r10c14.addNeighbors(new Node[] {r10c13, r9c14, r10c15});
        r10c15.addNeighbors(new Node[] {r10c14, r10c16});
        r10c16.addNeighbors(new Node[] {r10c15, r9c16, r10c17});
        r10c17.addNeighbors(new Node[] {r10c16, r10c18});
        r10c18.addNeighbors(new Node[] {r10c17, r9c18, r10c19});
        r10c19.addNeighbors(new Node[] {r10c18, r10c20});
        r10c20.addNeighbors(new Node[] {r10c19, r9c20, r10c21});
        r10c21.addNeighbors(new Node[] {r10c20, r10c22});
        r10c22.addNeighbors(new Node[] {r10c21, r9c22, r10c23});
        r10c23.addNeighbors(new Node[] {r10c22, r10c24});
        r10c24.addNeighbors(new Node[] {r10c23, r9c24, r10c25});
        r10c25.addNeighbors(new Node[] {r10c24, r10c26});
        r10c26.addNeighbors(new Node[] {r10c25, r9c26, r10c27});
        r10c27.addNeighbors(new Node[] {r10c26, r10c28});
        r10c28.addNeighbors(new Node[] {r10c27, r9c28});

        Node[][] graphPlane = new Node[][] {
            { r0c0, r0c1, r0c2, r0c3, r0c4, r0c5, r0c6, r0c7, r0c8, r0c9, r0c10, r0c11, r0c12, r0c13, r0c14, r0c15, r0c16, r0c17, r0c18, r0c19, r0c20, r0c21, r0c22, r0c23, r0c24, r0c25, r0c26, r0c27, r0c28 },

            { r1c0, null, r1c2, null, r1c4, null, r1c6, null, r1c8, null, r1c10, null, r1c12, null, r1c14, null, r1c16, null, r1c18, null, r1c20, null, r1c22, null, r1c24, null, r1c26, null, r1c28 },

            { r2c0, r2c1, r2c2, r2c3, r2c4, r2c5, r2c6, r2c7, r2c8, r2c9, r2c10, r2c11, r2c12, r2c13, r2c14, r2c15, r2c16, r2c17, r2c18, r2c19, r2c20, r2c21, r2c22, r2c23, r2c24, r2c25, r2c26, r2c27, r2c28 },

            { r3c0, null, r3c2, null, r3c4, null, r3c6, null, r3c8, null, r3c10, null, r3c12, null, r3c14, null, r3c16, null, r3c18, null, r3c20, null, r3c22, null, r3c24, null, r3c26, null, r3c28 },

            { r4c0, r4c1, r4c2, r4c3, r4c4, r4c5, r4c6, r4c7, r4c8, r4c9, r4c10, r4c11, r4c12, r4c13, r4c14, r4c15, r4c16, r4c17, r4c18, r4c19, r4c20, r4c21, r4c22, r4c23, r4c24, r4c25, r4c26, r4c27, r4c28 },

            { r5c0, null, r5c2, null, r5c4, null, r5c6, null, r5c8, null, r5c10, null, r5c12, null, r5c14, null, r5c16, null, r5c18, null, r5c20, null, r5c22, null, r5c24, null, r5c26, null, r5c28 },

            { r6c0, r6c1, r6c2, r6c3, r6c4, r6c5, r6c6, r6c7, r6c8, r6c9, r6c10, r6c11, r6c12, r6c13, r6c14, r6c15, r6c16, r6c17, r6c18, r6c19, r6c20, r6c21, r6c22, r6c23, r6c24, r6c25, r6c26, r6c27, r6c28 },

            { r7c0, null, r7c2, null, r7c4, null, r7c6, null, r7c8, null, r7c10, null, r7c12, null, r7c14, null, r7c16, null, r7c18, null, r7c20, null, r7c22, null, r7c24, null, r7c26, null, r7c28 },

            { r8c0, r8c1, r8c2, r8c3, r8c4, r8c5, r8c6, r8c7, r8c8, r8c9, r8c10, r8c11, r8c12, r8c13, r8c14, r8c15, r8c16, r8c17, r8c18, r8c19, r8c20, r8c21, r8c22, r8c23, r8c24, r8c25, r8c26, r8c27, r8c28 },

            { r9c0, null, r9c2, null, r9c4, null, r9c6, null, r9c8, null, r9c10, null, r9c12, null, r9c14, null, r9c16, null, r9c18, null, r9c20, null, r9c22, null, r9c24, null, r9c26, null, r9c28 },

            { r10c0, r10c1, r10c2, r10c3, r10c4, r10c5, r10c6, r10c7, r10c8, r10c9, r10c10, r10c11, r10c12, r10c13, r10c14, r10c15, r10c16, r10c17, r10c18, r10c19, r10c20, r10c21, r10c22, r10c23, r10c24, r10c25, r10c26, r10c27, r10c28 }
        };

        return graphPlane;
    }


}
