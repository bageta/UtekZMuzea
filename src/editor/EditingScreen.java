package editor;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import game.InGameCamera;
import game.Level;
import game.ObstacleType;
import game.Room;
import java.io.File;

import java.util.ArrayList;

/**
 *
 * @author Pavel
 */
public class EditingScreen extends AbstractAppState implements ScreenController {
    
    private final InGameCamera camera;
    
    private Editor app;
    private Node guiNode;
    private Node rootNode;
    private Node localGuiNode = new Node();
    private Node localRootNode = new Node();
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager stateManager;
    
    private Nifty nifty;
    private Screen screen;
    
    private Level editedLevel;
    
    private ActionType actionType= ActionType.NONE;
    private int index;
    private ArrayList<Room> newRooms = new ArrayList<Room>();
    
    private Room selectedRoom;
    private Room tempRoom;
    
    private DropDown typeSelector;
    
    public EditingScreen(SimpleApplication app){
        this.app = (Editor)app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
        this.assetManager = app.getAssetManager();
        camera = new InGameCamera(app.getCamera(), rootNode);
    }
    
    public void setEditedLevel(){
        editedLevel = new Level(assetManager);
        index = 0;
        initializeLevel();
    }
    
    private void initializeLevel(){
        localRootNode.detachAllChildren();
        localRootNode.attachChild(editedLevel);
    }
    
    public void setEditedLevel(String levelPath){
        editedLevel = new Level(assetManager, levelPath);
        editedLevel.load();
        index = editedLevel.rooms.length;
        initializeLevel();
    }
    
    @Override public void onEndScreen(){}
    
    @Override public void onStartScreen(){}
    
