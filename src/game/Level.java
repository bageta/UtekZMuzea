package game;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.items.*;
import game.obstacles.Obstacle;

import xml_support.LevelSaver;
import xml_support.LevelLoader;

/**
 * Třída pro reprezentaci levelu hry. Obsahuje seznam místností, tvořící level.
 * Informace o rozmístění předmětů, překážek. Informaci o dostupných překážkách.
 * Atributy levelu jako jméno atd. A metody pro prácí s řečenými.
 * @author Pavel Pilař
 */
public class Level extends Node{
    
    /** Jméno levelu. */
    public String name;
    /** Jméno následujícího levelu. */
    public String nextLevelName;
    
//    //podlaha:
//    Geometry floor;
    
    /** Pole místnostní tvořících level, defacto mapa levelu. */
    public Room[] rooms;
    /** Reference na počátční a cílovou místnost. */
    public Room start, finish;
    
    /** Casovy limit po který je zloděje poteřeba udržet v levelu v milisekundach. */
    public long timeLimit;
    
    /** List věcí umístěných v levelu. */
    public ArrayList<Item> items = new ArrayList<Item>();
    /** List překážek umístěných v levelu. */
    public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    
    /** Mapa, odpovídá počtu dostupných věcí pro vyřešení levelu. */
    public Map<ObstacleType, Integer> availableObst = new HashMap<ObstacleType, Integer>();
    
    /** Reference na AssetManager spravující materiály modely atd. */
    public AssetManager assetManager;
    
    /**
     * Konstruktor prázdného levelu. Určeného k zobrazování. Používá se v editoru
     * levelů.
     * @param assetManager assetManeger pro správu materiálů atd. 
     */
    public Level(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    /**
     * Konstruktor levelu, provede načtení levelu předaného jména ze souboru.
     * @param name jméno levelu, který má být načten
     * @param assetManager assetManeger pro správu materiálů atd.  
     */
    public Level(String name, AssetManager assetManager){
        this(assetManager);
        
        this.name = name;
        
        load();
    }
    
    /**
     * Konstruktor levelu s danným pořetem místností, používí se pro testování
     * plánovačů. Takto vytvořený level není určen k zobrazování, nebo použiti
     * ve hře.
     * @param roomsCount počet místností které se mají vytvořit.
     * @param assetManager assetManeger pro správu materiálů atd.
     */
    public Level(int roomsCount, AssetManager assetManager){
        this(assetManager);
        rooms = new Room[roomsCount];
        for(int i=0; i<rooms.length; ++i){
            rooms[i] = new Room(new Vector3f(0,0,0),0.0f,0.0f,0,true, assetManager);
        }
    }
    
    /**
     * Metoda pro přidání nové překážky do levelu v průběhu hry.
     * @param obstacle přidávaná překážka
     * @param to umístění překážky
     */
    public void addObstacle(Obstacle obstacle, Room to){
        obstacles.add(obstacle);
        obstacle.placeObstacle(to);
        to.setObstacle(obstacle);
        this.attachChild(obstacle);
    }
    
    /**
     * Metoda pro přidání věci do levelu. Dostává pouze typ věci, samotná věc,
     * která se přidává se vytvoří uvnitř této metody
     * @param itemType typ přidávané věci
     * @param to reference na místnost, do které se má věc přidat 
     */
    public void addItem(ObstacleType itemType, Room to){
        Item item;
        switch(itemType){
            case DOG:
                item= new DogItem(to, assetManager);
                break;
            case FIRE:
                item= new FireItem(to, assetManager);
                break;
            case FLASH:
                item= new FlashItem(to, assetManager);
                break;
            case GLASS:
                item= new GlassItem(to, assetManager);
                break;
            default:
                item = null;
        }
        items.add(item);
        to.setItem(item);
        this.attachChild(item);
    }
    
    /**
     * Vytvoří z předaných místností sousedy.
     * @param room1 první místnost
     * @param room2 druhá místnost
     */
    public void makeNeighbours(Room room1, Room room2){
        room1.addNeighbour(room2);
    }
    
    /**
     * 
     * @param cameraPosition
     * @param cameraDirection
     * @return 
     */
    public Room getRoom(Vector3f cameraPosition, Vector3f cameraDirection){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cameraPosition, cameraDirection);
        //ray.collideWith(new Plane(), results);
        this.collideWith(ray, results);
        if(results.size() > 0){
            CollisionResult closest = results.getClosestCollision();
            Geometry toCompare = closest.getGeometry();
            for(Room r: rooms){
                if(r.floor.equals(toCompare)){
                    return r;
                }
            }
        }
        return null;
    }
    
    public void save(){
        if(start == null){
            start = rooms[0];
        }
        if(finish == null){
            finish = rooms[rooms.length-1];
        }
        LevelSaver ls = new LevelSaver(this);
        ls.save();
    }
    
    public final void load(){
        this.detachAllChildren();
        items.clear();
        LevelLoader ll = new LevelLoader(this, name);
        ll.load();
        start.floor.getMaterial().setColor("Color", ColorRGBA.Green);
        finish.floor.getMaterial().setColor("Color", ColorRGBA.Red);
        obstacles.clear();
        for(Room r: rooms){
            this.attachChild(r);
        }
    }
}
