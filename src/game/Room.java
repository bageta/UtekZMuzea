package game;

import com.jme3.scene.Node;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * třída pro reprezentaci místnosti 
 * @author Pavel
 */
public class Room extends Node{

    private AssetManager assetManager;
    private Vector3f position;
    private float width, height;
    public ArrayList<Room> neigbours = new ArrayList<Room>();
    public int index;
    public Obstacle obstacle;
    public Item item;
    public Geometry floor;
    //nastaveni priznaku zda jde do mistnosti umistit prekazka(ve smyslu default)
    public boolean isAloved;
    private HashMap<Room, Door> doors = new HashMap<Room, Door>();
    
    
    public Room(Vector3f position, float width, float height, int index, AssetManager assetManager){
        this.position = position;
        this.width = width;
        this.height = height;
        this.index = index;
        this.assetManager = assetManager;
        generateFloor();
        generateWalls();
    }
    
    public Vector3f getPosition(){
        return position;
    }
    
    public float getWidth(){
        return width;
    }
    
    public float getHeight(){
        return height;
    }
    
    private void generateFloor(){
        Box b = new Box(position, width, 0.2f, height);
        floor = new Geometry("floor", b);
        
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/floor.jpg"));
        floor.setMaterial(floorMaterial);
        
        this.attachChild(floor);
    }
    
    private void generateWalls(){
        Material wallMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/wall.jpg"));
        
        Box b1 = new Box(new Vector3f(position.x, position.y+2, position.z-height), width, 3f, 0.5f);
        Geometry wall1 = new Geometry("wall1", b1);
        System.out.println("wall1: " + wall1.getLocalTranslation());
        wall1.setMaterial(wallMaterial);
        
        Geometry wall2 = new Geometry("wall2", b1);
        wall2.setMaterial(wallMaterial);
        wall2.setLocalTranslation(0,0,2*height);
        System.out.println("wall2: " + wall2.getLocalTranslation());
        
        Box b3 = new Box(new Vector3f(position.x-width, position.y+2, position.z), 0.5f, 3f, height);
        Geometry wall3 = new Geometry("wall3", b3);
        System.out.println("wall3: " + wall3.getLocalTranslation());
        wall3.setMaterial(wallMaterial);
        
        Geometry wall4 = new Geometry("wall4", b3);
        wall4.setMaterial(wallMaterial);
        wall4.setLocalTranslation(2*width, 0, 0);
        System.out.println("wall4: " + wall4.getLocalTranslation());
        
        this.attachChild(wall1);
        this.attachChild(wall2);
        this.attachChild(wall3);
        this.attachChild(wall4);
    }
    
    private void generateDoors(Room neighbour){
        Door door = neighbour.getDoor(this);
        if(door==null){
            door = new Door(new Vector3f((position.x+neighbour.getPosition().x)/2,
                    position.y+2,
                    ((position.z+neighbour.getPosition().z)/2)), assetManager);
            doors.put(neighbour, door);
        } else {
            doors.put(neighbour,door);
        }
        this.attachChild(door);
    }
    
    public Door getDoor(Room neighbour){
        return doors.get(neighbour);
    }
    
    public void setObstacle(Obstacle obstacle){
        if(this.obstacle == null){
            this.obstacle = obstacle;
        }
    }
    
    public void setItem(Item item){
        if(this.item == null){
            this.item = item;
        }    
    }
    
    public void addNeighbour(Room newNeighbour){
        neigbours.add(newNeighbour);
        generateDoors(newNeighbour);
        if(!newNeighbour.neigbours.contains(this)){
            newNeighbour.addNeighbour(this); 
        }
    }
    
    public boolean hasObstacle(){
        return !(obstacle==null);
    }
    
    public boolean isEmpty(){
        return (obstacle==null && item==null && isAloved);
    }
}
