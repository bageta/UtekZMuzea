package menu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import game.Level;
import game.Main;

import java.io.File;

/**
 * Třída pro reprezentaci stavu "v menu". Zárověň jde o controller, zajišťující
 * komunikaci menu s GUI.
 * @author Pavel Pilař
 */
public class StartScreen extends AbstractAppState implements ScreenController {
    
    /** reference na nifty spravujíci gui. */
    private Nifty nifty;
    private Screen screen;
    
    /** reference na instanci aplikace a její důležité atributy */
    private Main app;
    private Node rootNode;
    private Node guiNode;
    
    /** reference na assetManager spravující textury, modely atd. */
    private AssetManager assetManager;
    /** reference na inputManager zachytávající vstup od uživatele */
    private InputManager inputManager;
    
    /** lokální root a gui Node pro snažší prácí */
    private Node localRootNode = new Node("rootNode StartScreen stavu");
    private Node localGuiNode = new Node("guiNode StartScreen stavu");
    
    /**
     * Konstruktor stavu menu. Vytvoří se reference na instanci aplikace a
     * reference na potřebné komponenty jako RootNode atd.
     * @param app reference na aktuální instanci hry
     */
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
    
    /**
     * Při prvním zobrazení obrazovky, se uzamknou levely, do kterych hráč ještě
     * nemá přístup.
     */
    public void onStartScreen(){
        if(nifty.getCurrentScreen().getScreenId().equals("level_select")){
            for(int i=app.player.levelAchived+1; i<13; ++i){
                nifty.getScreen("level_select")
                        .findNiftyControl(("butt_" + i), Button.class).disable();
            }
        }
    }
    
    /**
     * při opouštění žádné obrazovky se nic neděje.
     */
    public void onEndScreen(){}
    
     /**
     * Metoda, která se provede při prvním přiřazení stavu jako aktuálního.
     * @param stateManager reference na stateManager spravující stav
     * @param app reference na aktuální aplikaci
     */
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        inputManager.setCursorVisible(true);    
    }
    
    /**
     * Update loop stavu "v menu".
     * @param tpf doba vyrenderování jednoho snímku
     */
    @Override public void update(float tpf){
        /*update loap*/
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
     * Metoda pro komunikaci s GUI. Zjistí se, který z vlastních levelů uživatel
     * vybral a provede se spuštění hry.
     */
    public void startSelectedLevel(){
        String selected = (String)nifty.getCurrentScreen().findNiftyControl("custom_levels",
                ListBox.class).getSelection().get(0);
        startGame("custom/" + selected);
    }
    
    /**
     * Spustí hru s levelem daného jména.
     * @param levelName jméno levelu
     */
    public void startGame(String levelName){
        app.inGameState.setLevel(new Level(levelName, assetManager));
        System.out.println("Probehne to?");
        app.getStateManager().detach(this);
        nifty.gotoScreen("hud");
        app.getStateManager().attach(app.inGameState);
        app.inGameState.setLevel(new Level(levelName, assetManager));
    }
    
    /**
     * Metoda pro komunikaci s GUI. Slouží pro přechod na danou obrazovku.
     * @param screenName jméno obrazovky na kterou se má přejít 
     */
    public void changeScreen(String screenName){
        nifty.gotoScreen(screenName);
    }
    
    /**
     * Metoda pro komunikaci s GUI. Slouží na přechod na obrazovku výběru levelu.
     * inicializuje se seznam vlasních levelů.
     */
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
    
    /** 
     * Metoda pro komunikaci s GUI, ukončuje hru.
     */
    public void quitGame(){
        app.stop();
    }
}
