package helper;

import com.jme3.math.Vector3f;

/**
 *
 * @author Pavel
 */
public class Position {
    
    public static boolean isClose(Vector3f position1, Vector3f position2, float diff){
        return (Math.abs(position1.x-position2.x)<diff &&
                Math.abs(position1.z - position2.z)<diff);
    }
    
}