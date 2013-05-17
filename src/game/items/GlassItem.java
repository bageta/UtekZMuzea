package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import game.ObstacleType;
import game.Room;

/**
 *
 * @author Pavel Pilar
 */
public class GlassItem extends Item {
    
    public GlassItem(Room to, AssetManager am){
        super(to, ObstacleType.GLASS, am);
        
        Cylinder b = new Cylinder(10, 10, 1, 3);
        model = new Geometry("cylinder", b);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        model.setMaterial(mat);
        
        this.attachChild(model);
    }
}
