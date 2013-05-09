package editor;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Pavel
 */
public class MainScreen extends AbstractAppState implements ScreenController {
    
    private Nifty nifty;
    private Screen screen;
    
    private Editor app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager stateManager;
    private Node localRootNode = new Node("rootNode MainScreen stavu");
    private Node localGuiNode = new Node("guiNode MainScreen stavu");
    
    public MainScreen(SimpleApplication app){
        this.app = (Editor)app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
    }
    
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        inputManager.setCursorVisible(true);
    }
    
    @Override public void onEndScreen(){}
    
    @Override public void onStartScreen(){}
    
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
    }
    
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    public void newLevel(){
        nifty.gotoScreen("editing");
        stateManager.detach(this);
        app.editingScreen.setEditedLevel();
        stateManager.attach(app.editingScreen);
    }
    
    public void editLevel(){
        nifty.gotoScreen("load");
    }
    
    public void quitEditor(){
        app.stop();
    }
    
}
