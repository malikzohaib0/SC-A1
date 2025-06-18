import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginScreen extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private Timer shakeTimer;
    private Point initialClick;
    private boolean isAdmin = false;

    public LoginScreen() {
        setTitle("Lab Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setResizable(true);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        // Make window draggable
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // Main panel with layered panes
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(20, 30, 48);
                Color color2 = new Color(36, 59, 85);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                g2d.setColor(new Color(255, 255, 255, 10));
                for (int i = 0; i < 5; i++) {
                    int size = 100 + i * 50;
                    g2d.fillOval(w/2 - size/2, h/3 - size/2, size, size);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 1200, 800);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Header panel with close button
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setOpaque(false);
        headerPanel.setBounds(0, 0, 1200, 50);
        
        JPanel closeButtonPanel = new JPanel(new BorderLayout());
        closeButtonPanel.setOpaque(false);
        closeButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(231, 76, 60, 150));
        closeButton.setOpaque(true);
        closeButton.setContentAreaFilled(true);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(231, 76, 60));
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(231, 76, 60, 150));
            }
        });
        closeButton.addActionListener(e -> System.exit(0));
        
        closeButtonPanel.add(closeButton, BorderLayout.EAST);
        headerPanel.add(closeButtonPanel);
        layeredPane.add(headerPanel, JLayeredPane.PALETTE_LAYER);

        // Login box
        JPanel loginBox = createLoginBox();
        loginBox.setBounds(400, 150, 450, 550);
        layeredPane.add(loginBox, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
        setLocationRelativeTo(null);
        setVisible(true);
        animateLoginBox(loginBox);
    }

    private JPanel createLoginBox() {
        JPanel loginBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int shadowOffset = 5;
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(shadowOffset, shadowOffset, 
                                 getWidth()-shadowOffset, getHeight()-shadowOffset, 20, 20);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-shadowOffset, getHeight()-shadowOffset, 20, 20);
            }
        };
        loginBox.setPreferredSize(new Dimension(400, 450));
        loginBox.setOpaque(false);

        // Logo
        JLabel logoLabel = new JLabel("CS Dept. Lab Management");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(new Color(33, 147, 176));
        
        // Title
        JLabel titleLabel = new JLabel("Welcome");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue to your dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(150, 150, 150));

        // Email Field
        JPanel emailPanel = createInputField("Email", "example@lab.com");
        emailField = (JTextField) ((JPanel)emailPanel.getComponent(1)).getComponent(0);

        // Password Field
        JPanel passwordPanel = createInputField("Password", "********");
        passwordField = (JPasswordField) ((JPanel)passwordPanel.getComponent(1)).getComponent(0);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Login Button
        loginButton = new JButton("Sign In");
        styleButton(loginButton);
        
        // Forgot password link
        JLabel forgotPassword = new JLabel("<html><u>Forgot password?</u></html>");
        forgotPassword.setForeground(new Color(33, 147, 176));
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginScreen.this, 
                    "Please visit the Admin Office to retrieve your password.", 
                    "Forgot Password", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        };
        emailField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        loginButton.addActionListener(loginAction);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0; loginBox.add(logoLabel, gbc);
        gbc.gridy = 1; loginBox.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = 2; loginBox.add(titleLabel, gbc);
        gbc.gridy = 3; loginBox.add(subtitleLabel, gbc);
        gbc.gridy = 4; loginBox.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = 5; loginBox.add(emailPanel, gbc);
        gbc.gridy = 6; loginBox.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = 7; loginBox.add(passwordPanel, gbc);
        gbc.gridy = 8; loginBox.add(errorLabel, gbc);
        gbc.gridy = 9; loginBox.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = 10; loginBox.add(loginButton, gbc);
        gbc.gridy = 11; loginBox.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = 12; loginBox.add(forgotPassword, gbc);

        return loginBox;
    }

    private JPanel createInputField(String labelText, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 100, 100));
        
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JComponent field;
        if (labelText.equals("Password")) {
            field = new JPasswordField();
            ((JPasswordField)field).setEchoChar('•');
        } else {
            field = new JTextField();
            ((JTextField)field).setText(placeholder);
            ((JTextField)field).setForeground(Color.GRAY);
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (((JTextField)field).getText().equals(placeholder)) {
                        ((JTextField)field).setText("");
                        ((JTextField)field).setForeground(Color.BLACK);
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (((JTextField)field).getText().isEmpty()) {
                        ((JTextField)field).setText(placeholder);
                        ((JTextField)field).setForeground(Color.GRAY);
                    }
                }
            });
        }
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);
        
        fieldPanel.add(field, BorderLayout.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(fieldPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(33, 147, 176));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(29, 131, 157));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(33, 147, 176));
            }
        });
        
        button.addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(new Color(25, 115, 138));
            } else if (button.getModel().isRollover()) {
                button.setBackground(new Color(29, 131, 157));
            } else {
                button.setBackground(new Color(33, 147, 176));
            }
        });
    }

    private void animateLoginBox(JPanel loginBox) {
        final int originalY = loginBox.getY();
        final int targetY = originalY - 20;
        
        Timer animationTimer = new Timer(10, e -> {
            int currentY = loginBox.getY();
            if (currentY > targetY) {
                loginBox.setLocation(loginBox.getX(), currentY - 1);
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        animationTimer.start();
    }

    private void shakeLoginBox(JPanel loginBox) {
        if (shakeTimer != null && shakeTimer.isRunning()) {
            shakeTimer.stop();
        }
        
        final int originalX = loginBox.getX();
        final int shakeDistance = 10;
        final int[] count = {0};
        
        shakeTimer = new Timer(50, e -> {
            if (count[0] >= 8) {
                loginBox.setLocation(originalX, loginBox.getY());
                ((Timer)e.getSource()).stop();
                return;
            }
            
            int x = originalX + (count[0] % 2 == 0 ? shakeDistance : -shakeDistance);
            loginBox.setLocation(x, loginBox.getY());
            count[0]++;
        });
        shakeTimer.start();
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || email.equals("example@lab.com") || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            shakeLoginBox((JPanel)getContentPane().getComponent(2)); // Changed index to 2 for loginBox
            return;
        }

        loginButton.setText("Authenticating...");
        loginButton.setEnabled(false);
        
        Timer loadingTimer = new Timer(1500, e -> {
            boolean authenticated = authenticateUser(email, password);

            if (authenticated) {
                dispose();
                
                if (isAdmin) {
                    new AdminInterface();
                } else {
                    new UserInterface(email);
                }
            } else {
                errorLabel.setText("Invalid email or password.");
                loginButton.setText("Sign In");
                loginButton.setEnabled(true);
                shakeLoginBox((JPanel)getContentPane().getComponent(2)); // Changed index to 2 for loginBox
            }
        });
        loadingTimer.setRepeats(false);
        loadingTimer.start();
    }

    private boolean authenticateUser(String email, String password) {
        AuthenticationController authController = new AuthenticationController();
        AuthenticationController.AuthenticationResult result = authController.authenticate(email, password);
        
        if (result.isAuthenticated()) {
            isAdmin = result.isAdmin();
            return true;
        } else {
            if (!result.getMessage().isEmpty()) {
                errorLabel.setText(result.getMessage());
            }
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginScreen();
        });
    }
}