package scheduler;

import model.*;
import java.util.*;

// This class is to use the Greedy strategy to pick job
public class GreedyProfitAlgorithms extends AbstractJobAlgorithms {

	// This method return the name of the algorithm 
    @Override
    public String getAlgorithmName() {
        return "Greedy Algorithm (Profit-based)";
    }

    // This method is the main logic of the algorithm 
    @Override
    public SchedulingResult schedule(List<Job> jobs) {
    	// Check whether the job list is empty or not
        if (jobs == null || jobs.isEmpty()) {
            return buildResult(getAlgorithmName(),
                    Collections.emptyList(), Collections.emptyList(), 0);
        }
        // Start the timer to see how fast the algorithm is 
        long startTime = System.nanoTime();
        // Sort the jobs
        List<Job> sorted = new ArrayList<>(jobs);
        Collections.sort(sorted);
        // Use the method at parent class to find the maximum deadline
        int maxDeadline = findMaxDeadline(sorted);
        // Create slot to place the job into it 
        Job[] slot = new Job[maxDeadline + 1];

        List<Job> selected = new ArrayList<>();

        // Try to fit each job into the latest possible empty slot 
        for (Job job : sorted) {
            for (int t = Math.min(maxDeadline, job.getDeadline()); t >= 1; t--) {
                if (slot[t] == null) { // If the slot is empty
                    slot[t] = job; // Place the job here
                    selected.add(job);
                    break; // Move to nect job
                }
            }
        }

        // Calculate time spend and return the final results
        long elapsed = System.nanoTime() - startTime;
        return buildResult(getAlgorithmName(), selected, jobs, elapsed);
    }
}
