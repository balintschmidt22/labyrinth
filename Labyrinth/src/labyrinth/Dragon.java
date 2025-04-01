package labyrinth;

import java.awt.Image;

/**
 * Implementing the dragon.
 * @author Bálint
 */
public class Dragon extends Sprite{
    private int posX = 0;
    private int posY = 0;
    
    protected int velx = 1;
    protected int vely = 1;
    
    protected boolean moving = false;
    
    public Dragon(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }
    /**
     * Function to move the dragon on the X axis.
     */
    public void moveX(){
        x += velx;
        if(x <= 0 || x >= 420){
            x = 390;
            y = 30;
            invertVelX();
        }
    }
    /**
     * Function to move the dragon on the Y axis.
     */
    public void moveY(){
        y += vely;
        if(y <=0 || y >= 360){
            x = 390;
            y = 30;
            invertVelY();
        } 
    }
    
    public void invertVelX(){
        velx = -velx;
    }
    
    public void invertVelY(){
        vely = -vely;
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