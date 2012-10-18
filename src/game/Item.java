package game;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * třída pro reprezentaci věcí, které může zloděj použít k odstranění překážek
 * @author Pavel
 */
public class Item extends Node{

    /**
     * typ překážky
     */
    public ObstacleType type;
    /**
     * reference na místnost, ve které je věc aktuálně umístěná
     */
    public Room actualPosition;
    
    //model pro zobrazení překážky:
    Spatial model;
    
    /**
     * konstruktor, který vytvoří překážku daného typu v dané místnosti
     * @param startPossiotion místnost, kde má být překážka vytvořena
     * @param type typ překážky
     */
    public Item(Room startPossiotion, ObstacleType type){
        this.type = type;
        actualPosition = startPossiotion;
        //TODO: load modelu podle typu
    }
}