package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;

/**
 *
 * @author Pavel
 */
public class HelloJME3 extends SimpleApplication implements AnimEventListener {
    
    Node player;
    AnimChannel channel;
    AnimControl control;
    
    public static void main(String args[]){
        HelloJME3 app = new HelloJME3();
        app.start();
    }

    @Override public void simpleInitApp(){
        mouseInput.setCursorVisible(true);
        inputManager.setCursorVisible(true);
        inputManager.removeListener(flyCam);
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        initKeys();
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        player.setLocalScale(0.5f);
        rootNode.attachChild(player);
        control = player.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("stand");
    }
    
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName){
//        if(animName.equals("Walk")){
//            channel.setAnim("stand", 0.5f);
//            channel.setLoopMode(LoopMode.DontLoop);
//            channel.setSpeed(1f);
//        }
    }
    
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName){
        
    }
    
    private void initKeys(){
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Walk");
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Walk") && !isPressed){
                if(!channel.getAnimationName().equals("Walk")){
                    channel.setAnim("Walk", 0.5f);
                    channel.setLoopMode(LoopMode.Loop);
                }
            }
        }
        
    };
}
