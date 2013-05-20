package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import java.util.ArrayList;
import java.util.HashMap;

import game.items.Item;
import game.obstacles.Obstacle;

/**
 * Třída pro reprezentaci jedné místnosti muzea. Dědí od Node, kvůli snadnému
 * zařazení do grafu scény. Místntost se generuje při vytvoření, má referenci
 * na všechny sousední místnosti a je v rámci levelu určená indexem.
 * @author Pavel Pilař
 */
public class Room extends Node{
    
    /** ArrayList obsahující referenci na všechny sousední místnosti. */
    public ArrayList<Room> neigbours = new ArrayList<Room>();
    
    /** Nastaveni priznaku zda jde do mistnosti umistit prekazka(ve smyslu uzamčení místnosti). */
    public boolean isAloved;
    
    /** Index místnosti v rámci levelu. */
    public int index;
    
    /** Reference na překážku, pokud v místnosti nějaká je. */
    public Obstacle obstacle;
    
    /** Reference na ppředmět, pokud v místnosti nějaký je. */
    public Item item;
    
    /** Geometrie podlahy místnosti. Slouží k určení místnosti po kliknutí uživatele */
    public Geometry floor;
    
    /** AssetManager pro správu materiálů atd. */
    private AssetManager assetManager;
    
    /** Vektor udávající pozici místnosti. */
    private Vector3f position;
    
    /** Hodnoty udávající rozměry místnosti. */
    private float width, height;
    
    /** Mapa dveří, které místnost obsahuje. V mapě, protože je potřeba určovat
     * se kterou místností jsou dveře sdíleny */
    private HashMap<Room, Door> doors = new HashMap<Room, Door>();
    
    /**
     * Konstruktor nové místnosti. Nastavují se zde všechny důležité atributy. 
     * Také se zde volá generování grafických prvků místnosti dle danných rozměrů.
     * @param position vektor udávající pozici místnosti
     * @param width udává šírky místnosti
     * @param height udává výšku místnosti
     * @param index index místnosti v rámci levelu
     * @param isAloved příznak, zda je místnost uzamčna
     * @param assetManager AssetManager pro správu materiálů, modelů atd.
     */
    public Room(Vector3f position, float width, float height, int index,
            boolean isAloved, AssetManager assetManager){
        this.position = position;
        this.width = width;
        this.height = height;
        this.index = index;
        this.assetManager = assetManager;
        this.isAloved = isAloved;
        generateFloor();
        generateWalls();
    }
    
    /**
     * Vrací pozici místnosti v rámci levelu.
     * @return vektor pozice místnosti
     */
    public Vector3f getPosition(){
        return position;
    }
    
    /**
     * Vrací šířku místnosti.
     * @return šířka
     */
    public float getWidth(){
        return width;
    }
    
    /**
     * Vrací výšku místnosti.
     * @return výška
     */
    public float getHeight(){
        return height;
    }
    
    /**
     * Metoda generující podlahu místnosti podle jejích rozměrů.
     */
    private void generateFloor(){
        Box b = new Box(position, width, 0.2f, height);
        floor = new Geometry("floor", b);
        
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/floor.jpg"));
        if(isAloved){
            floorMaterial.setColor("Color", ColorRGBA.White);
        } else {
            floorMaterial.setColor("Color", ColorRGBA.DarkGray);
        }
        floor.setMaterial(floorMaterial);
        
        this.attachChild(floor);
    }
    
    /**
     * Metoda generující jednotlivé zdi místnosti dle danných rozměrů.
     */
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
    
    /**
     * Metoda generující dveře s předaným sousedem. Pokud již dveře existují u 
     * souseda, pouze se přidají do mapy dveří, jinak se vytvoří nové.
     * @param neighbour reference na sousední místnost
     */
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
    
    /**
     * Vrací referenci na dveře sdílené s danou místností.
     * @param neighbour místnost, se kterou jsou dveře sdíleny.
     * @return reference na příslušné dveře
     */
    public Door getDoor(Room neighbour){
        return doors.get(neighbour);
    }
    
    /**
     * Nastaví místnosti překážku, pokud je to možné.
     * @param obstacle přidávaná překážka
     */
    public void setObstacle(Obstacle obstacle){
        if(this.obstacle == null){
            this.obstacle = obstacle;
        }
    }
    
    /**
     * Smaže překážku z místnosti.
     */
    public void deleteObstacle(){
        obstacle = null;
    }
    
    /**
     * Nastaví danný předmět místnosti, pokud to lze.
     * @param item přidávaný předmět
     */
    public void setItem(Item item){
        if(this.item == null){
            this.item = item;
        }    
    }
    
    /**
     * Smaže předmět, který místnost obsahuje.
     */
    public void deleteItem(){
        item = null;
    }
    
    /**
     * Přiřadí místnosti danného souseda a pokusí se vygenerovat dveře mezi nimy.
     * @param newNeighbour přiřazovaná sousední místnost 
     */
    public void addNeighbour(Room newNeighbour){
        neigbours.add(newNeighbour);
        generateDoors(newNeighbour);
        if(!newNeighbour.neigbours.contains(this)){
            newNeighbour.addNeighbour(this); 
        }
    }
    
    /**
     * Informace zda je místnost prázdná. A lze do ní tedy něco umístit
     * @return pokud je místnost prázdná true, jinak false
     */
    public boolean isEmpty(){
        return (obstacle==null && item==null && isAloved);
    }
    
    //POMOCNÉ METODY PRO EDITOR:
    
    /**
     * Změní barvu podlahy na běžnou místnost.
     */
    public void unsetStart(){
        floor.getMaterial().setColor("Color", ColorRGBA.White);
    }
    
    /**
     * Změní barvu podlahy na startovní.
     */
    public void setStart(){
        floor.getMaterial().setColor("Color", ColorRGBA.Green);
    }
    
    /**
     * Změní barvu podlahy na běžnou místnost.
     */
    public void unsetFinish(){
        floor.getMaterial().setColor("Color", ColorRGBA.White);
    }
    
    /**
     * Změní barvu podlahy na cílovou.
     */
    public void setFinish(){
        floor.getMaterial().setColor("Color", ColorRGBA.Red);
    }
    
    /**
     * Pokud je místnost uzamčená, tak se odemkne a naopak. Zároveň se provede
     * příslušná změna grafiky místnosti (barvy)
     */
    public void toggleAloved(){
        if(isAloved){
            isAloved = false;
            floor.getMaterial().setColor("Color", ColorRGBA.DarkGray);
        } else {
            isAloved = true;
            floor.getMaterial().setColor("Color", ColorRGBA.White);
        }
    }
}
