package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import game.ObstacleType;
import game.Room;

/**
 * Reprezentace věci na odstranění překážky typu oheň. Nahrává model hasícího
 * přístroje, který se bude ve hře zobrazovat a rodiči se nastaví typ oheň.
 * @author Pavel Pilař
 */
public class FireItem extends Item {
    
    Spatial model;
    
    /** Konstruktor věci na odstranění překážky typu oheň. */
    public FireItem(Room to, AssetManager am){
        super(to, ObstacleType.FIRE, am);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        model = am.loadModel("Models/Extincteur/fire.j3o");

        model.setMaterial(mat);
        model.setLocalScale(0.04f);
        model.setLocalTranslation(0.0f, 1.0f, 0.0f);
        
        this.attachChild(model);
    }
    
}
