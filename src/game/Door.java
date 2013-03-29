package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Pavel
 */
public class Door extends Node {
    
    Vector3f position;
    
    public Door(Vector3f position, AssetManager am){
        this.position = position;
        Box b1 = new Box(position, 2f, 2.5f, 2f);
        Geometry door = new Geometry("door", b1);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Brown);
        
        door.setMaterial(mat);
        
        this.attachChild(door);
    }
    
}
