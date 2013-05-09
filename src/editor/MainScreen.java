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
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.ScreenController;

import java.io.File;

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
        ListBox fileList = nifty.getScreen("load").findNiftyControl("file_list", ListBox.class);
        fileList.clear();
        
        File dir = new File("levels/custom/");
        File[] files = dir.listFiles();
        
        for(File f: files){
            if(f.isFile() && f.getName().endsWith(".xml")){
                String name = f.getName();
                name = name.substring(0, name.length()-4);
                fileList.addItem(name);
            }
        }

        nifty.gotoScreen("load");
    }
    
    public void editLevelLoad(){
        String levelName = (String)nifty.getCurrentScreen().findNiftyControl("file_list",
                ListBox.class).getSelection().get(0);
        nifty.gotoScreen("editing");
        stateManager.detach(this);
        app.editingScreen.setEditedLevel("levels/custom/" + levelName);
        stateManager.attach(app.editingScreen);
    }
    
    public void cancel(){
        nifty.gotoScreen("start");
    }
    
    public void quitEditor(){
        app.stop();
    }
    
}
