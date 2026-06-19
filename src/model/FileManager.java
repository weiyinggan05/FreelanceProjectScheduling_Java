package model;

import java.io.*;
import java.util.*;

//Manages reading and writing user job data files
//Stores jobs, selected jobs, unselected jobs and job status in text files
public class FileManager {

	//Creates the data folder if it does not exist
    public void ensureDataDirectory() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    //Check the user file exist in folder 
    public boolean userFileExists(User user) {
        return new File(user.getFilePath()).exists();
    }
    
    //Creates a new user file with default sections
    public void createUserFile(User user) throws IOException {
        ensureDataDirectory();
        try (FileWriter writer = new FileWriter(user.getFilePath())) {
            writer.write("# JOB LIST\n\n");
            writer.write("# SELECTED\n\n");
            writer.write("# UNSELECTED\n\n");
            writer.write("# STATUS\n");
        }
    }
    
    //Loads all jobs from user file
    public List<Job> loadJobs(User user) throws IOException {
        List<Job> jobs = new ArrayList<>();
        List<String> lines = readSection(user.getFilePath(), "JOB LIST");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            try {
                String jobIdStr = parts[0];
                if (!jobIdStr.startsWith("J")) {
                    System.err.println("Warning: Skipping line with invalid job ID (must start with J): " + line);
                    continue;
                }
                int id = Integer.parseInt(jobIdStr.replaceFirst("^J", ""));

                if (parts.length >= 5) {
                    String name = parts[1];
                    int deadline = Integer.parseInt(parts[2]);
                    int profit = Integer.parseInt(parts[3]);
                    String type = parts[4];
                    jobs.add(new Job(id, name, deadline, profit, type));
                } else if (parts.length >= 4) {
                    String name = parts[1];
                    int deadline = Integer.parseInt(parts[2]);
                    int profit = Integer.parseInt(parts[3]);
                    jobs.add(new Job(id, name, deadline, profit, "MANUAL"));
                } else if (parts.length >= 3) {
                    int deadline = Integer.parseInt(parts[1]);
                    int profit = Integer.parseInt(parts[2]);
                    jobs.add(new Job(id, "Job" + id, deadline, profit, "MANUAL"));
                } else {
                    System.err.println("Warning: Skipping malformed job line: " + line);
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Skipping line with invalid numbers: " + line);
            }
        }

        return jobs;
    }
    
    //Loads selected job IDs from file
    public List<String> loadSelected(User user) throws IOException {
        List<String> selected = new ArrayList<>();
        List<String> lines = readSection(user.getFilePath(), "SELECTED");

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                selected.add(line);
            }
        }
        return selected;
    }
    
    //Loads unselected job IDs from file
    public List<String> loadUnselected(User user) throws IOException {
        List<String> unselected = new ArrayList<>();
        List<String> lines = readSection(user.getFilePath(), "UNSELECTED");

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                unselected.add(line);
            }
        }
        return unselected;
    }
    
    //Loads job status from file
    public Map<String, String> loadStatus(User user) throws IOException {
        Map<String, String> statusMap = new HashMap<>();
        List<String> lines = readSection(user.getFilePath(), "STATUS");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                statusMap.put(parts[0], parts[1]);
            }
        }
        return statusMap;
    }
    
    //Adds one new job into file
    public void addJob(User user, Job job) throws IOException {
        List<Job> jobs = loadJobs(user);
        List<String> selected = loadSelected(user);
        List<String> unselected = loadUnselected(user);
        Map<String, String> statusMap = loadStatus(user);

        jobs.add(job);
        statusMap.put("J" + job.getId(), "PENDING");

        writeFile(user, jobs, selected, unselected, statusMap);
    }

    //Adds multiple jobs into file
    public void addJobs(User user, List<Job> newJobs) throws IOException {
        List<Job> jobs = loadJobs(user);
        List<String> selected = loadSelected(user);
        List<String> unselected = loadUnselected(user);
        Map<String, String> statusMap = loadStatus(user);

        for (Job job : newJobs) {
            jobs.add(job);
            statusMap.put("J" + job.getId(), "PENDING");
        }

        writeFile(user, jobs, selected, unselected, statusMap);
    }
    
    //Updates selected and unselected job results
    public void updateSchedulingResult(User user,
                                       List<Job> selectedJobs,
                                       List<Job> unselectedJobs) throws IOException {
        List<Job> jobs = loadJobs(user);
        Map<String, String> statusMap = loadStatus(user);

        List<String> selected = new ArrayList<>();
        for (Job job : selectedJobs) {
            selected.add("J" + job.getId());
        }

        List<String> unselected = new ArrayList<>();
        for (Job job : unselectedJobs) {
            unselected.add("J" + job.getId());
        }

        writeFile(user, jobs, selected, unselected, statusMap);
    }

    //Updates status of a job
    public void updateJobStatus(User user, String jobId, String status) throws IOException {
        List<Job> jobs = loadJobs(user);
        List<String> selected = loadSelected(user);
        List<String> unselected = loadUnselected(user);
        Map<String, String> statusMap = loadStatus(user);

        statusMap.put(jobId, status);

        writeFile(user, jobs, selected, unselected, statusMap);
    }

    //Writes all user data back to file
    private void writeFile(User user,
                           List<Job> jobs,
                           List<String> selected,
                           List<String> unselected,
                           Map<String, String> statusMap) throws IOException {
        ensureDataDirectory();
        try (FileWriter writer = new FileWriter(user.getFilePath())) {
            writer.write("# JOB LIST\n");
            for (Job job : jobs) {
                writer.write("J" + job.getId() + " " + job.getName() + " "
                        + job.getDeadline() + " " + job.getProfit() + " "
                        + job.getType() + "\n");
            }
            writer.write("\n");

            writer.write("# SELECTED\n");
            for (String id : selected) {
                writer.write(id + "\n");
            }
            writer.write("\n");

            writer.write("# UNSELECTED\n");
            for (String id : unselected) {
                writer.write(id + "\n");
            }
            writer.write("\n");

            writer.write("# STATUS\n");
            for (Job job : jobs) {
                String jobId = "J" + job.getId();
                String st = statusMap.getOrDefault(jobId, "PENDING");
                writer.write(jobId + " " + st + "\n");
            }
        }
    }

    //Reads one section from the file
    private List<String> readSection(String filePath, String sectionName) throws IOException {
        List<String> sectionLines = new ArrayList<>();
        boolean inSection = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.equals("# " + sectionName)) {
                    inSection = true;
                    continue;
                }

                if (inSection) {
                    if (trimmed.startsWith("# ")) {
                        break;
                    }
                    sectionLines.add(trimmed);
                }
            }
        }

        return sectionLines;
    }
}
