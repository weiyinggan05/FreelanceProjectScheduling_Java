package scheduler;

import model.*;
import java.util.*;

// This is abstract base class for job scheduling algorithms
public abstract class AbstractJobAlgorithms implements JobAlgorithms {

	// This method help to find the maximum deadline of the jobs
    protected int findMaxDeadline(List<Job> jobs) {  
    	int max = 0; // Initialize the maximum deadline
        // For loop to check every jobs
        for (Job job : jobs) {
            if (job.getDeadline() > max) {
                max = job.getDeadline();
            }
        }
        return max; // Return the maximum deadline
    }
    
    // This method is to build result that contain the selected jobs and unselected jobs
    protected SchedulingResult buildResult(String algorithmName,
                                           List<Job> selectedJobs,
                                           List<Job> allJobs,
                                           long elapsedNanos) {
        Set<Job> selectedSet = new HashSet<>(selectedJobs);
        List<Job> unselected = new ArrayList<>();
        // Check all jobs if the job is not inside the selectedSet it is unselected
        for (Job job : allJobs) {
            if (!selectedSet.contains(job)) {
                unselected.add(job);
            }
        }
        return new SchedulingResult(algorithmName, selectedJobs, unselected, elapsedNanos);
    }
}
