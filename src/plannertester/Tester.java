package plannertester;

import planner.*;
import game.Level;

/**
 *
 * @author Pavel Pilař
 */
public class Tester {
    
    private static Level[] toTest;
    
    public static void main(){
        TestGenerator tg = new TestGenerator();
        //toTest = tg.getTestSet();
        Tester tester = new Tester();
        PlannerInterface[] planners = {new Planner2()};
        tester.test(planners);
    }
    
    private void test(PlannerInterface[] planners){
        for(int i=0; i< planners.length; ++i){
            //start timer
            System.out.println("Planner " + i + " test output: ");
            for(int j=0; j< toTest.length; ++j){
                //start timer 2
                planners[i].setLevel(toTest[j]);
                planners[i].makeNewPlan();
                //stop timer 2
            }
            //stop timer - vypis celkovy cas
            System.out.println("Planner " + i + " output end");
        }                
    }
    
}
