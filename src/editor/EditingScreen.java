package editor;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import game.InGameCamera;
import game.Item;
import game.Level;
import game.ObstacleType;
import game.Room;
import java.util.ArrayList;

/**
 *
 * @author Pavel
 */
public class EditingScreen extends AbstractAppState implements ScreenController {
    
    private final InGameCamera camera;
    
    private Editor app;
    private Node guiNode;
    private Node rootNode;
    private Node localGuiNode = new Node();
    private Node localRootNode = new Node();
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager stateManager;
    
    private Nifty nifty;
    private Screen screen;
    
    private Level editedLevel;
    
    private ActionType actionType= ActionType.NONE;
    private int index;
    private ArrayList<Room> newRooms = new ArrayList<Room>();
    
    private Room selectedRoom;
    
    public EditingScreen(SimpleApplication app){
        this.app = (Editor)app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
        this.assetManager = app.getAssetManager();
        camera = new InGameCamera(app.getCamera(), rootNode);
    }
    
    public void setEditedLevel(){
        editedLevel = new Level(assetManager);
        index = 0;
    }
    
    public void setEditedLevel(String levelPath){
        editedLevel = new Level(assetManager, levelPath);
        index = editedLevel.rooms.length;
    }
    
    @Override public void onEndScreen(){}
    
    @Override public void onStartScreen(){}
    
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
        nifty.registerScreenController(this);
    }
    
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        camera.registerWithInput(inputManager);
        camera.setCenter(new Vector3f(20, 20, 20));
        
        inputManager.setCursorVisible(true);
        inputManager.addMapping("mouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, new String[]{"mouseClick"});
        
        localRootNode.attachChild(editedLevel);
    }
    
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    public void exitToMenu(String screen){
        nifty.gotoScreen(screen);
        stateManager.detach(this);
        stateManager.attach(app.mainScreen);
    }
    
    public void save(){
        editedLevel.rooms = (Room[])newRooms.toArray();
        if(editedLevel.name != null){
            editedLevel.save();
        } else {
            saveAs();
        }
    }
    
    public void saveAs(){
        //vyvolat dialog s vyberem mista ulozeni
    }
    
    public void addRoom(){
        actionType = ActionType.ADD_ROOM;
        System.out.println(actionType);
        System.out.println("Dostane se to sem?");
        System.out.println(this.hashCode());
    }
    
    public void addItem(){
        actionType = ActionType.ADD_ITEM;
    }
    
    public void addDoor(){
        actionType = ActionType.ADD_DOOR_ROOM_1;
    }
    
    public void delete(){
        actionType = ActionType.DELETE;
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            System.out.println("ASPON SEM?");
            if(name.equals("mouseClick") && !keyPressed){
                System.out.println("A SEM?");
                Vector2f mousePosition = inputManager.getCursorPosition();
                System.out.println(actionType);
                System.out.println(this.hashCode());
                switch(actionType){
                    case ADD_ROOM:
                        System.out.println("A KONECNE SEM?");
                        Box b = new Box(10000.0f, 0.1f, 10000.0f);
                        Material m = new Material();
                        Geometry floor = new Geometry("floor", b);
                        floor.setMaterial(m);
                        CollisionResults results = new CollisionResults();
                        Ray ray = new Ray(camera.getWorldCoordinates(mousePosition),
                               camera.getCoordinatedDirection(mousePosition));
                        floor.collideWith(ray, results);
                        CollisionResult result = results.getClosestCollision();
                        Room newRoom = new Room(result.getContactPoint(),
                                10,10, index, assetManager);
                        newRooms.add(newRoom);
                        editedLevel.attachChild(newRoom);
                        actionType = ActionType.NONE;
                        break;
                    case ADD_ITEM:
                        Room selected = getRoom(camera.getWorldCoordinates(mousePosition),
                               camera.getCoordinatedDirection(mousePosition));
                        if(selected!=null){
                            Item newItem = new Item(selected, ObstacleType.DOG,
                                    assetManager);
                            editedLevel.attachChild(newItem);
                            editedLevel.addItem(ObstacleType.DOG, selected);
                            actionType = ActionType.NONE;
                        }
                        break;
                    case DELETE:
                        CollisionResults del_results = new CollisionResults();
                        Ray ray2 = new Ray(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        editedLevel.collideWith(ray2, del_results);
                        CollisionResult closest = del_results.getClosestCollision();
                        if(closest != null){
                            editedLevel.detachChild(closest.getGeometry());
                            /*potreba upravit tak, aby to jeste po smazani odstranilo
                             * z mistnosti nebo veci atd...
                            */
                        }
                        break;
                    case ADD_DOOR_ROOM_1:
                        selectedRoom = getRoom(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        if(selectedRoom != null){
                            actionType = ActionType.ADD_DOOR_ROOM_2;
                        }
                        break;
                    case ADD_DOOR_ROOM_2:
                        Room neighbour = getRoom(camera.getWorldCoordinates(mousePosition),
                                camera.getWorldCoordinates(mousePosition));
                        if(neighbour != null){
                            selectedRoom.addNeighbour(neighbour);
                            actionType = ActionType.NONE;
                        }
                        break;
                }
            }
        }
        
    };
    
    enum ActionType{
        ADD_ROOM, ADD_ITEM, DELETE, NONE, ADD_DOOR_ROOM_1, ADD_DOOR_ROOM_2;
    }
    
    private Room getRoom(Vector3f cameraPosition, Vector3f cameraDirection){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cameraPosition, cameraDirection);
        //ray.collideWith(new Plane(), results);
        editedLevel.collideWith(ray, results);
        if(results.size() > 0){
            CollisionResult closest = results.getClosestCollision();
            Geometry toCompare = closest.getGeometry();
            for(Room r: newRooms){
                if(r.floor.equals(toCompare)){
                    return r;
                }
            }
        }
        return null;
    }
    
    @Override public void update(float tpf){
        
    }
}
