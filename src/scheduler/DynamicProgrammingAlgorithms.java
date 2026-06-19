package scheduler;

import model.*;
import java.util.*;

//This class is to use the Dynamic Programming strategy to pick job
public class DynamicProgrammingAlgorithms extends AbstractJobAlgorithms {

	// This method return name of the algorithm
    @Override
    public String getAlgorithmName() {
        return "Dynamic Programming";
    }

    // This method is the main logic of the algorithm
    @Override
    public SchedulingResult schedule(List<Job> jobs) {
    	// Check whether the jobs list is empty or not 
        if (jobs == null || jobs.isEmpty()) {
            return buildResult(getAlgorithmName(),
                    Collections.emptyList(), Collections.emptyList(), 0);
        }

        // Start the time to calculate time used
        long startTime = System.nanoTime();

        // Sort jobs by their deadlines in ascending order
        int n = jobs.size();
        List<Job> sorted = new ArrayList<>(jobs);
        sorted.sort((a, b) -> {
            int cmp = Integer.compare(a.getDeadline(), b.getDeadline());
            return cmp != 0 ? cmp : Integer.compare(b.getProfit(), a.getProfit());
        });

        int maxDeadline = findMaxDeadline(sorted);

        // Create a dp table which the row is jobs and column is time slots
        int[][] dp = new int[n + 1][maxDeadline + 1];
        boolean[][] choice = new boolean[n + 1][maxDeadline + 1];

        // This process is to fill the dp table
        for (int i = 1; i <= n; i++) {
            Job job = sorted.get(i - 1);
            for (int t = 0; t <= maxDeadline; t++) {
            	// Do not take the job, profit stay the same as before
                dp[i][t] = dp[i - 1][t];
                choice[i][t] = false;

                // Take the job if there is time and not past the deadline
                if (t >= 1 && job.getDeadline() >= t) {
                    int profitIfTaken = dp[i - 1][t - 1] + job.getProfit();
                    // Check whether the profit will be higher, then it will be choose
                    if (profitIfTaken > dp[i][t]) {
                        dp[i][t] = profitIfTaken;
                        choice[i][t] = true;
                    }
                }
            }
        }

        // Look for the highest profit in last row
        int bestT = 0;
        for (int ti = 1; ti <= maxDeadline; ti++) {
            if (dp[n][ti] > dp[n][bestT]) {
                bestT = ti;
            }
        }

        // Backtrack to find selected jobs
        List<Job> selected = new ArrayList<>();
        int t = bestT;
        for (int i = n; i >= 1; i--) {
            if (choice[i][t]) {
                selected.add(sorted.get(i - 1));
                t--; // Move back one time slot
            }
        }
        Collections.reverse(selected);

        // Calculate time spend and return the final results
        long elapsed = System.nanoTime() - startTime;
        return buildResult(getAlgorithmName(), selected, jobs, elapsed);
    }
}
