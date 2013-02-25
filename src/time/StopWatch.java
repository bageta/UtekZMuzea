package time;

/**
 *
 * @author Pavel
 */
public class StopWatch {
    
    private long startTime, stopTime;
    private boolean isRunning;
    
    public StopWatch(){
        startTime = 0;
        stopTime = 0;
        isRunning = false;
    }
    
    public void start(){
        startTime = System.currentTimeMillis();
        isRunning = true;
    }
    
    public void stop(){
        stopTime = System.currentTimeMillis();
        isRunning = false;
    }
    
    public void restart(){
        startTime = System.currentTimeMillis() - getElapsedMillis();
        isRunning = true;
    }
    
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
