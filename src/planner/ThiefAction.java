package planner;

import freeLunch.planning.model.ActionInfo;
/**
 *
 * @author Pavel
 */
public class ThiefAction implements ActionInfo{
    
    public ActionType actionType;
    public int from, to;
    
    public ThiefAction(ActionType actionType, int from, int to){
        this.actionType = actionType;
        this.from = from;
        this.to = to;
    }
    
    @Override public String getName(){
        return "action" + actionType.name() + ": " + from + " " + to;
    }
}
