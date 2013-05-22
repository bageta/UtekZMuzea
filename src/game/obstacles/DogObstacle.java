package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

import game.ObstacleType;

/**
 * Reprezentace překážky typu pes. Nahrává model psa,
 * který se bude ve hře zobrazovat a rodiči se nastaví typ sklo.
 * @author Pavel Pilař
 */
public class DogObstacle extends Obstacle {
    
    Spatial model;
    
    /** Konstruktor překážky typu pes. */
    public DogObstacle(AssetManager am){
        super(am, ObstacleType.DOG);
        
        model = am.loadModel("Models/Wolf/wolf.obj");
        model.setLocalScale(0.4f);
        this.attachChild(model);
    }
    
}
