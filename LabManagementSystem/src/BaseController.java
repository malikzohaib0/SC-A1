// BaseController.java
import javax.swing.*;
import java.util.regex.*;

public class BaseController {
    protected boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    protected boolean validateTextOnly(String text) {
        return text.matches("[a-zA-Z ]+");
    }

    protected boolean validateNumbersOnly(String text) {
        return text.matches("\\d+");
    }

    protected boolean validateAlphanumeric(String text) {
        return text.matches("^[a-zA-Z0-9 ]+$");
    }

    protected boolean validateDate(String date) {
        return date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}