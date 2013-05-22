package planner;

import game.Level;
import game.Room;
import game.Thief;

import java.util.ArrayList;
import java.util.List;

import core.planning.NonexistentPlanException;
import core.planning.TimeoutException;
import core.planning.forwardSearch.BasicForwardSearchSolver;
import core.planning.model.Condition;
import core.planning.model.SasAction;
import core.planning.model.SasParallelPlan;
import core.planning.model.SasProblem;
import core.planning.model.StateVariable;
import core.planning.sase.optimizer.PlanVerifier;
import core.planning.sase.sasToSat.SasProblemBuilder;
import core.planning.sase.sasToSat.incremental.IncrementalSolver;

/**
 * Modelovani plánovacího problému podle stavu lokaci. Způsob zvolený ve hře.
 * Tento způsob je detailně popsán a vysvětlen v přiloženém textu práce.
 * @author Pavel Pilař
 */
public class Planner2 implements PlannerInterface {
    
    /** reference na level */
    Level levelState;
    /** reference na zloděje ve hře */
    Thief thief;
    
    /** konstruktor plánovače. */
    public Planner2(Level actualLevel, Thief thief){
        levelState = actualLevel;
        this.thief = thief;
    }
    
    /**
     * Přiřadí plánovači nový level.
     * @param actualLevel reference na level
     */
    public void setLevel(Level actualLevel){
        levelState = actualLevel;
    }
    
    /**
     * Vytvoří pro zloděje nový plán.
     * @return vrací plán pro zloděje jako pole akcí typu ThiefAction
     */
    public ThiefAction[] makeNewPlan(){
        
        //vygenerujeme problém:
        SasProblemBuilder problem = generateProblem();
        SasProblem sasProblem = problem.getSasProblem();
        
        //solver generující optimální plán:
        IncrementalSolver planner = new IncrementalSolver(sasProblem);
        
        //rychlý solver, používaný pro zjištění existence plánu 
        BasicForwardSearchSolver planner2 = new BasicForwardSearchSolver(sasProblem);
        
        try{
            planner2.getSettings().setTimelimit(3);
            planner2.solve();       
        } catch(TimeoutException e){
            return null;
        } catch(NonexistentPlanException e){
            return null;
        }
        try{
            planner.getSettings().setTimelimit(5);
            SasParallelPlan plan = planner.solve();
            
            PlanVerifier verifier = new PlanVerifier();
            boolean valid = verifier.verifyPlan(sasProblem, plan);
            if(valid){
                ArrayList<SasAction> actions = new ArrayList<SasAction>();
                for(List<SasAction> list: plan.getPlan()){
                    for(SasAction action : list){
                        actions.add(action);
                    }
                }
                ThiefAction[] thiefActions = new ThiefAction[actions.size()];
                for(int i = 0; i<thiefActions.length; ++i){
                    thiefActions[i] = (ThiefAction)actions.get(i).getActionInfo();
                }
                return thiefActions;
            } else {
                return null;
            }
        } catch(TimeoutException e){
            return null;
        } catch(NonexistentPlanException e){
            return null;
        }
    }
    
