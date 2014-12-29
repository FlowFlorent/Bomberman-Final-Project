/**
 * Created by danielmacario on 14-12-20.
 */
package Database;

import com.mongodb.*;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Schema:
 *
 * username String , password String, realName String, highScore int, levelUnlocked int, gamesPlayed int
 */
public class MongoDBController {

    // DB object used for all interaction with the database
    private static DB db;

    // String representing the database_id, used to connect to the database.
    public static String database_id = "jdbc:sqlite:user_data.db";

    // String representing directory for which games will be saved to
    public static String saveDirectory = "src/res/data/savedgames/";

    public void initializeDataBase() {
        try {
            MongoClientURI uri = new MongoClientURI("mongodb://uname:pass@ds059887.mongolab.com:59887/bomberman");
            MongoClient client = new MongoClient(uri);
            db = client.getDB(uri.getDatabase());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public void testConnection() {

        DBCollection testCollection = db.getCollection("testCollection");

        BasicDBObject findQuery = new BasicDBObject("name", "Diego");
        DBCursor docs = testCollection.find(findQuery);

        DBObject doc = docs.next();
        DBObject attributes = (DBObject) doc.get("attributes");
        System.out.println("name = " + doc.get("name") + " lastName = " + doc.get("lastName") +
                           "Class = " + attributes.get("class"));
    }


    public static boolean createNewUser(String userName, String pass, String rName) {

        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", userName);
        DBCursor docs = users.find(findQuery);

        // Test if the username already exists in the db
        if (docs.size() != 0) {
            System.out.println("Username taken");
            return false;
        }
        docs.close();

        BasicDBObject doc = new BasicDBObject("username", userName)
                .append("password", pass)
                .append("realName", rName)
                .append("highScore", 0)
                .append("levelUnlocked", 1)
                .append("gamesPlayed", 0);
        users.insert(doc);

        System.out.println("New User inserted into database");
        createDirectoryForUserSavedFiles(userName);

        return true;
    }

    /**
     * Creates a directory savedgames where saved game data is stored.
     * @param username A string representing the name of the folder to be created, where the user's saved game files will be stored.
     *                 Corresponds to the user's username.
     */
    public static void createDirectoryForUserSavedFiles(String username) {
        File dir = new File(saveDirectory + username);
        dir.mkdirs();
    }

    /**
     * Getter method to return a password for a given username.
     * @param username A string representing the username of which the corresponding password is to be obtained.
     * @return returns a String representing the password corresponding to the username upon which getPassword() was called.
     * @throws ClassNotFoundException
     */
    public static String getPassword(String username) throws ClassNotFoundException {

        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", username);
        DBCursor docs = users.find(findQuery);

        DBObject doc = docs.next();
        String password = (String) doc.get("password");
        docs.close();
        return password;
    }

    /**
     * Getter method to return a real name for a given username.
     * @param username A string representing the username of which the corresponding real name is to be obtained.
     * @return returns a String representing the real name corresponding to the username upon which getRealName() was called.
     * @throws ClassNotFoundException
     */
    public static String getRealName(String username) throws ClassNotFoundException {

        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", username);
        DBCursor docs = users.find(findQuery);

        DBObject doc = docs.next();
        String password = (String) doc.get("password");
        docs.close();
        return password;
    }


    /**
     * Sets the levelUnlocked field in the database table to a specified level for a given username.
     * @param username A string representing the username of which the corresponding level is to be set.
     * @param level An int representing the level of which the corresponding user's levelUnlocked field in the database is to be set.
     * @throws ClassNotFoundException
     */
    public static void setLevelUnlocked(String username, int level) throws ClassNotFoundException {
        if (level >= 1 && level <= 60) {
            DBCollection users = db.getCollection("users");
            BasicDBObject searchQuery = new BasicDBObject().append("username", username);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.append("$set", new BasicDBObject().append("levelUnlocked", level));
            users.update(searchQuery, newDocument);
        } else {
            System.out.println("Level does not exist");
        }
    }

    public static int getLevelUnlocked(String username) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", username);
        DBCursor docs = users.find(findQuery);
        DBObject doc = docs.next();
        int levelUnlocked = (Integer) doc.get("levelUnlocked");
        docs.close();
        return levelUnlocked;
    }

    /**
     * Authenticates a user by querying the database for a specified username and password.
     * @param Uname A string representing the specified username of the user to be authenticated.
     * @param pass A string representing the specified password of the user to be authenticated.
     * @return Boolean returns true if the user was succesfully authenticated (user exists in the user_data.db table, and username/password match). Returns false otherwise.
     * @throws ClassNotFoundException
     */
    public static boolean authenticateUser(String Uname, String pass) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject();
        findQuery.put("username", Uname);
        findQuery.put("password", pass);
        DBCursor docs = users.find(findQuery);

        if (docs.size() == 0) {
            System.out.println("Username/Password does not exist");
            docs.close();
            return false;
        }
        docs.close();
        return true;
    }

