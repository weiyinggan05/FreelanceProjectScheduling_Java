package model;

//Used for scheduling algorithms
public class Job implements Comparable<Job> {

	//
    private final int id;
    private final String name;
    private final int deadline;
    private final int profit;
    private final String type;
    private final String description;

    //Creates a job with full details
    public Job(int id, String name, int deadline, int profit,
               String type, String description) {
        if (deadline < 1) {
            throw new IllegalArgumentException("Deadline must be >= 1, got " + deadline);
        }
        if (profit < 0) {
            throw new IllegalArgumentException("Profit must be >= 0, got " + profit);
        }
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.profit = profit;
        this.type = type;
        this.description = description;
    }

    //Creates a job without description
    public Job(int id, String name, int deadline, int profit, String type) {
        this(id, name, deadline, profit, type, "");
    }

    //Creates a manual job
    public Job(int id, String name, int deadline, int profit) {
        this(id, name, deadline, profit, "MANUAL", "");
    }

    //Returns job ID
    public int getId() {
        return id;
    }

    //Returns job name
    public String getName() {
        return name;
    }

    //Returns deadline value
    public int getDeadline() {
        return deadline;
    }

    //Returns profit value
    public int getProfit() {
        return profit;
    }

    //Returns job type
    public String getType() {
        return type;
    }

    //Returns job description
    public String getDescription() {
        return description;
    }

    @Override
    //Compares jobs by profit in descending order
    public int compareTo(Job other) {
        return Integer.compare(other.profit, this.profit);
    }

    @Override
    //Returns formatted job details
    public String toString() {
        return String.format("%-6s %-25s Deadline: %-4d Profit: %-6d Type: %s",
                "J" + id, name, deadline, profit, type);
    }

    @Override
    //Checks whether two jobs have the same ID
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job job = (Job) o;
        return id == job.id;
    }

    @Override
    // Returns hash code based on job ID
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
