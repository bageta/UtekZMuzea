package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import game.ObstacleType;

/**
 * Reprezentace překážky typu sklo. Nahrává model skleněných střepů,
 * který se bude ve hře zobrazovat a rodiči se nastaví typ sklo.
 * @author Pavel Pilař
 */
public class GlassObstacle extends Obstacle {
    
    Spatial model;
    
    /** Konstruktor překážky typu sklo. */
    public GlassObstacle(AssetManager am){
        super(am, ObstacleType.GLASS);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.5f, 1.0f, 1.0f, 1.0f));
        
        model = am.loadModel("Models/Glass/glass.j3o");

        model.setMaterial(mat);
        
        this.attachChild(model);
    }
}
