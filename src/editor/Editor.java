package editor;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavel
 */
public class Editor extends SimpleApplication {
    
    public MainScreen mainScreen;
    public EditingScreen editingScreen;
    
    public static void main(String[] args){
        Editor edit = new Editor();
        edit.start();
    }
    
    @Override public void simpleInitApp(){
        mainScreen = new MainScreen(this);
        editingScreen = new EditingScreen(this);
        
        stateManager.attach(mainScreen);
        
        setDisplayStatView(false);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/editor_screen.xml", "start", mainScreen, editingScreen);
        guiViewPort.addProcessor(niftyDisplay);
        
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
        
        inputManager.removeListener(flyCam);
    }
    
}
