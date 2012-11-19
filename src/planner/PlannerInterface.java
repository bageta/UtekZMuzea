package planner;

import game.Level;
/**
 *
 * @author Pavel
 */
public interface PlannerInterface {
    
    public ThiefAction[] makeNewPlan();
    
    public void setLevel(Level actualLevel);
    
}
