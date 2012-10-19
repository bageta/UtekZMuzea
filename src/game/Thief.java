package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * trida representujici zlodeje, obsahuje jeho model pro zobrazeni, aktualni
 * pozici a plan, ktery se prave vykonava
 * @author Pavel Pilar
 */
public class Thief extends Node {
    
    Spatial model;
    public Room actualPosition;
    Level map;
    private String[] actions;
    private Action actualAction;
    private int actualActionIndex;
    private State actualState;
    
    public Thief(AssetManager am, Level map){
        Box b = new Box(map.start.getPosition(), 1,1,1);
        actualPosition = map.start;
        Geometry geom = new Geometry("box", b);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        
        actions = new String[1];
        actualActionIndex = 2;
        actualState = State.WAIT;
        actualAction = new Action("init", 0, 0);
        
        this.attachChild(geom);
    }
    
    public void update(float tpf){
        Vector3f target = Vector3f.ZERO;
        if(actualState == State.DONE){
            if(hasNextAction()){
                System.out.println(actualActionIndex);
                actualAction = getNextAction();
                if(actualAction.name.equals("move")){
                    target = map.rooms[actualAction.to].getPosition();
                }
                actualState = State.INPROGRESS;
            } else{
                //NEMAM CO DELAT, ASI BY SE TO NEMELO STAVAT ;D
            }
        } else if(actualState == State.INPROGRESS){
            this.move(target);
            try{
                wait(500);
            } catch(Exception e){
                System.err.println(e);
            }
            actualState = State.DONE;
        }
    }
    
    public void setNewPlane(String plane){
        actualActionIndex = 2;
        actions = plane.split(" ");
        System.out.println("ACTIONS: -------------------------------------------");
        for(String s:actions){
            System.out.println(s);
        }
        System.out.println("----------------------------------------------------");
        actualState = State.DONE;
    }
    
    private boolean hasNextAction(){
        //return actualActionIndex+3 < actions.length? true:false;
        if(actualActionIndex+2< actions.length){
            return true;
        } else {
            return false;
        }
    }
    
    private Action getNextAction(){
        Action result = new Action(actions[actualActionIndex],
                Integer.getInteger(actions[actualActionIndex+1]),
                Integer.getInteger(actions[actualActionIndex+2]));
        actualActionIndex +=3;
        return result;
    }
    
    private enum State{
        INPROGRESS, DONE, WAIT;
    }
}
