import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Ewast extends JFrame {
    String currentUserId = null;
    String currentUserEmail = null;
    DefaultTableModel adminRequestModel; // To directly update the admin request table

    public Ewast() {
        setTitle("E-Waste Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLoginPage();
    }

    void showLoginPage() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton regBtn = new JButton("Register");

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginBtn);
        panel.add(regBtn);

        add(panel);
        revalidate();
        repaint();

        loginBtn.addActionListener(e -> {
            String enteredEmail = emailField.getText().trim();
            String enteredPassword = new String(passwordField.getPassword()).trim();

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 6 &&
                            data[1].trim().equals(enteredEmail) &&
                            data[4].trim().equals(enteredPassword)) {
                        currentUserId = data[0];
                        currentUserEmail = data[1];
                        if (data[1].equals("admin") && data[4].equals("admin"))
                            adminDashboard();
                        else
                            userDashboard();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        regBtn.addActionListener(e -> showRegistrationPage());
    }

    void showRegistrationPage() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridLayout(7, 2));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(registerBtn);
        panel.add(backBtn);

        add(panel);
        revalidate();
        repaint();

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty.");
                return;
            }
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number cannot be empty.");
                return;
            }
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address cannot be empty.");
                return;
            }
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
                String id = "U" + (new Random().nextInt(9000) + 1000); // e.g., U4513
                bw.write(id + "," + email + "," + name + "," +
                        phone + "," + password + "," +
                        address + "\n");
                JOptionPane.showMessageDialog(this, "Registered successfully");
                showLoginPage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e -> showLoginPage());
    }

    void loadUsersToTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear old data
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 6) {
                    model.addRow(data);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void loadUserRequests(DefaultTableModel model) {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 7 && data[1].equals(currentUserId)) {
                    model.addRow(new String[]{data[0], data[2], data[3], data[4], data[5], data[6]});
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void userDashboard() {
        getContentPane().removeAll();
        JTabbedPane tabs = new JTabbedPane();

        // Submit Request Tab
        JPanel submitPanel = new JPanel(new GridLayout(7, 2));
        String[] categories = {"Laptop", "Mobile", "TV", "Refrigerator", "Washing Machine"};
        JComboBox<String> category = new JComboBox<>(categories);
        JTextField descriptionField = new JTextField();
        JTextField pickupAddressField = new JTextField();
        JTextField dateField = new JTextField("");
        JButton submitBtn = new JButton("Submit Request");
        JButton clearBtn = new JButton("Clear");

        submitPanel.add(new JLabel("Category:"));
        submitPanel.add(category);
        submitPanel.add(new JLabel("Description:"));
        submitPanel.add(descriptionField);
        submitPanel.add(new JLabel("Pickup Address:"));
        submitPanel.add(pickupAddressField);
        submitPanel.add(new JLabel("Date of Request:"));
        submitPanel.add(dateField);
        submitPanel.add(submitBtn);
        submitPanel.add(clearBtn);

        // Report Tab
        JPanel reportPanel = new JPanel(new BorderLayout());
        String[] cols = {"Request ID", "Category", "Description", "Address", "Date", "Status"};
        DefaultTableModel reportModel = new DefaultTableModel(cols, 0);
        JTable reportTable = new JTable(reportModel);
        reportPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        loadUserRequests(reportModel);

        submitBtn.addActionListener(e -> {
            String selectedCategory = (String) category.getSelectedItem();
            String desc = descriptionField.getText().trim();
            String address = pickupAddressField.getText().trim();
            String date = dateField.getText().trim();

            if (desc.isEmpty() || address.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the request details.");
                return;
            }

            try (BufferedWriter fw = new BufferedWriter(new FileWriter("requests.txt", true))) {
                String reqId = "R" + (new Random().nextInt(9000) + 1000);
                fw.write(reqId + "," + currentUserId + "," + selectedCategory + "," +
                        desc + "," + address + "," +
                        date + ",Pending\n");
                JOptionPane.showMessageDialog(this, "Request Submitted");
                descriptionField.setText("");
                pickupAddressField.setText("");
                dateField.setText("");

                // Add the new request directly to the table model
                reportModel.addRow(new Object[]{reqId, selectedCategory, desc, address, date, "Pending"});
                reportTable.revalidate();
                reportTable.repaint();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        clearBtn.addActionListener(e -> {
            category.setSelectedIndex(0);
            descriptionField.setText("");
            pickupAddressField.setText("");
            dateField.setText("");
        });

        // Home Tab
        JPanel homePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to the E-Waste Management System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        homePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Profile Tab
        JPanel profilePanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JLabel emailLabel = new JLabel();
        JButton updateProfileBtn = new JButton("Update Profile");

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailLabel);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        profilePanel.add(formPanel, BorderLayout.CENTER);
        profilePanel.add(updateProfileBtn, BorderLayout.SOUTH);

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6 && data[0].equals(currentUserId)) {
                    emailLabel.setText(data[1]);
                    nameField.setText(data[2]);
                    phoneField.setText(data[3]);
                    addressField.setText(data[5]);
                    passwordField.setText(data[4]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        updateProfileBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number cannot be empty.");
                return;
            }
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address cannot be empty.");
                return;
            }
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                return;
            }

            List<String> updatedLines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(currentUserId)) {
                        data[2] = name;
                        data[3] = phone;
                        data[4] = password;
                        data[5] = address;
                        line = String.join(",", data);
                    }
                    updatedLines.add(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
                for (String l : updatedLines)
                    bw.write(l + "\n");
                JOptionPane.showMessageDialog(this, "Profile Updated Successfully");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Logout Tab
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> showLoginPage());
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(logoutBtn);

        // Add tabs
        tabs.add("Home", homePanel);
        tabs.add("Submit Request", submitPanel);
        tabs.add("Report", reportPanel);
        tabs.add("Profile", profilePanel);
        tabs.add("Logout", logoutPanel);

        add(tabs);
        revalidate();
        repaint();
    }

    void adminDashboard() {
        getContentPane().removeAll();
        JTabbedPane adminTabs = new JTabbedPane();

        // --------- Manage Requests Tab ---------
        JPanel requestPanel = new JPanel(new BorderLayout());

        String[] requestCols = {"Request ID", "User ID", "Category", "Description", "Address", "Date", "Status"};
        adminRequestModel = new DefaultTableModel(requestCols, 0);
        JTable requestTable = new JTable(adminRequestModel);

        try (BufferedReader br = new BufferedReader(new FileReader("requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    adminRequestModel.addRow(data);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JPanel requestBtnPanel = new JPanel();
        String[] statusOptions = {"Pending", "Picked Up", "Complete"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        JButton updateStatusBtn = new JButton("Update Status");

        updateStatusBtn.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                String reqId = (String) adminRequestModel.getValueAt(selectedRow, 0);
                String newStatus = (String) statusCombo.getSelectedItem();
                List<String> lines = new ArrayList<>();
                boolean updated = false;
                try (BufferedReader br = new BufferedReader(new FileReader("requests.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(reqId + ",")) {
                            String[] data = line.split(",");
                            if (data.length >= 7) {
                                data[6] = newStatus;
                                lines.add(String.join(",", data));
                                updated = true;
                            } else {
                                lines.add(line); // Keep the line as is if format is wrong
                            }
                        } else {
                            lines.add(line);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error reading requests file.");
                    return;
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter("requests.txt"))) {
                    for (String l : lines)
                        bw.write(l + "\n");
                    JOptionPane.showMessageDialog(this, "Status Updated to " + newStatus);
                    if (updated) {
                        adminRequestModel.setValueAt(newStatus, selectedRow, 6); // Update the table directly
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error writing to requests file.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a request to update.");
            }
        });

        requestBtnPanel.add(new JLabel("Set Status:"));
        requestBtnPanel.add(statusCombo);
        requestBtnPanel.add(updateStatusBtn);
        requestPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        requestPanel.add(requestBtnPanel, BorderLayout.SOUTH);

        // --------- Manage Users Tab ---------
        JPanel userPanel = new JPanel(new BorderLayout());

        String[] userCols = {"User ID", "Email", "Name", "Phone", "Password", "Address"};
        DefaultTableModel userModel = new DefaultTableModel(userCols, 0);
        JTable userTable = new JTable(userModel);

        // Load users into table
        loadUsersToTable(userModel);

        JButton deleteUserBtn = new JButton("Delete Selected User");

        deleteUserBtn.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.");
                return;
            }

            String userIdToDelete = userModel.getValueAt(selectedRow, 0).toString().trim();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this user and their requests?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            // Step 1: Remove user from users.txt
            List<String> updatedUsers = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(userIdToDelete + ",")) {
                        updatedUsers.add(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error reading users file.");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", false))) {
                for (String l : updatedUsers) {
                    bw.write(l);
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error writing to users file.");
                return;
            }

            // Step 2: Remove requests from requests.txt for that user
            List<String> updatedRequests = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("requests.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 2 && !data[1].equals(userIdToDelete)) {
                        updatedRequests.add(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("requests.txt", false))) {
                for (String l : updatedRequests) {
                    bw.write(l);
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "User and their requests deleted successfully.");
            // Refresh user table
            loadUsersToTable(userModel);
            // Refresh request table
            adminRequestModel.setRowCount(0);
            try (BufferedReader br = new BufferedReader(new FileReader("requests.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 7) {
                        adminRequestModel.addRow(data);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        userPanel.add(deleteUserBtn, BorderLayout.SOUTH);

        // --------- Add Tabs to Admin Panel ---------
        adminTabs.add("Manage Requests", requestPanel);
        adminTabs.add("Manage Users", userPanel);

        // --------- Add Logout Button ---------
        JPanel logoutPanel = new JPanel();
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> showLoginPage());
        logoutPanel.add(logoutBtn);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(adminTabs, BorderLayout.CENTER);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);

        add(mainPanel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ewast().setVisible(true));
    }
}