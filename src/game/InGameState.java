package game;

import planner.Planner2;

import time.CountDown;

import java.util.Map;
import java.util.HashMap;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.MouseInput;
import com.jme3.light.DirectionalLight;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Menu;
import de.lessvoid.nifty.controls.button.ButtonControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.controls.MenuItemActivatedEvent;
import org.bushe.swing.event.EventTopicSubscriber;

/**
 *
 * @author Pavel
 */
public class InGameState extends AbstractAppState implements ScreenController {
    
    public static Thief thief;
    
    final InGameCamera camera;
    
    Level actualLevel;
    Planner2 planner;
    CountDown counter;
    
    private boolean addingObstacle = false;    
    private boolean isRunning = true;
    private int buttonNumber = 0;
    
    private ObstacleType newObstacle;
    
    private Map<Integer, ObstacleType> buttonMapping = new HashMap<Integer, ObstacleType>();
    
    private Nifty nifty;
    private Screen screen;
    private Element popup;
    
    private Main app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Node localRootNode = new Node("rootNode nalezici InGameState");
    private Node localGuiNode = new Node("guiNode nalezici InGameState");
    
    public InGameState(SimpleApplication app){
        this.app = (Main)app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        camera = new InGameCamera(app.getCamera(), this.rootNode);
    }
    
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        camera.registerWithInput(inputManager);
        camera.setCenter(new Vector3f(20,20,20));
        
        inputManager.setCursorVisible(true);
        inputManager.addMapping("mouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("rightMouseClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, new String[]{"mouseClick", "rightMouseClick"});
        //inputManager.removeListener();
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f,-1.0f,1.0f).normalizeLocal());
        
        actualLevel = new Level(assetManager, "level1");
        initializeGui();
        thief = new Thief(assetManager, actualLevel);
        counter = new CountDown(actualLevel.timeLimit);
        
        planner = new Planner2(actualLevel);
        thief.setNewPlane(planner.makeNewPlan());
        counter.start();
        
        localRootNode.addLight(dl);
        localRootNode.attachChild(actualLevel);
        localRootNode.attachChild(thief);
    }
    
    @Override public void update(float tpf){
        if(isRunning && initialized){
            Element niftyElement = nifty.getCurrentScreen().findElementByName("timeLabel");
            niftyElement.getRenderer(TextRenderer.class).setText(counter.getRemainingMinutes());
            if(counter.getRemainingMillis() == 0){
                isRunning = false;
                thief.setAnimation("stand");
                nifty.gotoScreen("win");
            }
            if(thief.actualPosition.equals(actualLevel.finish)){
                isRunning = false;
                thief.setAnimation("stand");
                nifty.gotoScreen("fail");
            }
            thief.update(tpf);
        }
    }
    
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    public void initializeGui(){
        int i = 1;
        for(ObstacleType o : actualLevel.availableObst.keySet()){
            buttonMapping.put(i, o);
            nifty.getCurrentScreen().findControl("obstacle" + i, ButtonControl.class).setText(o.toString() + ": " + i);
            ++i;
        }
    }
    
    /*public void obstacleAddedAction(Obstacle obstacle, Room to){
        actualLevel.addObstacle(obstacle, to);
        thief.setNewPlane(planner.makeNewPlan());
    }*/
    
    @Override public void onEndScreen(){}
    
    @Override public void onStartScreen(){}
    
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
    }
    
    public void obstacleButtonPressed(String param){
        buttonNumber = Integer.parseInt(param);
        newObstacle = buttonMapping.get(buttonNumber);
        int i = actualLevel.availableObst.get(newObstacle);
        if(i > 0){
            addingObstacle = true;
        } else {
            addingObstacle = false;
            //play warning sound
        }
        
        System.out.println("Button of " + buttonNumber + ". item was pressed");
    }
    
    public void pause(){
        isRunning = false;
        counter.pause();
        thief.setAnimation("stand");
        nifty.gotoScreen("pause");
    }
    
    public void unpause(){
        isRunning = true;
        thief.setAnimation("Walk");
        counter.unpause();
        nifty.gotoScreen("hud");
    }
    
    public void restart(){
        //localRootNode.detachAllChildren();
        //initialize(null, app);
    }    
    
    public void exitToMenu(String target){
        localRootNode.detachAllChildren();
        nifty.gotoScreen(target);
        app.fromGameToMenu();
    }
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean keyPressed, float tpf){
            //System.out.println("Aspon sem? :(");
            if(name.equals("mouseClick") && !keyPressed && addingObstacle == true){
                System.out.println("DOSTANE SE TO SEM");
                //ziska pozici kamery
                Vector2f mousePosition = inputManager.getCursorPosition();
                Room selected = actualLevel.getRoom(camera.getWorldCoordinates(mousePosition),
                        camera.getCoordinatedDirection(mousePosition));
                if(selected != null){
                    actualLevel.addObstacle(new Obstacle(assetManager, newObstacle),
                            selected);
                    planner.setLevel(actualLevel);
                    thief.setNewPlane(planner.makeNewPlan());
                    int i = actualLevel.availableObst.get(newObstacle);
                    actualLevel.availableObst.put(newObstacle, i-1);
                    nifty.getCurrentScreen().findControl("obstacle" + buttonNumber, ButtonControl.class)
                    .setText(newObstacle.toString() + ": " + (i-1));
                    addingObstacle = false;
                }
            }
            if(name.equals("rightMouseClick") && !keyPressed){
                addingObstacle = false;
            }
        }
    };

}