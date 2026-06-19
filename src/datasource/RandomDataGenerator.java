package datasource;

import model.*;
import java.util.*;
///Generates random job data for testing and simulation
public class RandomDataGenerator implements DataSource {
	
	//Number of jobs to generate
    private final int numberOfJobs;
    //Number of jobs to generate
    private final int maxDeadline;
    //Maximum profit value
    private final int maxProfit;

    //List of sample project names
    private static final String[] PROJECT_NAMES = {
            "Website Redesign", "Mobile App", "Database Migration",
            "API Development", "Logo Design", "SEO Optimization",
            "Cloud Setup", "Security Audit", "Data Analysis",
            "UI/UX Design", "Backend Refactor", "Testing Suite",
            "Documentation", "DevOps Pipeline", "ML Model",
            "Chat Bot", "Payment Gateway", "Email Campaign",
            "Social Media App", "Dashboard Design", "Code Review",
            "Performance Tuning", "Backup System", "CRM Integration",
            "Report Generator", "Inventory System", "User Portal",
            "Analytics Tool", "Search Engine", "Notification Service"
    };

    //Creates a random data generator
    public RandomDataGenerator(int numberOfJobs, int maxProfit, int maxDeadline) {
        if (numberOfJobs < 1) {
            throw new IllegalArgumentException("Number of jobs must be >= 1");
        }
        if (maxDeadline < 1) {
            throw new IllegalArgumentException("Maximum deadline must be >= 1, got " + maxDeadline);
        }
        if (maxProfit < 1) {
            throw new IllegalArgumentException("Maximum profit must be >= 1, got " + maxProfit);
        }
        this.numberOfJobs = numberOfJobs;
        this.maxDeadline = maxDeadline;
        this.maxProfit = maxProfit;
    }

    @Override
    //Returns description of random data settings
    public String getDescription() {
        return String.format("Random (%d jobs, max profit %d, max deadline %d)",
                numberOfJobs, maxProfit, maxDeadline);
    }

    @Override
    //Generates a list of random jobs
    public List<Job> loadJobs() {
        List<Job> jobs = new ArrayList<>();
        Random random = new Random();

        List<String> availableNames = new ArrayList<>(Arrays.asList(PROJECT_NAMES));
        Collections.shuffle(availableNames, random);

        for (int i = 0; i < numberOfJobs; i++) {
            String name;
            if (i < availableNames.size()) {
                name = availableNames.get(i);
            } else {
                name = PROJECT_NAMES[random.nextInt(PROJECT_NAMES.length)];
            }

            int deadline = 1 + random.nextInt(maxDeadline);
            int profit = 1 + random.nextInt(maxProfit);

            String safeName = name.replaceAll("\\s+", "_");
            jobs.add(new Job(i + 1, safeName, deadline, profit, "RANDOM",
                    "Randomly generated project"));
        }

        return jobs;
    }
    
    //Returns all available project names
    public static String[] getProjectNames() {
        return PROJECT_NAMES.clone();
    }
}
