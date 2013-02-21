package game;

import planner.Planner2;

import time.CountDown;

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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

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
    
    private int addingObstacle = 0;
    
    private Nifty nifty;
    private Screen screen;
    
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
        
        actualLevel = new Level(assetManager);
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
        Element niftyElement = nifty.getCurrentScreen().findElementByName("timeLabel");
        niftyElement.getRenderer(TextRenderer.class).setText(counter.getRemainingMinutes());
        if(counter.getRemainingMillis() == 0){
            //win
        }
        if(thief.actualPosition.equals(actualLevel.finish)){
            //fail
        }
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
    
    @Override public void onEndScreen(){}
    
    @Override public void onStartScreen(){}
    
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
    }
    
    public void obstacleButtonPressed(String param){
        int buttonNumber = Integer.parseInt(param);
        addingObstacle = buttonNumber;
        System.out.println("Button of " + buttonNumber + ". item was pressed");
    }
    
    public void pause(){
        //pause
    }
    
    public void exitToMenu(String target){
        nifty.gotoScreen(target);
        app.fromGameToMenu();
    }
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean keyPressed, float tpf){
            //System.out.println("Aspon sem? :(");
            if(name.equals("mouseClick") && !keyPressed && addingObstacle != 0){
                System.out.println("DOSTANE SE TO SEM");
                ObstacleType newObstacleType = ObstacleType.DOG;
                switch(addingObstacle){
                    case 1:
                        newObstacleType = ObstacleType.GLASS;
                        break;
                    case 2:
                        newObstacleType = ObstacleType.DOG;
                        break;
                    default:
                        //asi hazet vyjimku a padat? stat by se to nemelo
                        break;
                }
                //ziska pozici kamery
                Vector2f mousePosition = inputManager.getCursorPosition();
                Room selected = actualLevel.getRoom(camera.getWorldCoordinates(mousePosition),
                        camera.getCoordinatedDirection(mousePosition));
                if(selected != null){
                    actualLevel.addObstacle(new Obstacle(assetManager, newObstacleType),
                            selected);
                    planner.setLevel(actualLevel);
                    thief.setNewPlane(planner.makeNewPlan());
                    addingObstacle = 0;
                }
            }
            if(name.equals("rightMouseClick") && !keyPressed){
                addingObstacle = 0;
            }
        }
    };
}