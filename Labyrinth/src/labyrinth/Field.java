package labyrinth;

import java.awt.Image;

/**
 * Implementing the fields in the labyrinth.
 * @author Bálint
 */
public class Field extends Sprite{
    private boolean isWall;
    private boolean isFinal;
    private boolean hasPlayer = false;
    
    public Field(int x, int y, int width, int height, Image image, boolean isWall, boolean isFinal) {
        super(x, y, width, height, image);
        this.isWall = isWall;
        this.isFinal = isFinal;
    }
    public Field(int x, int y, int width, int height, boolean isWall, boolean isFinal) {
        super(x, y, width, height);
        this.isWall = isWall;
        this.isFinal = isFinal;
    }

    public boolean isWall() {
        return isWall;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setIsWall(boolean isWall) {
        this.isWall = isWall;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean hasPlayer() {
        return hasPlayer;
    }

    public void setHasPlayer(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }
}
