package planner;

import core.planning.model.ActionInfo;

/**
 * Třída reprezentující akci zloděje. Slouží pro předání plánu z plánovače zloději
 * @author Pavel Pilař
 */
public class ThiefAction implements ActionInfo{
    
    /** typ akce */
    public ActionType actionType;
    /** index zdrojové a cílové místnosti */
    public int from, to;
    
    /**
     * Konstruktor akce zloděje. Nastaví se příslušný typ a indexy místností.
     * @param actionType typ akce
     * @param from zdrojová místnost
     * @param to cílová místnost
     */
    public ThiefAction(ActionType actionType, int from, int to){
        this.actionType = actionType;
        this.from = from;
        this.to = to;
    }
    
    /**
     * Vypíše typ akce a místnosti v přehledném formátu.
     * @return String popisující akci přívětivě pro uživatele
     */
    @Override public String getName(){
        return "action" + actionType.name() + ": " + from + " " + to;
    }
}
