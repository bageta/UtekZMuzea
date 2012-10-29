package game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

import planner.Planner;
import planner.Planner2;
import planner.ThiefAction;

/**
 * hlavni trida hry, dedi se SimpleApplication, dochazi ke spusteni hry,
 * obsahuje metodz pro inicializaci hry a auktualizaci herniho stavu
 * @author Pavel Pilar
 */
public class Main extends SimpleApplication {
    
    public static Thief  thief;
    
    private boolean cursor;
    
    Planner2 planner;
    Level actualLevel;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /**
     * metoda, ktera inicializuje hru, dochazi kde k nastaveni kamery nacteni
     * modelu, nastaveni vychozich pozic a nacteni zvoleneho levelu
     */
    @Override public void simpleInitApp() {
        //vytvori a vycentruje se nova kamera: 
        final InGameCamera camera = new InGameCamera(cam, rootNode);
        camera.registerWithInput(inputManager);
        camera.setCenter(new Vector3f(20,20,20));
        
        //odstani se listener pro flyCam, ktera hre nevyhovuje a nepouziva se
        inputManager.removeListener(flyCam);
        //inputManager.setCursorVisible(true);
        
        actualLevel = new Level(assetManager);
        
        thief = new Thief(assetManager, actualLevel);
        
        rootNode.attachChild(actualLevel);
        rootNode.attachChild(thief);
        
        //Planner planner1 = new Planner(actualLevel);
        //Planner2 planner2 = new Planner2(actualLevel);
        
        //planner1.makeNewPlan();
        //planner2.makeNewPlan();
        
        planner = new Planner2(actualLevel);
        thief.setNewPlane(planner.makeNewPlan());
        
        cursor = false;
    }

    /**
     * metoda, ktera se automaticky vola pri kazdem vyrendrovani snimku a slouzi
     * k auktualizaci stavu hernich objektu
     * @param tpf doba za kterou byl vyrendrovan snimek
     */
    @Override public void simpleUpdate(float tpf) {
        //TODO: resit pres appstate:
        if(!cursor){
            inputManager.setCursorVisible(true);
            cursor = true;
        }
        //END TODO------------------
        thief.update(tpf);
    }

    @Override public void simpleRender(RenderManager rm) {
    }
    
    public void obstacleAddedAction(Obstacle obstacle, Room to){
        actualLevel.addObstacle(obstacle, to);
        thief.setNewPlane(planner.makeNewPlan());
    }
}