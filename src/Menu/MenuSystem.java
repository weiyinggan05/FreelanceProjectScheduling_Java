package Menu;

import model.*;
import datasource.*;
import scheduler.*;
import java.io.*;
import java.util.*;

// This class is to store all the menu methods
public class MenuSystem {
    // Declare all the private instances
    private final Scanner scanner = new Scanner(System.in);
    private final FileManager fileManager = new FileManager();
    private User currentUser = null;
    private SchedulingResult lastResult = null;
    private boolean exitRequested = false;

    private final JobAlgorithms[] SCHEDULERS = {
            new GreedyProfitAlgorithms(),
            new DynamicProgrammingAlgorithms(),
            new BruteForceAlgorithms(),
            new WeightedPriorityAlgorithms()
    };
    
    // Declare all the constant instances for the formatting purpose
    private static final int BOX_WIDTH = 52;
    private static final int LABEL_WIDTH = BOX_WIDTH - 4;
    private static final String BOX_FORMAT = "  ║ %-" + LABEL_WIDTH + "s   ║%n";
    private static final String BORDER_LINE = "   " + repeat("═", BOX_WIDTH) + " ";
    private static final String DOUBLE_LINE = "   " + repeat("═", BOX_WIDTH) + " ";
    private static final String TABLE_LINE = "  " + repeat("═", BOX_WIDTH + 2);

    // This method use to clear the screen through insert many row of empty space
    private static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // This method use to pause the screen for the user to take a look of the screen 
    private void pause() {
        System.out.println();
        System.out.print("  Press Enter to continue...");
        scanner.nextLine();
    }

    // This method is to print the header
    private void printHeader(String title) {
        System.out.println();
        System.out.println(DOUBLE_LINE);
        System.out.printf(BOX_FORMAT, title);
        System.out.println(DOUBLE_LINE);
        System.out.println();
    }

    // This method is to print the section 
    void printSection(String label) {
        System.out.println();
        System.out.println(BORDER_LINE);
        System.out.printf(BOX_FORMAT, label);
        System.out.println(BORDER_LINE);
        System.out.println();
    }

    // This method is to repeat printing something
    private static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    // This method is to start the login page for whole Freelance Project Scheduling System 
    public void start() {
        boolean exitProgram = false;

        // Repeat the loop once the user do not want to terminate the program 
        while (!exitProgram) {  
            clearScreen();
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║       FREELANCE PROJECT SCHEDULING APPLICATION            ║");
            System.out.println("║          User-Based Job Sequencing System                 ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                        LOGIN                              ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println("  Welcome! Please log in to continue.");
            System.out.println();

            currentUser = login();  // call the login method and return the value as currentUser
            // if-else structure to determine whether the user login successfully, unsuccessfully or want to terminate the program 
            if (currentUser != null) {
                exitProgram = showMainMenu();
                lastResult = null;
            } 
            else if (exitRequested) {
                exitProgram = true;
            } 
            else {
                pause();
            }
        }

        // Display the following if the user want to terminate the program 
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                        GOODBYE !                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Thank you for using Job Sequencing System!");
        System.out.println("  See you next time.");
        System.out.println();
        scanner.close();
    }

    // This method to allow the user to login into Freelance Project Scheduling System
    private User login() {
        System.out.print("  Enter your username [0 = Exit]: "); // Allow user to terminate with 0

        String username = scanner.nextLine().trim(); // Allow user insert their username
        
        // if structure to allow user terminate the program 
        if (username.equals("0")) {
            exitRequested = true; 
            return null; // Return null if 0 inserted
        }
        // if structure to validate that the username cannot be empty
        if (username.isEmpty()) {
            System.out.println();
            System.out.println("   Username cannot be empty.");
            return null; // Return null if no username inserted 
        }
        // if structure to validate the username whether user follow the rules or not 
        if (!username.matches(User.USERNAME_PATTERN)) {
            System.out.println();
            System.out.println("   Username can only contain letters, numbers, and underscores.");
            return null; // Return null if username is not successfully validated
        }

        // Check whether the user exist before or not 
        try {
            User user = new User(username);

            if (fileManager.userFileExists(user)) {
                System.out.println();
                System.out.println("  ✓ Welcome back, " + username + "!");
                System.out.println("    Loading your data from " + user.getFilePath() + "...");
            }
            else {
                System.out.println();
                System.out.println("  New user detected. Creating your profile...");
                fileManager.createUserFile(user);
                System.out.println("  ✓ Welcome, " + username + "!");
                System.out.println("    Your file: " + user.getFilePath());
            }

            pause();
            return user; // Return user to continue the main menu 
        } 
        catch (IOException e) {
            System.out.println();
            System.out.println("   Error creating user file: " + e.getMessage());
            return null; // Return null if error occur
        } 
        catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("   Invalid username: " + e.getMessage());
            return null; // Return null if error occur
        }
    }

