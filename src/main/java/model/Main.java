package model;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.List;
import java.util.Set;

public class Main {
    // TODO implement 15 min time slots
    // TODO update double-booking constraint to be wrt 15 min slots.
    private static final String SOLVER_CONFIG = "mitoScheduleSolver.xml";

    public static void main(String[] args) throws Exception {
        SolverFactory<ScheduleSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);

        Solver<ScheduleSolution> solver = solverFactory.buildSolver();

        ScheduleSolution unsolvedSolution = new ScheduleSolution();

        ScheduleSolution solvedSolution = solver.solve(unsolvedSolution);

        displaySolution(solvedSolution);

        System.out.println(solver.explainBestScore());
        System.out.println();
        System.out.println("Printing constraint match total map");
        System.out.println();
        ScoreDirector<ScheduleSolution> director = solverFactory.getScoreDirectorFactory().buildScoreDirector();
        director.setWorkingSolution(solvedSolution);
        System.out.println(director.getConstraintMatchTotalMap());

        solvedSolution.writeAssignmentsToCsv();

    }

    public static void displaySolution(ScheduleSolution solution) {
        Set<ShiftAssignment> assignments = solution.getAssignments();
        int nullCount = 0;
        for (ShiftAssignment assignment: assignments) {
            if (assignment.isTaskAssigned()) {
                System.out.println(assignment);
            } else {
                nullCount+= 1;
            }
        }
        System.out.println();
        System.out.println("Shift assignment slots not used: " + nullCount);
        System.out.println();
        System.out.println("Assignments per PI Group:");
        solution.printPiGroupSplit();
        System.out.println();
        solution.printAllUnassignedTasks();
        System.out.println();
        System.out.println("Score: " + solution.getScore());
    }
}
