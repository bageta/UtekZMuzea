package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import helper.Position;

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
    int iterations = 0;
    
    private Vector3f target;
    private final float MOVEMENT_SPEED = 3.0f;
    
    public Thief(AssetManager am, Level map){
        this.map = map;
        
        Box b = new Box(map.start.getPosition(), 1,1,1);
        actualPosition = map.start;
        Geometry geom = new Geometry("box", b);
        
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        
        actions = new String[1];
        actualActionIndex = 1;
        actualState = State.WAIT;
        actualAction = new Action("init", 0, 0);
        
        target = Vector3f.ZERO;
        
        this.attachChild(geom);
    }
    
    public void update(float tpf){
        if(actualState == State.DONE){
            if(hasNextAction()){
                System.out.println(actualActionIndex);
                actualAction = getNextAction();
                if(actualAction.name.equals("move")){
                    System.out.println("Actual action to: " + actualAction.to);
                    System.out.println("target: " + map.rooms[actualAction.to]);
                    target = map.rooms[actualAction.to].getPosition();
                    System.out.println("target position: " + target);
                }
                actualState = State.INPROGRESS;
            } else{
                //NEMAM CO DELAT, ASI BY SE TO NEMELO STAVAT ;D
            }
        } else if(actualState == State.INPROGRESS){
            System.out.println(this.getLocalTranslation());
            Vector3f direction = new Vector3f(target.x-this.getLocalTranslation().x,
                    target.y - this.getLocalTranslation().y,
                    target.z - this.getLocalTranslation().z);
            System.out.println("target: " + target);
            System.out.println("direction: " + direction);
            
            direction = direction.normalize();
            this.move(direction.x*tpf*MOVEMENT_SPEED,
                    direction.y*tpf*MOVEMENT_SPEED,
                    direction.z*tpf*MOVEMENT_SPEED);
            if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                this.setLocalTranslation(target);
                actualState = State.DONE;
            }
        }
    }
    
    public void setNewPlane(String plane){
        actualActionIndex = 1;
        actions = plane.split("[ \n]+");
        System.out.println("ACTIONS: -------------------------------------------");
        for(int i=0; i< actions.length; ++i){
            if(actions[i].charAt(0) == '('){
                actions[i] = actions[i].substring(1);
            }
            if(actions[i].charAt(actions[i].length()-1) == ')'){
                actions[i] = actions[i].substring(0, actions[i].length()-1);
            }
            System.out.println(actions[i]);
        }
//        for(String s:actions){
//            if(s.charAt(0) == '('){
//                s = s.substring(1);
//            }
//            if(s.charAt(s.length()-1) == ')'){
//                s = s.substring(0, s.length()-1);
//            }
//            System.out.println(s);
//        }
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
                Integer.parseInt(actions[actualActionIndex+1]),
                Integer.parseInt(actions[actualActionIndex+2]));
        actualActionIndex +=4;
        return result;
    }
    
    private enum State{
        INPROGRESS, DONE, WAIT;
    }
}
