package model;

import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Comparator;
import java.util.List;

public class Main {
    // TODO fix broken constraints
    private static final String SOLVER_CONFIG = "mitoScheduleSolver.xml";

    public static void main(String[] args) throws Exception {
        SolverFactory<ScheduleSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);

        Solver<ScheduleSolution> solver = solverFactory.buildSolver();

        ScoreManager<ScheduleSolution> manager = ScoreManager.create(solverFactory);

        ScheduleSolution unsolvedSolution = new ScheduleSolution();

        ScheduleSolution solvedSolution = solver.solve(unsolvedSolution);

        displaySolution(solvedSolution);

        System.out.println();
        System.out.println(manager.explainScore(solvedSolution));
        System.out.println("Printing constraint match total map");
        System.out.println();
        ScoreDirector<ScheduleSolution> director = solverFactory.getScoreDirectorFactory().buildScoreDirector();
        director.setWorkingSolution(solvedSolution);
        System.out.println(director.getConstraintMatchTotalMap());

        solvedSolution.writeAssignmentsToCsv();

    }

    public static void displaySolution(ScheduleSolution solution) {
        System.out.println("Assignments:");
        List<TaskAssignment> assignments = solution.getAssignments();
        assignments.sort(new Comparator<TaskAssignment>() {
            @Override
            public int compare(TaskAssignment o1, TaskAssignment o2) {
                if(o1.isTaskAssigned() && !o2.isTaskAssigned()) return 1;
                if(!o1.isTaskAssigned() && o2.isTaskAssigned()) return -1;
                if(!o1.isTaskAssigned() && !o2.isTaskAssigned()) return 0;
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });
        int nullCount = 0;
        for (TaskAssignment assignment: assignments) {
            if (assignment.isTaskAssigned()) {
                System.out.println(assignment);
            } else {
                nullCount+= 1;
            }
        }
        System.out.println();
        System.out.println("Tasks not assigned: " + nullCount);
        System.out.println();
        System.out.println("Assignments per PI Group:");
        solution.printPiGroupSplit();
        System.out.println();
        solution.printAllUnassignedTasks();
        System.out.println();
        System.out.println("Score: " + solution.getScore());
    }
}
