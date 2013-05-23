package game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.spi.render.RenderFont;

import game.obstacles.DogObstacle;
import game.obstacles.FireObstacle;
import game.obstacles.FlashObstacle;
import game.obstacles.GlassObstacle;
import game.obstacles.Obstacle;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import planner.Planner2;
import planner.ThiefAction;

import time.CountDown;


/**
 * Třída reprezentující stav "ve hře" a zajišťující funkcionalitu samotné hry.
 * Zděděno od AbstractAppState, tedy reprezenutuje stav. Zároveň implementuje
 * rozhraní ScreenController, kvůli komunikaci s prvky GUI.
 * @author Pavel Pilař
 */
public class InGameState extends AbstractAppState implements ScreenController {
    
    /** Reference na objekt reprezentující zloděje. */
    public Thief thief;
    
    /** Herní kamera. */
    final InGameCamera camera;
    
    /** Reference na aktuální level. */
    Level actualLevel;
    
    /** Reference na plánovač, tvořící plán pro zloděje. */
    Planner2 planner;
    
    /** Odpočívávání času. */
    CountDown counter;
    
    /** Informace o tom, zda je právě přidávána překážka. */
    private boolean addingObstacle = false;
    /** Informace o tom, které tlačítko bylo zvoleno. */
    private int buttonNumber = 0;
    /** Informace o tom, zda hra běží, používá se při zapauzování hry. */
    private boolean isRunning = false;
    /** Typ přidávané překážky. */
    private ObstacleType newObstacle;
    
    /** Namapování překážek na tlačítka. */
    private Map<Integer, ObstacleType> buttonMapping = new HashMap<Integer, ObstacleType>();
    
    /** Reference na gui. */
    private Nifty nifty;
    private Screen screen;
    
    /** Reference na aktuální instanci hry. */
    private Main app;
    
    /** Reference na hlavní rootNode a guiNode programu. */
    private Node rootNode;
    private Node guiNode;
    /** Reference na assetManager, kvůli správě textur a modelů. */
    private AssetManager assetManager;
    /** inputManager slouží k zachytávání vstupu od uživatele. */
    private InputManager inputManager;
    /** Stav má vlastní rootNode a guiNode, zjednodušuje se práce se stavy. */ 
    private Node localRootNode = new Node("rootNode nalezici InGameState");
    private Node localGuiNode = new Node("guiNode nalezici InGameState");
    
    /**
     * Konstruktor herního stavu. Vytvoří se reference na instanci aplikace a
     * reference na potřebné komponenty jako RootNode atd. Také se vytvoří herní
     * kamera.
     * @param app reference na aktuální instanci hry
     */
    public InGameState(SimpleApplication app){
        this.app = (Main)app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        camera = new InGameCamera(app.getCamera(), this.rootNode);
    }
    
    /**
     * Metoda, která se provede při prvním přiřazení stavu jako aktuálního. Inicializují
     * se zde všechny proměnné, které nezávisí na aktuálním levelu a používají se
     * vždy během tohoho stavu.
     * @param stateManager reference na stateManager spravující stav
     * @param app reference na aktuální aplikaci
     */
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        camera.registerWithInput(inputManager);
        camera.setCenter(new Vector3f(20,20,20));
        
