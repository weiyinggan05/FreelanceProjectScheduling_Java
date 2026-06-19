package scheduler;

import model.*;
import java.util.*;

//This class is to use the Weighted Priority strategy to pick job
public class WeightedPriorityAlgorithms extends AbstractJobAlgorithms {

	// This method is to return the name of algorithm
    @Override
    public String getAlgorithmName() {
        return "Weighted Priority Scheduling";
    }

    // This method is the main logic of the algorithm 
    @Override
    public SchedulingResult schedule(List<Job> jobs) {
    	// Check whether the jobs list is empty or not 
        if (jobs == null || jobs.isEmpty()) {
            return buildResult(getAlgorithmName(),
                    Collections.emptyList(), Collections.emptyList(), 0);
        }

        // Start the timer 
        long startTime = System.nanoTime();

        // Sort the jobs by value density which is profit / deadline
        List<Job> sorted = new ArrayList<>(jobs);
        sorted.sort((a, b) -> {
        	// Calculate value density
            double scoreA = (double) a.getProfit() / a.getDeadline();
            double scoreB = (double) b.getProfit() / b.getDeadline();
            // Sort in decending order
            return Double.compare(scoreB, scoreA);
        });

        // Find the total deadline available
        int maxDeadline = findMaxDeadline(sorted);
        Job[] slot = new Job[maxDeadline + 1];

        List<Job> selected = new ArrayList<>();

        // Try to fit the high score jobs first
        for (Job job : sorted) {
        	// Start looking from the deadline and work backward
            for (int t = Math.min(maxDeadline, job.getDeadline()); t >= 1; t--) {
                if (slot[t] == null) {
                    slot[t] = job;
                    selected.add(job);
                    break;
                }
            }
        }

        // Calculate the time used and final results
        long elapsed = System.nanoTime() - startTime;
        return buildResult(getAlgorithmName(), selected, jobs, elapsed);
    }
}
