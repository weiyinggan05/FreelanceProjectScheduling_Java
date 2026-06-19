package datasource;

import model.*;
import java.io.*;
import java.util.*;

//Reads job data from a CSV or TXT file
public class FileDataReader implements DataSource {
	//Stores the file path provided by user
    private final String filePath;
    
    //Creates a file data reader with the given file path
    public FileDataReader(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path must not be null or empty.");
        }
        this.filePath = filePath.trim();
    }

    @Override
    //Returns file source description
    public String getDescription() {
        return "File: " + filePath;
    }
    
    //Checks whether the file exists and has supported format
    public void validateFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        if (!file.isFile()) {
            throw new IOException("Path is not a file: " + filePath);
        }
        String lower = filePath.toLowerCase();
        if (!lower.endsWith(".csv") && !lower.endsWith(".txt")) {
            throw new IOException(
                "Unsupported file format. Please use a .csv or .txt file.");
        }
    }

    @Override
    //Loads jobs from the file
    public List<Job> loadJobs() throws IOException {
        validateFile();
        List<Job> jobs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            int autoId = 1;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    System.err.println("  Warning: Skipping malformed line: " + line);
                    continue;
                }

                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }

                if (!headerSkipped) {
                    if (isHeaderRow(parts)) {
                        headerSkipped = true;
                        continue;
                    }
                    headerSkipped = true;
                }

                try {
                    Job job = parseRow(parts, autoId);
                    if (job != null) {
                        jobs.add(job);
                        autoId++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("  Warning: Skipping line with invalid numbers: " + line);
                } catch (IllegalArgumentException e) {
                    System.err.println("  Warning: Skipping invalid line: " + e.getMessage());
                }
            }
        }

        if (jobs.isEmpty()) {
            throw new IOException("No valid project data found in file: " + filePath);
        }

        return jobs;
    }
    
    //Checks whether the row is a header row
    private boolean isHeaderRow(String[] parts) {
        try {
            Integer.parseInt(parts[0]);
            return false;
        } catch (NumberFormatException e) {
            
        }
        if (parts.length >= 3) {
            try {
                Integer.parseInt(parts[1]);
                return false; 
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }
    
    //Converts one row into a Job object
    private Job parseRow(String[] parts, int autoId) {
        boolean idFirst;
        try {
            Integer.parseInt(parts[0]);
            idFirst = true;
        } catch (NumberFormatException e) {
            idFirst = false;
        }

        if (idFirst) {
            if (parts.length < 4) {
                return null;
            }
            int id = Integer.parseInt(parts[0]);
            String name = sanitizeName(parts[1]);
            int deadline = Integer.parseInt(parts[2]);
            int profit = Integer.parseInt(parts[3]);
            return new Job(id, name, deadline, profit, "CSV");
        } else {
            if (parts.length < 3) {
                return null;
            }
            String name = sanitizeName(parts[0]);
            int profit = Integer.parseInt(parts[1]);
            int deadline = Integer.parseInt(parts[2]);
            return new Job(autoId, name, deadline, profit, "CSV");
        }
    }

    //Removes spaces and replaces them with underscore
    private String sanitizeName(String name) {
        return name.replaceAll("\\s+", "_");
    }

    //Converts string to integer, returns 0 if conversion fails
    private int parseIntOrZero(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
