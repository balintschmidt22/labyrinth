package labyrinth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

/**
 * This class implements the database for the top10 leaderboard. 
 * @author Bálint
 */
public class HighScores {

    int maxScores;
    PreparedStatement insertStatement;
    PreparedStatement deleteStatement;
    Connection connection;

    public HighScores(int maxScores) throws SQLException {
        this.maxScores = maxScores;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "tanulo");
        connectionProps.put("password", "Asdf.123");
        connectionProps.put("serverTimezone", "UTC");
        String dbURL = "jdbc:mysql://localhost:3306/highscores";
        connection = DriverManager.getConnection(dbURL, connectionProps);
        
        String insertQuery = "INSERT INTO HIGHSCORES (TIMESTAMP, PLNAME, LEVELNUM, TIMEPASSED) VALUES (?, ?, ?, ?)";
        insertStatement = connection.prepareStatement(insertQuery);
        String deleteQuery = "DELETE FROM HIGHSCORES WHERE LEVELNUM=? AND TIMEPASSED=?";
        deleteStatement = connection.prepareStatement(deleteQuery);
    }

    public ArrayList<HighScore> getHighScores() throws SQLException {
        String query = "SELECT * FROM HIGHSCORES";
        ArrayList<HighScore> highScores = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet results = stmt.executeQuery(query);
        while (results.next()) {
            String name = results.getString("PLNAME");
            int score = results.getInt("LEVELNUM");
            int time = results.getInt("TIMEPASSED");
            highScores.add(new HighScore(name, score, time));
        }
        sortHighScores(highScores);
        return highScores;
    }

    public void putHighScore(String name, int score, int timepassed) throws SQLException {
        ArrayList<HighScore> highScores = getHighScores();
        if (highScores.size() < maxScores) {
            insertScore(name, score, timepassed);
        } else {
            int leastScore = highScores.get(highScores.size() - 1).getLevelNum();     
            int time = highScores.get(highScores.size()-1).getTimePassed();
            if (leastScore < score) {
                deleteScores(leastScore, time);
                insertScore(name, score, timepassed);
            }else if(leastScore == score){
                if(time > timepassed){
                    deleteScores(leastScore, time);
                    insertScore(name,score,timepassed);
                }
            }
        }
    }

    /**
     * Sort the high scores in descending order.
     * @param highScores 
     */
    private void sortHighScores(ArrayList<HighScore> highScores) {
        Collections.sort(highScores, new Comparator<HighScore>() {
            @Override
            public int compare(HighScore t, HighScore t1) {
                if(t1.getLevelNum() != t.getLevelNum()){
                    return t1.getLevelNum()- t.getLevelNum();
                }else{
                    return -1*(t1.getTimePassed() - t.getTimePassed());
                }
            }
        });
    }

    private void insertScore(String name, int score, int timepassed) throws SQLException {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        insertStatement.setTimestamp(1, ts);
        insertStatement.setString(2, name);
        insertStatement.setInt(3, score);
        insertStatement.setInt(4, timepassed);
        insertStatement.executeUpdate();
    }

    /**
     * Deletes all the highscores with score.
     *
     * @param score
     */
    private void deleteScores(int score, int time) throws SQLException {
        deleteStatement.setInt(1, score);
        deleteStatement.setInt(2, time);
        deleteStatement.executeUpdate();
    }
}

