import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JComboBox;

public class ComplaintController {
    private static final String COMPLAINTS_FILE = "complaints.txt";
    private static final String SPECS_FILE = "specs.txt";
    
    public void loadPCs(JComboBox<String> pcCombo) {
        pcCombo.removeAllItems();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SPECS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    pcCombo.addItem(parts[0]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading PC list: " + e.getMessage());
        }
    }
    
    public void submitComplaint(String email, String pc, String type, String details) {
        validateComplaintFields(pc, details);
        
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String record = String.join(",",
                email,
                date,
                type + " issue with " + pc,
                sanitizeInput(details),
                "Pending"
        );
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPLAINTS_FILE, true))) {
            writer.println(record);
        } catch (IOException e) {
            throw new RuntimeException("Error saving complaint: " + e.getMessage());
        }
    }
    
    private void validateComplaintFields(String pc, String details) {
        if (pc == null || pc.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a PC");
        }
        if (details == null || details.trim().isEmpty()) {
            throw new IllegalArgumentException("Details cannot be empty");
        }
        if (details.length() > 1000) {
            throw new IllegalArgumentException("Details too long (max 1000 chars)");
        }
    }
    
    private String sanitizeInput(String input) {
        return input.replace(",", "").replace("\n", " ").trim();
    }
}