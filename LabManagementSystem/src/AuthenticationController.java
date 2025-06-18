import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AuthenticationController {
    private static final String USER_DATABASE = "users.txt";
    
    public AuthenticationResult authenticate(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATABASE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String storedEmail = parts[0].trim();
                    String storedPassword = parts[1].trim();
                    boolean isAdmin = Boolean.parseBoolean(parts[2].trim());
                    
                    if (storedEmail.equalsIgnoreCase(email) && storedPassword.equals(password)) {
                        return new AuthenticationResult(true, isAdmin);
                    }
                }
            }
        } catch (IOException e) {
            return new AuthenticationResult(false, false, "Error accessing user database. Please contact administrator.");
        }
        return new AuthenticationResult(false, false, "Invalid email or password.");
    }
    
    public static class AuthenticationResult {
        private final boolean authenticated;
        private final boolean admin;
        private final String message;
        
        public AuthenticationResult(boolean authenticated, boolean admin) {
            this(authenticated, admin, "");
        }
        
        public AuthenticationResult(boolean authenticated, boolean admin, String message) {
            this.authenticated = authenticated;
            this.admin = admin;
            this.message = message;
        }
        
        public boolean isAuthenticated() {
            return authenticated;
        }
        
        public boolean isAdmin() {
            return admin;
        }
        
        public String getMessage() {
            return message;
        }
    }
}