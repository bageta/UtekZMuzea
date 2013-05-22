package time;

/**
 * Třída obsahující statické metody pro převod časových jednotek.
 * @author Pavel Pilař
 */
public class Converter {
    
    /**
     * Převádí milisekundy na sekundy
     * @param millis čas v milisekundách
     * @return čas v sekundách
     */
    public static long millisToSeconds(long millis){
        return millis/1000;
    }
    
    /**
     * Převádí milisekundy na minuty. Vrací minuty ve formátu string jako:
     * minuty : sekundy
     * @param millis čas v milisekundách
     * @return čas v sekundách
     */
    public static String millisToMinutes(long millis){
        long seconds = millisToSeconds(millis);
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes + " : " + seconds;
    }
    
}
