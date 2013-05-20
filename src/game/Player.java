package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Třída sloužící k ukládání hráčova dosaženého postupu. V budoucnosti se zde
 * bude ukládat tak napříkald dosažené skóre nejlepší časy pro jednotlivé level
 * atd.
 * @author Pavel Pilař
 */
public class Player implements Serializable {
    
    public int levelAchived = 1;
    
    public static Player load() throws IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("player.dat"));
        return (Player)in.readObject();
    }
    
    public void save() throws IOException{
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("player.dat"));
        out.writeObject(this);
    }
    
}
