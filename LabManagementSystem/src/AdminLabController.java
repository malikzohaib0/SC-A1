import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminLabController {
    private static final String LABS_FILE = "labs.txt";
    private static final String COMPUTERS_FILE = "computers.txt";
    
    // Lab operations
    public List<String[]> getAllLabs() throws IOException {
        List<String[]> labs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LABS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    labs.add(parts);
                }
            }
        }
        return labs;
    }
    
    public void addLab(String name, String department, String capacity, String location) 
            throws IllegalArgumentException, IOException {
        validateLabData(name, department, capacity, location);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(LABS_FILE, true))) {
            writer.println(String.join(",", name, department, capacity, location));
        }
    }
    
    public void updateLab(String oldName, String newName, String department, String capacity, String location) 
            throws IllegalArgumentException, IOException {
        validateLabData(newName, department, capacity, location);
        
        List<String[]> labs = getAllLabs();
        boolean found = false;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(LABS_FILE))) {
            for (String[] lab : labs) {
                if (lab[0].equals(oldName)) {
                    writer.println(String.join(",", newName, department, capacity, location));
                    found = true;
                } else {
                    writer.println(String.join(",", lab));
                }
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Lab not found");
        }
        
        // Update lab references in computers if name changed
        if (!oldName.equals(newName)) {
            updateComputerLabs(oldName, newName);
        }
    }
    
    public void deleteLab(String name) throws IOException {
        List<String[]> labs = getAllLabs();
        boolean removed = labs.removeIf(lab -> lab[0].equals(name));
        
        if (!removed) {
            throw new IllegalArgumentException("Lab not found");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(LABS_FILE))) {
            for (String[] lab : labs) {
                writer.println(String.join(",", lab));
            }
        }
        
        // Delete all computers in this lab
        deleteComputersByLab(name);
    }
    
    private void validateLabData(String name, String department, String capacity, String location) 
            throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        
        try {
            int cap = Integer.parseInt(capacity);
            if (cap <= 0) {
                throw new IllegalArgumentException("Capacity must be positive");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Capacity must be a number");
        }
        
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
    }
    
    // Computer operations
    public List<String[]> getAllComputers() throws IOException {
        List<String[]> computers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COMPUTERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    computers.add(parts);
                }
            }
        }
        return computers;
    }
    
    public void addComputer(String id, String lab, String os, String ram, String cpu, String date) 
            throws IllegalArgumentException, IOException {
        validateComputerData(id, lab, os, ram, cpu, date);
        
        // Check if lab exists
        if (!labExists(lab)) {
            throw new IllegalArgumentException("Lab does not exist");
        }
        
        // Check if computer ID already exists
        if (computerExists(id)) {
            throw new IllegalArgumentException("Computer with this ID already exists");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPUTERS_FILE, true))) {
            writer.println(String.join(",", id, lab, os, ram, cpu, date));
        }
    }
    
    public void updateComputer(String oldId, String newId, String lab, String os, String ram, String cpu, String date) 
            throws IllegalArgumentException, IOException {
        validateComputerData(newId, lab, os, ram, cpu, date);
        
        // Check if lab exists
        if (!labExists(lab)) {
            throw new IllegalArgumentException("Lab does not exist");
        }
        
        // Check if new ID is being used by another computer
        if (!oldId.equals(newId) && computerExists(newId)) {
            throw new IllegalArgumentException("Computer with this ID already exists");
        }
        
        List<String[]> computers = getAllComputers();
        boolean found = false;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPUTERS_FILE))) {
            for (String[] computer : computers) {
                if (computer[0].equals(oldId)) {
                    writer.println(String.join(",", newId, lab, os, ram, cpu, date));
                    found = true;
                } else {
                    writer.println(String.join(",", computer));
                }
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Computer not found");
        }
    }
    
    public void deleteComputer(String id) throws IOException {
        List<String[]> computers = getAllComputers();
        boolean removed = computers.removeIf(computer -> computer[0].equals(id));
        
        if (!removed) {
            throw new IllegalArgumentException("Computer not found");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPUTERS_FILE))) {
            for (String[] computer : computers) {
                writer.println(String.join(",", computer));
            }
        }
    }
    
    private void validateComputerData(String id, String lab, String os, String ram, String cpu, String date) 
            throws IllegalArgumentException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Computer ID cannot be empty");
        }
        
        if (lab == null || lab.trim().isEmpty()) {
            throw new IllegalArgumentException("Lab cannot be empty");
        }
        
        if (os == null || os.trim().isEmpty()) {
            throw new IllegalArgumentException("Operating system cannot be empty");
        }
        
        if (ram == null || ram.trim().isEmpty()) {
            throw new IllegalArgumentException("RAM cannot be empty");
        }
        
        if (cpu == null || cpu.trim().isEmpty()) {
            throw new IllegalArgumentException("CPU cannot be empty");
        }
        
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Install date cannot be empty");
        }
        
        // Simple date format validation (YYYY-MM-DD)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format");
        }
    }
    
    private boolean labExists(String labName) throws IOException {
        List<String[]> labs = getAllLabs();
        return labs.stream().anyMatch(lab -> lab[0].equals(labName));
    }
    
    private boolean computerExists(String computerId) throws IOException {
        List<String[]> computers = getAllComputers();
        return computers.stream().anyMatch(computer -> computer[0].equals(computerId));
    }
    
    private void updateComputerLabs(String oldLabName, String newLabName) throws IOException {
        List<String[]> computers = getAllComputers();
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPUTERS_FILE))) {
            for (String[] computer : computers) {
                if (computer[1].equals(oldLabName)) {
                    writer.println(String.join(",", 
                        computer[0], newLabName, computer[2], computer[3], computer[4], computer[5]));
                } else {
                    writer.println(String.join(",", computer));
                }
            }
        }
    }
    
    private void deleteComputersByLab(String labName) throws IOException {
        List<String[]> computers = getAllComputers();
        computers.removeIf(computer -> computer[1].equals(labName));
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPUTERS_FILE))) {
            for (String[] computer : computers) {
                writer.println(String.join(",", computer));
            }
        }
    }
}