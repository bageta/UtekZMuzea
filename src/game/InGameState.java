package game;

import planner.Planner2;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Pavel
 */
public class InGameState extends AbstractAppState {
    
    public static Thief thief;
    
    Level actualLevel;
    Planner2 planner;
    
    private SimpleApplication app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Node localRootNode = new Node("rootNode nalezici InGameState");
    private Node localGuiNode = new Node("guiNode nalezici InGameState");
    
    public InGameState(SimpleApplication app){
        this.app = app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
    }
    
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        final InGameCamera camera = new InGameCamera(app.getCamera(), rootNode);
        camera.registerWithInput(inputManager);
        camera.setCenter(new Vector3f(20,20,20));
        
        inputManager.setCursorVisible(true);
        //inputManager.removeListener();
        actualLevel = new Level(assetManager);
        thief = new Thief(assetManager, actualLevel);
        
        planner = new Planner2(actualLevel);
        thief.setNewPlane(planner.makeNewPlan());
        
        localRootNode.attachChild(actualLevel);
        localRootNode.attachChild(thief);
    }
    
    @Override public void update(float tpf){
        thief.update(tpf);
    }
    
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    public void obstacleAddedAction(Obstacle obstacle, Room to){
        actualLevel.addObstacle(obstacle, to);
        thief.setNewPlane(planner.makeNewPlan());
    }
}