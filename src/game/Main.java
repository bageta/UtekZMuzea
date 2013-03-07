package game;

import menu.StartScreen;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;

import de.lessvoid.nifty.Nifty;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * hlavni trida hry, dedi se SimpleApplication, dochazi ke spusteni hry,
 * obsahuje metodz pro inicializaci hry a auktualizaci herniho stavu
 * @author Pavel Pilar
 */
public class Main extends SimpleApplication {
    
    private StartScreen startScreenState;
    private InGameState inGameState;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /**
     * metoda, ktera inicializuje hru, dochazi kde k nastaveni kamery nacteni
     * modelu, nastaveni vychozich pozic a nacteni zvoleneho levelu
     */
    @Override public void simpleInitApp() {
        inGameState = new InGameState(this);
        startScreenState = new StartScreen(this);
        
        stateManager.attach(startScreenState);
        
        setDisplayStatView(false);
        
        /* inicializace gui*/
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,inputManager,audioRenderer,guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen.xml", "start", startScreenState, inGameState);
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
        guiViewPort.addProcessor(niftyDisplay); 
        //odstani se listener pro flyCam, ktera hre nevyhovuje a nepouziva se
        inputManager.removeListener(flyCam);
    }

    /**
     * metoda, ktera se automaticky vola pri kazdem vyrendrovani snimku a slouzi
     * k auktualizaci stavu hernich objektu
     * @param tpf doba za kterou byl vyrendrovan snimek
     */
    @Override public void simpleUpdate(float tpf) {}

    @Override public void simpleRender(RenderManager rm) {}
    
    public void fromMenuToGame(){
        stateManager.detach(startScreenState);
        stateManager.attach(inGameState);
    }
    
    public void fromGameToMenu(){
        stateManager.detach(inGameState);
        stateManager.detach(startScreenState);
    }
}