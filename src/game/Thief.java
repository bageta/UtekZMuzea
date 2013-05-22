package game;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import game.items.Item;
import game.obstacles.Obstacle;

import helper.Position;

import planner.ActionType;
import planner.ThiefAction;

/**
 * Třída reprezentující zloděje. Obsahuje jeho model pro zobrazení, aktualní
 * pozici a plán, který se právě vykonává. Zajišťuje se zde také provádění
 * jednotlivých akcí plánu. Dědí od Node kvůli přidání do grafu scény.
 * @author Pavel Pilař
 */
public class Thief extends Node {
    
    /** Zleděj si udržuje informaci o místnosti ve které aktuálně je. */
    private Room actualPosition;
    
    /** Zloděj má referenci na mapu, kvůli navigování v levelu. */
    private Level map;
    
    /** Grafický model zloděje. */
    private Spatial model;
    
    /** Prvky pro ovládání animací modelu. */
    private AnimControl control;
    private AnimChannel channel;
    
    /** Pole akcí, které má zloděj provést. */
    private ThiefAction[] actions;
    /** Kvůli zpřehlednění kódu, si držíme referenci na aktuální akci. */
    private ThiefAction actualAction;
    /** Pomatujeme si index prováděné akce, kvůli získání další. */
    private int actualActionIndex;
    /** Pamatujeme si v jakém stavu je aktuálně provádění akce. */
    private State actualState;
    
    /** Pozice dalšího cíle na který má zloděj jít. */
    private Vector3f target;
    /** Konstanta udávající rychlost pohybu zloděje. */
    private final float MOVEMENT_SPEED = 5.0f;
    
    /** Informace, zda má zloděj jen přejít do další místnoti, nebo vykonat
     * samotnou akci. */
    private boolean door;
    
    /** Zloděj si udržuje referenci na věc kterou nese. */
    private Item carrying;
    
    /**
     * Konstruktor zloděje. Nastaví se reference na level. Načte se grafický
     * 3D model a inicializují se proměnné.
     * @param assetManager AssetManager služící ke správě modelů a textur
     * @param map reference na aktuální level
     */
    public Thief(AssetManager assetManager, Level map){
        this.map = map;
        
        actualPosition = map.start;
        
        this.setLocalTranslation(map.start.getPosition());
        model = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
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
    
    /**
     * Update smyčka zloděje, zde dochází k samotnému zpracování akcí, podle jejich
     * typu a podle toho v jakém stavu se aktuáně provádění nachází.
     * @param tpf doba vyrenderování jednoho snímku
     */
    public void update(float tpf){
        // pokud zloděj něco nese, chceme to aktualizovat
        if(carrying != null){
            carrying.update(tpf);
        }
        // pokud má zloděj hotovo
        if(actualState == State.DONE){
            // a existuje další akce
            if(hasNextAction()){
                //nastaví se jako aktuální
                actualAction = getNextAction();
                door = true;
                //nastaví se první cíl podle typu akce:
                if(actualAction.actionType == ActionType.MOVE
                        ||actualAction.actionType == ActionType.PICK
                        ||actualAction.actionType == ActionType.USE){
                    target = map.rooms[actualAction.from].getDoor(map.rooms[actualAction.to]).position;
                }
                if(actualAction.actionType == ActionType.PUT){
                    target = map.rooms[actualAction.from].getPosition();
                }
                //a akce se začne vykonávat
                actualState = State.INPROGRESS;
            } else{
                actualState = State.WAIT;
            }
        //pokud se má vykonávat akce, rozhodne se podle typu a posune se směrem k cíli
        //případně se nastaví další cíl, dokud není akce dokončena
        } else if(actualState == State.INPROGRESS){
            if(actualAction.actionType == ActionType.MOVE){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target.x, 0.0f, target.z);
                    this.actualPosition = map.rooms[actualAction.to];
                    actualState = State.DONE;
                }
            }
            if(actualAction.actionType == ActionType.PICK){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target.x, 0.0f, target.z);
                    this.actualPosition = map.rooms[actualAction.to];
                    if(door){
                        target = map.rooms[actualAction.to].getPosition();
                        door = false;
                    } else {
                        carrying = map.rooms[actualAction.to].item;
                        this.attachChild(carrying);
                        carrying.setLocalTranslation(0,4.0f,0);
                        actualPosition.deleteItem();
                        actualState = State.DONE;
                    }
                }
            }
            if(actualAction.actionType == ActionType.PUT){
                moveThief(tpf);
                if(Position.isClose(getLocalTranslation(), target, 0.1f)){
                    this.setLocalTranslation(target.x, 0.0f, target.z);
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
                    this.setLocalTranslation(target.x, 0.0f, target.z);
                    this.actualPosition = map.rooms[actualAction.to];
                    if(door){
                        target = map.rooms[actualAction.to].getPosition();
                        door = false;
                    } else {
                        map.detachChild(carrying);
                        Obstacle tmp = map.rooms[actualPosition.index].obstacle;
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
    
    /**
     * Nastavení nového plánu zloději. Zloděj jej začíná vykonávat od začátku.
     * @param plane nový plán pro zloděje 
     */
    public void setNewPlane(ThiefAction[] plane){
        actualState = State.WAIT;
        actualActionIndex = 0;
        actions = plane;
        if(actions != null){
            actualState = State.DONE;
        }
        
    }
    
    /**
     * Pomocná metoda, zjišťujeme, zda existuje v plánu další akce.
     * @return true pokud exituje další akce v plánu, jinak false
     */
    private boolean hasNextAction(){
        if(actualActionIndex < actions.length){
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * Vrátí další akci k provedení v pořadí, a zvýší index, aktuální akce, aby
     * mohl opět příště dát další akci.
     * @return Další akce, která se má provést
     */
    private ThiefAction getNextAction(){
        ThiefAction result = actions[actualActionIndex];
        ++actualActionIndex;
        return result;
    }
    
    /**
     * Zajišťuje pohym modelu zloděje po levelu, řídí se nastaveným cílem. Zároveň
     * se počítá rotace, tak aby zloděj chodil čelem ke směru pohybu.
     * @param tpf čas vyrenderování snímku
     */
    public void moveThief(float tpf){
        Vector3f direction = new Vector3f(target.x-this.getLocalTranslation().x,
                0.0f,
                target.z - this.getLocalTranslation().z);
        
        direction = direction.normalize();
        this.move(direction.x*tpf*MOVEMENT_SPEED,
            0.0f,
            direction.z*tpf*MOVEMENT_SPEED);
        
        Vector3f start = new Vector3f(0.0f, 0.0f, 1.0f);
        
        float sign = Math.signum(direction.x);
        
        this.getLocalRotation().fromAngleAxis(sign * direction.angleBetween(start),
                Vector3f.UNIT_Y);
    }
    
    /**
     * Pomocná metoda pro nastavení animace zloděje.
     * @param name 
     */
    public void setAnimation(String name){
        channel.setAnim(name);
    }
    
    /**
     * Getter pro věc, kterou zloděj nese.
     * @return Věc kterou zloděj nese
     */
    public Item getCarrying(){
        return this.carrying;
    }
    
    /**
     * getter pro aktuální pozici zloděje.
     * @return místnost, ve které se zloděj nachází
     */
    public Room getActualPosition(){
        return this.actualPosition;
    }
    
    /**
     * Pomocný enum pro repzerentaci stavu provádění akce.
     */
    private enum State{
        INPROGRESS, DONE, WAIT;
    }
}
