package game;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import java.io.IOException;

/**
 * Kamera, která se chová jako běžná kamera v RTS hrách. Tedy pohled je mírně
 * zešikma shora a hráč může s kamerou hýbat 4 směry, rotovat, přibližovat a 
 * oddalovat a také měnit sklon kamery
 * @author Pavel Pilař
 */
public class InGameCamera implements Control, ActionListener {
    
    /**
     * enum pro representaci toho, jaká akce se s kamerou bude provádět.
     */
    private enum Degree{
        SIDE,
        FWD,
        ROTATE,
        TILT,
        DISTANCE
    }
    
    /** reference na inputManager aplikace */
    private InputManager inputManager;
    /** reference na kameru aplikace */
    private final Camera cam;
    
    /* pole směr pro různé akce */
    private int[] direction = new int[5];
    private float[] accelPeriod = new float[5];
    
    /* různé atributy jako rychlost atd pro různé akce */
    private float[] maxSpeed = new float[5];
    private float[] maxAccelPeriod = new float[5];
    private float[] minValue = new float[5];
    private float[] maxValue = new float[5];
    
    /** pozice kamery. */
    private Vector3f position = new Vector3f();
    
    /** centrální pozice kamery. */
    private Vector3f center = new Vector3f();
    /** naklonění, rotace a vzdálenost kamery */
    private float tilt = (float)(Math.PI / 4);
    private float rot = 0;
    private float distance = 15;
    
    /** ordinální hodnoty z enumu Degree */
    private static final int SIDE = Degree.SIDE.ordinal();
    private static final int FWD = Degree.FWD.ordinal();
    private static final int ROTATE = Degree.ROTATE.ordinal();
    private static final int TILT = Degree.TILT.ordinal();
    private static final int DISTANCE = Degree.DISTANCE.ordinal();
    
