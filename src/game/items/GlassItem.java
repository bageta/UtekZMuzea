package game.items;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import game.ObstacleType;
import game.Room;

/**
 * Reprezentace věci na odstranění překážky typu sklo. Nahrává model koštěte,
 * který se bude ve hře zobrazovat a rodiči se nastaví typ sklo.
 * @author Pavel Pilař
 */
public class GlassItem extends Item {
    
    Spatial model;
    
    /** Konstruktor věci na odstranění překážky typu sklo. */
    public GlassItem(Room to, AssetManager am){
        super(to, ObstacleType.GLASS, am);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Brown);
        
        model = am.loadModel("Models/Broom/broom.j3o");

        model.setMaterial(mat);
        
        this.attachChild(model);
    }
}
