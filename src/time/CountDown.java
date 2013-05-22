package time;

/**
 * Třída, která slouží jako odpočítavadlo od daného času.
 * @author Pavel Pilař
 */
public class CountDown {
    
    /** k odpočítávání používáme stopky */
    private StopWatch stopWatch;
    /** čas k odpočtu v milisekundách */
    private long timeToCount;
    
    /**
     * Konstruktor plánovače, bez předem daného času
     */
    public CountDown(){
        stopWatch = new StopWatch();
    }
    
    /**
     * Konstruktor plánovače, s nastavením času
     * @param millies čas v milisekundách 
     */
    public CountDown(long millies){
        this();
        timeToCount = millies;
    }
    
    /**
     * zahájení odpočtu
     */
    public void start(){
        stopWatch.start();
    }

    /**
     * pozastavení odpočtu
     */
    public void pause(){
        stopWatch.stop();
    }
    
    /**
     * znovuspuštění odpočtu
     */
    public void unpause(){
        stopWatch.restart();
    }
    
    /**
     * vrátí zbývající čas v milisekundách.
     * @return zbývající čas
     */
    public long getRemainingMillis(){
        long remain = timeToCount - stopWatch.getElapsedMillis();
        if(remain < 0){
            remain = 0;
        }
        return remain;
    }
    
    /**
     * vrací zbývající čas jako text v minutách.
     * @return zbývající čas
     */
    public String getRemainingMinutes(){
        return Converter.millisToMinutes(getRemainingMillis());
    }
}
