/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import game.ObstacleType;
import game.Room;

/**
 *
 * @author Pavel
 */
public class FlashItem extends Item {
    
    Spatial model;
    
    public FlashItem(Room to, AssetManager am){
        super(to, ObstacleType.FLASH, am);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        
        model = am.loadModel("Models/Wrench/wrench.j3o");

        model.setMaterial(mat);
        model.setLocalTranslation(0.0f, 0.5f, 0.0f);
        
        this.attachChild(model);
    }
}
