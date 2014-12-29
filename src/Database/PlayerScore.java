/**
 * Created by Owen Li on 14-11-18.
 */
package Database;

/**
 * Class used to define a wrapper object used to package the information
 * that will be displayed on the the leader boards.
 */
public class PlayerScore {
    public int score;
    public String username;
    public int gamesPlayed;
    public String realName;

    /**
     * Creates a new PlayerScore object from specified elements
     * @param username A string representing the username of the user.
     * @param score An int representing the score of the user.
     * @param realName A string representing the real name of the user.
     * @param gamesPlayed An int representing the number of games played by the user.
     * @return
     */
    public static PlayerScore createPlayer(String username, int score, String realName, int gamesPlayed) {
        PlayerScore player = new PlayerScore();
        player.score = score;
        player.username = username;
        player.gamesPlayed = gamesPlayed;
        player.realName = realName;
        return player;
    }
}



