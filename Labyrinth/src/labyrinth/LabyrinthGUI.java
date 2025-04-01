package labyrinth;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * GUI for the game. Implements the frame, menu buttons, timer and the leaderboard.
 * @author Bálint
 */
public class LabyrinthGUI{
    private JFrame frame;
    private GameEngine gameArea;
    private Timer t;
    private static int timePassed;
    private JLabel lblTime = new JLabel("0", SwingConstants.CENTER);
    private static JLabel lvlCnt = new JLabel("Level 1",SwingConstants.CENTER);
    
    public LabyrinthGUI(){
        frame = new JFrame("Labyrinth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        gameArea = new GameEngine();
        frame.getContentPane().add(gameArea, BorderLayout.CENTER);
        frame.getContentPane().add(lblTime, BorderLayout.SOUTH);
        frame.getContentPane().add(lvlCnt, BorderLayout.NORTH);
        lvlCnt.setFont(new Font("Arial", Font.PLAIN, 25));
        timer();
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);
        JMenuItem restartMenuItem = new JMenuItem("Restart");
        gameMenu.add(restartMenuItem);
        restartMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                gameArea.setLevelNum(0);
                t.stop();
                lblTime.setText("0");
                timer();
                gameArea.restart();
            }
        });
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        gameMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        JMenu leaderBoard = new JMenu("Scores");
        menuBar.add(leaderBoard);
        JMenuItem lBoardItem = new JMenuItem("Leaderboard");
        leaderBoard.add(lBoardItem);
        lBoardItem.addActionListener(new ActionListener(){
            @Override
            public  void actionPerformed(ActionEvent e){
                gameArea.setPaused(true);
                JFrame newFrame = new JFrame("Leaderboard");
                newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                String[] columnNames = {"pos", "name", "level completed", "time"};
                ArrayList<HighScore> hScores = new ArrayList<>();
                try{
                    hScores = gameArea.highScores.getHighScores();
                }catch(SQLException ex){
                    Logger.getLogger(LabyrinthGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                Object[][] data = new Object[hScores.size()][4];
                int i = 0;
                for(HighScore hS : hScores){
                    data[i][0] = i+1;
                    data[i][1] = hS.getPlName();
                    data[i][2] = hS.getLevelNum();
                    data[i][3] = hS.getTimePassed();
                    i++;
                }
                JTable table = new JTable(data, columnNames);
                newFrame.getContentPane().add(table, BorderLayout.CENTER);
                newFrame.setResizable(false);
                newFrame.setMinimumSize(new Dimension(300,100));
                newFrame.pack();
                newFrame.setVisible(true);
                newFrame.setLocationRelativeTo(null);
                
                newFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    gameArea.setPaused(false);
                }
});
            }
        });
        
        frame.setPreferredSize(new Dimension(600, 600));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
    /**
     * Timer for the game.
     */
    public void timer(){
        timePassed = 1;
        lblTime.setFont(new Font("Arial", Font.PLAIN, 32));
        t = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!gameArea.isPaused()){
                    lblTime.setText(String.valueOf(timePassed));
                    timePassed++;
                }
            }
        });
        t.start();
    }

    public static int getTimePassed() {
        return timePassed;
    }

    public static void setTimePassed(int timePassed) {
        LabyrinthGUI.timePassed = timePassed;
    }

    public static void setLvlCnt(String str) {
        lvlCnt.setText(str);
    }
}
