package planner;

import game.Level;
import game.Main;
import game.Room;

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
 * modelovani problemu podle stavu lokaci
 * TODO: NEJAKA CHYBKA TREBA OPRAVIT!!!!!
 * @author Pavel
 */
public class Planner2 {
    
    Level levelState;
    
    public Planner2(Level actualLevel){
        levelState = actualLevel;
    }
    
    public void makeNewPlan(){
        PlanningProblem problem = generateProblem();
        SasProblem sasProblem = problem.getSasProblem();
        
        IncrementalSolver planner = new IncrementalSolver(sasProblem);
        
        try{
            Settings.getSettings().setTimeout(3);
            SasParallelPlan plan = planner.solve();
            
            System.out.println("PLANOVAC - 2 - output: ");
            System.out.print(plan);
            
            PlanVerifier verifier = new PlanVerifier();
            boolean valid = verifier.verifyPlan(sasProblem, plan);
            if(valid){
                System.out.println("Plan is valid");
            } else {
                System.out.println("Plan in not valid");
            }
            
            System.out.println("PLANOVAC - 2 - output END -----------------------");
            
        } catch(TimeoutException e){
            System.out.println("Vyprsel cas: " + e);
        } catch(NonexistentPlanException e){
            System.out.println("Problem nema reseni: " + e);
        }
    }
    
    private PlanningProblem generateProblem(){
        PlanningProblem problem = new PlanningProblem();
        
        StateVariable[] roomState = new StateVariable[levelState.rooms.length];
        int statesInRoom = 2+levelState.obstacles.size()+levelState.items.size();
        for(int i=0; i<levelState.rooms.length; ++i){
            /*pro kazdou mistnost na mape si popiseme stavy, ktere v ni mohou nastat:
             * 0: prazdna mistnost
             * 1: zlodej
             * 2,..,k: 1,..,k-2. prekazka
             * k+1,..,l: 1,..,l-k-2. item
            */
            roomState[i] = problem.newVariable("room" + i, statesInRoom);
        }
        /* narozdil od prvniho modelu zde neudava pozici zlodeje, ale cislo veci
         * kterou nese, plus 0.pozice pro pripad, ze nenese nic:
        */
        StateVariable thief = problem.newVariable("thief", levelState.items.size()+1);
        
        for(Room location: levelState.rooms){
            for(Room location2: location.neigbours){
                for(int i=0; i<levelState.items.size()+1; ++i){
                    addMoveThiefAction(problem, roomState[location.index], roomState[location2.index],
                            thief, i);
                    addUseItemAction(problem, roomState[location.index], roomState[location2.index],
                            thief, i);
                    addPickUpItemAction(problem, roomState[location.index], roomState[location2.index],
                            thief, i);
                }
            }
        }
        
        for(int location=0; location< levelState.rooms.length; ++location){
            boolean wasAdded = false;
            if(levelState.rooms[location]==Main.thief.actualPosition){
                problem.addInitialStateCondition(new Condition(roomState[location], 1));
                wasAdded = true;
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
            if(!wasAdded)
                problem.addInitialStateCondition(new Condition(roomState[location],0));
        }
        problem.addInitialStateCondition(new Condition(thief, 0));
        
        problem.addGoalCondition(new Condition(roomState[levelState.finish.index], 1));
        
        return problem;
    }
    
    private void addMoveThiefAction(PlanningProblem problem, StateVariable from,
            StateVariable to, StateVariable thief, int thiefCarry){
        SasAction op = problem.newAction(new ThiefAction(ActionType.MOVE,from.getId(), to.getId()));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("move: %d->%d", from.getId(), to.getId())));
        
        op.getPreconditions().add(new Condition(from, 1));
        op.getPreconditions().add(new Condition(to, 0));
        
        op.getEffects().add(new Condition(from, 0));
        op.getEffects().add(new Condition(to, 1));
        
        op.getPreconditions().add(new Condition(thief, thiefCarry));
    }
    
    private void addUseItemAction(PlanningProblem problem, StateVariable from,
            StateVariable to, StateVariable thief, int thiefCarry){
        SasAction op = problem.newAction(new ThiefAction(ActionType.USE,from.getId(), to.getId()));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("use: %d->%d", from.getId(), to.getId())));
        
        op.getPreconditions().add(new Condition(to, thiefCarry + 2));
        op.getPreconditions().add(new Condition(thief, thiefCarry));
        
        op.getEffects().add(new Condition(thief, 0));
        op.getEffects().add(new Condition(to, 0));
        
        op.getPreconditions().add(new Condition(from, 1));
    }
    
    private void addPickUpItemAction(PlanningProblem problem, StateVariable from,
            StateVariable to, StateVariable thief, int item){
        SasAction op = problem.newAction(new ThiefAction(ActionType.PICK,from.getId(), to.getId()));
        //SasAction op = problem.newAction(new StringActionInfo(String.format("pick: %d->%d", from.getId(), to.getId())));
        
        op.getPreconditions().add(new Condition(to, item+2+levelState.obstacles.size()));
        op.getPreconditions().add(new Condition(from, 1));
        op.getPreconditions().add(new Condition(thief, 0));
        
        op.getEffects().add(new Condition(to, 1));
        op.getEffects().add(new Condition(from, 0));
        op.getEffects().add(new Condition(thief, item));
    }

}
