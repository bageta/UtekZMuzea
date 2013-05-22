package game.obstacles;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import game.ObstacleType;

/**
 * Reprezentace překážky typu elektřina. Vytváří částicový efekt elektrického
 * výboje, který se bude ve hře zobrazovat a rodiči se nastaví typ elektřina.
 * @author Pavel Pilař
 */
public class FlashObstacle extends Obstacle {
    
    /** Konstruktor překážky typu elektřina. */
    public FlashObstacle(AssetManager am){
        super(am, ObstacleType.FLASH);
        
        /** Uses Texture from jme3-test-data library! */
        ParticleEmitter flash = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 24);
        Material mat_red = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", am.loadTexture("Effects/Explosion/flash.png"));
        flash.setMaterial(mat_red);
        flash.setSelectRandomImage(true);
        flash.setImagesX(2); flash.setImagesY(2);
        flash.setEndColor(  new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        flash.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f));
        flash.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 5, 0));
        flash.setShape(new EmitterSphereShape(Vector3f.ZERO,0.05f));
        flash.setStartSize(0.1f);
        flash.setEndSize(3.0f);
        flash.setGravity(0f,0f,0f);
        flash.setLowLife(0.2f);
        flash.setHighLife(0.2f);
        flash.getParticleInfluencer().setVelocityVariation(1.0f);
        flash.setLocalTranslation(new Vector3f(0.0f, 1.5f, 0.0f));
        this.attachChild(flash);
    }
    
}
