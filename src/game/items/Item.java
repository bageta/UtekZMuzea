package game.items;

import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
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
        
//        Cylinder b = new Cylinder(10, 10, 1, 3);
//        model = new Geometry("cylinder", b);
//        
//        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
//        switch(type){
//            case DOG:
//                mat.setColor("Color", ColorRGBA.Brown);
//                break;
//            case GLASS:
//                mat.setColor("Color", ColorRGBA.White);
//                break;
//            case FIRE:
//                mat.setColor("Color", ColorRGBA.Red);
//                break;
//            case FLASH:
//                mat.setColor("Color", ColorRGBA.Yellow);
//                break;
//        }
//        model.setMaterial(mat);
//        
//        this.attachChild(model);
        this.setLocalTranslation(startPossiotion.getPosition());
        //TODO: load modelu podle typu
    }
    
}