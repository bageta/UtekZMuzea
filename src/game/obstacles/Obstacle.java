package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import game.ObstacleType;
import game.Room;

/**
 * Třída pro reprezentaci překážek, generická překážka. Třída je abstraktní,
 * slouží jako předek překážkám s konkrétním typem a modelem a poskytuje
 * rozhraní pro práci s překážkami.
 * @author Pavel Pilař
 */
public abstract class Obstacle extends Node {
    
    /** aktuální poloha překážky: */
    private Room actualPosition;
    
    /** typ překážky: */
    public ObstacleType type;
    
    /**
     * kostruktor pro vytvoření nové překážky daného typu. Nastaví se typ
     * překážky.
     * @param type typ překážky
     */
    public Obstacle(AssetManager am, ObstacleType type){
        this.type = type;
    }
    
    /**
     * umístění překážky do dané místnosti
     * @param position místnost kam se překážka umístí
     */
    public void placeObstacle(Room position){
        actualPosition = position;
        this.setLocalTranslation(position.getPosition());
    }
    
    /**
     * vrací místnost ve, které je překážka.
     * @return reference na místnost obsahující tuto překážku
     */
    public Room getPosition(){
        return actualPosition;
    }
    
}