package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Třída reprezentující objekt dveří. Je zděněná od Node, kvůli snadnému zařezení
 * do grafu scény. Jde o o třídu reprezentující objekt dveří,je napsána ve vlastní
 * třídě, kvůli lepší přehlednoti kódu a přístu z objektu Room, jedny dveře jsou
 * vždy sdíleny dvěma místnostmy.
 * @author Pavel Pilař
 */
public class Door extends Node{
    
    /** Vektor udávající pozici dveří. */
    Vector3f position;
    
    /**
     * Konstruktor třídy Door. Vytvoří dvře na danné pozici.
     * @param position pozice na které mají být dveře vytvořeny
     * @param assetManager AssetManager, používaný k načtení materiálu dveří
     */
    public Door(Vector3f position, AssetManager assetManager){
        this.position = position;
        Box b1 = new Box(position, 2f, 2.5f, 2f);
        Geometry door = new Geometry("door", b1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Brown);
        
        door.setMaterial(mat);
        
        this.attachChild(door);
    }
    
}