        inputManager.setCursorVisible(true);
        inputManager.addMapping("mouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("rightMouseClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, new String[]{"mouseClick", "rightMouseClick"});
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f,-1.0f,1.0f).normalizeLocal());
        
        localRootNode.addLight(dl);
    }
    
    /**
     * Metoda sloužící k přiřazení nového levelu do stavu.
     * @param level reference na level, který se má hrát.
     */
    public void setLevel(Level level){
        actualLevel = level;
        initializeLevel();
    }
    
    /**
     * Inicializace nově přiřazeného levelu. Zároveň se inicializují všechny proměnné,
     * které jsou závislé na konkrétním levelu jako jsou zloděj atd.
     */
    public void initializeLevel(){
        
        localRootNode.detachAllChildren();
        
        if(nifty!= null){
            initializeGui();
        }
        
        thief = new Thief(assetManager, actualLevel);
        
        counter = new CountDown(actualLevel.timeLimit);

        planner = new Planner2(actualLevel, thief);
        ThiefAction[] plan = planner.makeNewPlan();
        if(plan != null){
            thief.setNewPlane(plan);
        } else {
            isRunning = false;
            thief.setAnimation("stand");
            nifty.gotoScreen("win");
        }

        Vector3f startPosition = actualLevel.start.getPosition();
        camera.setCenter(new Vector3f(startPosition.x, 20, startPosition.z+15));
        
        localRootNode.attachChild(actualLevel);
        localRootNode.attachChild(thief);
        
        counter.start();
        isRunning = true;
    }
    
    /**
     * Update smyčka stavu, provádí se kdykoliv je stav přiřazen jako aktivní.
     * Pokud hra běží(není zapauzováno) tak se aktualizuje pohyb zloděje, hlídá
     * se zda již zloděj nedosáhl cíle, nebo nevypršel čas nutný ke splnění levelu.
     * Také se aktualizuje informace o zbývajícím čase na panelu GUI.
     * @param tpf čas vyrenderování snímku 
     */
    @Override public void update(float tpf){
        if(isRunning && initialized){
            Element niftyElement = nifty.getCurrentScreen().findElementByName("timeLabel");
            niftyElement.getRenderer(TextRenderer.class).setText(counter.getRemainingMinutes());
            if(counter.getRemainingMillis() == 0){
                isRunning = false;
                thief.setAnimation("stand");
                if(actualLevel.index >= app.player.levelAchived){
                    int next = actualLevel.index+1;
                    app.player.levelAchived = next;
                    try{
                        app.player.save();
                    } catch (IOException e){}
                }
                nifty.gotoScreen("win");
            }
            if(thief.getActualPosition().equals(actualLevel.finish)){
                isRunning = false;
                thief.setAnimation("stand");
                nifty.gotoScreen("fail");
            }
            thief.update(tpf);
        }
    }
    
    /**
     * Metoda, která se provede vždy, když je stav nastaven jako aktivní. Do
     * RootNode a GuiNode se přiřadí jejich lokální ekvivalenty. Dojede tedy k
     * zobrezení věcí přiřazených stavu.
     * @param stateManager stateManager spravující stav
     */
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    /**
     * Metoda, která se provede vždy, když je stav nastaven jako deaktivní. Z
     * RootNode a GuiNode se odeberou jejich lokální ekvivalenty. Dojede tedy ke
     * skrytí věcí přiřazených stavu.
     * @param stateManager tateManager spravující stav
     */
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    /**
     * Metoda, která inicializuje prvku gui, podle jejich stavu v aktuálním levelu.
     */
    public void initializeGui(){
        System.out.println(actualLevel.availableObst.keySet());
        int i = 1;
        buttonMapping.clear();
        for(ObstacleType o : actualLevel.availableObst.keySet()){
            buttonMapping.put(i, o);
            nifty.getScreen("hud").findNiftyControl("obstacle" + i, Button.class)
                    .setText(o.toString() + ": " + actualLevel.availableObst.get(o));
            ++i;
        }
    }
    
    
    @Override public void onEndScreen(){}
    
    /**
     * Provede poté co je poprvé zobrazená obrazovka GUI. Spouští se zde inicializace
     * jednotlivých prvků GUI.
     */
    @Override public void onStartScreen(){
        initializeGui();
    }
    
    /**
     * Provádí se při přiřazení obrazovky do správce nifty.
     * @param nifty
     * @param screen 
     */
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
    }
    
    /**
     * Metoda pro komunikaci s GUI. Provádí se po stisknutí tlačítka překážky.
     * Nastaví se příslušné proměnné, tedy, že je přidávána překážka a jaký je
     * její typ.
     * @param param číslo stisknutého tlačítka
     */
    public void obstacleButtonPressed(String param){
        buttonNumber = Integer.parseInt(param);
        newObstacle = buttonMapping.get(buttonNumber);
        int i = actualLevel.availableObst.get(newObstacle);
        if(i > 0){
            addingObstacle = true;
        } else {
            addingObstacle = false;
        }
    }
    
    /**
     * Metoda pro komunukaci s GUI. Zapauzuje hru. Nastaví isRunning na false a
     * přepne obrazovku na obrazovku pauzy.
     */
    public void pause(){
        isRunning = false;
        counter.pause();
        thief.setAnimation("stand");
        nifty.gotoScreen("pause");
    }
    
    /**
     * Metoda pro komunikaci s GUI. Odpauzuje hru a nastaví obrazovku na herní.
     */
    public void unpause(){
        isRunning = true;
        thief.setAnimation("Walk");
        counter.unpause();
        nifty.gotoScreen("hud");
    }
    
    /**
     * Metoda pro restartování levelu.
     */
    public void restart(){
        actualLevel.load();
        setLevel(actualLevel);
        nifty.gotoScreen("hud");
        isRunning = true;
    }
    
    /**
     * Metoda pro přechod do dalšího levelu, pokud existuje.
     */
    public void toNextLevel(){
        if(actualLevel.nextLevelName != null){
            setLevel(new Level(actualLevel.nextLevelName, assetManager));
            unpause();
        } else {
            nifty.gotoScreen("start");
            app.getStateManager().detach(this);
            app.getStateManager().attach(app.startScreenState);
        }
    }
    
    /**
     * Metoda pro komunikaci s GUI. Slouží k opuštění hry a návrat do menu.
     * @param target cílová obrazovka menu, na kterou se má přejít
     */
    public void exitToMenu(String target){
        localRootNode.detachAllChildren();
        app.getStateManager().detach(this);
        nifty.gotoScreen(target);
        for(int i=1 ; i<app.player.levelAchived+1; ++i){
                nifty.getScreen("level_select")
                        .findNiftyControl(("butt_" + i), Button.class).enable();
        }
        app.getStateManager().attach(app.startScreenState);
    }
    
    /**
     * Action Listener sloužící pro zachycení vstupu od uživatele.
     */
    private ActionListener actionListener = new ActionListener(){
        
        /**
         * Metoda pro, která se automacky volá po té co uživatel provede nějakou
         * definovanou akci. Provede se, pokud je přidávána překážka.
         */
        public void onAction(String name, boolean keyPressed, float tpf){
            if(name.equals("mouseClick") && !keyPressed && addingObstacle == true){
                //ziska pozici kurzoru:
                Vector2f mousePosition = inputManager.getCursorPosition();
                //výpočet místnosti kam se přidá překážka:
                Room selected = actualLevel.getRoom(camera.getWorldCoordinates(mousePosition),
                        camera.getCoordinatedDirection(mousePosition));
                //přidá se překážka zvoleného typu:
                if(selected != null && !thief.getActualPosition().equals(selected) && selected.isEmpty()){
                    Obstacle toAdd;
                    switch(newObstacle){
                        case FIRE:
                            toAdd = new FireObstacle(assetManager);
                            break;
                        case FLASH:
                            toAdd = new FlashObstacle(assetManager);
                            break;
                        case DOG:
                            toAdd = new DogObstacle(assetManager);
                            break;
                        case GLASS:
                            toAdd = new GlassObstacle(assetManager);
                            break;
                        default:
                            toAdd = null;
                    }
                    actualLevel.addObstacle(toAdd,
                            selected);
                    planner.setLevel(actualLevel);
                    ThiefAction[] plan = planner.makeNewPlan();
                    if(plan != null){
                        thief.setNewPlane(plan);
                    } else {
                        isRunning = false;
                        thief.setAnimation("stand");
                        nifty.gotoScreen("win");
                        if(actualLevel.index >= app.player.levelAchived){
                            int next = actualLevel.index+1;
                            app.player.levelAchived = next;
                            try{
                                app.player.save();
                            } catch (IOException e){}
                        }
                    }
                    int i = actualLevel.availableObst.get(newObstacle);
                    actualLevel.availableObst.put(newObstacle, i-1);
                    nifty.getCurrentScreen().findNiftyControl("obstacle" + buttonNumber, Button.class)
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