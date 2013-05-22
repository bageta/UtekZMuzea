package game.items;

import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.asset.AssetManager;

import game.ObstacleType;
import game.Room;

/**
 * Třída pro reprezentaci věcí, které může zloděj použít k odstranění překážek.
 * Třída je abstraktní, slouží jako předek pro věci s určeným typem.
 * @author Pavel Pilař
 */
public abstract class Item extends Node{

    /** typ překážky */
    public ObstacleType type;
    
    /** reference na místnost, ve které je věc aktuálně umístěná */
    public Room actualPosition;
    
    /** model pro zobrazení překážky: */
    public Geometry model;
    
    /**
     * Konstruktor, který vytvoří překážku daného typu v dané místnosti.
     * @param startPossiotion místnost, kde má být překážka vytvořena
     * @param type typ překážky
     */
    public Item(Room startPossiotion, ObstacleType type, AssetManager am){
        this.type = type;
        actualPosition = startPossiotion;
        
        this.setLocalTranslation(startPossiotion.getPosition());
    }
    
    /**
     * Update smyčka věci. Věc rotuje kolem své osy.
     * @param tpf čas vyrenderování snímku.
     */
    public void update(float tpf){
        this.rotate(0.0f, 1.0f*tpf, 0.f);
    }
    
}