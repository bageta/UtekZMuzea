package plannertester;

import java.util.Random;

import game.Level;
import game.obstacles.Obstacle;
import game.ObstacleType;
import game.items.Item;

/**
 *
 * @author Pavel
 */
public class TestGenerator {
    
    Random randomizer;
    
    public Level[] getTestSet(){
        randomizer = new Random();
        Level[] levels = new Level[10];
        //loadLevel(String levelName);
        for(int i=0; i<levels.length; ++i){
            levels[i] = generateRandomLevel();
        }
        return levels;
    }
    
    private Level generateRandomLevel(){
        Level lvl = new Level(randomizer.nextInt(100));
        for(int i = 0; i<lvl.rooms.length; ++i){
            for(int j = 0; j < lvl.rooms.length; ++j){
                if(randomizer.nextInt(100)%3 == 0 && i!=j){
                    lvl.makeNeighbours(lvl.rooms[i], lvl.rooms[j]);
                }
            }
        }
        int obstacles = lvl.rooms.length/10;
        for(int i=0; i< obstacles; ++i){
            if(i%2==0){
//                lvl.addObstacle(new Obstacle(null, ObstacleType.DOG), lvl.rooms[randomizer.nextInt(lvl.rooms.length)]);
                lvl.addItem(ObstacleType.DOG, lvl.rooms[randomizer.nextInt(lvl.rooms.length)]);
            }
//                lvl.addObstacle(new Obstacle(null, ObstacleType.GLASS), lvl.rooms[randomizer.nextInt(lvl.rooms.length)]);
            lvl.addItem(ObstacleType.GLASS, lvl.rooms[randomizer.nextInt(lvl.rooms.length)]);
        }
        return lvl;
    }
}