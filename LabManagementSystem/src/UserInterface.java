import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class UserInterface extends JFrame {
    private String userEmail;
    private SystemSpecsController specsController;
    private RequestController requestController;
    private ComplaintController complaintController;

    public UserInterface(String email) {
        this.userEmail = email;
        this.specsController = new SystemSpecsController();
        this.requestController = new RequestController();
        this.complaintController = new ComplaintController();
        
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User Dashboard - " + userEmail);
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color1 = new Color(20, 30, 48);
                Color color2 = new Color(36, 59, 85);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("User Dashboard - " + userEmail);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new LoginScreen();
            dispose();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttonNames = {
            "View System Specifications",
            "Submit HW/SW Request",
            "Submit Complaint"
        };

        for (String name : buttonNames) {
            JButton button = new JButton(name);
            styleMenuButton(button);
            buttonPanel.add(button);
        }

        // Add action listeners to buttons
        ((JButton) buttonPanel.getComponent(0)).addActionListener(e -> new SpecsViewer(userEmail));
        ((JButton) buttonPanel.getComponent(1)).addActionListener(e -> new RequestSubmitter(userEmail));
        ((JButton) buttonPanel.getComponent(2)).addActionListener(e -> new ComplaintSubmitter(userEmail));
        
        return buttonPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(33, 147, 176));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(29, 131, 157));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(33, 147, 176));
            }
        });
    }

    private void styleMenuButton(JButton button) {
        button.setBackground(new Color(44, 62, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }
        });
    }

    // Inner class for System Specifications Viewer
    class SpecsViewer extends JFrame {
        public SpecsViewer(String email) {
            setTitle("System Specifications");
            setSize(900, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Load data using controller
            DefaultTableModel model;
            try {
                model = specsController.loadSpecs();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTable specsTable = new JTable(model);
            specsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            specsTable.setForeground(Color.WHITE);
            specsTable.setBackground(new Color(44, 62, 80));
            specsTable.setSelectionBackground(new Color(33, 147, 176));
            specsTable.setGridColor(new Color(100, 100, 100));
            specsTable.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(specsTable);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            // Refresh button
            JButton refreshButton = new JButton("Refresh");
            styleButton(refreshButton);
            refreshButton.addActionListener(e -> {
                try {
                    model.setRowCount(0);
                    DefaultTableModel newModel = specsController.loadSpecs();
                    for (int i = 0; i < newModel.getRowCount(); i++) {
                        Object[] row = new Object[newModel.getColumnCount()];
                        for (int j = 0; j < newModel.getColumnCount(); j++) {
                            row[j] = newModel.getValueAt(i, j);
                        }
                        model.addRow(row);
                    }
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setOpaque(false);
            buttonPanel.add(refreshButton);
            
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }
    }

    // Inner class for Request Submitter
    class RequestSubmitter extends JFrame {
        private JComboBox<String> typeCombo, pcCombo;
        private JTextField itemField;
        private JTextArea reasonArea;
    
        public RequestSubmitter(String email) {
            setTitle("Submit HW/SW Request");
            setSize(600, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setOpaque(false);
    
            typeCombo = new JComboBox<>(new String[]{"Software", "Hardware"});
            typeCombo.setBackground(new Color(44, 62, 80));
            typeCombo.setForeground(Color.WHITE);
            typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            pcCombo = new JComboBox<>();
            pcCombo.setBackground(new Color(44, 62, 80));
            pcCombo.setForeground(Color.WHITE);
            pcCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
            itemField = createStyledTextField();
            reasonArea = new JTextArea(5, 20);
            reasonArea.setLineWrap(true);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            reasonArea.setForeground(Color.WHITE);
            reasonArea.setBackground(new Color(44, 62, 80));
            reasonArea.setCaretColor(Color.WHITE);
            JScrollPane reasonScroll = new JScrollPane(reasonArea);
            reasonScroll.setOpaque(false);
            reasonScroll.getViewport().setOpaque(false);
            reasonScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
    
            formPanel.add(createStyledLabel("Request Type:"));
            formPanel.add(typeCombo);
            formPanel.add(createStyledLabel("PC (if applicable):"));
            formPanel.add(pcCombo);
            formPanel.add(createStyledLabel("Item Name:"));
            formPanel.add(itemField);
            formPanel.add(createStyledLabel("Reason:"));
            formPanel.add(reasonScroll);
    
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonPanel.setOpaque(false);
            
            JButton submitButton = new JButton("Submit Request");
            styleButton(submitButton);
            submitButton.addActionListener(e -> {
                try {
                    requestController.submitRequest(
                        email,
                        (String) typeCombo.getSelectedItem(),
                        (String) pcCombo.getSelectedItem(),
                        itemField.getText().trim(),
                        reasonArea.getText().trim()
                    );
                    JOptionPane.showMessageDialog(this, 
                        "Request submitted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error submitting request: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Load PCs using controller
            try {
                requestController.loadPCs(pcCombo);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
            add(mainPanel);
            setVisible(true);
        }
    }

    // Inner class for Complaint Submitter
    class ComplaintSubmitter extends JFrame {
        private JComboBox<String> pcCombo, typeCombo;
        private JTextArea detailsArea;

        public ComplaintSubmitter(String email) {
            setTitle("Submit Complaint");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setOpaque(false);

            pcCombo = new JComboBox<>();
            pcCombo.setBackground(new Color(44, 62, 80));
            pcCombo.setForeground(Color.WHITE);
            pcCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            typeCombo = new JComboBox<>(new String[]{"Hardware", "Software", "Network", "Other"});
            typeCombo.setBackground(new Color(44, 62, 80));
            typeCombo.setForeground(Color.WHITE);
            typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            detailsArea = new JTextArea(5, 20);
            detailsArea.setLineWrap(true);
            detailsArea.setWrapStyleWord(true);
            detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            detailsArea.setForeground(Color.WHITE);
            detailsArea.setBackground(new Color(44, 62, 80));
            detailsArea.setCaretColor(Color.WHITE);
            JScrollPane detailsScroll = new JScrollPane(detailsArea);
            detailsScroll.setOpaque(false);
            detailsScroll.getViewport().setOpaque(false);
            detailsScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

            formPanel.add(createStyledLabel("PC:"));
            formPanel.add(pcCombo);
            formPanel.add(createStyledLabel("Complaint Type:"));
            formPanel.add(typeCombo);
            formPanel.add(createStyledLabel("Details:"));
            formPanel.add(detailsScroll);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonPanel.setOpaque(false);
            
            JButton submitButton = new JButton("Submit Complaint");
            styleButton(submitButton);
            submitButton.addActionListener(e -> {
                try {
                    complaintController.submitComplaint(
                        email,
                        (String) pcCombo.getSelectedItem(),
                        (String) typeCombo.getSelectedItem(),
                        detailsArea.getText().trim()
                    );
                    JOptionPane.showMessageDialog(this, 
                        "Complaint submitted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error submitting complaint: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Load PCs using controller
            try {
                complaintController.loadPCs(pcCombo);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }
    }

    // Helper methods for styled components
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(44, 62, 80));
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserInterface("student@example.com"));
    }
}