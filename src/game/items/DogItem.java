package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

import game.ObstacleType;
import game.Room;

/**
 * Reprezentace věci na odstranění překážky typu pes. Nahrává model šunky, který
 * se bude ve hře zobrazovat a rodiči se nastaví typ pes.
 * @author Pavel Pilař
 */
public class DogItem extends Item {
    
    /** model věci. */
    Spatial itemModel;
    
    /** Konstruktor věci na odstranění překážky typu pes. */
    public DogItem(Room to, AssetManager am){
        super(to, ObstacleType.DOG, am);
        
        itemModel = am.loadModel("Models/Ham/ham.j3o");

        this.attachChild(itemModel);
    }
    
}
