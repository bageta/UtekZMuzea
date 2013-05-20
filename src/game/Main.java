package game;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import menu.StartScreen;

/**
 * Hlavní třída hry, dědí se od SimpleApplication, dochazí ke spuštení hry a
 * načtení a inicializaci uživatelského rozhraní. Důležité jsou její členské
 * proměnné, které slouží k reprezentaci hlavních stavů celé hry.
 * @author Pavel Pilař
 */
public class Main extends SimpleApplication {
    
    public StartScreen startScreenState;
    public InGameState inGameState;
    
    public Player player;

    /**
     * Metoda, která se provede při spuštení hry, vytvoří se v ní instance hlavní
     * třídy hry a na ní se zavolá spuštění celé aplikace.
     * @param args argumenty příkazové řádky, nijak se nezpracovávají
     */
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    /**
     * Metoda, která se automaticky volá po spuštění hry, zajišťuje inicializaci
     * jednotilvých herních stavů, načtení GUI a nastaveni kamery.
     * modelu, nastaveni vychozich pozic a nacteni zvoleneho levelu
     */
    @Override public void simpleInitApp() {
        
        /* vytvoření základních herních stavů */
        inGameState = new InGameState(this);
        startScreenState = new StartScreen(this);
        
        /* nastavení úvodního stavu jako výchozího */
        stateManager.attach(startScreenState);
        
        /* vypnutí výpisu statistik na obrazovku */
        setDisplayStatView(false);
        
        /* inicializace gui*/
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,inputManager,audioRenderer,guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen.xml", "start", startScreenState, inGameState);
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
        guiViewPort.addProcessor(niftyDisplay);
        
        /* nacteni profilu hrace */
        try{
            player = Player.load();
        } catch (IOException e){
            player = new Player();
        } catch (ClassNotFoundException e){
            player = new Player();
        }
        
        /*odstranění listeneru pro flyCam, která hře nevyhovuje a nepoužíva se */
        inputManager.removeListener(flyCam);
    }

    /**
     * Metoda, která se automaticky volá při každém vyrendrování snímku a slouží
     * k auktualizaci stavu herních objektů. V programu je nahrazena update metodami,
     * jednotlivých stavů hry.
     * @param tpf doba za kterou byl vyrendrován snímek
     */
    @Override public void simpleUpdate(float tpf) {
    }

    @Override public void simpleRender(RenderManager rm) {}
}