package xml_support;

import com.jme3.math.Vector3f;
import game.Level;
import game.ObstacleType;
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
            
            System.out.println(path + ".xml");
            Document doc = builder.parse("levels/" + path+ ".xml");
            
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
        list = doc.getElementsByTagName("next");
        if(list.getLength() > 0){
            levelReference.nextLevelName = list.item(0).getTextContent();
        }
        list = doc.getElementsByTagName("index");
        if(list.getLength() > 0){
            levelReference.index = Integer.parseInt(list.item(0).getTextContent());
        }
        list = doc.getElementsByTagName("timeLimit");
        if(list.getLength() > 0){
            levelReference.timeLimit = Integer.parseInt(list.item(0).getTextContent());
        }
        list = doc.getElementsByTagName("room");
        levelReference.rooms = new Room[list.getLength()];
        for(int i = 0; i < list.getLength(); ++i){
            Element room = (Element)list.item(i);
            int index = 0;
            float width, height = width = 0;
            float x,y,z = x = y = 0.0f;
            boolean isAloved = true;
            NodeList childs = room.getElementsByTagName("index");
            if(childs.getLength() > 0){
                index = Integer.parseInt(childs.item(0).getTextContent());
            }
            childs = room.getElementsByTagName("width");
            if(childs.getLength() > 0){
                width = Float.parseFloat(childs.item(0).getTextContent());
            }
            childs = room.getElementsByTagName("height");
            if(childs.getLength() > 0){
                height = Float.parseFloat(childs.item(0).getTextContent());
            }
            childs = room.getElementsByTagName("position");
            if(childs.getLength() > 0){
                String s = childs.item(0).getTextContent();
                String[] ss = s.split(",");
                ss[0] = ss[0].substring(1);
                ss[2] = ss[2].substring(0, ss[2].length()-1);
                x = Float.parseFloat(ss[0]);
                y = Float.parseFloat(ss[1]);
                z = Float.parseFloat(ss[2]);
            }
            childs = room.getElementsByTagName("isAloved");
            if(childs.getLength() > 0){
                isAloved = Boolean.valueOf(childs.item(0).getTextContent());
            }
            levelReference.rooms[i] = new Room(new Vector3f(x, y, z), width,
                    height, index, isAloved, levelReference.assetManager);
        }
        
        list = doc.getElementsByTagName("room");
        for(int i = 0; i<list.getLength(); ++i){
            Element room = (Element)list.item(i);
            NodeList childs = room.getElementsByTagName("neighbour");
            for(int j=0; j<childs.getLength(); ++j){
                //System.out.println("DOSTANU SE SEM?");
                int neighbourIndex = Integer.parseInt(childs.item(j).getTextContent());
                levelReference.rooms[i].addNeighbour(levelReference.rooms[neighbourIndex]);
            }
            childs = room.getElementsByTagName("item");
            if(childs.getLength() > 0){
                ObstacleType itemType = ObstacleType.valueOf(childs.item(0).getTextContent());
                levelReference.addItem(itemType, levelReference.rooms[i]);
            }
        }
        
        list = doc.getElementsByTagName("start");
        levelReference.start = levelReference.rooms[
                Integer.parseInt(list.item(0).getTextContent())];
        
        list = doc.getElementsByTagName("finish");
        levelReference.finish = levelReference.rooms[
                Integer.parseInt(list.item(0).getTextContent())];
        
        list = doc.getElementsByTagName("obstacle");
        for(int i = 0; i<list.getLength(); ++i){
            Element item = (Element)list.item(i);
            NodeList childs = item.getElementsByTagName("type");
            if(childs.getLength() > 0){
                ObstacleType type = ObstacleType.valueOf(childs.item(0).getTextContent());
                childs = item.getElementsByTagName("count");
                if(childs.getLength() > 0){
                    levelReference.availableObst.put(type,
                    Integer.parseInt(childs.item(0).getTextContent()));
                }
            }
        }
    }
}
