package game;


import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author Pavel
 */
public class Level extends Node{
    
    Geometry floor;
    Room[] rooms;
    Room start, finish;
    
    public Level(AssetManager assetManager){
        
        rooms = new Room[3];
        rooms[0] = new Room(new Vector3f(0,0,0),10,8);
        rooms[1] = new Room(new Vector3f(14,0,0),3,20);
        rooms[2] = new Room(new Vector3f(0,0,17),8,8);
        
        start = rooms[0];
        finish = rooms[2];
        
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
    }
    
}
