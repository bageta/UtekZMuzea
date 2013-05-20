package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import game.ObstacleType;
import game.Room;

/**
 *
 * @author Pavel
 */
public class DogItem extends Item {
    
    Spatial model;
    
    public DogItem(Room to, AssetManager am){
        super(to, ObstacleType.DOG, am);
        
        model = am.loadModel("Models/Ham/ham.j3o");

        this.attachChild(model);
    }
    
}
