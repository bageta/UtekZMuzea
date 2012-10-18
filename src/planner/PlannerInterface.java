package planner;

import plansat.sasToSat.PlanningProblem;
/**
 *
 * @author Pavel
 */
public interface PlannerInterface {
    
    public void makeNewPlan();
    PlanningProblem generateProblem();
}
