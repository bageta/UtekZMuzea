package editor;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Pavel
 */
public class Editor extends SimpleApplication {
    
    private MainScreen mainScreen;
    
    public static void main(String[] args){
        Editor edit = new Editor();
        edit.start();
    }
    
    @Override public void simpleInitApp(){
        mainScreen = new MainScreen(this);
        
        stateManager.attach(mainScreen);
        
        setDisplayStatView(false);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/editor_screen.xml", "start", mainScreen);
        guiViewPort.addProcessor(niftyDisplay);
        
        inputManager.removeListener(flyCam);
    }
    
}
