package plannertester;

import planner.*;
import game.Level;

/**
 *
 * @author Pavel
 */
public class Tester {
    
    private static Level[] toTest;
    
    public static void main(){
        TestGenerator tg = new TestGenerator();
        toTest = tg.getTestSet();
        Tester tester = new Tester();
        Planner2 planner = new Planner2();
        tester.test(planner);
    }
    
    private void test(PlannerInterface planner){
        
        
    }
    
}
