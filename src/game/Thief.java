package game;

import game.items.Item;
import game.obstacles.Obstacle;
import planner.ThiefAction;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import helper.Position;
import planner.ActionType;

/**
 * trida representujici zlodeje, obsahuje jeho model pro zobrazeni, aktualni
 * pozici a plan, ktery se prave vykonava
 * @author Pavel Pilar
 */
public class Thief extends Node {
    
    Spatial model;
    public Room actualPosition;
    Level map;
    private ThiefAction[] actions;
    private ThiefAction actualAction;
    private int actualActionIndex;
    private State actualState;
    int iterations = 0;
    public game.items.Item carrying;
    
    private AnimControl control;
    private AnimChannel channel;
    
    private Vector3f target;
    private final float MOVEMENT_SPEED = 5.0f;
    
    private boolean door;
    
    public Thief(AssetManager am, Level map){
        this.map = map;
        
        actualPosition = map.start;
        
        this.setLocalTranslation(map.start.getPosition());
        model = am.loadModel("Models/Oto/Oto.mesh.xml");
        model.setLocalScale(0.4f);
        model.setLocalTranslation(0.0f, 2.0f, 0.0f);
        
        actualAction = new ThiefAction(ActionType.MOVE, 0, 0);
        actions = new ThiefAction[1];
        actualActionIndex = 0;
        actualState = State.WAIT;
        
        control = model.getControl(AnimControl.class);
        channel = control.createChannel();
        channel.setLoopMode(LoopMode.Loop);
        channel.setSpeed(1.0f);
        channel.setAnim("Walk");
        
        target = Vector3f.ZERO;
        
        this.attachChild(model);
    }
    
    public void update(float tpf){
        if(carrying != null){
            carrying.update(tpf);
        }
        if(actualState == State.DONE){
            if(hasNextAction()){
                System.out.println(actualActionIndex);
                actualAction = getNextAction();
                door = true;
                if(actualAction.actionType == ActionType.MOVE || actualAction.actionType == ActionType.PICK){
                    target = map.rooms[actualAction.from].getDoor(map.rooms[actualAction.to]).position;
                }
                if(actualAction.actionType == ActionType.PUT){
                    target = map.rooms[actualAction.from].getPosition();
                }
                if(actualAction.actionType == ActionType.USE){
                    target = map.rooms[actualAction.from].getDoor(map.rooms[actualAction.to]).position;
                }
                actualState = State.INPROGRESS;
            } else{
                //NEMAM CO DELAT, ASI BY SE TO NEMELO STAVAT ;D
            }
        } else if(actualState == State.INPROGRESS){
            if(actualAction.actionType == ActionType.MOVE){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target);
                    this.actualPosition = map.rooms[actualAction.to];
                    actualState = State.DONE;
                }
            }
            if(actualAction.actionType == ActionType.PICK){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target);
                    this.actualPosition = map.rooms[actualAction.to];
                    if(door){
                        target = map.rooms[actualAction.to].getPosition();
                        door = false;
                    } else {
                        carrying = map.rooms[actualAction.to].item;
                        //carrying.actualPosition = actualPosition;
                        this.attachChild(carrying);
                        //Vector3f pos = this.getLocalTranslation();
                        carrying.setLocalTranslation(0,4.0f,0);
                        //prehrat pick up animaci
                        actualPosition.deleteItem();
                        actualState = State.DONE;
                    }
                }
            }
            if(actualAction.actionType == ActionType.PUT){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target);
                    if(door){
                        this.detachChild(carrying);
                        map.rooms[actualAction.from].setItem(carrying);
                        carrying.setLocalTranslation(map.rooms[actualAction.from].getPosition());
                        map.attachChild(carrying);
                        carrying = null;
                        target = map.rooms[actualAction.from].getDoor(map.rooms[actualAction.to]).position;
                        door = false;
                    } else {
                        this.actualPosition = map.rooms[actualAction.to];
                        actualState = State.DONE;
                    }
                }
            }
            if(actualAction.actionType == ActionType.USE){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target);
                    this.actualPosition = map.rooms[actualAction.to];
                    //animace pro pouyiti itemu
                    if(door){
                        target = map.rooms[actualAction.to].getPosition();
                        door = false;
                    } else {
                        map.detachChild(carrying);
                        Obstacle tmp = map.rooms[actualPosition.index].obstacle;
                        //map.detachChild(map.rooms[actualAction.to].obstacle);
                        map.detachChild(tmp);
                        actualPosition.deleteObstacle();
                        map.obstacles.remove(tmp);
                        this.detachChild(carrying);
                        carrying = null;
                        actualState = State.DONE;
                    }
                }
            }
        }
    }
    
    public void setNewPlane(ThiefAction[] plane){
        actualState = State.WAIT;
        actualActionIndex = 0;
        actions = plane;
        if(actions != null){
            actualState = State.DONE;
        }
        
    }
    
    private boolean hasNextAction(){
        if(actualActionIndex < actions.length){
            return true;
        }
        else{
            return false;
        }
    }
    
    private ThiefAction getNextAction(){
        ThiefAction result = actions[actualActionIndex];
        ++actualActionIndex;
        return result;
    }
    
    private enum State{
        INPROGRESS, DONE, WAIT;
    }
    
    public void moveThief(float tpf){
        Vector3f direction = new Vector3f(target.x-this.getLocalTranslation().x,
                target.y - this.getLocalTranslation().y,
                target.z - this.getLocalTranslation().z);
        
        direction = direction.normalize();
        this.move(direction.x*tpf*MOVEMENT_SPEED,
            direction.y*tpf*MOVEMENT_SPEED,
            direction.z*tpf*MOVEMENT_SPEED);
        
        Vector3f start = new Vector3f(0.0f, 0.0f, 1.0f);
        
        float sign = Math.signum(direction.x);
        
        this.getLocalRotation().fromAngleAxis(sign * direction.angleBetween(start),
                Vector3f.UNIT_Y);
    }
    
    public void setAnimation(String name){
        channel.setAnim(name);
    }
}
