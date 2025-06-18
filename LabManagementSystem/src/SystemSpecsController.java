import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SystemSpecsController {
    private static final String SPECS_FILE = "specs.txt";
    
    public DefaultTableModel loadSpecs() {
        String[] columns = {"PC ID", "OS", "RAM", "Storage", "Graphics"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SPECS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    model.addRow(parts);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading specifications: " + e.getMessage());
        }
        
        return model;
    }
}