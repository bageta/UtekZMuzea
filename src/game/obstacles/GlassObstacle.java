package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import game.ObstacleType;

/**
 *
 * @author Pavel
 */
public class GlassObstacle extends Obstacle {
    
    public GlassObstacle(AssetManager am){
        super(am, ObstacleType.GLASS);
        
        Sphere b = new Sphere(15, 15, 1);
        Geometry geom = new Geometry("box", b);
        
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        
        this.attachChild(geom);
    }
}
