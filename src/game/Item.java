package game;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Pavel
 */
public class Item extends Node{

    ObstacleType type;
    public Room actualPosition;
    
    Spatial model;
    
    public Item(Room startPossiotion, ObstacleType type){
        this.type = type;
        actualPosition = startPossiotion;
        //TODO: load modelu podle typu
    }
}