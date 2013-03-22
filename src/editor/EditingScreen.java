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
import game.Level;
import game.Room;
import java.util.ArrayList;

/**
 *
 * @author Pavel
 */
public class EditingScreen extends AbstractAppState implements ScreenController {
    
    private final InGameCamera camera;
    
    private Node guiNode;
    private Node rootNode;
    private Node localGuiNode = new Node();
    private Node localRootNode = new Node();
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager stateManager;
    
    private AbstractAppState nextState;
    
    private Nifty nifty;
    private Screen screen;
    
    private Level editedLevel;
    
    private ActionType actionType;
    private int index;
    private ArrayList<Room> newRooms = new ArrayList<Room>();
    
    public EditingScreen(SimpleApplication app){
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
        this.assetManager = app.getAssetManager();
        camera = new InGameCamera(app.getCamera(), rootNode);
        editedLevel = new Level(assetManager);
        actionType=ActionType.NONE;
        index=0;
    }
    
    public EditingScreen(SimpleApplication app, String levelPath){
        this(app);
        editedLevel = new Level(assetManager, levelPath);
        //nakopirovat z array do array listu
        //newRooms = editedLevel.rooms.clone();
        index = editedLevel.rooms.length;
    }
    
    @Override public void onEndScreen(){}
    
    @Override public void onStartScreen(){}
    
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
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
    
    public void setNextState(AbstractAppState nextState){
        this.nextState = nextState;
    }
    
    public void exitToMenu(String screen){
        nifty.gotoScreen(screen);
        stateManager.detach(this);
        stateManager.attach(nextState);
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
        System.out.println("Dostane se to sem?");
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            System.out.println("ASPON SEM?");
            if(name.equals("mouseClick") && !keyPressed){
                System.out.println("A SEM?");
                //switch(actionType){
                    /*case ADD_ROOM:
                    {*/
                        System.out.println("A KONECNE SEM?");
                        Vector2f mousePosition = inputManager.getCursorPosition();
                        Box b = new Box(10000.0f, 0.1f, 10000.0f);
                        Material m = new Material();
                        Geometry floor = new Geometry("floor", b);
                        floor.setMaterial(m);
                        CollisionResults results = new CollisionResults();
                        Ray ray = new Ray(camera.getWorldCoordinates(mousePosition), camera.getCoordinatedDirection(mousePosition));
                        floor.collideWith(ray, results);
                        CollisionResult result = results.getClosestCollision();
                        Room newRoom = new Room(result.getContactPoint(),
                                10,10, index, assetManager);
                        newRooms.add(newRoom);
                        editedLevel.attachChild(newRoom);
                        actionType = ActionType.NONE;
                        //break;
                    //}
                        
                //}
            }
        }
        
    };
    
    enum ActionType{
        ADD_ROOM, ADD_ITEM, DELETE, NONE;
    }
}
