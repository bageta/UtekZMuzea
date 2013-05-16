package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import game.ObstacleType;

/**
 *
 * @author Pavel
 */
public class FireObstacle extends Obstacle{
    
    public FireObstacle(AssetManager am){
        super(am, ObstacleType.FIRE);
            
        /** Uses Texture from jme3-test-data library! */
        ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 60);
        Material mat_red = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", am.loadTexture("Effects/Explosion/flame.png"));
        fire.setMaterial(mat_red);
        fire.setImagesX(2); fire.setImagesY(2); // 2x2 texture animation
        fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fire.setStartSize(0.6f);
        fire.setEndSize(0.1f);
        fire.setGravity(0f,0f,0f);
        fire.setLowLife(0.5f);
        fire.setHighLife(3f);
        fire.getParticleInfluencer().setVelocityVariation(0.3f);
        fire.setLocalScale(2.0f);
        this.attachChild(fire);
    }
}
