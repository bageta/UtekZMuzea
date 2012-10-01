package game;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

/**
 * hlavni trida hry, dedi se SimpleApplication, dochazi ke spusteni hry,
 * obsahuje metodz pro inicializaci hry a auktualizaci herniho stavu
 * @author Pavel Pilar
 */
public class Main extends SimpleApplication {
    
    static Thief  thief;

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
        
        Level level1 = new Level(assetManager);
        
        thief = new Thief(assetManager, level1);
        
        rootNode.attachChild(level1);
        rootNode.attachChild(thief);
    }

    /**
     * metoda, ktera se automaticky vola pri kazdem vyrendrovani snimku a slouzi
     * k auktualizaci stavu hernich objektu
     * @param tpf doba za kterou byl vyrendrovan snimek
     */
    @Override public void simpleUpdate(float tpf) {
        thief.update(tpf);
    }

    @Override public void simpleRender(RenderManager rm) {
    }
}