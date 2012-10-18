package game;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * třída pro reprezentaci překážek
 * @author Pavel
 */
public class Obstacle extends Node {
    
    //aktuální poloha překážky:
    private Room actualPosition;
    //model pro zobrazení:
    Spatial model;
    
    //typ překážky:
    public ObstacleType type;
    
    /**
     * kostruktor pro vytvoření nové překážky daného typu
     * @param type typ překážky
     */
    public Obstacle(ObstacleType type){
        this.type = type;
        //load model
    }
    
    /**
     * umístění překážky do dané místnosti
     * @param position místnost kam se překážka umístí
     */
    public void placeObstacle(Room position){
        actualPosition = position;
    }
    
    /**
     * vrací aktuální pozici
     * @return 
     */
    public Room getPosition(){
        return actualPosition;
    }
    
    /**
     * vrací typ překážky jako číslo
     * @return typ překážky jako číslo
     */
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