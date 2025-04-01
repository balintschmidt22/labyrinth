package labyrinth;

import java.awt.Image;

/**
 * Implementing the player.
 * @author Bálint
 */
public class Player extends Sprite{
    private int posX = 0;
    private int posY = 0;
    
    public Player(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}