package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import game.ObstacleType;
import game.Room;

/**
 *
 * @author Pavel Pilař
 */
public class DogItem extends Item {
    
    Spatial model;
    
    public DogItem(Room to, AssetManager am){
        super(to, ObstacleType.DOG, am);
        
        model = am.loadModel("Models/Ham/ham.j3o");

        this.attachChild(model);
    }
    
}
