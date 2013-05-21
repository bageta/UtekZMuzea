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
 * modelovani problemu podle stavu lokaci
 * TODO: TEORETICKY FUNKCNI VERZE, TREBA OTESTOVAT
 * @author Pavel
 */
public class Planner2 implements PlannerInterface {
    
    Level levelState;
    Thief thief;
    
    public Planner2(Level actualLevel, Thief thief){
        levelState = actualLevel;
        this.thief = thief;
    }
    
    public Planner2(){}
    
    public void setLevel(Level actualLevel){
        levelState = actualLevel;
    }
    
    public ThiefAction[] makeNewPlan(){
        SasProblemBuilder problem = generateProblem();
        SasProblem sasProblem = problem.getSasProblem();
        
        IncrementalSolver planner = new IncrementalSolver(sasProblem);
        
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
            
            
            System.out.println("PLANOVAC - 2 - output: ");
            System.out.print(plan);
            
            PlanVerifier verifier = new PlanVerifier();
            boolean valid = verifier.verifyPlan(sasProblem, plan);
            if(valid){
                ArrayList<SasAction> actions = new ArrayList<SasAction>();
                System.out.println("Plan is valid");
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
                System.out.println("Plan in not valid");
            }
            
            System.out.println("PLANOVAC - 2 - output END -----------------------");
        } catch(TimeoutException e){
            return null;
        } catch(NonexistentPlanException e){
            return null;
        }
        return null;
    }
    
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
        
        //TODO: upravit tak, aby to korespondovalo s novymi akcemi
        for(Room location: levelState.rooms){
            for(Room location2: location.neigbours){
                System.out.println("CYCLE - START-------------------------------");
                System.out.println("MOVEACTION: form: " + roomState[location.index] + "to: " + roomState[location2.index]);
                addMoveThiefAction(problem, roomState[location.index], roomState[location2.index]);
                for(int j=2+levelState.obstacles.size()+levelState.items.size();
                        j<statesInRoom; ++j){
                    System.out.println("MOVE_WITH_ACTION: form: " + roomState[location.index] + " to: " + roomState[location2.index] + " item: " + j);
                    addMoveWithItemAction(problem, roomState[location.index],
                           roomState[location2.index], j);
                    System.out.println("PUT_ACTION: form: " + roomState[location.index] + " to: " + roomState[location2.index] + " item: " + j);
                    addPutDownItemAction(problem, roomState[location.index],
                           roomState[location2.index], j);
                    for(int obstacle=2; obstacle< levelState.obstacles.size()+2; ++obstacle){
                        if(levelState.obstacles.get(obstacle-2).type == levelState.items.get(j-levelState.items.size()-levelState.obstacles.size()-2).type){
                            System.out.println("USE_ACTION: form: " + roomState[location.index] + " to: " + roomState[location2.index] + " item: " + j + " obstacle: " + obstacle);
                            addUseItemAction(problem, roomState[location.index],
                                   roomState[location2.index], obstacle, j);
                        }
                    }
                }
                for(int item=2+levelState.obstacles.size();
                        item<2+levelState.obstacles.size()+levelState.items.size();
                        ++item){
                    System.out.println("PICK_ACTION: form: " + roomState[location.index] + " to: " + roomState[location2.index] + " item: " + item);
                    addPickUpItemAction(problem, roomState[location.index],
                            roomState[location2.index], item);
                }
            }
        }
        System.out.println("Projde checkpoint1");
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
                        System.out.println("INICIALNI PODMINKA: mistonost:" + location + "hodnota: " + (i+2+levelState.obstacles.size()+levelState.items.size()));
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
                    System.out.println("Mistnost " + location + " prekazka: " + (i+2));
                    wasAdded = true;
                }
            }
            if(!wasAdded){
                problem.addInitialStateCondition(new Condition(roomState[location],0));
            }
        }
        
        problem.addGoalCondition(new Condition(roomState[levelState.finish.index], 1));
        
        return problem;
    }
    
    private void addMoveThiefAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE,from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(from, 1));
        op.getPreconditions().add(new Condition(to, 0));
        
        op.getEffects().add(new Condition(from, 0));
        op.getEffects().add(new Condition(to, 1));
    }
    
    private void addUseItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int obstacle, int thiefAndItem){
        SasAction op = problem.newAction(new ThiefAction(ActionType.USE,from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(to, obstacle));
        op.getPreconditions().add(new Condition(from, thiefAndItem));
        
        op.getEffects().add(new Condition(from, 0));
        op.getEffects().add(new Condition(to, 1));
    }
    
    private void addPickUpItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int item){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PICK,from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(to, item));
        op.getPreconditions().add(new Condition(from, 1));
        
        op.getEffects().add(new Condition(to, item+levelState.items.size()));
        op.getEffects().add(new Condition(from, 0));
    }
    
    private void addMoveWithItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int thiefAndItem){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE, from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(to, 0));
        op.getPreconditions().add(new Condition(from, thiefAndItem));
        
        op.getEffects().add(new Condition (to, thiefAndItem));
        op.getEffects().add(new Condition(from, 0));
    }
    
    private void addPutDownItemAction(SasProblemBuilder problem, StateVariable from,
            StateVariable to, int thiefAndItem){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PUT, from.getId(), to.getId()));
        
        op.getPreconditions().add(new Condition(from, thiefAndItem));
        op.getPreconditions().add(new Condition(to, 0));
        
        op.getEffects().add(new Condition(from, thiefAndItem - levelState.items.size()));
        op.getEffects().add(new Condition(to, 1));
    }
    
}