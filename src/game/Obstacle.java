package game;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Pavel
 */
public class Obstacle extends Node {
    
    public Room actualPosition;
    Spatial model;
    
    public ObstacleType type;
    
    public Obstacle(ObstacleType type){
        this.type = type;
        //load model
    }
    
    public void placeObstacle(Room position){
        actualPosition = position;
    }
    
    public Room getPosition(){
        return actualPosition;
    }
    
    public int getTypeOrdinal(){
        int result = 0;
        if(type == ObstacleType.DOG){
            result = ObstacleType.DOG.ordinal();
        } else if(type == ObstacleType.GLASS){
            result = ObstacleType.GLASS.ordinal();
        }
        return result;
    }
}