    /**
     * Konstruktor třídy s kamerou. Nastavují se hranice pohybu, naklonění,
     * atd. A maximální rychlosi pro různé akce.
     * @param cam kamera, která se bude ovládat
     * @param target cílový spatial, ke kterému se kamera přidá
     */
    public InGameCamera(Camera cam, Spatial target){
        this.cam = cam;
        
        setMinMaxValues(Degree.SIDE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        setMinMaxValues(Degree.FWD, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        setMinMaxValues(Degree.ROTATE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        setMinMaxValues(Degree.TILT, 0.2f, (float)(Math.PI / 2) - 0.001f);
        setMinMaxValues(Degree.DISTANCE, 2, Float.POSITIVE_INFINITY);
 
        setMaxSpeed(Degree.SIDE,10f,0.4f);
        setMaxSpeed(Degree.FWD,10f,0.4f);
        setMaxSpeed(Degree.ROTATE,2f,0.4f);
        setMaxSpeed(Degree.TILT,1f,0.4f);
        setMaxSpeed(Degree.DISTANCE,15f,0.4f);
        
        target.addControl(this);
    }
    
    /**
     * Nastavení maximální rychlosti kamary pro různé akce.
     * @param deg akce, pro kterou se rychlost nastavuje
     * @param maxSpeed maximální rychlost kamery
     * @param accelTime čas akcelerace
     */
    private void setMaxSpeed(Degree deg, float maxSpeed, float accelTime){
        this.maxSpeed[deg.ordinal()] = maxSpeed/accelTime;
        maxAccelPeriod[deg.ordinal()] = accelTime;
    }
    
     /**
     * natavení horní a dolní meze daného pohybu
     * @param dg Degree určující daný pohyb
     * @param min dolní mez
     * @param max horní mez
     */
    private void setMinMaxValues(Degree dg, float min, float max) {
        minValue[dg.ordinal()] = min;
        maxValue[dg.ordinal()] = max;
    }
    
    /**
     * přiřazení zachycování vstupu z klávesnice ke kameře
     * @param inputManager nastavení inputManageru, který události zpracovává
     */
    public void registerWithInput(InputManager inputManager){
        this.inputManager = inputManager;
        
        String[] mappings = new String[] { "+SIDE", "+FWD", "+ROTATE", "+TILT", "+DISTANCE",
                "-SIDE", "-FWD", "-ROTATE", "-TILT", "-DISTANCE", };
        
        inputManager.addMapping("-SIDE", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("+SIDE", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("+FWD", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("-FWD", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("+ROTATE", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("-ROTATE", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("+TILT", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("-TILT", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("-DISTANCE", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("+DISTANCE", new KeyTrigger(KeyInput.KEY_X));
        
        inputManager.addListener(this, mappings);
        inputManager.setCursorVisible(true);
    }
   
    /**
     * Update metoda kamery, provádí se pohyb kamery podle zachyceného vstupu z
     * klávesnice
     * @param tpf čas vyrendrování jednoho snímku
     */
    public void update(final float tpf) {
        for (int i = 0; i < direction.length; i++) {
            int dir = direction[i];
            switch (dir) {
            case -1:
                accelPeriod[i] = clamp(-maxAccelPeriod[i],accelPeriod[i]-tpf,accelPeriod[i]);
                break;
            case 0:
                if (accelPeriod[i] != 0) {
                    double oldSpeed = accelPeriod[i];
                    if (accelPeriod[i] > 0) {
                        accelPeriod[i] -= tpf;
                    } else {
                        accelPeriod[i] += tpf;
                    }
                    if (oldSpeed * accelPeriod[i] < 0) {
                        accelPeriod[i] = 0;
                    }
                }
                break;
            case 1:
                accelPeriod[i] = clamp(accelPeriod[i],accelPeriod[i]+tpf,maxAccelPeriod[i]);
                break;
            }     
        }
        
        distance += maxSpeed[DISTANCE] * accelPeriod[DISTANCE] * tpf;
        tilt += maxSpeed[TILT] * accelPeriod[TILT] * tpf;
        rot += maxSpeed[ROTATE] * accelPeriod[ROTATE] * tpf;
         
        distance = clamp(minValue[DISTANCE],distance,maxValue[DISTANCE]);
        rot = clamp(minValue[ROTATE],rot,maxValue[ROTATE]);
        tilt = clamp(minValue[TILT],tilt,maxValue[TILT]);
 
        double offX = maxSpeed[SIDE] * accelPeriod[SIDE] * tpf;
        double offZ = maxSpeed[FWD] * accelPeriod[FWD] * tpf;
 
        center.x += offX * Math.cos(-rot) + offZ * Math.sin(rot);
        center.z += offX * Math.sin(-rot) + offZ * Math.cos(rot);
 
        position.x = center.x + (float)(distance * Math.cos(tilt) * Math.sin(rot));
        position.y = center.y + (float)(distance * Math.sin(tilt));
        position.z = center.z + (float)(distance * Math.cos(tilt) * Math.cos(rot));
 
         
        cam.setLocation(position);
        cam.lookAt(center, new Vector3f(0,1,0));
        
    }
    
    /**
     * Oříznutí předané hodnoty podle daného intervalu.
     * @param min spodní mez
     * @param value ořezávaná hodnota
     * @param max horní mez
     * @return číslo oříznuté dle daného intervalu
     */
    private static float clamp(float min, float value, float max) {
        if ( value < min ) {
            return min;
        } else if ( value > max ) {
            return max;
        } else {
            return value;
        }
    }
    
    /**
     * Nastavení počátečního bodu kamery, od kterého se počítají meze pro pohyb.
     * @param center souřadnice počátečního bodu
     */
    public void setCenter(Vector3f center) {
        this.center.set(center);
    }
    
    /**
     * Zachytávání vstupu od uživatele, podle zachyceného vstupu se nastavuje,
     * direction příslušné akce.
     * @param name Jméno akce namapované na klávesu
     * @param isPressed je klávesa stále stisknutá?
     * @param tpf čas vyrenderování snímku
     */
    public void onAction(String name, boolean isPressed, float tpf) {
        int press = isPressed ? 1 : 0;
         
        char sign = name.charAt(0);
        if ( sign == '-') {
            press = -press;
        } else if (sign != '+') {
            return;
        }
         
        Degree deg = Degree.valueOf(name.substring(1));
        direction[deg.ordinal()] = press;
    }
    
    /**
     * Metoda, která vrací pozici kamery ve světových souřadnicích v závislosti
     * na pozici bodu obrazovky.
     * @param screenPos souřednice bodu na obrazovce 
     * @return 
     */
    public Vector3f getWorldCoordinates(Vector2f screenPos){
        return cam.getWorldCoordinates(screenPos, 0f);
    }
    
    /**
     * metoda, která vrací směrový vektor kamery s počátkem v bodu obrazovky.
     * @param screenPos souřadnice bodu na obrazovce
     * @return 
     */
    public Vector3f getCoordinatedDirection(Vector2f screenPos){
        Vector3f coordinates = cam.getWorldCoordinates(screenPos, 0f);
        return cam.getWorldCoordinates(screenPos, 1f).subtractLocal(coordinates).normalizeLocal();
    }
    
    
    //metody se nepoužívají, ale musí zde být kvůli rozhraní Control:
    
    @Override
    public void write(JmeExporter ex) throws IOException {
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
    }
    
     /**
     * vytvoření kopie kamery
     * @param spatial nový Spatial, pro který se kopie vytváří
     * @return nakopírovaná kameru
     */
    public Control cloneForSpatial(Spatial spatial) {
        InGameCamera other = new InGameCamera(cam, spatial);
        other.registerWithInput(inputManager);
        return other;
    }
    
    public void setSpatial(Spatial spatial) {  
    }
    
    public void render(RenderManager rm, ViewPort vp) {
    }
}