    /**
     * Metoda generující problém pro plánovač.
     * @return vrací problém jako objekt typu SasProblemBuilder
     */
    private SasProblemBuilder generateProblem(){
        SasProblemBuilder problem = new SasProblemBuilder();
        
        StateVariable[] roomState = new StateVariable[levelState.rooms.length];
        int statesInRoom = 2+levelState.obstacles.size()+(2*levelState.items.size());
        for(int i=0; i<levelState.rooms.length; ++i){
            /*pro kazdou mistnost na mape si popiseme stavy, ktere v ni mohou nastat:
             * 0: prazdna mistnost
             * 1: zlodej
             * 2,..,k: 1,..,k-2. prekazka
             * k+1,..,l: 1,..,l-k-2. item
             * l+1,..,l+(l-k)+1: zlodej + 1,..,l-k-2. item
            */
            roomState[i] = problem.newVariable("room" + i, statesInRoom);
        }
        
        for(Room location: levelState.rooms){
            for(Room location2: location.neigbours){
                //přidáme všechny možné přesuny:
                addMoveThiefAction(problem, roomState[location.index], roomState[location2.index]);
                for(int j=2+levelState.obstacles.size()+levelState.items.size();
                        j<statesInRoom; ++j){
                    //přidáme všechny možné přesuny s věcí:
                    addMoveWithItemAction(problem, roomState[location.index],
                           roomState[location2.index], j);
                    //přidáme všechny možné akce položení věci:
                    addPutDownItemAction(problem, roomState[location.index],
                           roomState[location2.index], j);
                    for(int obstacle=2; obstacle< levelState.obstacles.size()+2; ++obstacle){
                        if(levelState.obstacles.get(obstacle-2).type == levelState.items.get(j-levelState.items.size()-levelState.obstacles.size()-2).type){
                            //přidáme všechny možné akce použítí věci:
                            addUseItemAction(problem, roomState[location.index],
                                   roomState[location2.index], obstacle, j);
                        }
                    }
                }
                for(int item=2+levelState.obstacles.size();
                        item<2+levelState.obstacles.size()+levelState.items.size();
                        ++item){
                    //přidáme všechny možné akce zvednutí věci:
                    addPickUpItemAction(problem, roomState[location.index],
                            roomState[location2.index], item);
                }
            }
        }
        //vložíme počáteční podmínky:
        for(int location=0; location< levelState.rooms.length; ++location){
            boolean wasAdded = false;
            if(levelState.rooms[location]==thief.getActualPosition()
                    && thief.getCarrying() == null){
                problem.addInitialStateCondition(new Condition(roomState[location], 1));
                wasAdded = true;
            }
            if(levelState.rooms[location]==thief.getActualPosition()
                    && thief.getCarrying() != null){
                for(int i=0; i<levelState.items.size(); ++i){
                    if(levelState.items.get(i)==thief.getCarrying()){
                        problem.addInitialStateCondition(new Condition(roomState[location],
                                i+2+levelState.obstacles.size()+levelState.items.size()));
                        wasAdded = true;
                    }
                }
            }
            for(int i=0; i<levelState.items.size(); ++i){
                if(levelState.items.get(i).actualPosition == levelState.rooms[location]){
                    problem.addInitialStateCondition(new Condition(roomState[location], i+2+levelState.obstacles.size()));
                    wasAdded = true;
                }
            }
            for(int i=0; i<levelState.obstacles.size(); ++i){
                if(levelState.obstacles.get(i).getPosition() == levelState.rooms[location]){
                    problem.addInitialStateCondition(new Condition(roomState[location], i+2));
                    wasAdded = true;
                }
            }
            if(!wasAdded){
                problem.addInitialStateCondition(new Condition(roomState[location],0));
            }
        }
        
        //přidáme cílovou podmínku:
        problem.addGoalCondition(new Condition(roomState[levelState.finish.index], 1));
        
        return problem;
    }
    
    /**
     * Metoda pro přidání akce přesunu.
     * @param problem problém do kterého se akce přidá
     * @param from odkud akce vede
     * @param to kam akce vede
     */
    private void addMoveThiefAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE,from.getId(), to.getId()));
        
        //nastavení podmínek, které akce musí splňovat:
        op.getPreconditions().add(new Condition(from, 1));
        op.getPreconditions().add(new Condition(to, 0));
        
        //nastavení efektů akce:
        op.getEffects().add(new Condition(from, 0));
        op.getEffects().add(new Condition(to, 1));
    }
    
    /**
     * Metoda pro přidání akce použití věci.
     * @param problem problém do kterého se akce přidá
     * @param from odkud akce vede
     * @param to kam akce vede
     * @param obstacle hodnota reprezentující překážku
     * @param thiefAndItem hodnota reprezentující zloděj a věc 
     */
    private void addUseItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int obstacle, int thiefAndItem){
        SasAction op = problem.newAction(new ThiefAction(ActionType.USE,from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(to, obstacle));
        op.getPreconditions().add(new Condition(from, thiefAndItem));
        
        op.getEffects().add(new Condition(from, 0));
        op.getEffects().add(new Condition(to, 1));
    }
    
    /**
     * Metoda pro přidání akce zvednutí.
     * @param problem problém do kterého se akce přidá
     * @param from odkud akce vede
     * @param to kam akce vede
     * @param item hodnota reprezentující věc
     */
    private void addPickUpItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int item){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PICK,from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(to, item));
        op.getPreconditions().add(new Condition(from, 1));
        
        op.getEffects().add(new Condition(to, item+levelState.items.size()));
        op.getEffects().add(new Condition(from, 0));
    }
    
    /**
     * Metoda pro přesun zloděje s věcí.
     * @param problem problém do kterého se akce přidá
     * @param from odkud akce vede
     * @param to kam akce vede
     * @param thiefAndItem hodnota reprezentující zloděje a věc 
     */
    private void addMoveWithItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int thiefAndItem){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE, from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(to, 0));
        op.getPreconditions().add(new Condition(from, thiefAndItem));
        
        op.getEffects().add(new Condition (to, thiefAndItem));
        op.getEffects().add(new Condition(from, 0));
    }
    
    /**
     * Metoda pro položení věci do místnosti.
     * @param problem problém do kterého se akce přidá
     * @param from odkud akce vede
     * @param to kam akce vede
     * @param thiefAndItem hodnota reprezentující zloděje a věc
     */
    private void addPutDownItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int thiefAndItem){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PUT, from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(from, thiefAndItem));
        op.getPreconditions().add(new Condition(to, 0));
        
        op.getEffects().add(new Condition(from, thiefAndItem - levelState.items.size()));
        op.getEffects().add(new Condition(to, 1));
    }
    
}