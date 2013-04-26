package xml_support;

import com.jme3.math.Vector3f;
import game.Level;
import game.Room;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 *
 * @author Pavel Pilar
 */
public class LevelLoader {
    
    private Level levelReference;
    private String path;
    
    public LevelLoader(Level levelReference, String path){
        this.levelReference = levelReference;
        this.path = path;
    }
    
    public void load(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            dbf.setValidating(false);
            
            DocumentBuilder builder = dbf.newDocumentBuilder();
            
            Document doc = builder.parse(path);
            
            makeLoad(doc);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void makeLoad(Document doc){
        NodeList list = doc.getElementsByTagName("name");
        if(list.getLength() > 0){
            levelReference.name = list.item(0).getTextContent();
        }
        list = doc.getElementsByTagName("timeLimit");
        if(list.getLength() > 0){
            levelReference.timeLimit = Integer.parseInt(list.item(0).getTextContent());
        }
        list = doc.getElementsByTagName("room");
        levelReference.rooms = new Room[list.getLength()];
        for(int i = 0; i < list.getLength(); ++i){
            Element room = (Element)list.item(i);
            int index,width,height = index = width = 0;
            float x,y,z = x = y = 0.0f;
            boolean isAloved = true;
            NodeList childs = room.getElementsByTagName("index");
            if(childs.getLength() > 0){
                index = Integer.parseInt(childs.item(0).getTextContent());
            }
            childs = room.getElementsByTagName("width");
            if(childs.getLength() > 0){
                width = Integer.parseInt(childs.item(0).getTextContent());
            }
            childs = room.getElementsByTagName("height");
            if(childs.getLength() > 0){
                height = Integer.parseInt(childs.item(0).getTextContent());
            }
            childs = room.getElementsByTagName("position");
            if(childs.getLength() > 0){
                String s = childs.item(0).getTextContent();
                String[] ss = s.split(",");
                x = Float.parseFloat(ss[0]);
                y = Float.parseFloat(ss[1]);
                z = Float.parseFloat(ss[2]);
            }
            childs = room.getElementsByTagName("isAloved");
            if(childs.getLength() > 0){
                isAloved = Boolean.getBoolean(childs.item(0).getTextContent());
            }
            levelReference.rooms[i] = new Room(new Vector3f(x, y, z), width,
                    height, index, isAloved, levelReference.assetManager);
        }
    }
}
