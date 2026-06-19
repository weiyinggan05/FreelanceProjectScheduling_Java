package scheduler;

import model.*;
import java.util.*;

// This interface define standard contract for job scheduling 
public interface JobAlgorithms {
    String getAlgorithmName();
    //Schedules the given list of jobs and returns the result.
    SchedulingResult schedule(List<Job> jobs);
}
