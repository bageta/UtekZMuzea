package xml_support;

import game.Level;
import game.ObstacleType;
import game.Room;

import java.io.File;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Pavel Pilar
 */
public class LevelSaver {

    private Level levelRefernce;
    
    public LevelSaver(Level levelReference){
        this.levelRefernce = levelReference;
    }
    
    public void save(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            dbf.setValidating(false);
            
            DocumentBuilder builder = dbf.newDocumentBuilder();
            
            Document doc = builder.newDocument();
            
            makeSave(doc);
            
            TransformerFactory tf = TransformerFactory.newInstance();
            
            Transformer writer = tf.newTransformer();
            
            writer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            
            writer.transform(new DOMSource(doc),
                    new StreamResult(new File(levelRefernce.name + ".xml")));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void makeSave(Document doc){
        Element root = doc.createElement("level");
        root.appendChild(doc.createElement("name")).setTextContent(levelRefernce.name);
        root.appendChild(doc.createElement("timeLimit"))
                .setTextContent(levelRefernce.timeLimit+"");
        Element rooms = doc.createElement("rooms");
        for(Room r : levelRefernce.rooms){
            Element room = doc.createElement("room");
            room.appendChild(doc.createElement("index"))
                    .setTextContent(r.index+"");
            room.appendChild(doc.createElement("width"))
                    .setTextContent(r.getWidth()+"");
            room.appendChild(doc.createElement("height"))
                    .setTextContent(r.getHeight()+"");
            room.appendChild(doc.createElement("position"))
                    .setTextContent(r.getPosition().toString());
            if(r.item != null){
                room.appendChild(doc.createElement("item"))
                        .setTextContent(r.item.type.toString());
            }
            room.appendChild(doc.createElement("isAloved"))
                    .setTextContent(r.isAloved+"");
            Element neighbours = doc.createElement("neighbours");
            for(Room nr: r.neigbours){
                neighbours.appendChild(doc.createElement("neighbour")).
                        setTextContent(nr.index+"");
            }
            room.appendChild(neighbours);
            rooms.appendChild(room);
        }
        root.appendChild(rooms);
        
        root.appendChild(doc.createElement("start"))
                .setTextContent(levelRefernce.start.index+"");
        
        root.appendChild(doc.createElement("finish"))
                .setTextContent(levelRefernce.finish.index+"");
        
        Element obstacles = doc.createElement("avalibleObst");
        
        for(ObstacleType t: levelRefernce.availableObst.keySet()){
            Element obstacle = doc.createElement("obstacle");
            obstacle.appendChild(doc.createElement("type"))
                    .setTextContent(t.toString());
            obstacle.appendChild(doc.createElement("count"))
                    .setTextContent(levelRefernce.availableObst.get(t).toString());
            obstacles.appendChild(obstacle);
        }
        
        root.appendChild(obstacles);
        
        doc.appendChild(root);
    }
    
}
