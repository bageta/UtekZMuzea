package jme3test.helloworld;

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

public class LogisticsExampleTest {

	public static void main(String[] args) {
		LogisticsExampleTest test = new LogisticsExampleTest();
		test.testLogisticsProblem();
	}

	public void testLogisticsProblem() {
		// We generate the planning problem (see below)
		PlanningProblem problem = generateProblem();
		SasProblem sasProb = problem.getSasProblem();

		// We initialize the planner with the problem
		IncrementalSolver planner = new IncrementalSolver(sasProb);

		// We solve the problem
		try {
			Settings.getSettings().setTimeout(2); //time limit is 2 seconds
			SasParallelPlan plan = planner.solve();

			// print the plan
			System.out.println(plan);

			// Optionally we can verify the plan
			PlanVerifier verifier = new PlanVerifier();
			boolean valid = verifier.verifyPlan(sasProb, plan);
			if (valid) {
				System.out.println("Plan is VALID");
			} else {
				System.out.println("Plan in INVALID");
			}

		} catch (TimeoutException e) {
			System.out.println("Timeout occured");
		} catch (ContradictionException e) {
			System.out.println("The planning problem has no solution");
		}
	}

	/**
	 * Generate an example logistics planning problem with 3 trucks,
	 * 5 cities and 10 packages to deliver.
	 * Each truck can carry an arbitrary number of packages.
	 * @return the planning problem
	 */
	private PlanningProblem generateProblem() {

		// Initialize the planning problem
		PlanningProblem problem = new PlanningProblem();

		// ==========================
		// Create the state variables
		// ==========================

		// State variables of truck locations
		// domain size = number of locations
		StateVariable truckLocation[] = new StateVariable[3];
		for (int i = 0; i < 3; i++) {
			truckLocation[i] = problem.newVariable("t" + i, 5);
		}

		// state variables of package locations
		// a package can be either at one of the locations
		// or on one of the trucks
		// domain size = number of location + number of tracks
		StateVariable[] packageLocation = new StateVariable[10];
		for (int i = 0; i < 10; i++) {
			packageLocation[i] = problem.newVariable("p"+i, 8);
		}

		// ==============================
		// Define the actions (operators)
		// ==============================

		// Move truck actions for each truck
		for (int i = 0; i < 3; i++) {
			// 0->1
			addMoveTruckAction(problem, truckLocation[i], 0, 1);
			// 1->0
			addMoveTruckAction(problem, truckLocation[i], 1, 0);
			// 1->2
			addMoveTruckAction(problem, truckLocation[i], 1, 2);
			// 2->3
			addMoveTruckAction(problem, truckLocation[i], 2, 3);
			// 3->1
			addMoveTruckAction(problem, truckLocation[i], 3, 1);
			// 3->4
			addMoveTruckAction(problem, truckLocation[i], 3, 4);
			// 4->3
			addMoveTruckAction(problem, truckLocation[i], 4, 3);
		}

		// Load and unload package actions for each package, location and truck
		for (int pack = 0; pack < 10; pack++) {
			for (int location = 0; location < 5; location++) {
				for (int truck = 0; truck < 3; truck++) {
					// loads
					addLoadPackageAction(problem, truckLocation[truck], packageLocation[pack], location);
					// unloads
					addUnloadPackageAction(problem, truckLocation[truck], packageLocation[pack], location);
				}
			}
		}

		// ================================
		// Define the initial state
		// ================================

		// truck 0 starts at location 0
		problem.addInitialStateCondition(new Condition(truckLocation[0], 0));
		// truck 1 starts at location 4
		problem.addInitialStateCondition(new Condition(truckLocation[1], 4));
		// truck 2 starts at location 2
		problem.addInitialStateCondition(new Condition(truckLocation[2], 2));

		// initial package locations
		for (int i = 0; i < 10; i++) {
			// package i is at location i modulo 5
			problem.addInitialStateCondition(new Condition(packageLocation[i], i % 5));
		}

		// =================================
		// Define the goal state
		// =================================

		// we do not care about the final location of the trucks
		// we define the desired locations of the packages
		for (int i = 0; i < 10; i++) {
			// the desired location of package i is i+2 modulo 5
			problem.addGoalCondition(new Condition(packageLocation[i], (i+2) % 5));
		}

		return problem;
	}

	private void addMoveTruckAction(PlanningProblem problem, StateVariable truckLocation, int from, int to) {
		// Create a new operator named like MoveTruck-1:3->1
		Operator op = problem.newAction(String.format("MoveTruck-%s:%d->%d", truckLocation.getName(), from, to));
		// add the preconditions - the truck must be at the "from" location
		op.getPreconditions().add(new Condition(truckLocation, from));
		// add the effects - the truck is at the "to" location
		op.getEffects().add(new Condition(truckLocation, to));
	}

	private void addLoadPackageAction(PlanningProblem problem, StateVariable truckLocation, StateVariable packageLocation, int location) {
		Operator op = problem.newAction(String.format("LoadPackage-%s-onTruck-%s-at-%d", packageLocation.getName(), truckLocation.getName(), location));
		// the precondition - the package is at the correct location
		op.getPreconditions().add(new Condition(packageLocation, location));
		// the prevailing condition - the truck must remain at the location
		op.getPrevailConditions().add(new Condition(truckLocation, location));
		// the effect - the package location changes to the truck (locations 5,6,7 are trucks 0,1,2)
		op.getEffects().add(new Condition(packageLocation, truckLocation.getId() + 5));
	}

	private void addUnloadPackageAction(PlanningProblem problem, StateVariable truckLocation, StateVariable packageLocation, int location) {
		Operator op = problem.newAction(String.format("UnloadPackage-%s-onTruck-%s-at-%d", packageLocation.getName(), truckLocation.getName(), location));
		// the precondition - the package is on the correct truck (locations 5,6,7 are trucks 0,1,2)
		op.getPreconditions().add(new Condition(packageLocation, truckLocation.getId() + 5));
		// the prevailing condition - the truck must remain at the location
		op.getPrevailConditions().add(new Condition(truckLocation, location));
		// the effect - the package location changes to the location
		op.getEffects().add(new Condition(packageLocation, location));
	}

}

