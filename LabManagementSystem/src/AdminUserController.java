import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AdminUserController {
    private static final String USERS_FILE = "users.txt";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public List<String[]> getAllUsers() throws IOException {
        List<String[]> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String role = parts[2].equals("true") ? "admin" : "user";
                    users.add(new String[]{parts[0], parts[1], role});
                }
            }
        }
        return users;
    }
    
    public void addUser(String email, String password, String role) throws IllegalArgumentException, IOException {
        validateUserData(email, password, role);
        
        // Check if user exists
        List<String[]> users = getAllUsers();
        for (String[] user : users) {
            if (user[0].equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("User with this email already exists");
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            String roleValue = role.equalsIgnoreCase("admin") ? "true" : "false";
            writer.println(email + "," + password + "," + roleValue);
        }
    }
    
    public void updateUser(String oldEmail, String newEmail, String newPassword, String newRole) 
            throws IllegalArgumentException, IOException {
        validateUserData(newEmail, newPassword, newRole);
        
        List<String[]> users = getAllUsers();
        boolean found = false;
        
        // Check if new email is being changed to an existing one (except the current user)
        if (!oldEmail.equalsIgnoreCase(newEmail)) {
            for (String[] user : users) {
                if (user[0].equalsIgnoreCase(newEmail)) {
                    throw new IllegalArgumentException("User with this email already exists");
                }
            }
        }
        
        // Update the user
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (String[] user : users) {
                if (user[0].equalsIgnoreCase(oldEmail)) {
                    String roleValue = newRole.equalsIgnoreCase("admin") ? "true" : "false";
                    writer.println(newEmail + "," + newPassword + "," + roleValue);
                    found = true;
                } else {
                    String roleValue = user[2].equals("admin") ? "true" : "false";
                    writer.println(user[0] + "," + user[1] + "," + roleValue);
                }
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("User not found");
        }
    }
    
    public void deleteUser(String email) throws IOException {
        List<String[]> users = getAllUsers();
        boolean removed = users.removeIf(user -> user[0].equalsIgnoreCase(email));
        
        if (!removed) {
            throw new IllegalArgumentException("User not found");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (String[] user : users) {
                String roleValue = user[2].equals("admin") ? "true" : "false";
                writer.println(user[0] + "," + user[1] + "," + roleValue);
            }
        }
    }
    
    private void validateUserData(String email, String password, String role) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
            throw new IllegalArgumentException("Role must be either 'admin' or 'user'");
        }
    }
}