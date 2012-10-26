package planner;

import game.Level;
import game.Main;
import game.Room;

import java.util.List;
import java.util.ArrayList;

import freeLunch.planning.NonexistentPlanException;
import freeLunch.planning.TimeoutException;
import freeLunch.planning.cmdline.Settings;
import freeLunch.planning.model.Condition;
import freeLunch.planning.model.SasAction;
import freeLunch.planning.model.SasParallelPlan;
import freeLunch.planning.model.SasProblem;
import freeLunch.planning.model.StateVariable;
//import freeLunch.planning.model.StringActionInfo;
import freeLunch.planning.sase.optimizer.PlanVerifier;
import freeLunch.planning.sase.sasToSat.PlanningProblem;
import freeLunch.planning.sase.sasToSat.incremental.IncrementalSolver;

/**
 *
 * @author Pavel
 */
public class Planner{
    
    Level levelState;
    
    public Planner(Level levelState){
        this.levelState = levelState;
    }
    
    public ThiefAction[] makeNewPlan(){
        PlanningProblem problem = generateProblem();
        SasProblem sasProblem = problem.getSasProblem();
        
        IncrementalSolver planner = new IncrementalSolver(sasProblem);
        
        try{
            Settings.getSettings().setTimeout(3);
            SasParallelPlan plan = planner.solve();
            
            System.out.println("PLANOVAC - 1 - output: ");
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
            
            System.out.println("PLANOVAC - 1 - output END -----------------------");
            
        } catch(TimeoutException e){
            System.out.println("Vyprsel cas: " + e);
        } catch(NonexistentPlanException e){
            System.out.println("Problem nema reseni: " + e);
        }
        return null;
    }
    
    private PlanningProblem generateProblem(){
        
        PlanningProblem problem = new PlanningProblem();
        
        //StateVariable pro polohu zlodeje:
        StateVariable thiefLocation = problem.newVariable("thief", levelState.rooms.length);
        
        //StateVarialbes udavajici polohy prekazek:
        
            StateVariable[] obstaclesLocations = new StateVariable[levelState.obstacles.size()];
            StateVariable[] obstacleActive = new StateVariable[levelState.obstacles.size()];
            for(int i=0; i<levelState.obstacles.size(); ++i){
                obstaclesLocations[i] = problem.newVariable("obstacle" + i, levelState.rooms.length);
                obstacleActive[i] = problem.newVariable("obstacleActive" + i, 2);
            }

        //StateVariables udavajici polohy veci nutnych k ostraneni prekazek:
        StateVariable[] itemsLocations = new StateVariable[levelState.items.size()];
        for (int i=0; i<levelState.items.size(); ++i){
            itemsLocations[i] = problem.newVariable("item" + i, levelState.rooms.length + 1);
        }
        
        for(Room r : levelState.rooms){
            for(Room rr: r.neigbours)
                if(!rr.hasObstacle())
                    addMoveThiefAction(problem, thiefLocation, r.index, rr.index);
        }
        
        for(Room r : levelState.rooms){
            for(Room rr: r.neigbours)
                for(int i=0; i<levelState.obstacles.size(); ++i)
                    addMoveThiefOverAction(problem, thiefLocation, r.index, rr.index,
                            obstaclesLocations[i]);
        }
        
        for(int item=0; item< levelState.items.size(); ++item){
            for(int location=0; location< levelState.rooms.length; ++location){
                addPickUpItemAction(problem, thiefLocation, itemsLocations[item], location);
                addPutDownItemAction(problem, thiefLocation, itemsLocations[item], location);
            }
        }
        
        for(int item=0; item< levelState.items.size(); ++item){
            for(int obstacle=0; obstacle< levelState.obstacles.size(); ++obstacle){
                if(levelState.items.get(item).type==levelState.obstacles.get(obstacle).type){
                    for(int location=0; location< levelState.rooms.length; ++location){
                        for(int location1=0; location1< levelState.rooms.length; ++location1){
                            if(location == location1)
                                continue;
                            addUseItemAction(problem, thiefLocation, itemsLocations[item], 
                                    obstaclesLocations[obstacle], obstacleActive[obstacle], location, location);
                        }
                    }
                }
            }
        }
        
        problem.addInitialStateCondition(new Condition(thiefLocation, Main.thief.actualPosition.index));
        
        for(int i=0; i<levelState.items.size(); ++i){
            problem.addInitialStateCondition(new Condition(itemsLocations[i],
                    levelState.items.get(i).actualPosition.index));
        }
        
        for(int i=0; i<levelState.obstacles.size(); ++i){
            problem.addInitialStateCondition(new Condition(obstaclesLocations[i],
                    levelState.obstacles.get(i).getPosition().index));
            problem.addInitialStateCondition(new Condition(obstacleActive[i],0));
        }
        
        problem.addGoalCondition(new Condition(thiefLocation, levelState.finish.index));
        return problem;
    }
    
    private void addMoveThiefAction(PlanningProblem problem, StateVariable thiefLocation, int from, int to){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE, from, to));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("move %d %d", from, to)));
        
        op.getPreconditions().add(new Condition(thiefLocation, from));
        
        op.getEffects().add(new Condition(thiefLocation, to));
    }
    
    private void addMoveThiefOverAction(PlanningProblem problem, StateVariable thiefLocation, int from, int to, StateVariable obstacleActive){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE, from, to));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("move %d %d", from, to)));
        
        op.getPreconditions().add(new Condition(thiefLocation, from));
        
        op.getEffects().add(new Condition(thiefLocation, to));
        
        op.getPreconditions().add(new Condition(obstacleActive, 1));
    }
    
    private void addPickUpItemAction(PlanningProblem problem, StateVariable thiefLocation,
            StateVariable itemLocation, int location){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PICK, itemLocation.getId(), location));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("pick %d %d", itemLocation.getName(), location)));
        
        op.getPreconditions().add(new Condition(itemLocation, location));
        
        op.getEffects().add(new Condition(itemLocation, levelState.rooms.length+1));
        
        op.getPreconditions().add(new Condition(thiefLocation, location));
    }
    
    private void addUseItemAction(PlanningProblem problem, StateVariable thiefLocation, 
            StateVariable itemLocation, StateVariable obstacleLocation, StateVariable obstacleActive,
            int from, int to){
        SasAction op = problem.newAction(new ThiefAction(ActionType.USE, itemLocation.getId(), to));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("use %d %d", itemLocation.getName(), to)));
        
        op.getPreconditions().add(new Condition(obstacleActive, 0));
        op.getPreconditions().add(new Condition(obstacleLocation, levelState.rooms.length + 1));
        
        op.getEffects().add(new Condition(obstacleActive, 1));
        op.getEffects().add(new Condition(obstacleLocation, to));
        
        op.getPreconditions().add(new Condition(thiefLocation, from));
        op.getPreconditions().add(new Condition(obstacleLocation, to));
    }
    
    private void addPutDownItemAction(PlanningProblem problem, StateVariable thiefLocation,
            StateVariable itemLocation, int location){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PUT,itemLocation.getId(), thiefLocation.getId()));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("put %d %d", itemLocation.getName(), thiefLocation)));
        
        op.getPreconditions().add(new Condition(itemLocation, levelState.rooms.length + 1));
        
        op.getEffects().add(new Condition(itemLocation, location));
        
        op.getPreconditions().add(new Condition(thiefLocation, location));
    }
}