    @Override public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
        nifty.registerScreenController(this);
    }
    
    @Override public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        initializeGui();
        
        camera.registerWithInput(inputManager);
        camera.setCenter(new Vector3f(20, 20, 20));
        
        inputManager.setCursorVisible(true);
        inputManager.addMapping("mouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, new String[]{"mouseClick"});
        
        //localRootNode.attachChild(editedLevel);
    }
    
    @Override public void stateAttached(AppStateManager stateManager){
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }
    
    @Override public void stateDetached(AppStateManager stateManager){
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
    
    public void exitToMenu(String screen){
        nifty.gotoScreen(screen);
        stateManager.detach(this);
        stateManager.attach(app.mainScreen);
    }
    
    public void save(){
        editedLevel.rooms = newRooms.toArray(new Room[0]);
        for(int i=0; i<editedLevel.rooms.length; ++i){
            editedLevel.rooms[i].index = i;
        }
        //editedLevel.name = "level";
        System.out.println(editedLevel.name);
        if(editedLevel.name != null){
            editedLevel.save();
        } else {
            saveAs();
        }
    }
    
    public void saveAs(){
        System.out.println("Dostane se to sem");
        File dir = new File("levels/custom/");
        System.out.println("sem to projde?");
        File[] files = dir.listFiles();
        
        ListBox fileList = nifty.getScreen("save_as").findNiftyControl("file_list", ListBox.class);
        fileList.clear();
        
        for(File f : files){
            if(f.isFile() && f.getName().endsWith(".xml")){
                String name = f.getName();
                name = name.substring(0, name.length()-4);
                fileList.addItem(name);
            }
        }
        
        //BUGOVALO TO PRI EDITACI EXISTUJICIHO LEVELU
//        if(editedLevel.name != null){
//            System.out.println(editedLevel.name);
//            TextField t = nifty.getScreen("save_as").findNiftyControl("file_name", TextField.class);
//            System.out.println(t);
//            t.setText(editedLevel.name);
//        }
        
        nifty.gotoScreen("save_as");
    }
    
    public void addRoom(){
        Vector2f mousePosition = inputManager.getCursorPosition();
        tempRoom = new Room(Vector3f.ZERO, 10,10, index, true, assetManager);
        editedLevel.attachChild(tempRoom);
        System.out.println(actionType);
        System.out.println("Dostane se to sem?");
        System.out.println(this.hashCode());
        actionType = ActionType.ADD_ROOM;
    }
    
    public void addItem(){
        actionType = ActionType.ADD_ITEM;
    }
    
    public void addDoor(){
        actionType = ActionType.ADD_DOOR_ROOM_1;
    }
    
    public void delete(){
        actionType = ActionType.DELETE;
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            System.out.println("ASPON SEM?");
            if(name.equals("mouseClick") && !keyPressed){
                System.out.println("A SEM?");
                Vector2f mousePosition = inputManager.getCursorPosition();
                System.out.println(actionType);
                System.out.println(this.hashCode());
                switch(actionType){
                    case ADD_ROOM:
                        System.out.println("A KONECNE SEM?");
                        Room newRoom = new Room(tempRoom.getLocalTranslation(), tempRoom.getWidth(),
                                tempRoom.getHeight(), index,true, assetManager);
                        newRooms.add(newRoom);
                        editedLevel.attachChild(newRoom);
                        editedLevel.detachChild(tempRoom);
                        actionType = ActionType.NONE;
                        break;
                    case ADD_ITEM:
                        Room selected = getRoom(camera.getWorldCoordinates(mousePosition),
                               camera.getCoordinatedDirection(mousePosition));
                        if(selected!=null){
                            ObstacleType newItemType = ObstacleType.valueOf((String)typeSelector.getSelection());
                            editedLevel.addItem(newItemType, selected);
                            actionType = ActionType.NONE;
                        }
                        break;
                    case DELETE:
                        CollisionResults del_results = new CollisionResults();
                        Ray ray2 = new Ray(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        editedLevel.collideWith(ray2, del_results);
                        CollisionResult closest = del_results.getClosestCollision();
                        if(closest != null){
                            System.out.println("Neco se vybere...");
                            editedLevel.detachChild(closest.getGeometry());
                            deleteRoom(closest.getGeometry());
                            deleteItem(closest.getGeometry());
                        }
                        break;
                    case ADD_DOOR_ROOM_1:
                        selectedRoom = getRoom(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        if(selectedRoom != null){
                            System.out.println("mistnost 1 - prepinam");
                            actionType = ActionType.ADD_DOOR_ROOM_2;
                        }
                        break;
                    case ADD_DOOR_ROOM_2:
                        Room neighbour = getRoom(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        if(neighbour != null){
                            System.out.println("mistnosts 2");
                            selectedRoom.addNeighbour(neighbour);
                            actionType = ActionType.NONE;
                        } else {
                            System.out.println("Sakra proc?");
                        }
                        break;
                    case SELECT_START:
                        Room selectedStart = getRoom(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        if(selectedStart != null && editedLevel.finish!=selectedStart){
                            if(editedLevel.start != null){
                                editedLevel.start.unsetStart();
                            }
                            editedLevel.start = selectedStart;
                            editedLevel.start.setStart();
                            actionType = ActionType.NONE;
                        }
                        break;
                    case SELECT_FINISH:
                        Room selectedFinish = getRoom(camera.getWorldCoordinates(mousePosition),
                              camera.getCoordinatedDirection(mousePosition));
                        if(selectedFinish != null && editedLevel.start!=selectedFinish){
                            if(editedLevel.finish != null){
                                editedLevel.finish.unsetFinish();
                            }
                            editedLevel.finish = selectedFinish;
                            editedLevel.finish.setFinish();
                            actionType = ActionType.NONE;
                        }
                        break;
                    case TOGGLE_ALOVED:
                        Room toggled = getRoom(camera.getWorldCoordinates(mousePosition),
                                camera.getCoordinatedDirection(mousePosition));
                        if(toggled != null){
                            toggled.toggleAloved();
                            actionType = ActionType.NONE;
                        }
                        break;
                }
            }
        }
        
    };
    
    enum ActionType{
        ADD_ROOM, ADD_ITEM, DELETE, NONE, ADD_DOOR_ROOM_1, ADD_DOOR_ROOM_2,
        SELECT_START, SELECT_FINISH, TOGGLE_ALOVED;
    }
    
    private Room getRoom(Vector3f cameraPosition, Vector3f cameraDirection){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cameraPosition, cameraDirection);
        editedLevel.collideWith(ray, results);
        if(results.size() > 0){
            CollisionResult closest = results.getClosestCollision();
            Geometry toCompare = closest.getGeometry();
            for(Room r: newRooms){
                //System.out.println(r);
                if(r.floor.equals(toCompare)){
                    return r;
                }
            }
        }
        return null;
    }
    
    private Vector3f getFloorContactPosition(Vector3f cameraPosition, Vector3f cameraDirection){
        Box b = new Box(10000.0f, 0.1f, 10000.0f);
                        Material m = new Material();
        Geometry floor = new Geometry("floor", b);
                        floor.setMaterial(m);
                        CollisionResults results = new CollisionResults();
                        Ray ray = new Ray(cameraPosition, cameraDirection);
                        floor.collideWith(ray, results);
                        CollisionResult result = results.getClosestCollision();
        if(result != null){
            return result.getContactPoint();
        } else {
            return Vector3f.ZERO;
        }
    }
    
    public void selectStart(){
        actionType = ActionType.SELECT_START;
    }
    
    public void selectFinish(){
        actionType = ActionType.SELECT_FINISH;
    }
    
    public void toggleAloved(){
        actionType = ActionType.TOGGLE_ALOVED;
    }
    
    public void setObstacles(){
        for(ObstacleType o : editedLevel.availableObst.keySet()){
            nifty.getScreen("obstacle_select").findNiftyControl(o.toString().toLowerCase() + "_field",
                    TextField.class).setText(editedLevel.availableObst.get(o).toString());
        }
        //nifty.getScreen("obstacle_select").findNiftyControl("time_limit", TextField.class)
        //        .setText(editedLevel.timeLimit+"");
        nifty.gotoScreen("obstacle_select");
    }
    
    public void saveObstacles(){
        for(ObstacleType o : ObstacleType.values()){
            TextField t = nifty.getScreen("obstacle_select").findNiftyControl(o.toString().
                    toLowerCase()+"_field",TextField.class);
            if(t != null){
                editedLevel.availableObst.put(o, Integer.parseInt(t.getText()));
            }
        }
        editedLevel.timeLimit = Integer.parseInt(nifty.getCurrentScreen()
                .findNiftyControl("time_limit", TextField.class).getText())*1000;
        nifty.gotoScreen("editing");
    }
    
    public void cancel(){
        nifty.gotoScreen("editing");
    }
    
    public void saveAsConfirm(){
        String levelName = nifty.getCurrentScreen().findNiftyControl("file_name", TextField.class).getText();
        editedLevel.name = "custom/" + levelName;
        save();
        nifty.gotoScreen("editing");
    }
    
    private void deleteRoom(Geometry toCompare){
        int toDelete = -1;
        for(int i=0; i<newRooms.size(); ++i){
            if(toCompare.equals(newRooms.get(i).floor)){
                toDelete = i;
                break;
            }
        }
        if(toDelete != -1){
            if(newRooms.get(toDelete).item == null){
                editedLevel.detachChild(newRooms.get(toDelete));
                newRooms.remove(toDelete);
            }
        }
    }
    
    private void deleteItem(Geometry toCompare){
        int toDelete = -1;
        for(int i=0; i<editedLevel.items.size(); ++i){
            if(toCompare.equals(editedLevel.items.get(i).model)){
                toDelete = i;
                break;
            }
        }
        if(toDelete != -1){
            editedLevel.detachChild(editedLevel.items.get(toDelete));
            editedLevel.items.get(toDelete).actualPosition.deleteItem();
            editedLevel.items.remove(toDelete);
        }
    }
    
    private void initializeGui(){
        typeSelector = nifty.getCurrentScreen().findNiftyControl("item_drop_down", DropDown.class);
        for(ObstacleType o : ObstacleType.values()){
            typeSelector.addItem(o.toString());
        }
        Element e = nifty.getScreen("obstacle_select").findElementByName("scroll_inside");
        if(e.getElements().size() == 0){
            System.out.println(e);
            for(final ObstacleType o : ObstacleType.values()){
                PanelBuilder builder = new PanelBuilder(o.toString()+"_panel"){{
                    childLayoutHorizontal();
                    height("10%");
                    width("100%");

                    control(new LabelBuilder(o.toString() + "_text", o.toString()+": "){{
                        height("100%");
                        width("50%");
                    }});

                    control(new TextFieldBuilder(o.toString().toLowerCase() + "_field", "0"){{
                        height("100%");
                        width("50%");
                    }});

                }};
                builder.build(nifty, nifty.getScreen("obstacle_select"), e);
            }
        }
    }
    
    @Override public void update(float tpf){
        if(actionType == ActionType.ADD_ROOM){
            Vector2f mousePosition = inputManager.getCursorPosition();
            tempRoom.setLocalTranslation(getFloorContactPosition(
                    camera.getWorldCoordinates(mousePosition),
                    camera.getCoordinatedDirection(mousePosition)));
        }
    }
    
    @NiftyEventSubscriber(id="file_list")
    public void onMyListSelectionChange(final String id, final ListBoxSelectionChangedEvent<String> event){
        String selectedItem = event.getSelection().get(0);
        nifty.getScreen("save_as").findNiftyControl("file_name", TextField.class)
                .setText(selectedItem);
    }
   
}