    /**
     * Updates the password field in the user database of a given user to a new specified password.
     * @param newPass A string representing the new specified password for the user.
     * @param Uname A string representing the specified username for which the password field must be updated.
     * @return Boolean returns true if the user's password was succesfully updated. Returns false otherwise.
     * @throws ClassNotFoundException
     */
    public static boolean updatePassword(String newPass, String Uname) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("password", newPass));
        BasicDBObject searchQuery = new BasicDBObject().append("username", Uname);

        if (newPass != null && !newPass.isEmpty()) {
            users.update(searchQuery, newDocument);
        } else {
            System.out.println("Password not changed");
            return false;
        }

        return true;
    }

    /**
     * Updates the real name field in the user database of a given user to a new specified real name.
     * @param newRealName A string representing the new specified real name for the user.
     * @param Uname A string representing the specified username for which the real name field must be updated.
     * @return  Boolean returns true if the user's real name was successfully updated. Returns false otherwise.
     * @throws ClassNotFoundException
     */
    public static boolean updateRealName(String newRealName, String Uname) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("realName", newRealName));
        BasicDBObject searchQuery = new BasicDBObject().append("username", Uname);

        if (newRealName != null && !newRealName.isEmpty()) {
            users.update(searchQuery, newDocument);
        } else {
            System.out.println("Real name not changed");
            return false;
        }

        return true;
    }

    /**
     * Removes all the fields in the database table associated to a username.
     * @param username A string representing the username of a user, for which all fields should be deleted.
     * @return Boolean returns true if the deletion was successful (all fields corresponding to the username removed from the table). Returns false otherwise.
     * @throws ClassNotFoundException
     */
    public static boolean deleteAccount(String username) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");
        BasicDBObject document = new BasicDBObject();
        document.put("username", username);
        users.remove(document);
        return true;
    }

    /**
     * Getter method to return an int score corresponding to a given username.
     * @param username A string representing the username of which the corresponding score is to be obtained.
     * @return Returns an int representing the score corresponding to the username upon which getScore() was called.
     * @throws ClassNotFoundException
     */
    public static int getScore(String username) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", username);
        DBCursor docs = users.find(findQuery);
        DBObject doc = docs.next();
        int score = (Integer) doc.get("highScore");
        docs.close();
        return score;
    }

    /**
     * Adds a specified score to the existing score of a user in the database, as specified by the username upon which setScore() is called.
     * The score for a given user is culmulative.
     * @param username A string representing the username of which the specified score is to be added to the existing score.
     * @param score An int representing the score of which the corresponding user's score field in the database is to be incremented by.
     * @throws ClassNotFoundException
     */
    public static void setScore(String username, int score) throws ClassNotFoundException {
        if (score < 0) {
            System.out.println("negative score is invalid");
            return;
        }

        DBCollection users = db.getCollection("users");
        DBObject modifier = new BasicDBObject("highScore", score);
        DBObject incQuery = new BasicDBObject("$inc", modifier);
        BasicDBObject searchQuery = new BasicDBObject("username", username);
        users.update(searchQuery, incQuery);
    }

    /**
     * Creates an arraylist of type PlayerScore, populated by PlayerScore objects corresponding to all users in the database.
     * The arraylist is sorted by score, in descending order.
     * @return Returns an arraylist populated with PlayerScore objects corresponding to all users in the database sorted by score in descending order.
     * @throws ClassNotFoundException
     */
    public static ArrayList<PlayerScore> getTopScoresSet() throws ClassNotFoundException {

        DBCollection users = db.getCollection("users");
        BasicDBObject query = new BasicDBObject();
        query.put("highScore", -1);
        DBCursor docs = users.find().sort(query);

        ArrayList<PlayerScore> playerScores = new ArrayList<PlayerScore>();
        while (docs.hasNext()) {
            DBObject doc = docs.next();
            playerScores.add(PlayerScore.createPlayer(
                    (String) doc.get("username"),
                    (Integer) doc.get("highScore"),
                    (String) doc.get("realName"),
                    (Integer) doc.get("gamesPlayed")));
        }

        return playerScores;
    }

    /**
     * Increments the number of games played by 1 for a specified user.
     * @param username A string representing the username for which the corresponding gamesPlayed field is to be incremented by 1.
     * @throws ClassNotFoundException
     */
    public static void incrementGamesPlayed(String username) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");
        DBObject modifier = new BasicDBObject("gamesPlayed", 1);
        DBObject incQuery = new BasicDBObject("$inc", modifier);
        BasicDBObject searchQuery = new BasicDBObject("username", username);
        users.update(searchQuery, incQuery);
    }

    /**
     * Getter method to return the number of games played corresponding to a given username.
     * @param username A string representing the username of which the corresponding number of games played is to be obtained.
     * @return Returns an int representing the number of games played corresponding to the username upon which getGamesPlayed() was called.
     * @throws ClassNotFoundException
     */
    public static int getGamesPlayed(String username) throws ClassNotFoundException {
        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", username);
        DBCursor docs = users.find(findQuery);
        DBObject doc = docs.next();
        int gamesPlayed = (Integer) doc.get("gamesPlayed");
        docs.close();
        return gamesPlayed;
    }

    /**
     * Creates a PlayerScore object with the entries corresponding to a specified username in the database.
     * @param username A string representing the username for which the relevant corresponding entries in the database are to be pulled.
     * @return Returns a PlayerScore object defined by elements corresponding to the specified username.
     * @throws ClassNotFoundException
     */
    public static PlayerScore getPlayerObject(String username) throws ClassNotFoundException {
        PlayerScore player;
        DBCollection users = db.getCollection("users");

        BasicDBObject findQuery = new BasicDBObject("username", username);
        DBCursor docs = users.find(findQuery);
        DBObject doc = docs.next();
        player = PlayerScore.createPlayer(
                username,
                (Integer) doc.get("highScore"),
                (String) doc.get("realName"),
                (Integer) doc.get("gamesPlayed")
        );

        System.out.println("Player Object created successfully");
        return player;

    }
}
