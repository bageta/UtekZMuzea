package game;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 * třída pro reprezentaci překážek
 * @author Pavel
 */
public class Obstacle extends Node {
    
    //aktuální poloha překážky:
    private Room actualPosition;
    //model pro zobrazení:
    Spatial model;
    
    //typ překážky:
    public ObstacleType type;
    
    /**
     * kostruktor pro vytvoření nové překážky daného typu
     * @param type typ překážky
     */
    public Obstacle(AssetManager am, ObstacleType type){
        this.type = type;
        
        Sphere b = new Sphere(15, 15, 1);
        Geometry geom = new Geometry("box", b);
        
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        switch(type){
            case DOG:
                mat.setColor("Color", ColorRGBA.Brown);
                break;
            case GLASS:
                mat.setColor("Color", ColorRGBA.White);
                break;
            case FIRE:
                mat.setColor("Color", ColorRGBA.Red);
                break;
            case FLASH:
                mat.setColor("Color", ColorRGBA.Yellow);
                break;
        }
        geom.setMaterial(mat);
        
        this.attachChild(geom);
        //load model
    }
    
    /**
     * umístění překážky do dané místnosti
     * @param position místnost kam se překážka umístí
     */
    public void placeObstacle(Room position){
        actualPosition = position;
        this.setLocalTranslation(position.getPosition());
    }
    
    /**
     * vrací aktuální pozici
     * @return 
     */
    public Room getPosition(){
        return actualPosition;
    }
    
    /**
     * vrací typ překážky jako číslo
     * @return typ překážky jako číslo
     */
    public int getTypeOrdinal(){
        int result = 0;
        if(type == ObstacleType.DOG){
            result = ObstacleType.DOG.ordinal();
        } else if(type == ObstacleType.GLASS){
            result = ObstacleType.GLASS.ordinal();
        }
        return result;
    }
}