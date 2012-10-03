package game;

import launcher.Settings;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import plansat.optimizer.PlanVerifier;
import plansat.sasToSat.PlanningProblem;
import plansat.sasToSat.incremental.IncrementalSolver;
import plansat.sasToSat.model.Condition;
import plansat.sasToSat.model.Operator;
import plansat.sasToSat.model.SasParallelPlan;
import plansat.sasToSat.model.SasProblem;
import plansat.sasToSat.model.StateVariable;
/**
 *
 * @author Pavel
 */
public class Planner {
    
    Level levelState;
    
    public Planner(Level levelState){
        this.levelState = levelState;
    }
    
    public void makeNewPlan(){
        PlanningProblem problem = generateProblem();
        SasProblem sasProblem = problem.getSasProblem();
        
        IncrementalSolver planner = new IncrementalSolver(sasProblem);
        
        try{
            Settings.getSettings().setTimeout(3);
            SasParallelPlan plan = planner.solve();
            
            System.out.print(plan);
            
            PlanVerifier verifier = new PlanVerifier();
            boolean valid = verifier.verifyPlan(sasProblem, plan);
            if(valid){
                System.out.println("Plan is valid");
            } else {
                System.out.println("Plan in not valid");
            }
        } catch(TimeoutException e){
            System.out.println("Vyprsel cas: " + e);
        } catch(ContradictionException e){
            System.out.println("Problem nema reseni: " + e);
        }
    }
    
    private PlanningProblem generateProblem(){
        
        PlanningProblem problem = new PlanningProblem();
        
        //StateVariable pro polohu zlodeje:
        StateVariable thiefLocation = problem.newVariable("thief", levelState.rooms.length);
        
        //StateVarialbes udavajici polohy prekazek:
        
            StateVariable[] obstaclesLocations = new StateVariable[levelState.obstacles.size()];
            for(int i=0; i<levelState.obstacles.size(); ++i){
                obstaclesLocations[i] = problem.newVariable("obstacle" + i, levelState.rooms.length + 1);
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
        
        problem.addInitialStateCondition(new Condition(thiefLocation, Main.thief.actualPosition.index));
        
        for(int i=0; i<levelState.items.size(); ++i){
            problem.addInitialStateCondition(new Condition(itemsLocations[i],
                    levelState.items.get(i).actualPosition.index));
        }
        
        for(int i=0; i<levelState.obstacles.size(); ++i){
            problem.addInitialStateCondition(new Condition(itemsLocations[i],
                    levelState.obstacles.get(i).actualPosition.index));
        }
        
        problem.addGoalCondition(new Condition(thiefLocation, levelState.finish.index));
        return problem;
    }
    
    private void addMoveThiefAction(PlanningProblem problem, StateVariable thiefLocation, int from, int to){
        Operator op = problem.newAction(String.format("move: %d->%d", from, to));
        
        op.getPreconditions().add(new Condition(thiefLocation, from));
        
        op.getEffects().add(new Condition(thiefLocation, to));
    }
    
    private void addMoveThiefOverAction(PlanningProblem problem, StateVariable thiefLocation, int from, int to, StateVariable obstacleLocation){
        Operator op = problem.newAction(String.format("move: %d->%d", from, to));
        
        op.getPreconditions().add(new Condition(thiefLocation, from));
        op.getPreconditions().add(new Condition(obstacleLocation, levelState.rooms.length + 1));
        
        op.getEffects().add(new Condition(thiefLocation, to));
    }
    
    private void addPickUpItemAction(PlanningProblem problem, StateVariable thiefLocation,
            StateVariable itemLocation, int location){
        Operator op = problem.newAction(String.format("pick: %d->%d", itemLocation.getName(), location));
        
        op.getPreconditions().add(new Condition(itemLocation, location));
        
        op.getPrevailConditions().add(new Condition(thiefLocation, location));
        
        op.getEffects().add(new Condition(itemLocation, levelState.rooms.length+1));
    }
}