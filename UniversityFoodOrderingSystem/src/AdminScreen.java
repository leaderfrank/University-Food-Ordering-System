import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminScreen extends JFrame {
    // -- Text Areas
    private JTextArea receiptDetailsTextArea;
    // -- Panels
    private JPanel registrationPanel, topUpPanel, transReceiptPanel;
    // -- Buttons
    private JButton notifyButton, registrationButton, topUpButton, transReceiptButton, findButton, createButton,
            updateButton,
            deleteButton, searchButton,
            topUpCreditButton;
    // -- Labels
    private JLabel lblWelcome, searchLabel, userDetailsLabel, topUpLabel, usernameLabel, nameLabel, typeLabel,
            passwordLabel, filterLabel;
    // -- Combo Boxes
    private JComboBox<String> typeComboBox, filterComboBox;
    // -- Text Fields
    private JTextField usernameField, searchUsernameField, userDetailsField, topUpAmountField, nameField, passwordField,
            searchField;
    // -- Tables
    private JTable userTable, transactionTable;
    // -- Table Models
    private DefaultTableModel tableModel, transactionTableModel;
    // -----------
    private ArrayList<User> users;
    private User adminUser;
    private boolean isRegistrationPanelVisible = true;
    private boolean isTopUpPanelVisible = false;
    private boolean isTransactionReceiptVisible = false;

    public AdminScreen(User loggedUser) {
        super("Admin Management System");
        adminUser = loggedUser;
        users = readUsersFromFile();
        setLayout(null);
        // - Construct components
        // -- Panels
        registrationPanel = createRegistrationPanel();
        topUpPanel = createTopUpPanel();
        transReceiptPanel = createTransactionPanel();
        // -- Buttons
        registrationButton = new JButton("Registration");
        topUpButton = new JButton("Top Up Credits");
        transReceiptButton = new JButton("Transaction Receipt");
        // -- Labels
        lblWelcome = new JLabel("Welcome");
        // --------------------------------------------
        // Add components
        add(registrationButton);
        add(topUpButton);
        add(transReceiptButton);
        add(lblWelcome);
        add(registrationPanel);
        add(topUpPanel);
        add(transReceiptPanel);
        // Update welcome label text if loggedUser is not null
        if (adminUser != null) {
            lblWelcome.setText("Welcome " + adminUser.getUsername() + " - " + adminUser.getType());
        }
        // Set component bounds
        registrationButton.setBounds(25, 35, 150, 30);
        topUpButton.setBounds(25, 75, 150, 30);
        transReceiptButton.setBounds(25, 115, 150, 30);
        lblWelcome.setBounds(345, 10, 300, 25);
        // Add action listeners to buttons
        registrationButton.addActionListener(e -> showRegistrationPanel());
        topUpButton.addActionListener(e -> showTopUpPanel());
        transReceiptButton.addActionListener(e -> showTransReceiptPanel());
        // Initialize tables & registeration panel
        showRegistrationPanel();
        updateTable();
        updateTransactionTable();
    }

    private void showRegistrationPanel() {
        registrationPanel.setVisible(true);
        topUpPanel.setVisible(false);
        transReceiptPanel.setVisible(false);
        isRegistrationPanelVisible = true;
        isTopUpPanelVisible = false;
        isTransactionReceiptVisible = false;
    }

    private void showTopUpPanel() {
        registrationPanel.setVisible(false);
        topUpPanel.setVisible(true);
        transReceiptPanel.setVisible(false);
        isRegistrationPanelVisible = false;
        isTopUpPanelVisible = true;
        isTransactionReceiptVisible = false;
    }

    private void showTransReceiptPanel() {
        registrationPanel.setVisible(false);
        topUpPanel.setVisible(false);
        transReceiptPanel.setVisible(true);
        isRegistrationPanelVisible = false;
        isTopUpPanelVisible = false;
        isTransactionReceiptVisible = true;
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Registeration"));
        panel.setLayout(null);
        // - Construct components
        // -- Buttons
        createButton = new JButton("Create");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        findButton = new JButton("Find");
        // -- Labels
        filterLabel = new JLabel("Filter By");
        typeLabel = new JLabel("Type");
        passwordLabel = new JLabel("Password");
        usernameLabel = new JLabel("Username");
        usernameField = new JTextField(15);
        nameLabel = new JLabel("Name");
        // -- ComboBoxes
        String[] typeComboBoxItems = { "admin", "vendor", "customer", "driver" };
        String[] filterComboBoxItems = { "all", "admin", "vendor", "customer", "driver" };
        typeComboBox = new JComboBox<>(typeComboBoxItems);
        filterComboBox = new JComboBox<>(filterComboBoxItems);
        // -- Text Fields
        usernameField = new JTextField(15);
        nameField = new JTextField(15);
        passwordField = new JTextField(15);
        searchField = new JTextField(15);
        // -- Tables
        tableModel = new DefaultTableModel();
        userTable = new JTable(tableModel);
        tableModel.addColumn("Username");
        tableModel.addColumn("Name");
        tableModel.addColumn("Password");
        tableModel.addColumn("Type");
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        // --- Select by row on the table
        userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Retrieve data from the selected row and display it in the text fields
                        usernameField.setText(userTable.getValueAt(selectedRow, 0).toString());
                        nameField.setText(userTable.getValueAt(selectedRow, 1).toString());
                        passwordField.setText(userTable.getValueAt(selectedRow, 2).toString());
                        // Set the typeComboBox to the selected user's type
                        typeComboBox.setSelectedItem(userTable.getValueAt(selectedRow, 3).toString());
                    }
                }
            }
        });
        // --------------------------------------------
        // Add components
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeComboBox);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(createButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(filterLabel);
        panel.add(filterComboBox);
        panel.add(searchField);
        panel.add(findButton);
        panel.add(tableScrollPane);
        // Set component bounds
        usernameLabel.setBounds(30, 20, 100, 25);
        usernameField.setBounds(95, 20, 150, 25);
        nameLabel.setBounds(260, 20, 100, 25);
        nameField.setBounds(300, 20, 200, 25);
        typeLabel.setBounds(510, 20, 100, 25);
        typeComboBox.setBounds(545, 20, 160, 25);
        passwordLabel.setBounds(30, 60, 100, 25);
        passwordField.setBounds(95, 60, 150, 25);
        createButton.setBounds(260, 60, 80, 25);
        updateButton.setBounds(345, 60, 80, 25);
        deleteButton.setBounds(625, 60, 80, 25);
        filterLabel.setBounds(30, 120, 100, 25);
        filterComboBox.setBounds(95, 120, 100, 25);
        searchField.setBounds(205, 120, 200, 25);
        findButton.setBounds(410, 120, 100, 25);
        tableScrollPane.setBounds(30, 150, 675, 400);
        panel.setBounds(195, 35, 745, 560);

        // Set ActionListeners
        createButton.addActionListener(e -> createUser());
        findButton.addActionListener(e -> readUsers());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        return panel;
    }

    private JPanel createTopUpPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Top Up Credit"));
        panel.setLayout(null);
        // - Construct components
        // -- Buttons
        searchButton = new JButton("Search");
        topUpCreditButton = new JButton("Top Up");
        // -- Labels
        searchLabel = new JLabel("Search Username");
        userDetailsLabel = new JLabel("User Details");
        topUpLabel = new JLabel("Top Up Amount");
        // -- Text Fields
        searchUsernameField = new JTextField(15);
        topUpAmountField = new JTextField(15);
        userDetailsField = new JTextField();
        userDetailsField.setFont(userDetailsField.getFont().deriveFont(Font.BOLD, 14f));
        userDetailsField.setEditable(false);
        // Add components to the panel
        panel.add(searchLabel);
        panel.add(searchUsernameField);
        panel.add(searchButton);
        panel.add(userDetailsLabel);
        panel.add(userDetailsField);
        panel.add(topUpLabel);
        panel.add(topUpAmountField);
        panel.add(topUpCreditButton);

        // Set component bounds
        searchLabel.setBounds(30, 20, 150, 25);
        searchUsernameField.setBounds(180, 20, 150, 25);
        searchButton.setBounds(340, 20, 80, 25);
        userDetailsLabel.setBounds(30, 60, 150, 25);
        userDetailsField.setBounds(180, 60, 500, 30);
        topUpLabel.setBounds(30, 100, 150, 25);
        topUpAmountField.setBounds(180, 100, 150, 25);
        topUpCreditButton.setBounds(340, 100, 80, 25);
        panel.setBounds(195, 35, 745, 560);

        // Add action listener to the search button
        searchButton.addActionListener(e -> searchCustomer());

        // Add action listener to the top-up button
        topUpCreditButton.addActionListener(e -> topUpCredits());

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Transaction Receipts"));
        panel.setLayout(null);

        // Construct components
        notifyButton = new JButton("Notify Customer");
        receiptDetailsTextArea = new JTextArea();
        transactionTable = new JTable();
        transactionTableModel = new DefaultTableModel();
        JScrollPane receiptScrollPane = new JScrollPane(receiptDetailsTextArea);
        JScrollPane receiptTableScrollPane = new JScrollPane(transactionTable);
        transactionTable.setModel(transactionTableModel);
        transactionTableModel.addColumn("Username");
        transactionTableModel.addColumn("Name");
        transactionTableModel.addColumn("Amount");
        transactionTableModel.addColumn("Credit");
        transactionTableModel.addColumn("Timestamp");
        // --- Select by row on the table
        transactionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = transactionTable.getSelectedRow();
                    if (selectedRow != -1) {
                        receiptDetailsTextArea.setText(
                                "GENERATED RECEIPT\n\nTransaction Receipt for Customer ID : "
                                        + transactionTable.getValueAt(selectedRow, 0)
                                        + "\nDear " + transactionTable.getValueAt(selectedRow, 1)
                                        + ",\nThis is a generated receipt for top up your credit with amount of "
                                        + transactionTable.getValueAt(selectedRow, 2) + " at "
                                        + transactionTable.getValueAt(selectedRow, 4) + "\nYour new credit is "
                                        + transactionTable.getValueAt(selectedRow, 3));
                    }
                }
            }
        });
        // Add components to the panel
        panel.add(notifyButton);
        panel.add(receiptScrollPane);
        panel.add(receiptTableScrollPane);

        // Set component bounds
        notifyButton.setBounds(30, 20, 150, 25);
        receiptScrollPane.setBounds(30, 60, 675, 150);
        receiptTableScrollPane.setBounds(30, 220, 675, 330);
        panel.setBounds(195, 35, 745, 560);

        // Set ActionListeners
        updateTransactionTable();
        notifyButton.addActionListener(e -> notifyCustomer());

        return panel;
    }

    private void updateTransactionTable() {
        // Clear the existing table data
        transactionTableModel.setRowCount(0);

        // Read receipt data from the file and populate the table
        ArrayList<TransactionReceipt> receipts = readReceiptsFromFile();
        for (TransactionReceipt receipt : receipts) {
            transactionTableModel.addRow(new Object[] {
                    receipt.getUsername(),
                    receipt.getName(),
                    receipt.getAmount(),
                    receipt.getCredit(),
                    receipt.getTimestamp()
            });
        }
    }

    private ArrayList<TransactionReceipt> readReceiptsFromFile() {
        ArrayList<TransactionReceipt> receiptList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\receipt.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String username = data[0].trim();
                String name = data[1].trim();
                double amount = Double.parseDouble(data[2].trim());
                double credit = Double.parseDouble(data[3].trim());
                String timestamp = data[4].trim();
                TransactionReceipt receipt = new TransactionReceipt(username, name, amount, credit, timestamp);
                receiptList.add(receipt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiptList;
    }

    private void notifyCustomer() {
        int selectedRow = transactionTable.getSelectedRow();
        // Check if a row is selected
        if (selectedRow != -1) {
            // Get data from the selected row
            String customerId = transactionTable.getValueAt(selectedRow, 0).toString();
            String customerName = transactionTable.getValueAt(selectedRow, 1).toString();
            String topUpAmount = transactionTable.getValueAt(selectedRow, 2).toString();
            String credit = transactionTable.getValueAt(selectedRow, 3).toString();
            String timestamp = transactionTable.getValueAt(selectedRow, 4).toString();
            // Create a Notification object
            Notification notification = new Notification("Top Up", adminUser.getUsername(), customerId, "success",
                    timestamp,
                    "GENERATED RECEIPT\n\nTransaction Receipt for Customer ID : "
                            + customerId
                            + "\nDear " + customerName
                            + ",\nThis is a generated receipt for top up your credit with amount of "
                            + topUpAmount + " at "
                            + timestamp + "\nYour new credit is "
                            + credit);
            // Append the notification to notification.txt
            appendToNotificationFile(notification);
            // Show pop-up message
            JOptionPane.showMessageDialog(null, "Customer notified successfully!");
        }
    }

    private void appendToNotificationFile(Notification notification) {
        Path filePath = Path.of("C:\\UniversityFoodOrderingSystem\\src\\db\\notification.txt");
        // Create a list with the notification string
        List<String> lines = Arrays.asList(notification.toString());
        // Write the lines to the file (appending)
        try {
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchCustomer() {
        String searchUsername = searchUsernameField.getText();
        User customer = findUser(searchUsername);

        if (customer != null && customer.getType().equalsIgnoreCase("customer")) {
            userDetailsField.setText("Username: " + customer.getUsername() + " | Name: " + customer.getName() +
                    " | Credit: " + customer.getCredit());
        } else {
            userDetailsField.setText("Customer not found");
        }
    }

    private void topUpCredits() {
        String searchUsername = searchUsernameField.getText();
        User customer = findUser(searchUsername);

        if (customer != null && customer.getType().equalsIgnoreCase("customer")) {
            try {
                double topUpAmount = Double.parseDouble(topUpAmountField.getText());
                customer.setCredit(customer.getCredit() + topUpAmount);

                // Update the user details in the file
                writeUsersToFile();

                // Generate a receipt in receipt.txt file
                generateReceipt(customer, topUpAmount);

                clearFields();

                JOptionPane.showMessageDialog(this, "Top-up successful. Receipt generated.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid top-up amount. Please enter a valid number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found");
        }
    }

    private void generateReceipt(User customer, double topUpAmount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                "C:\\UniversityFoodOrderingSystem\\src\\db\\receipt.txt",
                true))) {
            bw.write(customer.getUsername() + "," + customer.getName() + "," + topUpAmount + "," +
                    customer.getCredit() + "," + java.time.LocalDateTime.now() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUser() {
        String username = usernameField.getText();
        String name = nameField.getText();
        String password = passwordField.getText();

        // Get the selected item from the typeComboBox
        String type = typeComboBox.getSelectedItem().toString();

        // Check if the username already exists
        if (findUser(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.");
            return;
        }

        try {
            User newUser = new User(username, name, password, type, 0.0);
            users.add(newUser);

            writeUsersToFile();

            clearFields();
            JOptionPane.showMessageDialog(this, "User created successfully.");

            // Update the table with the new data
            updateTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid credit value. Please enter a valid number.");
        }
    }

    private void readUsers() {
        // Clear the existing table data
        tableModel.setRowCount(0);

        String filter = filterComboBox.getSelectedItem().toString().toLowerCase();
        String searchKeyword = searchField.getText().toLowerCase();

        for (User user : users) {
            boolean matchesFilter = filter.equals("all") || user.getType().toLowerCase().equals(filter);
            boolean matchesSearch = user.getUsername().toLowerCase().contains(searchKeyword)
                    || user.getName().toLowerCase().contains(searchKeyword);

            if (matchesFilter && matchesSearch) {
                // Add each user to the table
                tableModel.addRow(new Object[] { user.getUsername(), user.getName(), user.getPassword(), user.getType(),
                        user.getCredit() });
            }
        }
    }

    private void updateTable() {
        // Clear the existing table data
        tableModel.setRowCount(0);

        // Populate the table with the current user data
        System.out.println(users);
        for (User user : users) {
            tableModel
                    .addRow(new Object[] { user.getUsername(), user.getName(), user.getPassword(), user.getType(),
                            user.getCredit() });
        }
    }

    private void updateUser() {
        String username = usernameField.getText();
        User userToUpdate = findUser(username);

        if (userToUpdate != null) {
            userToUpdate.setName(nameField.getText());
            userToUpdate.setPassword(passwordField.getText());
            userToUpdate.setType(typeComboBox.getSelectedItem().toString());
            userToUpdate.setCredit(userToUpdate.getCredit());

            writeUsersToFile();

            clearFields();
            JOptionPane.showMessageDialog(this, "User updated successfully.");
            // Update the table with the new data
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "User not found");
        }
    }

    private void deleteUser() {
        String username = usernameField.getText();
        User userToDelete = findUser(username);

        if (userToDelete != null) {
            users.remove(userToDelete);
            writeUsersToFile();

            clearFields();
            JOptionPane.showMessageDialog(this, "User deleted successfully.");

            // Update the table with the new data
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "User not found.");
        }
    }

    private User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void clearFields() {
        usernameField.setText("");
        nameField.setText("");
        passwordField.setText("");
        typeComboBox.setSelectedIndex(0);
        searchUsernameField.setText("");
        userDetailsField.setText("");
        topUpAmountField.setText("");
    }

    private ArrayList<User> readUsersFromFile() {
        ArrayList<User> userList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\users.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String username = data[0];
                String name = data[1];
                String password = data[2];
                String type = data[3];
                double credit = Double.parseDouble(data[4]);
                User user = new User(username, name, password, type, credit);
                userList.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private void writeUsersToFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\users.txt"))) {
            bw.write("username,name,password,type,credit\n");
            for (User user : users) {
                bw.write(user.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}