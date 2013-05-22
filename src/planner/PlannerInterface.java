package planner;

import game.Level;

/**
 * Interface, který by měl každý plánovač splňovat. Plánovač implementují tento
 * interface je bez velkých úprav kodu použitý ve hře.
 * @author Pavel Pilař
 */
public interface PlannerInterface {
    
    /**
     * Plánovač musí vracet plán v daném formátu.
     * @return vracet plán jako pole typu ThiefAction 
     */
    public ThiefAction[] makeNewPlan();
    
    /**
     * Plánovači musí jít přiřadit level.
     * @param actualLevel reference na level 
     */
    public void setLevel(Level actualLevel);
    
}
