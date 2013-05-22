package plannertester;

import game.Level;
import game.Thief;

import planner.*;
import time.StopWatch;

/**
 * Třída pro testování výkonu plánovačů, neověřuje správnost plánu.
 * @author Pavel Pilař
 */
public class Tester {
    
    /** levely k otestovani */
    private static Level[] toTest;
    /** kvuli planovani je potreba zlodej */
    Thief thief;
    
    /**
     * Spuštění testů
     * @param args jako argumenty se předávají cesty k jednotlivým levelům
     */
    public static void main(String[] args){
        Tester tester = new Tester();
        toTest = new Level[args.length];
        for(int i=0; i<args.length; ++i){ 
            toTest[i] = new Level(args[i], null);
        }
        tester.thief = new Thief(null, toTest[0]);
        PlannerInterface[] planners = {new Planner2(null, tester.thief),
            new Planner(null, tester.thief)};
        tester.test(planners);
    }
    
    /**
     * spustí samotné testování pro předané plánovače. Na výstup vypíše výsledky
     * testů.
     * @param planners jednotlivé plánovače v poli
     */
    private void test(PlannerInterface[] planners){
        for(int i=0; i< planners.length; ++i){
            StopWatch watch1 = new StopWatch();
            watch1.start();
            System.out.println("Planner " + i + " test output: ");
            for(int j=0; j< toTest.length; ++j){
                thief = new Thief(null, toTest[j]);
                StopWatch watch2 = new StopWatch();
                watch2.start();
                planners[i].setLevel(toTest[j]);
                planners[i].makeNewPlan();
                watch2.stop();
                System.out.println("Test: " + j + " cas: " + watch2.getElapsedMillis());
            }
            watch1.stop();
            System.out.println("Celkovy cas: " + watch1.getElapsedMillis());
            System.out.println("Planner " + i + " output end");
        }                
    }
    
}
