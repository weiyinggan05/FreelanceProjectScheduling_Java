package scheduler;

import model.*;
import java.util.*;

//This class is to use the Brute Force strategy to pick job
public class BruteForceAlgorithms extends AbstractJobAlgorithms {

	// This method is to return name of algorithm
    @Override
    public String getAlgorithmName() {
        return "Brute Force / Exhaustive Search";
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
        // Declare and assign the job size and maximum deadline
        int n = jobs.size();
        int maxDeadline = findMaxDeadline(jobs);
        // Iniialize the bestSelection and bestProfit
        List<Job> bestSelection = new ArrayList<>();
        int bestProfit = 0;
        // totalSubnets = 2 to the power to n
        int totalSubsets = 1 << n;
        // Loop every possible combination
        for (int mask = 0; mask < totalSubsets; mask++) {
            List<Job> candidate = new ArrayList<>();
            for (int i = 0; i < n; i++) {
            	// if the i-th bit is 1, include the i-th job in this candidate list
                if ((mask & (1 << i)) != 0) {
                    candidate.add(jobs.get(i));
                }
            }

            if (isValidSchedule(candidate, maxDeadline)) {
                int profit = 0;
                for (Job job : candidate) {
                    profit += job.getProfit();
                }
                // Save if the group is better than previous one
                if (profit > bestProfit) {
                    bestProfit = profit;
                    bestSelection = new ArrayList<>(candidate);
                }
            }
        }
        // Calculate the time used and return the final results
        long elapsed = System.nanoTime() - startTime;
        return buildResult(getAlgorithmName(), bestSelection, jobs, elapsed);
    }

    // This method help to check whether a group can be completed without missing any deadlines
    private boolean isValidSchedule(List<Job> candidate, int maxDeadline) {
        boolean[] slot = new boolean[maxDeadline + 1];

        // Sort by deadline and try to fit them to check the to check validity
        List<Job> sorted = new ArrayList<>(candidate);
        sorted.sort(Comparator.comparingInt(Job::getDeadline));

        for (Job job : sorted) {
            boolean assigned = false;
            // Try to make a free spot for this job before its deadline
            for (int t = Math.min(maxDeadline, job.getDeadline()); t >= 1; t--) {
                if (!slot[t]) {
                    slot[t] = true;
                    assigned = true;
                    break;
                }
            }
            // The whole is invalid if even one job cannot fit
            if (!assigned) {
                return false;
            }
        }
        return true;
    }
}
