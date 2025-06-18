import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

public class RequestController {
    private static final String REQUESTS_FILE = "requests.txt";
    private static final String SPECS_FILE = "specs.txt";
    
    public void loadPCs(JComboBox<String> pcCombo) {
        pcCombo.removeAllItems();
        pcCombo.addItem("N/A");
        
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
    
    public void submitRequest(String email, String type, String pc, String item, String reason) {
        validateRequestFields(item, reason);
        
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String record = String.join(",",
                email,
                date,
                type,
                pc.equals("N/A") ? "" : pc,
                sanitizeInput(item),
                sanitizeInput(reason),
                "Pending"
        );
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(REQUESTS_FILE, true))) {
            writer.println(record);
        } catch (IOException e) {
            throw new RuntimeException("Error saving request: " + e.getMessage());
        }
    }
    
    private void validateRequestFields(String item, String reason) {
        if (item == null || item.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        if (item.length() > 100) {
            throw new IllegalArgumentException("Item name too long (max 100 chars)");
        }
        if (reason.length() > 500) {
            throw new IllegalArgumentException("Reason too long (max 500 chars)");
        }
    }
    
    private String sanitizeInput(String input) {
        return input.replace(",", "").replace("\n", " ").trim();
    }
}