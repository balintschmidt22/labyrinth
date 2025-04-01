package labyrinth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.sql.SQLException;
import static labyrinth.LabyrinthGUI.getTimePassed;
import static labyrinth.LabyrinthGUI.setTimePassed;
import static labyrinth.LabyrinthGUI.setLvlCnt;

/**
 * This class implements the game, with creating the labyrinth, player and the dragon.
 * The implementation follows the rules of the original game.
 * @author Bálint
 */
public class GameEngine extends JPanel{
    private final int FPS = 240;
    private final int FSIZE = 30;
    private final int HEIGHT = 13;
    private final int WIDTH = 15;
    private int[][] maze = new int[HEIGHT][WIDTH];
    private Field[][] fields = new Field[HEIGHT][WIDTH];
    private int levelNum = 0;
    private boolean paused = false;
    private boolean dragonPlaced = false;
    private boolean haveWalls = false;
    private ArrayList<Field> walls = new ArrayList<>();
    private List<Field> paths = new ArrayList<>();
    private String plName;
    private Level level;
    private Player player;
    private Dragon dragon;
    private final Timer newFrameTimer;
    protected HighScores highScores;
    
    /**
     * Constructor for the class. Also actionlisteners for the arrows and ESC button.
     */
    public GameEngine() {
        super();
        try{
            highScores = new HighScores(10);
        }catch (SQLException ex){
            Logger.getLogger(GameEngine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "pressed left");
        this.getActionMap().put("pressed left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(!paused){
                    if(!fields[player.getPosX()][player.getPosY()-1].isWall()){
                        player.setX(player.getX()-FSIZE);
                        fields[player.getPosX()][player.getPosY()].setHasPlayer(false);
                        player.setPosY(player.getPosY()-1);
                        repaint();
                        if(fields[player.getPosX()][player.getPosY()].isFinal()){
                            win();
                        }
                    }
                }
            }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "pressed right");
        this.getActionMap().put("pressed right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(!paused){
                    if(!fields[player.getPosX()][player.getPosY()+1].isWall()){
                        player.setX(player.getX()+FSIZE);
                        fields[player.getPosX()][player.getPosY()].setHasPlayer(false);
                        player.setPosY(player.getPosY()+1);
                        repaint();
                        if(fields[player.getPosX()][player.getPosY()].isFinal()){
                            win();
                        }
                    }
                }
            }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "pressed down");
        this.getActionMap().put("pressed down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(!paused){
                    if(!fields[player.getPosX()+1][player.getPosY()].isWall()){
                        player.setY(player.getY()+FSIZE);
                        fields[player.getPosX()][player.getPosY()].setHasPlayer(false);
                        player.setPosX(player.getPosX()+1);
                        repaint();
                        if(fields[player.getPosX()][player.getPosY()].isFinal()){
                            win();
                        }
                    }
                }
            }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "pressed up");
        this.getActionMap().put("pressed up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(!paused){
                    if(!fields[player.getPosX()-1][player.getPosY()].isWall()){
                        player.setY(player.getY()-FSIZE);
                        fields[player.getPosX()][player.getPosY()].setHasPlayer(false);
                        player.setPosX(player.getPosX()-1);
                        repaint();
                        if(fields[player.getPosX()][player.getPosY()].isFinal()){
                            win();
                        }
                    }
                }
            }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        this.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                paused = !paused;
                checkGameOver();
            }
        });
        restart();
        newFrameTimer = new Timer(1000 / FPS, new NewFrameListener());
        newFrameTimer.start();
    }

    /**
     * This method is called, when the player reaches the final field.
     */
    public void win(){
        levelNum++;
        if(levelNum == 5){
            paused = true;
            plName = JOptionPane.showInputDialog(this, "You won! Enter your name:");
            if(plName == null || plName.equals("")){
                plName = "Player";
            }
            try{
                highScores.putHighScore(plName, levelNum, getTimePassed());
            }catch (SQLException ex){
                Logger.getLogger(GameEngine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            levelNum = 0;
            setTimePassed(0);
            restart();
        }else{
            restart();
        }
    }
    
    /**
     * Places the dragon on a random field, which is not a wall or near the player.
     */
    public void placeDragon(){
        paths.clear();
        for (int row = 1; row < fields.length-1; row++) {
            for (int col = 1; col < fields[0].length-1; col++){
                if(!fields[row][col].isWall()){
                    if(!(fields[row][col].hasPlayer() || fields[row-1][col].hasPlayer() || fields[row+1][col].hasPlayer() || fields[row][col+1].hasPlayer() || fields[row][col-1].hasPlayer())){
                        paths.add(fields[row][col]);   
                    }
                }
            }
        }
        Collections.shuffle(paths);
        dragon.x = paths.get(0).x;
        dragon.y = paths.get(0).y;
        dragon.setPosX(paths.get(0).y/30);
        dragon.setPosY(paths.get(0).x/30);
        dragonPlaced = true;
        int nm = getRandomNumber(0,2);
        switch(nm){
            case 0: dragon.velx = 0; dragon.vely = 1;break;
            case 1: dragon.velx = 1; dragon.vely = 0;break;
        }
        repaint();
    }
    
    /**
     * The dragon moves every time this ActionListener is called.
     */
    class NewFrameListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(!paused){
                if(dragon.vely == 1 || dragon.vely == -1){
                    dragon.velx = 0;
                    dragon.moveY();
                }else if(dragon.velx == 1 || dragon.velx == -1){
                    dragon.vely = 0;
                    dragon.moveX();
                }
                if(check()){
                    int orPosX = dragon.x;
                    int orPosY = dragon.y;
                    do{
                    if(dragon.vely != 0){
                        orPosY -= dragon.vely; 
                    }else if(dragon.velx != 0){
                        orPosX -= dragon.velx;
                    }
                    dragon.y = orPosY;
                    dragon.x = orPosX;
                    int num = getRandomNumber(0,4);
                    switch(num){
                        case 0: dragon.vely = 0; dragon.velx = -1; dragon.moveX(); break;
                        case 1: dragon.velx = 0; dragon.vely = -1; dragon.moveY(); break;
                        case 2: dragon.vely = 0; dragon.velx = 1; dragon.moveX(); break;
                        case 3: dragon.velx = 0; dragon.vely = 1; dragon.moveY(); break;
                    }
                    repaint();
                    }while(check());
                }
                checkGameOver();
                repaint();
            }
            
        }
    }
    /**
     * Helps with generating random integersr from min to max-1;
     * @param min
     * @param max
     * @return 
     */
    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
          .findFirst()
          .getAsInt();
    }
    /**
     * Checks whether the dragon collides with the walls.
     * @return 
     */
    public boolean check(){
        for(Field f : walls){
            if(dragon.collides(f) || (dragon.x == f.x && dragon.y == f.y)){
                return true;
            }
        }
        return false;
    }
    /**
     * Checks whether the dragon kills the player by being too close to it.
     */
    public void checkGameOver(){
        int dx = dragon.getX();
        int dy = dragon.getY();
        int px = player.getX();
        int py = player.getY();
        
        if(dragon.collides(player) || (dx == px && dy == py) || (dx+FSIZE == px && dy == py) || (dx-FSIZE == px && dy == py) || (dx == px && dy-FSIZE == py) || (dx == px && dy+FSIZE == py)){
            paused = true;
            plName = JOptionPane.showInputDialog(this, "You lost. Enter your name:");
            if(plName == null || plName.equals("")){
                plName = "Player";
            }
            try{
                highScores.putHighScore(plName, levelNum, getTimePassed());
            }catch (SQLException ex){
                Logger.getLogger(GameEngine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            levelNum = 0;
            setTimePassed(0);
            restart();
        }
    }
    /**
     * Restarts the game.
     */
    public void restart(){
        try{
            level = new Level("lvl" + levelNum + ".txt", maze);
        }catch(FileNotFoundException | InvalidInputException ex){
            Logger.getLogger(GameEngine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            System.exit(-1);
        }
        Image playerImage = new ImageIcon("player.png").getImage();
        player = new Player(FSIZE, FSIZE*(maze.length-2), FSIZE, FSIZE, playerImage);
        player.setPosX(HEIGHT-2);
        player.setPosY(1);
        dragon = new Dragon(1,1,FSIZE,FSIZE, new ImageIcon("dragon.png").getImage());
        paused = false;
        haveWalls = false;
        setLvlCnt("Level " + (levelNum+1));
        if(dragonPlaced){
            fields[player.getPosX()][player.getPosY()].setHasPlayer(true); 
            placeDragon();
        }
        repaint();
    }
    /**
     * Adds all Fields which are walls to the walls ArrayList.
     */
    public void getWalls(){
        walls.clear();
        for (int row = 0; row < fields.length; row++) {
            for (int col = 0; col < fields[0].length; col++) {
                if(level.array[row][col] == 1){
                    walls.add(fields[row][col]);
                }
            }
        }
        haveWalls = true;
    }
    /**
     * Draws the labyrinth, the player and the dragon. Also draws the dark effect except near the player.
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        
        g.translate(50, 50);
        
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[0].length; col++) {
                Color color;
                switch (maze[row][col]) {
                    case 1 : 
                            Image wallImage = new ImageIcon("mwall.png").getImage(); 
                            Field wall = new Field(FSIZE*col, FSIZE*row, FSIZE, FSIZE, wallImage, true, false);
                            wall.draw(g);
                            fields[row][col] = wall;
                            break;
                    case 2 :
                            Field startF = new Field(FSIZE*col, FSIZE*row, FSIZE, FSIZE, false, false);
                            fields[row][col] = startF;
                            color = Color.GREEN; g.setColor(color);
                            g.fillRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                            g.setColor(Color.GREEN);
                            g.drawRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                            break;
                    case 9 : 
                            Image finalImage = new ImageIcon("target.png").getImage(); 
                            Field finalF = new Field(FSIZE*col, FSIZE*row, FSIZE, FSIZE, finalImage, false, true);
                            finalF.draw(g);
                            fields[row][col] = finalF;
                            break;
                    default :
                            Field path = new Field(FSIZE*col, FSIZE*row, FSIZE, FSIZE, false, false);
                            fields[row][col] = path;
                            color = Color.WHITE;  g.setColor(color);
                            g.fillRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                            g.setColor(Color.WHITE);
                            g.drawRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                }
            }
        }
        player.draw(g);
        fields[player.getPosX()][player.getPosY()].setHasPlayer(true);
        if(!dragonPlaced){
            placeDragon();
        }
        dragon.draw(g);
        if(!haveWalls){
            getWalls();
        }
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[0].length; col++){
                if(row >= player.getPosX()+4){
                    Color color;
                    color = Color.BLACK;  g.setColor(color);
                    g.fillRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                }
                if(row <= player.getPosX()-4){
                    Color color;
                    color = Color.BLACK;  g.setColor(color);
                    g.fillRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);

                }
                if(col >= player.getPosY()+4){
                    Color color;
                    color = Color.BLACK;  g.setColor(color);
                    g.fillRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                }
                if(col <= player.getPosY()-4){
                    Color color;
                    color = Color.BLACK;  g.setColor(color);
                    g.fillRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(FSIZE * col, FSIZE * row, FSIZE, FSIZE);

                }
            }
        }
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }
    
    boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public String getPlName() {
        return plName;
    }

    public int getLevelNum() {
        return levelNum;
    }
}
