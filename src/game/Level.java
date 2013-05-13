package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import xml_support.LevelSaver;
import xml_support.LevelLoader;

/**
 * třída pro reprezentaci levelu hry
 * @author Pavel
 */
public class Level extends Node{
    
    public String name;
    public String nextLevelName;
    
    //podlaha:
    Geometry floor;
    
    //pole místnostní tvořících level, defacto mapa levelu:
    public Room[] rooms;
    //počátční a cílová místnost:
    public Room start, finish;
    
    //casovy limit po ktery je zlodeje potereba udrzet v levelu v milisekundach:
    public long timeLimit;
    
    //list věcí umístěných v levelu
    public ArrayList<Item> items = new ArrayList<Item>();
    //list překážek umístěných v levelu
    public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    
    //mapa, odpovida poctu dostupnych veci pro vyreseni levelu:
    public Map<ObstacleType, Integer> availableObst = new HashMap<ObstacleType, Integer>();
    
    public AssetManager assetManager;
    
    public Level(int roomsCount){
        rooms = new Room[roomsCount];
        for(int i=0; i<rooms.length; ++i){
            rooms[i] = new Room(new Vector3f(0,0,0),0.0f,0.0f,0,true, assetManager);
        }
    }
    
    /**
     * konstruktor levelu, tady budou určitě eště změny protože level se bude
     * načítat ze souboru
     * @param assetManager 
     */
    public Level(AssetManager assetManager, String name){
        
        this.name = name;
        this.assetManager = assetManager;
        
        load();
        
        /*
         * prepsat tak, aby to vsechno nacteny pridalo na AM
         */
//        rooms = new Room[9];
//        rooms[0] = new Room(new Vector3f(0,0,0),10,10,0,true, assetManager);
//        rooms[1] = new Room(new Vector3f(21,0,0),10,10,1,true, assetManager);
//        rooms[2] = new Room(new Vector3f(42,0,0),10,10,2,true, assetManager);
//        rooms[3] = new Room(new Vector3f(0,0,21),10,10,3,true, assetManager);
//        rooms[4] = new Room(new Vector3f(21,0,21),10,10,4,true, assetManager);
//        rooms[5] = new Room(new Vector3f(42,0,21),10,10,5,true, assetManager);
//        rooms[6] = new Room(new Vector3f(0,0,42),10,10,6,true, assetManager);
//        rooms[7] = new Room(new Vector3f(21,0,42),10,10,7,true, assetManager);
//        rooms[8] = new Room(new Vector3f(42,0,42),10,10,8,true, assetManager);
        
//        addItem(ObstacleType.GLASS, rooms[2]);
//        addObstacle(new Obstacle(assetManager, ObstacleType.GLASS), rooms[7]);
        
//        availableObst.put(ObstacleType.GLASS, 1);
        
//        start = rooms[0];
//        finish = rooms[8];
        
        for(Room r: rooms){
            this.attachChild(r);
        }
        
//        rooms[0].addNeighbour(rooms[1]);
//        
//        rooms[0].addNeighbour(rooms[3]);
//        
//        rooms[2].addNeighbour(rooms[5]);
//        
//        rooms[3].addNeighbour(rooms[4]);
//        
//        rooms[3].addNeighbour(rooms[6]);
//        
//        rooms[4].addNeighbour(rooms[5]);
//        
//        rooms[6].addNeighbour(rooms[7]);
//        
//        rooms[7].addNeighbour(rooms[8]);
    }
    
    public Level(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    /**
     * metoda pro přidání nové překážky do levelu v průběhu hry
     * @param obstacle přidávaná překážka
     * @param to umístění překážky
     */
    public void addObstacle(Obstacle obstacle, Room to){
        obstacles.add(obstacle);
        obstacle.placeObstacle(to);
        to.setObstacle(obstacle);
        this.attachChild(obstacle);
    }
    
    public void addItem(ObstacleType itemType, Room to){
        Item item = new Item(to, itemType, assetManager);
        items.add(item);
        to.setItem(item);
        this.attachChild(item);
    }
    
    public void makeNeighbours(Room r1, Room r2){
        r1.addNeighbour(r2);
    }
    
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
        LevelLoader ll = new LevelLoader(this, name);
        ll.load();
    }
}
