package game.items;

import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.asset.AssetManager;
import game.ObstacleType;
import game.Room;

/**
 * třída pro reprezentaci věcí, které může zloděj použít k odstranění překážek
 * @author Pavel
 */
public abstract class Item extends Node{

    /**
     * typ překážky
     */
    public ObstacleType type;
    
    /**
     * reference na místnost, ve které je věc aktuálně umístěná
     */
    public Room actualPosition;
    
    //model pro zobrazení překážky:
    public Geometry model;
    
    private Node parent;
    
    /**
     * konstruktor, který vytvoří překážku daného typu v dané místnosti
     * @param startPossiotion místnost, kde má být překážka vytvořena
     * @param type typ překážky
     */
    public Item(Room startPossiotion, ObstacleType type, AssetManager am){
        this.type = type;
        actualPosition = startPossiotion;
        
        this.setLocalTranslation(startPossiotion.getPosition());
    }
    
    public void update(float tpf){
        this.rotate(0.0f, 1.0f*tpf, 0.f);
    }
    
}