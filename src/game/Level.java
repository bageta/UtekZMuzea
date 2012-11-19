package game;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * třída pro reprezentaci levelu hry
 * @author Pavel
 */
public class Level extends Node{
    
    //podlaha:
    Geometry floor;
    
    //pole místnostní tvořících level, defacto mapa levelu:
    public Room[] rooms;
    //počátční a cílová místnost:
    public Room start, finish;
    
    //list věcí umístěných v levelu
    public ArrayList<Item> items = new ArrayList<Item>();
    //list překážek umístěných v levelu
    public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    
    AssetManager assetManager;
    
    public Level(){}
    
    /**
     * konstruktor levelu, tady budou určitě eště změny protože level se bude
     * načítat ze souboru
     * @param assetManager 
     */
    public Level(AssetManager assetManager){
        
        this.assetManager = assetManager;
        
        rooms = new Room[4];
        rooms[0] = new Room(new Vector3f(0,0,0),10,10,0);
        rooms[1] = new Room(new Vector3f(21,0,0),10,10,1);
        rooms[2] = new Room(new Vector3f(0,0,21),10,10,2);
        rooms[3] = new Room(new Vector3f(21,0,21),10,10,3);
        
        
        addObstacle(new Obstacle(assetManager, ObstacleType.GLASS), rooms[1]);
        addItem(ObstacleType.GLASS, rooms[2]);
        
        start = rooms[0];
        finish = rooms[3];
        
        for(Room r: rooms){
            r.setAssetManager(assetManager);
            this.attachChild(r.generateFloor());
            Geometry[] walls = r.generateWalls();
            for(Geometry wall : walls){
                this.attachChild(wall);
            }
//            Geometry[] doors = r.generateDoors();
//            for(Geometry door : doors){
//                this.attachChild(door);
//            }
        }
        
        rooms[0].addNeighbour(rooms[1]);
        this.attachChild(rooms[0].generateDoors(rooms[1]));
        
        rooms[0].addNeighbour(rooms[2]);
        this.attachChild(rooms[0].generateDoors(rooms[2]));
        
        rooms[3].addNeighbour(rooms[1]);
        this.attachChild(rooms[3].generateDoors(rooms[1]));
        
        //rooms[3].addNeighbour(rooms[2]);
        //this.attachChild(rooms[0].generateDoors(rooms[1]));
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
    
}
