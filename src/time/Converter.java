/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package time;

/**
 *
 * @author Pavel
 */
public class Converter {
    
    public static long millisToSeconds(long millis){
        return millis/1000;
    }
    
    public static String millisToMinutes(long millis){
        long seconds = millisToSeconds(millis);
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes + " : " + seconds;
    }
    
}
