package model;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.util.List;

public class main {
    private static final String SOLVER_CONFIG = "mitoScheduleSolver.xml";

    public static void main(String[] args) throws Exception {
        SolverFactory<ScheduleSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);

        Solver<ScheduleSolution> solver = solverFactory.buildSolver();

        ScheduleSolution unsolvedSolution = new ScheduleSolution();

        ScheduleSolution solvedSolution = solver.solve(unsolvedSolution);

        displaySolution(solvedSolution);

        System.out.println("Score: " + solvedSolution.getScore());

    }

    public static void displaySolution(ScheduleSolution solution) {
        List<ShiftAssignment> assignments = solution.getAssignments();
        int nullCount = 0;
        for (ShiftAssignment assignment: assignments) {
            if (assignment.isTaskAssigned()) {
                System.out.println(assignment);
            } else {
                nullCount+= 1;
            }
        }
        System.out.println("Shift assignment slots not used: " + nullCount);
        System.out.println("Number of unassigned tasks: " + solution.getNumberUnassignedTasks());
    }
}
