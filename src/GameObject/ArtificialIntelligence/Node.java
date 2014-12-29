/**
 * Created by danielmacario on 14-11-22.
 */
package GameObject.ArtificialIntelligence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Node object used to represent tiles in the graph that the high intelligence
 * enemies use to determine the shortest path to the player.
 */
public class Node implements Serializable {

    private List<Node> neighbors = new ArrayList<Node>();
    private Node parent;
    private int overallCost;
    private int costToReachNodeFromStart;
    private int estimatedCostToReachDestination;
    private int x;
    private int y;
    private int cost;
    private boolean isObstacle;

    /**
     * A node is defined by a coordinate representing a tile on the grid, and a cost
     * value used in the A* algorithm.
     * @param x
     * @param y
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.cost = 1;
    }

    /**
     * Populates the neighbors ArrayList representing the adjacent tiles to the
     * tile represented by this node instance.
     * @param newNeighbors An array containing the neighbouring tiles.
     */
    public void addNeighbors(Node[] newNeighbors) {
        for (Node neighbor : newNeighbors) {
            this.neighbors.add(neighbor);
        }
    }

    /**
     * Retrieves the neighbors of this ArrayList instance.
     * @return An arrayList containing the adjacent nodes in the graph to this instance.
     */
    public List<Node> getNeighbors() {
        return neighbors;
    }

    /**
     * Get the parent node that was used to arrive at this instance during a traversal of
     * the A* algorithm.
     * @return The node object used to reach this node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Set the parent node that was used to arrive at this instance during a traversal of
     * the A* algorithm.
     * @param parent The node object that was used to reach this node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Get the overall cost value of this tile used to calculate the paths in the A*
     * algorithm.
     * @return An integer representing the overall cost of this tile.
     */
    public int getOverallCost() {
        return overallCost;
    }

    /**
     * Set the overall cost values of this tile used to calculate the paths in the A*
     * algorithm.
     * @param overallCost An integer representing the overall cost of this tile.
     */
    public void setOverallCost(int overallCost) {
        this.overallCost = overallCost;
    }

    /**
     * Get the cost to reach this node from the starting tile of the A* algorithm.
     * @return An integer representing the cost to reach this node from the starting tile of the A* algorithm.
     */
    public int getCostToReachNodeFromStart() {
        return costToReachNodeFromStart;
    }

    /**
     * Set the cost to reach this node from the starting tile of the A* algorithm.
     * @param costToReachNodeFromStart An integer specifying the cost to reach this
     *                                 node from the starting tile of the A* algorithm.
     */
    public void setCostToReachNodeFromStart(int costToReachNodeFromStart) {
        this.costToReachNodeFromStart = costToReachNodeFromStart;
    }

    /**
     * Get the estimated cost to reach the destination from this node instance.
     * @return An integer representing the estimated cost to reach the destination
     * from this node instance.
     */
    public int getEstimatedCostToReachDestination() {
        return estimatedCostToReachDestination;
    }

    /**
     * Get the estimated cost to reach the destination from this node instance.
     * @param estimatedCostToReachDestination An integer representing the estimated
     *                                        cost to reach the destination from this node instance.
     */
    public void setEstimatedCostToReachDestination(int estimatedCostToReachDestination) {
        this.estimatedCostToReachDestination = estimatedCostToReachDestination;
    }

    /**
     * Get the x coordinate contained within this node instance.
     * @return An integer representing the X coordinate of this node instance.
     */
    public int getX() {
        return x;
    }

    /**
     * Set the x coordinate contained within this node instance.
     * @param x An integer specifying the X coordinate of this node instance.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the y coordinate contained within this node instance.
     * @return An integer representing the y coordinate of this node instance.
     */
    public int getY() {
        return y;
    }

    /**
     * Set the y coordinate contained within this node instance.
     * @param y An integer specifying the X coordinate of this node instance.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get the cost attribute of this node instance.
     * @return An integer specifying the cost attribute of this node instance.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Determine whether the tile represented by this node is an obstacle (Brick wall or Concrete wall)
     * @return A boolean specifying whether the tile represented by this node is an obstacle
     * or not.
     */
    public boolean isObstacle() {
        return isObstacle;
    }

    /**
     * Specify whether this node instance represents a tile that contains an obstacle.
     * @param isObstacle A boolean specifying whether this node instance represents a tile
     *                   that contains an obstacle.
     */
    public void setObstacle(boolean isObstacle) {
        this.isObstacle = isObstacle;
    }
}