    // This method is to display the main menu and allow user to do what they desired
    private boolean showMainMenu() {
    	// Infinite loop to make sure the screen always display
        while (true) {
            clearScreen();
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                        Main Menu                          ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println("  Logged in as: ( " + currentUser.getUsername()+" )");
            System.out.println();

            System.out.println("  [Jobs]");
            System.out.println("    1. View Jobs");
            System.out.println("    2. Add Job");
            System.out.println("    3. Generate Random Data");
            System.out.println("======================================================");
            System.out.println("  [Scheduling]");
            System.out.println("    4. Run Algorithm");
            System.out.println("    5. View Results");
            System.out.println("    6. Update Job Status");
            System.out.println("======================================================");
            System.out.println("  [Session]");
            System.out.println("    7. Logout");
            System.out.println("    8. Exit");
            System.out.println("======================================================");
            System.out.print("  Enter your choice (1-8): ");

            // Allow user to choose which process want to do 
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewJobs(); // Call viewJobs if 1 inserted
                    break;
                case "2":
                    addJob(); // Call addJob if 2 inserted
                    break;
                case "3":
                    generateRandomData(); // Call generateRandomData if 3 inserted
                    break;
                case "4":
                    showAlgorithmMenu(); // Call showAlgorithmMenu if 4 inserted
                    break;
                case "5":
                    viewResults(); // Call viewResult if 5 inserted
                    break;
                case "6":
                    updateJobStatus(); // Call updateJobStatus if 6 inserted
                    break;
                case "7":
                    clearScreen();
                    printHeader("LOGOUT");
                    System.out.println("  ✓ Logged out successfully.");
                    System.out.println("    Returning to login screen...");
                    pause();
                    return false; // To break the loop and back to the login page 
                case "8":
                    return true; // To break the loop and terminate the program 
                default: // To validate the choice inserted within the range of selection 
                    System.out.println();
                    System.out.println("   Invalid choice. Please try again.");
                    pause();
            }
        }
    }

    // This method is to view the jobs or projects of the respective user
    private void viewJobs() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                     YOUR JOB LIST                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        try {
            List<Job> jobs = fileManager.loadJobs(currentUser); // Load the jobs from the user text file
            // Back to main menu if the user text file is empty
            if (jobs.isEmpty()) {
                System.out.println("   No jobs found. Use 'Add Job' to create some.");
                pause();
                return; 
            }

            Map<String, String> statusMap = fileManager.loadStatus(currentUser); // Load the status of the user on each project

            System.out.printf("  %-6s %-20s %-10s %-10s %-12s %-8s%n",
                    "ID", "Name", "Deadline", "Profit", "Status", "Type");
            System.out.println(TABLE_LINE);

            int completedCount = 0; // initialize completedCount
            int pendingCount = 0; // initialize pendingCount

            // Display each job of the user
            for (Job job : jobs) {
                String jobId = "J" + job.getId();
                String status = statusMap.getOrDefault(jobId, "PENDING");
                System.out.printf("  %-6s %-20s %-10d %-10d %-12s %-8s%n",
                        jobId, job.getName(), job.getDeadline(), job.getProfit(),
                        status, job.getType());

                if (status.equals("COMPLETED")) {
                    completedCount++; // Increase the completedCount by 1 if the status is COMPLETED
                } 
                else {
                    pendingCount++; // Increase the pendingCount by 1 if the status is PENDING
                }
            }

            System.out.println(TABLE_LINE);
            System.out.println();
            System.out.println("  Total Jobs      : " + jobs.size()); // Display the total jobs
            System.out.println("  Completed Jobs  : " + completedCount); // Display completed jobs
            System.out.println("  Pending Jobs    : " + pendingCount); // Display pending jobs

        } 
        catch (IOException e) {
            System.out.println("   Error reading jobs: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  [Returning to Main Menu]");
        pause(); // Pause before back to main menu 
    }

    // This method is to allow the user to choose the method to input the jobs or projects
    private void addJob() {
        while (true) {
            clearScreen();
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                     ADD NEW JOB                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("  Choose how to add jobs:");
            System.out.println();
            System.out.println("    1. Manual Entry");
            System.out.println("    2. Import from CSV File");
            System.out.println("    0. Back to Main Menu");
            System.out.println();
            System.out.print("  Enter your choice (0-2): ");

            // Let the user to choose the method to input jobs or projects
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "0":
                    return; // Back to main menu if 0 inserted
                case "1":
                    addJobManual(); // Call addJobManual if 1 inserted
                    break;
                case "2":
                    importFromFile(); // Call importFromFile if 2 inserted
                    break;
                default: // Validate the choice and continue looping if the choice is not in range 
                    System.out.println();
                    System.out.println("   Invalid choice. Please try again.");
                    pause(); 
            }
        }
    }

    // This method is to allow user input the jobs or projects manually
    private void addJobManual() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   MANUAL JOB ENTRY                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        try {
            // Automatically generate next job ID from highest existing ID
            List<Job> existing = fileManager.loadJobs(currentUser); 
            int nextId = getNextJobId(existing);

            System.out.println("  Auto-assigned Job ID: J" + nextId);
            System.out.println();

            String name = readStringOrBack("Enter project name ");  // Get the project name
            if (name.equals("0")) {
            	return; // Insert 0 to back to add job menu 
            }
            name = name.replaceAll("\\s+", "_"); // Replace spaces with underscores to avoid file parsing issues

            int deadline = readIntOrBack("Enter Deadline (positive integer) "); // Get the deadline
            if (deadline == 0) {
            	return; // Insert 0 to back to add job menu 
            }

            int profit = readIntOrBack("Enter Profit (positive integer)"); // Get the profit
            if (profit == 0) {
            	return; // Insert 0 to back to add job menu 
            }

            Job newJob = new Job(nextId, name, deadline, profit, "MANUAL"); // Create a new job
            fileManager.addJob(currentUser, newJob); // Append the new job into the user text file

            // Display the job is successfully added
            System.out.println();
            System.out.println("  ✓ Job J" + nextId + " added successfully!");
            System.out.printf("    Name: %s | Deadline: %d | Profit: %d | Type: MANUAL%n",
                    name, deadline, profit);

        } 
        // Catch Exception and display error message
        catch (IOException e) { 
            System.out.println();
            System.out.println("   Error saving job: " + e.getMessage());
        } 
        catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("   Invalid input: " + e.getMessage());
        }

        // Pause before back to add job menu 
        System.out.println();
        System.out.println("  [Returning to Add Job Menu]");
        pause();
    }

    // This method is to allow user input the jobs or project through importing from csv database file 
    private void importFromFile() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                IMPORT FROM CSV FILE                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Supported formats:");
        System.out.println("    - name, profit, deadline");
        System.out.println("    - id, name, deadline, profit");
        System.out.println("  Sample file: data/sample_projects.csv");
        System.out.println();

        // Get file path from user
        String filePath = readStringOrBack("Enter file path (e.g. data/sample_projects.csv)");
        if (filePath.equals("0")) {
        	return;
        }

        //  Validate and read the file
        List<Job> importedJobs;
        try {
            FileDataReader reader = new FileDataReader(filePath);
            reader.validateFile();
            importedJobs = reader.loadJobs(); // Load the jobs from the file into importedJobs list
        } 
        // Catch exception
        catch (IOException e) {
            System.out.println();
            System.out.println("  ✗ Error: " + e.getMessage());
            pause();
            return;
        } 
        catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("  ✗ Invalid input: " + e.getMessage());
            pause();
            return;
        }

        // Display the jobs or projects in the csv database file
        System.out.println();
        System.out.println("  ✓ Loaded " + importedJobs.size() + " projects from file.");
        System.out.println();
        displayImportedJobsTable(importedJobs);

        // Let user select which projects to add
        List<Job> selectedJobs = selectProjectsFromList(importedJobs); // Get the selected jobs
        if (selectedJobs.isEmpty()) {
            System.out.println();
            System.out.println("  No projects selected. Nothing was added.");
            pause();
            return; // Back to add job menu if selected job is empty
        }

        try {
        	// Automatically generate next job ID from highest existing ID
            List<Job> existing = fileManager.loadJobs(currentUser);
            int nextId = getNextJobId(existing);

            // Let the jobs to save into an ArrayList and save each job into the respective user file
            List<Job> jobsToSave = new ArrayList<>();
            // For loop to make sure every selected job is added into the ArrayList
            for (Job job : selectedJobs) { 
                jobsToSave.add(new Job(nextId, job.getName(), job.getDeadline(),
                        job.getProfit(), "CSV"));
                nextId++; // Make sure the id not duplicated
            }

            fileManager.addJobs(currentUser, jobsToSave); // Save the ArrayList of selected jobs into the user file

            // Display the successfully added message 
            System.out.println();
            System.out.println("  ✓ " + jobsToSave.size() + " project(s) imported successfully!");
            System.out.println("    Saved to: " + currentUser.getFilePath());
            System.out.println();

            System.out.printf("  %-6s %-25s %-10s %-10s %-8s%n",
                    "ID", "Name", "Deadline", "Profit", "Type");
            System.out.println(TABLE_LINE);
            for (Job job : jobsToSave) {
                System.out.printf("  %-6s %-25s %-10d %-10d %-8s%n",
                        "J" + job.getId(), job.getName(), job.getDeadline(),
                        job.getProfit(), job.getType());
            }

        } 
        // Catch exception 
        catch (IOException e) {
            System.out.println();
            System.out.println("  ✗ Error saving projects: " + e.getMessage());
        }

        // Pause before back to add job menu 
        System.out.println();
        System.out.println("  [Returning to Add Job Menu]");
        pause();
    }
    
    // This method is to display the imported jobs in arranged table
    private void displayImportedJobsTable(List<Job> jobs) {

        System.out.printf("  %-4s %-25s %-10s %-10s%n",
                "#", "Project Name", "Profit", "Deadline");
        System.out.println(TABLE_LINE);
        // For loop to display every imported jobs in the csv file
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.printf("  %-4d %-25s %-10d %-10d%n",
                    (i + 1), job.getName(), job.getProfit(),
                    job.getDeadline());
        }
        System.out.println(TABLE_LINE);
    }
    
    // This method is to select the projects or jobs from the imported jobs
    private List<Job> selectProjectsFromList(List<Job> jobs) {
        System.out.println();
        System.out.println("  Select projects to import:");
        System.out.println("    - Enter 'all' to import all projects");
        System.out.println("    - Enter row numbers separated by commas (e.g. 1,3,5)");
        System.out.println("    - Enter a range (e.g. 1-5)");
        System.out.println("    - Enter 0 to cancel");
        System.out.println();
        System.out.print("  Your selection: ");

        // Get the selection of projects from user
        String input = scanner.nextLine().trim();
        if (input.equals("0")) {
            return Collections.emptyList(); // Back if 0 inserted
        }

        List<Job> selected = new ArrayList<>(); // Create a selected ArrayList to store the selected jobs

        // Add all imorted jobs or projects if add all inserted
        if (input.equalsIgnoreCase("all")) {
            selected.addAll(jobs);
        } 
        // Add the range of selection that inserted by the user
        else if (input.contains("-") && !input.contains(",")) {
            try {
                String[] range = input.split("-"); // Get the range
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                // Validate the range that inserted by the user
                if (start < 1 || end > jobs.size() || start > end) {
                    System.out.println("  ✗ Invalid range. Must be between 1 and " + jobs.size());
                    return Collections.emptyList();
                }
                // Add every selected jobs into the selected ArrayList 
                for (int i = start; i <= end; i++) {
                    selected.add(jobs.get(i - 1));
                }
            } 
            // Catch exception
            catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("  ✗ Invalid range format. Use e.g. 1-5");
                return Collections.emptyList();
            }
        } 
        // Get the number of jobs or projects that would like to added by the user 
        else {
            String[] parts = input.split(","); // Get the part by spliting the data using ,
            // Validate and add every part that inserted by user
            for (String part : parts) {
                try {
                    int idx = Integer.parseInt(part.trim());
                    if (idx < 1 || idx > jobs.size()) {
                        System.out.println("  ✗ Invalid row number: " + idx
                                + ". Must be between 1 and " + jobs.size());
                        return Collections.emptyList();
                    }
                    selected.add(jobs.get(idx - 1)); // Add the selected jobs that inserted by the user
                } 
                // Catch exception
                catch (NumberFormatException e) {
                    System.out.println("  ✗ Invalid number: " + part.trim());
                    return Collections.emptyList();
                }
            }
        }
        return selected; // Return the final selected ArrayList
    }
    
    // This method is to help the user randomly generate projects or jobs with the specific range 
    private void generateRandomData() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                  GENERATE RANDOM DATA                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Configure the random project generator:");
        System.out.println("  (Names are selected from a pool of "
                + RandomDataGenerator.getProjectNames().length + " predefined project names)");
        System.out.println();

        try {
            // Get user configuration
            int n = readIntOrBack("Number of projects to generate"); // Get the number of projects 
            if (n == 0){
            	return; // Back to the main menu if 0 inserted
            }

            System.out.println();
            int maxProfit = readIntOrBack("Maximum Profit"); // Get the maximum profit
            if (maxProfit == 0) {
            	return; // Back to the main menu if 0 inserted
            }

            System.out.println();
            int maxDeadline = readIntOrBack("Maximum Deadline"); // Get the maximum deadline
            if (maxDeadline == 0) {
            	return; // Back to the main menu if 0 inserted
            }

            // Generate random data using the RandomDataGenerator
            RandomDataGenerator generator = new RandomDataGenerator(n, maxProfit, maxDeadline);

            List<Job> generatedJobs = generator.loadJobs();

            // Display generated dataset in table format
            System.out.println();
            System.out.println("  ✓ Generated " + generatedJobs.size() + " random projects!");
            System.out.println();
            System.out.printf("  %-4s %-25s %-10s %-10s%n",
                    "#", "Project Name", "Profit", "Deadline");
            System.out.println(TABLE_LINE);
            for (int i = 0; i < generatedJobs.size(); i++) {
                Job job = generatedJobs.get(i);
                System.out.printf("  %-4d %-25s %-10d %-10d%n",
                        (i + 1), job.getName(), job.getProfit(),
                        job.getDeadline());
            }
            System.out.println(TABLE_LINE);

            // Ask user whether to save random generated jobs or not
            System.out.println();
            System.out.print("  Save these projects to your file? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase(); // Get the confirm from user

            // Save the project generated if y or yes inserted
            if (confirm.equals("y") || confirm.equals("yes")) {
                // Re-assign IDs based on existing jobs
                List<Job> existing = fileManager.loadJobs(currentUser);
                int nextId = getNextJobId(existing);

                List<Job> jobsToSave = new ArrayList<>();
                for (Job job : generatedJobs) {
                    jobsToSave.add(new Job(nextId, job.getName(), job.getDeadline(),
                            job.getProfit(), "RANDOM"));
                    nextId++;
                }

                fileManager.addJobs(currentUser, jobsToSave); // Add the generated jobs into the user text file

                System.out.println();
                System.out.println("  ✓ " + jobsToSave.size()
                        + " projects saved to " + currentUser.getFilePath());
            }
            else {
                System.out.println();
                System.out.println("  Projects discarded. Nothing was saved.");
            }

        } 
        // Catch exception 
        catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("  ✗ Invalid configuration: " + e.getMessage());
        } 
        catch (IOException e) {
            System.out.println();
            System.out.println("  ✗ Error saving projects: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  [Returning to Main Menu]");
        pause();
    }

    // This method is to show the algorithm that allow the user to choose
    private void showAlgorithmMenu() {
        while (true) {
            clearScreen();
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                    SELECT ALGORITHM                       ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");

            try {
                List<Job> jobs = fileManager.loadJobs(currentUser); // Load the jobs of the current user
                // Back to main menu if there are no any job in the current user text file
                if (jobs.isEmpty()) {
                    System.out.println("   No jobs found. Use 'Add Job' to create some first.");
                    pause();
                    return;
                }
                // Allow the user to choose the algorithm if there are record of jobs
                System.out.println("  Available Algorithms:");
                System.out.println();
                for (int i = 0; i < SCHEDULERS.length; i++) {
                    System.out.println("    " + (i + 1) + ". " + SCHEDULERS[i].getAlgorithmName());
                }
                System.out.println();
                System.out.println("    0. Back to Main Menu");
                System.out.println();
                System.out.print("  Enter your choice (1-" + SCHEDULERS.length + ") [0 = Back]: ");
                
                String choice = scanner.nextLine().trim(); // Allow the user to make choice

                if (choice.equals("0")) {
                    return; // Back to main menu if 0 inserted
                }
                // Parse the choice into interger index - 1
                int idx;
                try {
                    idx = Integer.parseInt(choice) - 1;
                } 
                // Catch exception
                catch (NumberFormatException e) {
                    System.out.println();
                    System.out.println("   Invalid choice. Please try again.");
                    pause();
                    continue; // Continue the loop if the exception catched
                }
                
                // Validate the range of the choice within 0 and schedulers.length
                if (idx < 0 || idx >= SCHEDULERS.length) {
                    System.out.println();
                    System.out.println("   Invalid choice. Please try again.");
                    pause();
                    continue; // Continue the loop if the index not in the range 
                }
                runAlgorithm(jobs, idx); // Call the runAlgorithm with the current user jobs and algorithm selected

            } 
            // Catch exception 
            catch (IOException e) {
                System.out.println();
                System.out.println("   Error: " + e.getMessage());
                pause();
                return;
            }
        }
    }
    
    // This method is to run the selected algorithms with the current user jobs
    private void runAlgorithm(List<Job> jobs, int idx) {
        try {
            clearScreen();
            printHeader(SCHEDULERS[idx].getAlgorithmName());

            // Display sorting preview
            printSection("Sorting Preview (by Profit, descending)");
            List<Job> sorted = new ArrayList<>(jobs);
            Collections.sort(sorted);
            for (int i = 0; i < sorted.size(); i++) {
                Job job = sorted.get(i);
                System.out.printf("  %d. J%d  Deadline: %d  Profit: %d%n",
                        i + 1, job.getId(), job.getDeadline(), job.getProfit());
            }
            

            System.out.println();
            System.out.println("  Running " + SCHEDULERS[idx].getAlgorithmName() + "...");
            lastResult = SCHEDULERS[idx].schedule(jobs); // Keep the lastest result to updated

            // Update the scheduling result using the last result
            fileManager.updateSchedulingResult(currentUser, lastResult.getSelectedJobs(), lastResult.getUnselectedJobs());
            // Display the result of algorithms selected
            displayResultSummary(lastResult);

            System.out.println("  ✓ Results saved to " + currentUser.getFilePath());
            System.out.println();
            System.out.println("  [Returning to Algorithm Menu]");

        } 
        // Catch exception
        catch (IOException e) {
            System.out.println();
            System.out.println("   Error: " + e.getMessage());
        }

        pause();
    }
 
    // This method is to view the latest results of the current user
    private void viewResults() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   SCHEDULING RESULTS                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        try {
            List<Job> jobs = fileManager.loadJobs(currentUser); // Load jobs of the current user
            List<String> selected = fileManager.loadSelected(currentUser); // Load the selected jobs of the current user
            List<String> unselected = fileManager.loadUnselected(currentUser); // Load the unselected jobs of the current user
            Map<String, String> statusMap = fileManager.loadStatus(currentUser); // Load the status of the jobs

            // Back to main menu if both selected and unselected
            if (selected.isEmpty() && unselected.isEmpty()) {
                System.out.println("   No results yet. Run an algorithm first.");
                pause();
                return;
            }

            // Print the selected jobs
            printSection("Selected Jobs");
            // Display none if no any selected jobs
            if (selected.isEmpty()) {
                System.out.println("  (none)");
            }
            else {
                System.out.printf("  %-6s %-20s %-10s %-10s %-12s %-8s%n", "ID", "Name", "Deadline", "Profit", "Status", "Type");
                System.out.println(TABLE_LINE);
                // For loop to get every selected job
                for (String jobId : selected) {
                    Job job = findJobById(jobs, jobId); // Find the job using Id
                    if (job != null) {
                        String status = statusMap.getOrDefault(jobId, "PENDING");
                        System.out.printf("  %-6s %-20s %-10d %-10d %-12s %-8s%n",
                                jobId, job.getName(), job.getDeadline(), job.getProfit(),
                                status, job.getType());
                    } 
                    else {
                        System.out.println("  " + jobId);
                    }
                }
            }

            // Print the unselected jobs
            printSection("Unselected Jobs");
            // Display none if no any unselected jobs
            if (unselected.isEmpty()) {
                System.out.println("  (none)");
            }
            else {
                System.out.printf("  %-6s %-20s %-10s %-10s %-12s %-8s%n",
                        "ID", "Name", "Deadline", "Profit", "Status", "Type");
                System.out.println(TABLE_LINE);
                // For loop to get every unselected job
                for (String jobId : unselected) {
                    Job job = findJobById(jobs, jobId);
                    if (job != null) {
                        String status = statusMap.getOrDefault(jobId, "PENDING");
                        System.out.printf("  %-6s %-20s %-10d %-10d %-12s %-8s%n",
                                jobId, job.getName(), job.getDeadline(), job.getProfit(),
                                status, job.getType());
                    } 
                    else {
                        System.out.println("  " + jobId);
                    }
                }
            }

            // Display the total profit of the selected jobs
            int totalProfit = 0;
            for (String jobId : selected) {
                Job job = findJobById(jobs, jobId);
                if (job != null) {
                    totalProfit += job.getProfit(); // Get the sum of profit of the selected jobs
                }
            }
            System.out.println();
            System.out.println(TABLE_LINE);
            System.out.println("  Total Profit from Selected: " + totalProfit);

        } 
        // Catch exception 
        catch (IOException e) {
            System.out.println("   Error reading results: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  [Returning to Main Menu]");
        pause();
    }

    // This method is to allow user update the job status
    private void updateJobStatus() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   UPDATE JOB STATUS                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        try {
            List<Job> jobs = fileManager.loadJobs(currentUser); // Load the jobs of the current user
            // Back if there are no any jobs found
            if (jobs.isEmpty()) {
                System.out.println("   No jobs found. Use 'Add Job' to create some first.");
                pause();
                return;
            }
            
            Map<String, String> statusMap = fileManager.loadStatus(currentUser); // Load the statusMap

            System.out.printf("  %-6s %-20s %-10s %-10s %-12s %-8s%n", "ID", "Name", "Deadline", "Profit", "Status", "Type");
            System.out.println(TABLE_LINE);

            // Display the jobs of current user with the respective status
            for (Job job : jobs) {
                String jobId = "J" + job.getId();
                String status = statusMap.getOrDefault(jobId, "PENDING");
                System.out.printf("  %-6s %-20s %-10d %-10d %-12s %-8s%n",
                        jobId, job.getName(), job.getDeadline(), job.getProfit(),
                        status, job.getType());
            }

            System.out.println(TABLE_LINE);
            System.out.println();
            System.out.print("  Enter Job ID to update (e.g. J1) [0 = Back]: ");
            String jobId = scanner.nextLine().trim().toUpperCase(); // Get the jobId that would like to be updated
            // Back if 0 is inserted
            if (jobId.equals("0")) {
                return;
            }
            // Back if there are no jobId is inserted
            if (jobId.isEmpty()) {
                System.out.println();
                System.out.println("   No Job ID entered.");
                pause();
                return;
            }
            // Add J if the jobId do not start with J
            if (!jobId.startsWith("J")) {
                jobId = "J" + jobId;
            }
            // Find the job using jobId that inserted
            boolean found = false; // Initialize the found as false
            for (Job job : jobs) { // Check every job in the jobs ArrayList
                if (("J" + job.getId()).equals(jobId)) {
                    found = true; // Change the found into true if the job found
                    break;
                }
            }
            // Back if the job cannot be found using the jobId
            if (!found) {
                System.out.println();
                System.out.println("   Job " + jobId + " not found.");
                pause();
                return;
            }
            // Display the status to allow the user to update
            System.out.println();
            System.out.println("  Select new status:");
            System.out.println("    1. COMPLETED");
            System.out.println("    2. PENDING");
            System.out.println("    0. Back");
            System.out.print("  Enter choice (1 or 2) [0 = Back]: ");
            // Allow the user to make the status choice
            String statusChoice = scanner.nextLine().trim();
            String newStatus;
            // Use switch structure to get the updated status
            switch (statusChoice) {
                case "0":
                    return;
                case "1":
                    newStatus = "COMPLETED";
                    break;
                case "2":
                    newStatus = "PENDING";
                    break;
                default:
                    System.out.println();
                    System.out.println("   Invalid choice. Please try again.");
                    pause();
                    return;
            }
            // Save the updated job status
            fileManager.updateJobStatus(currentUser, jobId, newStatus);
            System.out.println();
            System.out.println("  ✓ " + jobId + " status updated to " + newStatus);

        } 
        // Catch exception
        catch (IOException e) {
            System.out.println();
            System.out.println("   Error updating status: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  [Returning to Main Menu]");
        pause();
    }

    // This method is to display the summary result that include the selected, unselected job and summary of result
    private void displayResultSummary(SchedulingResult result) {
        printSection("Result: " + result.getAlgorithmName());
        printSelectedJobs(result.getSelectedJobs());
        printUnselectedJobs(result.getUnselectedJobs());
        printSummary(result);
    }
    
    // This method is to display the selected jobs
    private void printSelectedJobs(List<Job> selected) {
    	printSection("Selected Jobs");
    	// Print none if selected jobs is empty
    	if (selected.isEmpty()) {
    		System.out.println(" (none)");
    	}
    	else {
    		for(int i = 0; i < selected.size(); i++) {
    			Job job = selected.get(i);
    			System.out.printf(" %d. J%d\tDeadline: %d Profit %d%n", i + 1, job.getId(), job.getDeadline(), job.getProfit());
    		}
    	}
    	System.out.println();
    }

    // This method is to display the unselected jobs
    private void printUnselectedJobs(List<Job> selected) {
    	printSection("Unselected Jobs");
    	// Print none if unselected jobs is empty
    	if (selected.isEmpty()) {
    		System.out.println(" (none)");
    	}
    	else {
    		for(int i = 0; i < selected.size(); i++) {
    			Job job = selected.get(i);
    			System.out.printf(" %d. J%d Deadline: %d Profit %d%n", i + 1, job.getId(), job.getDeadline(), job.getProfit());
    		}
    	}
    	System.out.println();
    }

    // This method is to display the summary of the result
    private void printSummary(SchedulingResult result) {
        List<Job> selected = result.getSelectedJobs(); // Load the selected jobs
        List<Job> unselected = result.getUnselectedJobs(); // Load the unselected jobs

        StringBuilder selectedSeq = new StringBuilder();
        for (int i = 0; i < selected.size(); i++) {
            if (i > 0) {
                selectedSeq.append(" -> ");
            }
            selectedSeq.append("J").append(selected.get(i).getId()); // Arrange the selected jobs in sequence
        }

        StringBuilder unselectedSeq = new StringBuilder();
        for (int i = 0; i < unselected.size(); i++) {
            if (i > 0) {
                unselectedSeq.append(" -> ");
            }
            unselectedSeq.append("J").append(unselected.get(i).getId()); // Arrange the unselected jobs in sequence
        }
        
        // Display the summary of result
        System.out.printf("Selected Sequence : %s%n", selected.isEmpty() ? "(none)" : selectedSeq.toString());
        System.out.printf("Unselected Sequence : %s%n", unselected.isEmpty() ? "(none)" : unselectedSeq.toString());
        System.out.printf("Total Profit : %d%n", result.getTotalProfit());
        System.out.printf("Execution Time : %.3f ms%n", result.getExecutionTimeNanos() / 1_000_000.0);
        System.out.println();
    }

    // This method is to help the job searching process through jobId
    private Job findJobById(List<Job> jobs, String jobId) {
        try {
            int id = Integer.parseInt(jobId.replaceFirst("^J", "")); // Parse the id into integer
            // Try to search within every jobs in the jobs list
            for (Job job : jobs) { 
                if (job.getId() == id) {
                    return job; // Return the jobId if found 
                }
            }
        } 
        // Catch exception
        catch (NumberFormatException e) {
           
        }
        return null; // Return null if the jobId not found
    }

    // This method is to get the next job ID and prevent ID duplicate
    private int getNextJobId(List<Job> existingJobs) {
        int nextId = 1;
        // Loop every existing jobs
        for (Job job : existingJobs) {
            if (job.getId() >= nextId) {
                nextId = job.getId() + 1; // Add 1 to get the next ID
            }
        }
        return nextId; // Return the next ID
    }

    // This method is to read the String from the user input
    private String readStringOrBack(String prompt) {
        while (true) {
            System.out.print("  " + prompt + " [0 = Back]: ");
            String input = scanner.nextLine().trim(); // Get the input from user
            if (input.equals("0")) {
                return "0"; // Return 0 string if 0 is inserted for the back purpose
            }
            if (input.isEmpty()) {
                System.out.println("   Input cannot be empty. Please try again or enter 0 to go back.");
                continue; // Continue looping if the input is not validated
            }
            return input; // Return the string input after the validation
        }
    }

    // This method is to read the integer from the user input
    private int readIntOrBack(String prompt) {
        while (true) {
            System.out.print("  " + prompt + " [0 = Back]: ");
            String input = scanner.nextLine().trim(); // Get the input from the user
            if (input.equals("0")) {
                return 0; // Return 0 if the 0 string is inserted by the user for back purpose
            }
            try {
                int val = Integer.parseInt(input); // Parse the string input into interger val
                if (val > 0) {
                    return val; // Return val if val is larger than 0 to make sure a positive integer is inserted
                }
                // Display validataion message 
                System.out.println("   Invalid input. Please enter a positive integer or 0 to go back.");
            }
            // Catch exception
            catch (NumberFormatException e) {
                System.out.println("   Invalid input. Please enter a positive integer or 0 to go back.");
            }
        }
    }
}
