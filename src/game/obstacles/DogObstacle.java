package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import game.ObstacleType;

/**
 *
 * @author Pavel
 */
public class DogObstacle extends Obstacle {
    
    Spatial model;
    
    public DogObstacle(AssetManager am){
        super(am, ObstacleType.DOG);
        
        model = am.loadModel("Models/Wolf/wolf.obj");
        model.setLocalScale(0.4f);
        this.attachChild(model);
    }
    
}
