# mito-planner

Work in progress.

A Java program making use of Optaplanner to schedule NCL mitochondrial research group socially-distanced schedule during the Covid-19 pandemic.

Each person has a different research group, and each task has various requirements, including equipment usage, room usage etc.

Hard and soft constraints are implemented to determine a score for a given solution.

Hard constraints currently include (currently implemented to varying degrees):
 - Weekly shift limits on researchers
 - Not double-booking researchers
 - Required equipment must be available for a task's scheduled time
 - Room capacity should not be exceeded
 - Task due dates should be respected
 - Tasks should only be scheduled once
 - Tasks with pre-requisite tasks should be scheduled after those pre-requisites.

Soft constraints currently include:
 - Scheduling should be split fairly between each research group
 - Higher priority tasks should be prioritised more often
 - As much work should be scheduled as possible

The optimiser assigns tasks to shifts, "greedily" exploring the search space to find good solutions.