/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package time;

/**
 *
 * @author Pavel
 */
public class CountDown {
    
    private StopWatch stopWatch;
    private long timeToCount;
    
    public CountDown(){
        stopWatch = new StopWatch();
    }
    
    public CountDown(long millies){
        super();
        timeToCount = millies;
    }
    
    public void start(){
        stopWatch.start();
    }
    
    public long getRemainingMillis(){
        long remain;
        remain = timeToCount - stopWatch.getElapsedMillis();
        return remain;
    }
}
