import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminInterface extends JFrame {
    private AdminUserController userController;
    private AdminLabController labController;
    private AdminSpecsController specsController;
    private AdminRequestController requestController;
    private AdminComplaintController complaintController;
    
    public AdminInterface() {
        // Initialize controllers
        userController = new AdminUserController();
        labController = new AdminLabController();
        specsController = new AdminSpecsController();
        requestController = new AdminRequestController();
        complaintController = new AdminComplaintController();
        
        setTitle("Admin Dashboard");
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
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard");
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

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttonNames = {
                "User Management",
                "Labs/Computers Management",
                "Specifications Management",
                "HW/SW Requests",
                "Complaints Management"
        };

        for (String name : buttonNames) {
            JButton button = new JButton(name);
            styleMenuButton(button);
            buttonPanel.add(button);
        }

        // Add action listeners to buttons
        ((JButton) buttonPanel.getComponent(0)).addActionListener(e -> new UserLoginManager());
        ((JButton) buttonPanel.getComponent(1)).addActionListener(e -> new LabComManager());
        ((JButton) buttonPanel.getComponent(2)).addActionListener(e -> new SpecsManager());
        ((JButton) buttonPanel.getComponent(3)).addActionListener(e -> new RequestManager());
        ((JButton) buttonPanel.getComponent(4)).addActionListener(e -> new ComplaintsManager());

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(33, 147, 176));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

    // Inner classes for each management module
    class UserLoginManager extends JFrame {
        private List<String[]> users = new ArrayList<>();
        private JTable userTable;
        private JTextField emailField, passwordField, roleField;
        private DefaultTableModel tableModel;

        public UserLoginManager() {
            setTitle("User Management");
            setSize(800, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            loadUsers();

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Table
            String[] columns = { "Email", "Password", "Role" };
            tableModel = new DefaultTableModel(users.toArray(new Object[0][]), columns);
            userTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(userTable);

            // Add selection listener
            userTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        emailField.setText(users.get(selectedRow)[0]);
                        passwordField.setText(users.get(selectedRow)[1]);
                        roleField.setText(users.get(selectedRow)[2]);
                    }
                }
            });

            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            emailField = new JTextField();
            passwordField = new JPasswordField();
            roleField = new JTextField();

            formPanel.add(new JLabel("Email:"));
            formPanel.add(emailField);
            formPanel.add(new JLabel("Password:"));
            formPanel.add(passwordField);
            formPanel.add(new JLabel("Role (admin/user):"));
            formPanel.add(roleField);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton addButton = new JButton("Add User");
            JButton updateButton = new JButton("Update User");
            JButton deleteButton = new JButton("Delete User");

            addButton.addActionListener(e -> addUser());
            updateButton.addActionListener(e -> updateUser());
            deleteButton.addActionListener(e -> deleteUser());

            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(formPanel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }

        private void loadUsers() {
            try {
                users = userController.getAllUsers();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void addUser() {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleField.getText().trim().toLowerCase();

            try {
                userController.addUser(email, password, role);
                users = userController.getAllUsers();
                tableModel.addRow(new Object[] { email, password, role });
                clearFields();
                JOptionPane.showMessageDialog(this, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateUser() {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String oldEmail = users.get(selectedRow)[0];
            String newEmail = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleField.getText().trim().toLowerCase();

            try {
                userController.updateUser(oldEmail, newEmail, password, role);
                users = userController.getAllUsers();
                tableModel.setValueAt(newEmail, selectedRow, 0);
                tableModel.setValueAt(password, selectedRow, 1);
                tableModel.setValueAt(role, selectedRow, 2);
                clearFields();
                JOptionPane.showMessageDialog(this, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteUser() {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String email = users.get(selectedRow)[0];
            
            try {
                userController.deleteUser(email);
                users = userController.getAllUsers();
                tableModel.removeRow(selectedRow);
                clearFields();
                JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearFields() {
            emailField.setText("");
            passwordField.setText("");
            roleField.setText("");
        }
    }

    class LabComManager extends JFrame {
        private List<String[]> labs = new ArrayList<>();
        private List<String[]> computers = new ArrayList<>();
        private JTable labTable, computerTable;
        private JTextField labNameField, labDeptField, labCapacityField, labLocationField;
        private JTextField compIdField, compLabField, compOSField, compRAMField, compCPUField, compDateField;
        private DefaultTableModel labTableModel, computerTableModel;
    
        public LabComManager() {
            setTitle("Lab & Computer Management");
            setSize(1200, 800);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
            loadLabs();
            loadComputers();
    
            JTabbedPane tabbedPane = new JTabbedPane();
    
            // Lab Management Tab
            JPanel labPanel = new JPanel(new BorderLayout());
            labPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            // Lab Table
            String[] labColumns = { "Lab Name", "Department", "Capacity", "Location" };
            labTableModel = new DefaultTableModel(labColumns, 0);
            labTable = new JTable(labTableModel);
            refreshLabTable();
            
            // Add mouse listener for lab table
            labTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = labTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        labNameField.setText(labTable.getValueAt(selectedRow, 0).toString());
                        labDeptField.setText(labTable.getValueAt(selectedRow, 1).toString());
                        labCapacityField.setText(labTable.getValueAt(selectedRow, 2).toString());
                        labLocationField.setText(labTable.getValueAt(selectedRow, 3).toString());
                    }
                }
            });
            
            JScrollPane labScrollPane = new JScrollPane(labTable);
    
            // Lab Form
            JPanel labFormPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            labNameField = new JTextField();
            labDeptField = new JTextField();
            labCapacityField = new JTextField();
            labLocationField = new JTextField();
    
            labFormPanel.add(new JLabel("Lab Name:"));
            labFormPanel.add(labNameField);
            labFormPanel.add(new JLabel("Department:"));
            labFormPanel.add(labDeptField);
            labFormPanel.add(new JLabel("Capacity:"));
            labFormPanel.add(labCapacityField);
            labFormPanel.add(new JLabel("Location:"));
            labFormPanel.add(labLocationField);
    
            // Lab Buttons
            JPanel labButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton addLabButton = new JButton("Add Lab");
            JButton updateLabButton = new JButton("Update Lab");
            JButton deleteLabButton = new JButton("Delete Lab");
            JButton clearLabButton = new JButton("Clear");
    
            addLabButton.addActionListener(e -> addLab());
            updateLabButton.addActionListener(e -> updateLab());
            deleteLabButton.addActionListener(e -> deleteLab());
            clearLabButton.addActionListener(e -> clearLabFields());
    
            labButtonPanel.add(addLabButton);
            labButtonPanel.add(updateLabButton);
            labButtonPanel.add(deleteLabButton);
            labButtonPanel.add(clearLabButton);
    
            labPanel.add(labFormPanel, BorderLayout.NORTH);
            labPanel.add(labScrollPane, BorderLayout.CENTER);
            labPanel.add(labButtonPanel, BorderLayout.SOUTH);
    
            // Computer Management Tab
            JPanel computerPanel = new JPanel(new BorderLayout());
            computerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            // Computer Table
            String[] compColumns = { "Computer ID", "Lab", "OS", "RAM", "CPU", "Install Date" };
            computerTableModel = new DefaultTableModel(compColumns, 0);
            computerTable = new JTable(computerTableModel);
            refreshComputerTable();
            
            // Add mouse listener for computer table
            computerTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = computerTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        compIdField.setText(computerTable.getValueAt(selectedRow, 0).toString());
                        compLabField.setText(computerTable.getValueAt(selectedRow, 1).toString());
                        compOSField.setText(computerTable.getValueAt(selectedRow, 2).toString());
                        compRAMField.setText(computerTable.getValueAt(selectedRow, 3).toString());
                        compCPUField.setText(computerTable.getValueAt(selectedRow, 4).toString());
                        compDateField.setText(computerTable.getValueAt(selectedRow, 5).toString());
                    }
                }
            });
            
            JScrollPane compScrollPane = new JScrollPane(computerTable);
    
            // Computer Form
            JPanel compFormPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            compIdField = new JTextField();
            compLabField = new JTextField();
            compOSField = new JTextField();
            compRAMField = new JTextField();
            compCPUField = new JTextField();
            compDateField = new JTextField();
    
            compFormPanel.add(new JLabel("Computer ID:"));
            compFormPanel.add(compIdField);
            compFormPanel.add(new JLabel("Lab:"));
            compFormPanel.add(compLabField);
            compFormPanel.add(new JLabel("Operating System:"));
            compFormPanel.add(compOSField);
            compFormPanel.add(new JLabel("RAM:"));
            compFormPanel.add(compRAMField);
            compFormPanel.add(new JLabel("CPU:"));
            compFormPanel.add(compCPUField);
            compFormPanel.add(new JLabel("Install Date (YYYY-MM-DD):"));
            compFormPanel.add(compDateField);
    
            // Computer Buttons
            JPanel compButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton addCompButton = new JButton("Add Computer");
            JButton updateCompButton = new JButton("Update Computer");
            JButton deleteCompButton = new JButton("Delete Computer");
            JButton clearCompButton = new JButton("Clear");
    
            addCompButton.addActionListener(e -> addComputer());
            updateCompButton.addActionListener(e -> updateComputer());
            deleteCompButton.addActionListener(e -> deleteComputer());
            clearCompButton.addActionListener(e -> clearComputerFields());
    
            compButtonPanel.add(addCompButton);
            compButtonPanel.add(updateCompButton);
            compButtonPanel.add(deleteCompButton);
            compButtonPanel.add(clearCompButton);
    
            computerPanel.add(compFormPanel, BorderLayout.NORTH);
            computerPanel.add(compScrollPane, BorderLayout.CENTER);
            computerPanel.add(compButtonPanel, BorderLayout.SOUTH);
    
            // Add tabs
            tabbedPane.addTab("Lab Management", labPanel);
            tabbedPane.addTab("Computer Management", computerPanel);
    
            add(tabbedPane);
            setVisible(true);
        }
    
        private void loadLabs() {
            try {
                labs = labController.getAllLabs();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading labs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void loadComputers() {
            try {
                computers = labController.getAllComputers();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading computers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void addLab() {
            String name = labNameField.getText().trim();
            String dept = labDeptField.getText().trim();
            String capacity = labCapacityField.getText().trim();
            String location = labLocationField.getText().trim();
    
            try {
                labController.addLab(name, dept, capacity, location);
                labs = labController.getAllLabs();
                refreshLabTable();
                clearLabFields();
                JOptionPane.showMessageDialog(this, "Lab added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void updateLab() {
            int selectedRow = labTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a lab to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String oldName = labs.get(selectedRow)[0];
            String newName = labNameField.getText().trim();
            String dept = labDeptField.getText().trim();
            String capacity = labCapacityField.getText().trim();
            String location = labLocationField.getText().trim();
    
            try {
                labController.updateLab(oldName, newName, dept, capacity, location);
                labs = labController.getAllLabs();
                computers = labController.getAllComputers();
                refreshLabTable();
                refreshComputerTable();
                clearLabFields();
                JOptionPane.showMessageDialog(this, "Lab updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void deleteLab() {
            int selectedRow = labTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a lab to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String labName = labs.get(selectedRow)[0];
            
            try {
                labController.deleteLab(labName);
                labs = labController.getAllLabs();
                computers = labController.getAllComputers();
                refreshLabTable();
                refreshComputerTable();
                clearLabFields();
                JOptionPane.showMessageDialog(this, "Lab deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void addComputer() {
            String id = compIdField.getText().trim();
            String lab = compLabField.getText().trim();
            String os = compOSField.getText().trim();
            String ram = compRAMField.getText().trim();
            String cpu = compCPUField.getText().trim();
            String date = compDateField.getText().trim();
    
            try {
                labController.addComputer(id, lab, os, ram, cpu, date);
                computers = labController.getAllComputers();
                refreshComputerTable();
                clearComputerFields();
                JOptionPane.showMessageDialog(this, "Computer added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void updateComputer() {
            int selectedRow = computerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a computer to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String oldId = computers.get(selectedRow)[0];
            String newId = compIdField.getText().trim();
            String lab = compLabField.getText().trim();
            String os = compOSField.getText().trim();
            String ram = compRAMField.getText().trim();
            String cpu = compCPUField.getText().trim();
            String date = compDateField.getText().trim();
    
            try {
                labController.updateComputer(oldId, newId, lab, os, ram, cpu, date);
                computers = labController.getAllComputers();
                refreshComputerTable();
                clearComputerFields();
                JOptionPane.showMessageDialog(this, "Computer updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void deleteComputer() {
            int selectedRow = computerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a computer to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String computerId = computers.get(selectedRow)[0];
            
            try {
                labController.deleteComputer(computerId);
                computers = labController.getAllComputers();
                refreshComputerTable();
                clearComputerFields();
                JOptionPane.showMessageDialog(this, "Computer deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        private void refreshLabTable() {
            labTableModel.setRowCount(0);
            for (String[] lab : labs) {
                labTableModel.addRow(lab);
            }
        }
    
        private void refreshComputerTable() {
            computerTableModel.setRowCount(0);
            for (String[] computer : computers) {
                computerTableModel.addRow(computer);
            }
        }
    
        private void clearLabFields() {
            labNameField.setText("");
            labDeptField.setText("");
            labCapacityField.setText("");
            labLocationField.setText("");
        }
    
        private void clearComputerFields() {
            compIdField.setText("");
            compLabField.setText("");
            compOSField.setText("");
            compRAMField.setText("");
            compCPUField.setText("");
            compDateField.setText("");
        }
    }

    class SpecsManager extends JFrame {
        private List<String[]> specs = new ArrayList<>();
        private JTable specsTable;
        private JTextField computerIdField, osField, ramField, storageField, gpuField;
        private DefaultTableModel specsTableModel;

        public SpecsManager() {
            setTitle("Computer Specifications Management");
            setSize(900, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            loadSpecs();

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Table
            String[] columns = { "Computer ID", "Operating System", "RAM", "Storage", "GPU" };
            specsTableModel = new DefaultTableModel(columns, 0);
            specsTable = new JTable(specsTableModel);
            refreshSpecsTable();
            
            specsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(specsTable);

            // Add selection listener to populate fields when row is selected
            specsTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && specsTable.getSelectedRow() != -1) {
                    int selectedRow = specsTable.getSelectedRow();
                    computerIdField.setText((String) specsTable.getValueAt(selectedRow, 0));
                    osField.setText((String) specsTable.getValueAt(selectedRow, 1));
                    ramField.setText((String) specsTable.getValueAt(selectedRow, 2));
                    storageField.setText((String) specsTable.getValueAt(selectedRow, 3));
                    gpuField.setText((String) specsTable.getValueAt(selectedRow, 4));
                }
            });

            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
            computerIdField = new JTextField();
            osField = new JTextField();
            ramField = new JTextField();
            storageField = new JTextField();
            gpuField = new JTextField();

            formPanel.add(new JLabel("Computer ID:"));
            formPanel.add(computerIdField);
            formPanel.add(new JLabel("Operating System:"));
            formPanel.add(osField);
            formPanel.add(new JLabel("RAM:"));
            formPanel.add(ramField);
            formPanel.add(new JLabel("Storage:"));
            formPanel.add(storageField);
            formPanel.add(new JLabel("GPU:"));
            formPanel.add(gpuField);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton addButton = new JButton("Add Specs");
            JButton updateButton = new JButton("Update Specs");
            JButton deleteButton = new JButton("Delete Specs");
            JButton refreshButton = new JButton("Refresh");

            styleSpecsButton(addButton);
            styleSpecsButton(updateButton);
            styleSpecsButton(deleteButton);
            styleSpecsButton(refreshButton);

            addButton.addActionListener(e -> addSpecs());
            updateButton.addActionListener(e -> updateSpecs());
            deleteButton.addActionListener(e -> deleteSpecs());
            refreshButton.addActionListener(e -> refreshSpecs());

            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(formPanel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }

        private void styleSpecsButton(JButton button) {
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }

        private void loadSpecs() {
            try {
                specs = specsController.getAllSpecs();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading specifications: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refreshSpecsTable() {
            specsTableModel.setRowCount(0);
            for (String[] spec : specs) {
                specsTableModel.addRow(spec);
            }
        }

        private void addSpecs() {
            String computerId = computerIdField.getText().trim();
            String os = osField.getText().trim();
            String ram = ramField.getText().trim();
            String storage = storageField.getText().trim();
            String gpu = gpuField.getText().trim();

            try {
                specsController.addSpecs(computerId, os, ram, storage, gpu);
                specs = specsController.getAllSpecs();
                refreshSpecsTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Specifications added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateSpecs() {
            int selectedRow = specsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a specification to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String computerId = computerIdField.getText().trim();
            String os = osField.getText().trim();
            String ram = ramField.getText().trim();
            String storage = storageField.getText().trim();
            String gpu = gpuField.getText().trim();

            try {
                specsController.updateSpecs(computerId, os, ram, storage, gpu);
                specs = specsController.getAllSpecs();
                refreshSpecsTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Specifications updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteSpecs() {
            int selectedRow = specsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a specification to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String computerId = specs.get(selectedRow)[0];
            
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete these specifications?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    specsController.deleteSpecs(computerId);
                    specs = specsController.getAllSpecs();
                    refreshSpecsTable();
                    clearFields();
                    JOptionPane.showMessageDialog(this, "Specifications deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException | IOException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void refreshSpecs() {
            loadSpecs();
            refreshSpecsTable();
            clearFields();
        }

        private void clearFields() {
            computerIdField.setText("");
            osField.setText("");
            ramField.setText("");
            storageField.setText("");
            gpuField.setText("");
        }
    }

    class RequestManager extends JFrame {
        private List<String[]> requests = new ArrayList<>();
        private JTable requestTable;
        private JTextField userField, dateField, typeField, pcField, nameField, descriptionField, commentField;
        private JComboBox<String> statusComboBox;
        private DefaultTableModel requestTableModel;

        public RequestManager() {
            setTitle("HW/SW Request Management");
            setSize(1300, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            loadRequests();

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Table
            String[] columns = { "User Email", "Date", "Type", "PC", "Name", "Description", "Status", "Comment" };
            requestTableModel = new DefaultTableModel(columns, 0);
            requestTable = new JTable(requestTableModel);
            refreshRequestTable();
            
            JScrollPane scrollPane = new JScrollPane(requestTable);

            // Form panel
            JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
            userField = new JTextField();
            dateField = new JTextField();
            typeField = new JTextField();
            pcField = new JTextField();
            nameField = new JTextField();
            descriptionField = new JTextField();
            commentField = new JTextField();

            String[] statusOptions = { "Pending", "Approved", "Denied", "Completed" };
            statusComboBox = new JComboBox<>(statusOptions);

            formPanel.add(new JLabel("User Email:"));
            formPanel.add(userField);
            formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
            formPanel.add(dateField);
            formPanel.add(new JLabel("Type (HW/SW):"));
            formPanel.add(typeField);
            formPanel.add(new JLabel("PC:"));
            formPanel.add(pcField);
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Description:"));
            formPanel.add(descriptionField);
            formPanel.add(new JLabel("Status:"));
            formPanel.add(statusComboBox);
            formPanel.add(new JLabel("Comment:"));
            formPanel.add(commentField);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton updateButton = new JButton("Update Status");
            JButton addCommentButton = new JButton("Add Comment");
            JButton refreshButton = new JButton("Refresh");

            updateButton.addActionListener(e -> updateRequest());
            addCommentButton.addActionListener(e -> addComment());
            refreshButton.addActionListener(e -> refreshRequests());

            buttonPanel.add(updateButton);
            buttonPanel.add(addCommentButton);
            buttonPanel.add(refreshButton);

            // Add table selection listener
            requestTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = requestTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        userField.setText((String) requestTable.getValueAt(selectedRow, 0));
                        dateField.setText((String) requestTable.getValueAt(selectedRow, 1));
                        typeField.setText((String) requestTable.getValueAt(selectedRow, 2));
                        pcField.setText((String) requestTable.getValueAt(selectedRow, 3));
                        nameField.setText((String) requestTable.getValueAt(selectedRow, 4));
                        descriptionField.setText((String) requestTable.getValueAt(selectedRow, 5));
                        statusComboBox.setSelectedItem(requestTable.getValueAt(selectedRow, 6));
                        commentField.setText(requestTable.getValueAt(selectedRow, 7) != null ? 
                                           (String) requestTable.getValueAt(selectedRow, 7) : "");
                    }
                }
            });

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(formPanel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }

        private void loadRequests() {
            try {
                requests = requestController.getAllRequests();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refreshRequestTable() {
            requestTableModel.setRowCount(0);
            for (String[] request : requests) {
                requestTableModel.addRow(request);
            }
        }

        private void updateRequest() {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userEmail = requests.get(selectedRow)[0];
            String date = requests.get(selectedRow)[1];
            String newStatus = (String) statusComboBox.getSelectedItem();

            try {
                requestController.updateRequestStatus(userEmail, date, newStatus);
                requests = requestController.getAllRequests();
                refreshRequestTable();
                JOptionPane.showMessageDialog(this, "Request status updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void addComment() {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to comment on", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String comment = commentField.getText().trim();
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a comment", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userEmail = requests.get(selectedRow)[0];
            String date = requests.get(selectedRow)[1];

            try {
                requestController.addCommentToRequest(userEmail, date, comment);
                requests = requestController.getAllRequests();
                refreshRequestTable();
                JOptionPane.showMessageDialog(this, "Comment added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refreshRequests() {
            loadRequests();
            refreshRequestTable();
            clearFields();
        }

        private void clearFields() {
            userField.setText("");
            dateField.setText("");
            typeField.setText("");
            pcField.setText("");
            nameField.setText("");
            descriptionField.setText("");
            commentField.setText("");
            statusComboBox.setSelectedIndex(0);
        }
    }

    class ComplaintsManager extends JFrame {
        private List<String[]> complaints = new ArrayList<>();
        private JTable complaintsTable;
        private JTextArea complaintArea;
        private JTextArea replyArea;
        private JComboBox<String> statusCombo;

        public ComplaintsManager() {
            setTitle("Complaints Management");
            setSize(900, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            loadComplaints();

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Table
            String[] columns = {"User Email", "Date", "Type", "Complaint", "Status", "Reply"};
            complaintsTable = new JTable(new ComplaintTableModel(complaints, columns));
            complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            complaintsTable.getSelectionModel().addListSelectionListener(e -> showComplaintDetails());

            JScrollPane tableScroll = new JScrollPane(complaintsTable);
            tableScroll.setPreferredSize(new Dimension(600, 400));

            // Details panel
            JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
            detailsPanel.setBorder(BorderFactory.createTitledBorder("Complaint Details"));

            // Complaint display area
            complaintArea = new JTextArea();
            complaintArea.setEditable(false);
            complaintArea.setLineWrap(true);
            complaintArea.setWrapStyleWord(true);
            complaintArea.setBorder(BorderFactory.createTitledBorder("Complaint Text"));

            // Reply area
            replyArea = new JTextArea();
            replyArea.setLineWrap(true);
            replyArea.setWrapStyleWord(true);
            replyArea.setBorder(BorderFactory.createTitledBorder("Admin Reply"));

            // Status panel
            statusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Resolved", "Rejected"});
            statusCombo.setBorder(BorderFactory.createTitledBorder("Status"));

            JButton updateButton = new JButton("Update Complaint");
            updateButton.addActionListener(e -> updateComplaint());
            styleButton(updateButton);

            JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
            statusPanel.add(statusCombo, BorderLayout.CENTER);
            statusPanel.add(updateButton, BorderLayout.SOUTH);

            // Combine components
            JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            textPanel.add(new JScrollPane(complaintArea));
            textPanel.add(new JScrollPane(replyArea));

            detailsPanel.add(textPanel, BorderLayout.CENTER);
            detailsPanel.add(statusPanel, BorderLayout.NORTH);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> refreshComplaints());
            styleButton(refreshButton);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            styleButton(closeButton);

            buttonPanel.add(refreshButton);
            buttonPanel.add(closeButton);

            // Add components
            mainPanel.add(tableScroll, BorderLayout.CENTER);
            mainPanel.add(detailsPanel, BorderLayout.EAST);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }

        private void styleButton(JButton button) {
            button.setBackground(new Color(33, 147, 176));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }

        private void loadComplaints() {
            try {
                complaints = complaintController.getAllComplaints();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading complaints: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void showComplaintDetails() {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < complaints.size()) {
                String[] complaint = complaints.get(selectedRow);
                complaintArea.setText(complaint[3]);
                statusCombo.setSelectedItem(complaint[4]);
                replyArea.setText(complaint.length > 5 ? complaint[5] : "");
            }
        }

        private void updateComplaint() {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a complaint", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] complaint = complaints.get(selectedRow);
            String email = complaint[0];
            String date = complaint[1];
            String status = (String) statusCombo.getSelectedItem();
            String reply = replyArea.getText().trim();

            try {
                complaintController.updateComplaint(email, date, status, reply);
                complaints = complaintController.getAllComplaints();
                ((ComplaintTableModel) complaintsTable.getModel()).fireTableDataChanged();
                JOptionPane.showMessageDialog(this, "Complaint updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refreshComplaints() {
            loadComplaints();
            ((ComplaintTableModel) complaintsTable.getModel()).fireTableDataChanged();
            complaintArea.setText("");
            replyArea.setText("");
            statusCombo.setSelectedIndex(0);
        }

        private class ComplaintTableModel extends AbstractTableModel {
            private final List<String[]> data;
            private final String[] columns;

            public ComplaintTableModel(List<String[]> data, String[] columns) {
                this.data = data;
                this.columns = columns;
            }

            @Override
            public int getRowCount() {
                return data.size();
            }

            @Override
            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                String[] row = data.get(rowIndex);
                switch (columnIndex) {
                    case 0: return row[0]; // Email
                    case 1: return row[1]; // Date
                    case 2: return row[2]; // Type
                    case 3: // Complaint (abbreviated)
                        String complaint = row[3];
                        return complaint.length() > 50 ? complaint.substring(0, 47) + "..." : complaint;
                    case 4: return row[4]; // Status
                    case 5: // Reply (abbreviated)
                        String reply = row.length > 5 ? row[5] : "";
                        return reply.length() > 30 ? reply.substring(0, 27) + "..." : reply;
                    default: return null;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminInterface());
    }
}