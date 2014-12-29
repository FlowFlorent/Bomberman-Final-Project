/**
 * Created by danielmacario on 14-11-17.
 */
package GamePlay;

import GameObject.KillSet;

import java.io.Serializable;
import java.util.*;

/**
 * System used to calculate the scores obtained from killing enemies based
 * on the specifications requested by the client.
 */
public class ScoreManager implements Serializable {

    /**
     * Main method of the class in charge of deciding the logic that will be used
     * to calculate the score from the kills.
     * @param enemiesKilled An ArrayList of KillSet objects specifying the type of
     *                      enemy that was killed, where it died, and where the bomb
     *                      that killed it was located.
     * @return An integer representing the score obtained from the enemies killed
     * by the player.
     */
    public int determineScoreFromKills(ArrayList<KillSet> enemiesKilled) {

        // If only one enemy was killed, we return its score.
        if (enemiesKilled.size() == 1) return enemiesKilled.get(0).getScore();

        Hashtable<Coordinate, ArrayList<KillSet>> killsPerBomb = new Hashtable<Coordinate, ArrayList<KillSet>>();

        // We group the enemies killed using a hash based on the origin of
        // the bomb that killed them. The hash associates the coordinate
        // where they were killed with an ArrayList that holds the KillSet
        // object representing the enemy killed.
        for (KillSet deadEnemy : enemiesKilled) {

            Coordinate locationOfBomb = deadEnemy.getLocationOfBomb();

            if (killsPerBomb.containsKey(locationOfBomb)) {
                ArrayList<KillSet> bombKills = killsPerBomb.get(locationOfBomb);
                bombKills.add(deadEnemy);
            } else {
                ArrayList<KillSet> bombKills = new ArrayList<KillSet>();
                bombKills.add(deadEnemy);
                killsPerBomb.put(locationOfBomb, bombKills);
            }
        }

        Set<Coordinate> keys = killsPerBomb.keySet();

        // For each of the groups of killed enemies, calculate the score
        // obtained from each following the logic outlined in the
        // requirements.
        int score = 0;
        for (Coordinate key: keys){
            ArrayList<KillSet> kills = killsPerBomb.get(key);
            score += calculateScore(kills);
        }

        return score;
    }

    /**
     * Based on the number of killed enemies, calls a specific function
     * to determine the score from the group of enemies killed.
     * @param kills An ArrayList specifying the enemies killed by
     *              a specific bomb.
     * @return An integer representing the score obtained from
     * the enemies killed by the player.
     */
    public int calculateScore(ArrayList<KillSet> kills) {
        int score = 0;

        if (kills.size() == 2) {
            score = determineScoreForTwoKills(kills.get(0), kills.get(1));
        } else {
            score = determineScoreForManyKills(kills);
        }

        return score;
    }

    /**
     * Determines the score obtained when a single bomb kills more than 2 enemies.
     * It first sorts the kills based on distance to the location of the bomb that
     * killed them. And then it doubles the double for each successive kill.
     * @param kills An ArrayList specifying the enemies killed by a specific bomb.
     * @return An integer specifying the score obtained from killing more than two
     * enemies with a single bomb blast.
     */
    public int determineScoreForManyKills(ArrayList<KillSet> kills) {

        // We sort the enemies killed based on their distance to the
        // location of the bomb that killed them.
        Collections.sort(kills, new Comparator<KillSet>() {
            public int compare(KillSet o1, KillSet o2) {
                return o1.getDistanceToBombOrigin() - o2.getDistanceToBombOrigin();
            }
        });

        // As specified in the requirements, we double the double for each successive kill.
        // For example, killing three Ballooms (score 100) results in a total score of
        // 100 + 2 * 100 + 4 * 100 = 700
        int score = 0;
        int multiplier = 0;
        for (KillSet deadEnemy : kills) {
            if (multiplier == 0) {
                score += deadEnemy.getScore();
                multiplier = 1;
            } else {
                score += (deadEnemy.getScore() * multiplier);
            }
            multiplier *= 2;
        }

        return score;
    }

    /**
     * Determines the score obtained from killing two enemies.
     * In this case, the lowest value kill gets doubled. Order
     * of the enemies passed does not matter.
     * @param killedEnemyA Enemy instance killed by the bomb blast.
     * @param killedEnemyB Enemy instance killed by the bomb blast.
     * @return An integer speicfying the score obtained from killing
     * two enemies with a single bomb blast.
     */
    public int determineScoreForTwoKills(KillSet killedEnemyA, KillSet killedEnemyB) {
        int killedEnemyAScore = killedEnemyA.getScore();
        int killedEnemyBScore = killedEnemyB.getScore();
        if (killedEnemyAScore < killedEnemyBScore) {
            return killedEnemyAScore * 2 + killedEnemyBScore;
        }
        return killedEnemyBScore * 2 + killedEnemyAScore;
    }

}
