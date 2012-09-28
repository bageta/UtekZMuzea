package game;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Pavel
 */
public class Obstacle extends Node {
    
    Room actualPosition;
    Spatial model;
    
    ObstacleType type;
    
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
}