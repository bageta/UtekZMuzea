package menu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import game.Level;
import game.Main;
import java.io.File;

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
    private AssetManager assetManager;
    private InputManager inputManager;
    private Node localRootNode = new Node("rootNode StartScreen stavu");
    private Node localGuiNode = new Node("guiNode StartScreen stavu");
    
    public StartScreen(SimpleApplication app){
        this.app = (Main)app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
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
    
    public void startSelectedLevel(){
        String selected = (String)nifty.getCurrentScreen().findNiftyControl("custom_levels",
                ListBox.class).getSelection().get(0);
        startGame("levels/custom/" + selected);
    }
    
    public void startGame(String levelName){
        app.inGameState.setLevel(new Level(assetManager, levelName));
        System.out.println("Probehne to?");
        app.getStateManager().detach(this);
        nifty.gotoScreen("hud");
        app.inGameState.setLevel(new Level(assetManager, levelName));
        app.getStateManager().attach(app.inGameState);
    }
    
    public void changeScreen(String screenName){
        nifty.gotoScreen(screenName);
    }
    
    public void  gotoLevelSelect(){
        ListBox customLevels = nifty.getScreen("level_select")
                .findNiftyControl("custom_levels", ListBox.class);
        
        customLevels.clear();
        
        File dir = new File("levels/custom/");
        File[] levels = dir.listFiles();
        
        for(File f : levels){
            if(f.isFile() && f.getName().endsWith(".xml")){
                String name = f.getName();
                name = name.substring(0, name.length()-4);
                customLevels.addItem(name);
            }
        }
        
        nifty.gotoScreen("level_select");
    }
    
    public void quitGame(){
        app.stop();
    }
}
