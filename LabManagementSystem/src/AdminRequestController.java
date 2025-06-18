import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class AdminRequestController {
    private static final String REQUESTS_FILE = "requests.txt";
    
    public List<String[]> getAllRequests() throws IOException {
        List<String[]> requests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REQUESTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split into maximum 8 parts (comment may contain commas)
                String[] parts = line.split(",", 8);
                if (parts.length >= 7) {
                    // Ensure we always have 8 elements (comment might be missing)
                    String[] request = new String[8];
                    System.arraycopy(parts, 0, request, 0, Math.min(parts.length, 8));
                    requests.add(request);
                }
            }
        }
        return requests;
    }
    
    public void updateRequestStatus(String userEmail, String date, String newStatus) 
            throws IllegalArgumentException, IOException {
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        List<String[]> requests = getAllRequests();
        boolean found = false;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(REQUESTS_FILE))) {
            for (String[] request : requests) {
                if (request[0].equals(userEmail) && request[1].equals(date)) {
                    request[6] = newStatus; // Update status
                    found = true;
                }
                
                // Only write fields up to the last non-empty one
                int lastNonEmpty = request.length - 1;
                while (lastNonEmpty >= 0 && (request[lastNonEmpty] == null || request[lastNonEmpty].isEmpty())) {
                    lastNonEmpty--;
                }
                String[] toWrite = Arrays.copyOfRange(request, 0, lastNonEmpty + 1);
                writer.println(String.join(",", toWrite));
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Request not found");
        }
    }
    
    public void addCommentToRequest(String userEmail, String date, String comment) 
            throws IllegalArgumentException, IOException {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        
        List<String[]> requests = getAllRequests();
        boolean found = false;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(REQUESTS_FILE))) {
            for (String[] request : requests) {
                if (request[0].equals(userEmail) && request[1].equals(date)) {
                    if (request.length > 7 && request[7] != null && !request[7].isEmpty()) {
                        // Append to existing comment
                        request[7] = request[7] + "; " + comment;
                    } else {
                        // Create new comment
                        String[] newRequest = new String[8];
                        System.arraycopy(request, 0, newRequest, 0, 7);
                        newRequest[7] = comment;
                        request = newRequest;
                    }
                    found = true;
                }
                
                // Only write fields up to the last non-empty one
                int lastNonEmpty = request.length - 1;
                while (lastNonEmpty >= 0 && (request[lastNonEmpty] == null || request[lastNonEmpty].isEmpty())) {
                    lastNonEmpty--;
                }
                String[] toWrite = Arrays.copyOfRange(request, 0, lastNonEmpty + 1);
                writer.println(String.join(",", toWrite));
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Request not found");
        }
    }
}