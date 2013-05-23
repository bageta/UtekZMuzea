package editor;

import game.Level;
import game.ObstacleType;
import game.Room;
import game.Thief;
import game.obstacles.DogObstacle;
import game.obstacles.FireObstacle;
import game.obstacles.FlashObstacle;
import game.obstacles.GlassObstacle;
import game.obstacles.Obstacle;
import planner.Planner2;

/**
 *
 * @author Pavel Pilar
 */
public class LevelTester {
    
    private Level level;
    private Thief thief;
    
    private Planner2 planner;
    
    public LevelTester(Level level){
        this.level = level;
        //thief = new Thief(level);
        planner = new Planner2(level, thief);
    }
    
    public boolean test(){
        System.out.print("TADZY TO DOJDE");
        for(Room r: level.rooms){
            System.out.println("TADY CYKLI");
            thief.setActualPosition(r);
            for(ObstacleType o : level.availableObst.keySet()){
                System.out.println("NEBO TADY");
                if(level.availableObst.get(o) > 0){
                    Obstacle toAdd;
                    switch(o){
                        case DOG:
                            toAdd = new DogObstacle(null);
                            break;
                        case GLASS:
                            toAdd = new GlassObstacle(null);
                            break;
                        case FIRE:
                            toAdd = new FireObstacle(null);
                            break;
                        case FLASH:
                            toAdd = new FlashObstacle(null);
                            break;
                        default:
                            toAdd =null;
                                
                    }
                    for(Room rr : level.rooms){
                        System.out.println("NEBO TU");
                        level.addObstacle(toAdd, rr);
                        planner.setLevel(level);
                        if(planner.makeNewPlan()==null){
                            return false;
                        }
                        rr.deleteObstacle();
                        level.obstacles.remove(toAdd);
                    }
                }
            }
        }
        return true;
    }
}
