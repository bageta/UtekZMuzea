package time;

/**
 * Třída sloužící jako stopky.
 * @author Pavel Pilař
 */
public class StopWatch {
    
    /** čas spuštění a zastavení */
    private long startTime, stopTime;
    /** proměnná určující, zda stopky běží */
    private boolean isRunning;
    
    /**
     * konstruktor stopek.
     */
    public StopWatch(){
        startTime = 0;
        stopTime = 0;
        isRunning = false;
    }
    
    /**
     * spuštění stopek.
     */
    public void start(){
        startTime = System.currentTimeMillis();
        isRunning = true;
    }
    
    /**
     * zastavení stopek.
     */
    public void stop(){
        stopTime = System.currentTimeMillis();
        isRunning = false;
    }
    
    /**
     * znovuspuštění stopek
     */
    public void restart(){
        startTime = System.currentTimeMillis() - getElapsedMillis();
        isRunning = true;
    }
    
    /**
     * vrátí uběhlý čas.
     * @return uběhlý čas v milisekundách
     */
    public long getElapsedMillis(){
        long elapsed;
        if(isRunning){
            elapsed = System.currentTimeMillis() - startTime;
        } else {
            elapsed = stopTime - startTime;
        }
        return elapsed;
    }
}
