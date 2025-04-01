package labyrinth;

/**
 * Class for the highscores achieved by players.
 * @author Bálint
 */
public class HighScore {
    private final String plName;
    private final int levelNum;
    private final int timePassed;

    public HighScore(String plName, int levelNum, int timePassed) {
        this.plName = plName;
        this.levelNum = levelNum;
        this.timePassed = timePassed;
    }

    @Override
    public String toString() {
        return "HighScore{" + "name=" + plName + ", level completed=" + levelNum + ", time=" + timePassed + '}';
    }

    public int getLevelNum() {
        return levelNum;
    }

    public String getPlName() {
        return plName;
    }

    public int getTimePassed() {
        return timePassed;
    }
}
