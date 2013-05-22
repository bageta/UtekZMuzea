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
public class FireItem extends Item {
    
    Spatial model;
    
    public FireItem(Room to, AssetManager am){
        super(to, ObstacleType.FIRE, am);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        model = am.loadModel("Models/Extincteur/fire.j3o");

        model.setMaterial(mat);
        model.setLocalScale(0.04f);
        model.setLocalTranslation(0.0f, 1.0f, 0.0f);
        
        this.attachChild(model);
    }
    
}
