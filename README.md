# mito-planner

Work in progress.

A Java program making use of Optaplanner to schedule NCL mitochondrial research group socially-distanced schedule during the Covid-19 pandemic.

Each person has a different research group, and each task has various requirements, including equipment usage, room usage etc.

Hard and soft constraints are implemented to determine a score for a given solution.
 - One hard constraint is that a room's capacity may not be exceeded.
 - One soft constraint is that shifts should be split fairly between each research group.

The optimiser assigns tasks to shifts, "greedily" exploring the search space to find good solutions.
