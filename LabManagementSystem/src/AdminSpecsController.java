import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminSpecsController {
    private static final String SPECS_FILE = "specs.txt";
    
    public List<String[]> getAllSpecs() throws IOException {
        List<String[]> specs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SPECS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    specs.add(parts);
                }
            }
        }
        return specs;
    }
    
    public void addSpecs(String computerId, String os, String ram, String storage, String gpu) 
            throws IllegalArgumentException, IOException {
        validateSpecsData(computerId, os, ram, storage, gpu);
        
        // Check if specs already exist for this computer
        if (specsExist(computerId)) {
            throw new IllegalArgumentException("Specifications already exist for this computer");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(SPECS_FILE, true))) {
            writer.println(String.join(",", computerId, os, ram, storage, gpu));
        }
    }
    
    public void updateSpecs(String computerId, String os, String ram, String storage, String gpu) 
            throws IllegalArgumentException, IOException {
        validateSpecsData(computerId, os, ram, storage, gpu);
        
        List<String[]> specs = getAllSpecs();
        boolean found = false;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(SPECS_FILE))) {
            for (String[] spec : specs) {
                if (spec[0].equals(computerId)) {
                    writer.println(String.join(",", computerId, os, ram, storage, gpu));
                    found = true;
                } else {
                    writer.println(String.join(",", spec));
                }
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Specifications not found for this computer");
        }
    }
    
    public void deleteSpecs(String computerId) throws IOException {
        List<String[]> specs = getAllSpecs();
        boolean removed = specs.removeIf(spec -> spec[0].equals(computerId));
        
        if (!removed) {
            throw new IllegalArgumentException("Specifications not found for this computer");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(SPECS_FILE))) {
            for (String[] spec : specs) {
                writer.println(String.join(",", spec));
            }
        }
    }
    
    private void validateSpecsData(String computerId, String os, String ram, String storage, String gpu) 
            throws IllegalArgumentException {
        if (computerId == null || computerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Computer ID cannot be empty");
        }
        
        if (os == null || os.trim().isEmpty()) {
            throw new IllegalArgumentException("Operating system cannot be empty");
        }
        
        if (ram == null || ram.trim().isEmpty()) {
            throw new IllegalArgumentException("RAM cannot be empty");
        }
        
        if (storage == null || storage.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage cannot be empty");
        }
        
        if (gpu == null || gpu.trim().isEmpty()) {
            throw new IllegalArgumentException("GPU cannot be empty");
        }
    }
    
    private boolean specsExist(String computerId) throws IOException {
        List<String[]> specs = getAllSpecs();
        return specs.stream().anyMatch(spec -> spec[0].equals(computerId));
    }
}