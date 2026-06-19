package model;

import java.util.*;
//Stores the result of a scheduling algorithm
public class SchedulingResult {

    private final String algorithmName;
    private final List<Job> selectedJobs;
    private final List<Job> unselectedJobs;
    private final int totalProfit;
    private final long executionTimeNanos;

    //Creates a scheduling result object
    public SchedulingResult(String algorithmName,
                            List<Job> selectedJobs,
                            List<Job> unselectedJobs,
                            long executionTimeNanos) {
        this.algorithmName = algorithmName;
        this.selectedJobs = Collections.unmodifiableList(new ArrayList<>(selectedJobs));
        this.unselectedJobs = Collections.unmodifiableList(new ArrayList<>(unselectedJobs));
        this.totalProfit = selectedJobs.stream().mapToInt(Job::getProfit).sum();
        this.executionTimeNanos = executionTimeNanos;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public List<Job> getSelectedJobs() {
        return selectedJobs;
    }

    public List<Job> getUnselectedJobs() {
        return unselectedJobs;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    @Override
    //Returns formatted scheduling result
    public String toString() {
        StringBuilder sb = new StringBuilder();
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-89s ║%n", "Algorithm : " + algorithmName);
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════════════╝");

        sb.append("\n--- Selected Jobs (Scheduled) ---\n");
        if (selectedJobs.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            sb.append(String.format("  %-6s %-25s %-12s %s\n", "ID", "Name", "Deadline", "Profit"));
            sb.append("  --------------------------------------------------------\n");
            for (Job job : selectedJobs) {
                sb.append(String.format("  %-6s %-25s %-12d %d\n",
                        "J" + job.getId(), job.getName(), job.getDeadline(), job.getProfit()));
            }
        }

        sb.append("\n--- Unselected Jobs ---\n");
        if (unselectedJobs.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            sb.append(String.format("  %-6s %-25s %-12s %s\n", "ID", "Name", "Deadline", "Profit"));
            sb.append("  --------------------------------------------------------\n");
            for (Job job : unselectedJobs) {
                sb.append(String.format("  %-6s %-25s %-12d %d\n",
                        "J" + job.getId(), job.getName(), job.getDeadline(), job.getProfit()));
            }
        }

        sb.append("\n  Total Profit  : ").append(totalProfit).append("\n");
        sb.append("  Execution Time: ").append(String.format("%.3f ms", executionTimeNanos / 1_000_000.0)).append("\n");
        sb.append("=============================================================\n");
        return sb.toString();
    }
}
