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
        this();
        timeToCount = millies;
    }
    
    public void start(){
        stopWatch.start();
    }
    
    public void pause(){
        stopWatch.stop();
    }
    
    public void unpause(){
        stopWatch.restart();
    }
    
    public long getRemainingMillis(){
        long remain = timeToCount - stopWatch.getElapsedMillis();
        if(remain < 0){
            remain = 0;
        }
        return remain;
    }
    
    public String getRemainingMinutes(){
        return Converter.millisToMinutes(getRemainingMillis());
    }
}
