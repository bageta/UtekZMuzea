package plannertester;

import game.Level;
/**
 *
 * @author Pavel
 */
public class TestGenerator {
    
    public Level[] getTestSet(){
        Level[] levels = new Level[10];
        //loadLevel(String levelName);
        for(int i=0; i<levels.length; ++i){
            levels[i] = generateRandomLevel();
        }
        return levels;
    }
    
    private Level generateRandomLevel(){
        Level lvl = new Level();
        return lvl;
    }
}
