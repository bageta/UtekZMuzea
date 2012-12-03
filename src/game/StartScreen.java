package game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Pavel
 */
public class StartScreen extends AbstractAppState implements ScreenController {
    
    private Nifty nifty;
    private Screen screen;
    
    private Main app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager asserManager;
    private InputManager inputManager;
    private AbstractAppState nextState;
    private Node localRootNode = new Node("rootNode StartScreen stavu");
    private Node localGuiNode = new Node("guiNode StartScreen stavu");
    
    public StartScreen(Main app){
        this.app = app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.asserManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        //this.nextState = nextState;
    }
    
    public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
    }
    
    public void onStartScreen(){}
    
    public void onEndScreen(){}
    
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        inputManager.setCursorVisible(true);    
    }
    
    @Override public void update(float tpf){
        /*update loap*/
    }
    
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    public void startGame(String nextScreen){
        nifty.gotoScreen(nextScreen);
        app.fromMenuToGame();
    }
    
    public void quitGame(){
        app.stop();
    }
}
