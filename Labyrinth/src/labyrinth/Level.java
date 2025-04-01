package labyrinth;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class helps with getting data from the txt files.
 * @author Bálint
 */
public class Level {
    protected int[][] array;
    
    public Level(String fileName, int[][] maze) throws FileNotFoundException, InvalidInputException {
        loadLevel(fileName, maze);
    }
    
    public void loadLevel(String fileName, int[][] maze) throws FileNotFoundException, InvalidInputException{
        Scanner sc = new Scanner(new File(fileName));
        if(sc.hasNextInt()){
            int width = sc.nextInt();
            int height = sc.nextInt();
            array = new int[height][width];
            if(sc.hasNextInt()){
                for(int i = 0; i < height; ++i){
                    for(int j = 0; j < width; ++j){
                        if(sc.hasNextInt()){
                            int field = sc.nextInt();
                            maze[i][j] = field;
                            if(field == 1){
                                array[i][j] = 1;
                            }
                        }
                    }
                }
            }
        }else{
            throw new InvalidInputException();
        }
        sc.close();
    }
}
