package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Pavel
 */
public class Thief extends Node {
    
    Spatial model;
    public Room actualPosition;
    Level map;
    
    public Thief(AssetManager am, Level map){
        Box b = new Box(map.start.getPosition(), 1,1,1);
        actualPosition = map.start;
        Geometry geom = new Geometry("box", b);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        
        this.attachChild(geom);
    }
    
    public void update(float tpf){
        //this.move(1*tpf, 0, 2*tpf);
    }
    
    public void setNewPlane(){
        
    }
}
