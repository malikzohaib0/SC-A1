import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminComplaintController {
    private static final String COMPLAINTS_FILE = "complaints.txt";
    
    public List<String[]> getAllComplaints() throws IOException {
        List<String[]> complaints = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COMPLAINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: email,date,type,complaint,status,reply
                String[] parts = line.split(",", 6);
                if (parts.length >= 5) {
                    String status = parts[4];
                    String reply = parts.length == 6 ? parts[5] : "";
                    complaints.add(new String[]{parts[0], parts[1], parts[2], parts[3], status, reply});
                }
            }
        }
        return complaints;
    }
    
    public void updateComplaint(String email, String date, String newStatus, String reply) 
            throws IllegalArgumentException, IOException {
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        List<String[]> complaints = getAllComplaints();
        boolean found = false;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPLAINTS_FILE))) {
            for (String[] complaint : complaints) {
                if (complaint[0].equals(email) && complaint[1].equals(date)) {
                    complaint[4] = newStatus;
                    if (complaint.length > 5) {
                        complaint[5] = reply;
                    } else {
                        // If the array isn't big enough, create a new one with reply
                        complaint = new String[]{
                            complaint[0], complaint[1], complaint[2], 
                            complaint[3], newStatus, reply
                        };
                    }
                    found = true;
                }
                
                writer.println(String.join(",",
                    complaint[0], // email
                    complaint[1], // date
                    complaint[2], // type
                    complaint[3], // complaint text
                    complaint[4], // status
                    complaint.length > 5 ? complaint[5] : "" // reply
                ));
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Complaint not found");
        }
    }
}