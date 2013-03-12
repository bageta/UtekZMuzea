package editor;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import game.InGameCamera;

/**
 *
 * @author Pavel
 */
public class EditingScreen extends AbstractAppState implements ScreenController {
    
    private final InGameCamera camera;
    
    private Node rootNode;
    private InputManager inputManager;
    
    private Nifty nifty;
    private Screen screen;
    
    public EditingScreen(SimpleApplication app){
        this.rootNode = app.getRootNode();
        this.inputManager = app.getInputManager();
        camera = new InGameCamera(app.getCamera(), rootNode);
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
        camera.setCenter(Vector3f.ZERO);
    }
    
}
