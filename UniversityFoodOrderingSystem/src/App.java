import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeComboBox;
    private User loggedUser;

    public App() {
        setTitle("Tech University Food Ordering");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JLabel titleLabel = new JLabel("Food Ordering System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userTypeLabel = new JLabel("User Type:");
        usernameField = new JTextField();
        setMinWidth(usernameField, 200);
        addPlaceholder(usernameField, "Enter your username");

        passwordField = new JPasswordField();
        setMinWidth(passwordField, 200);
        addPlaceholder(passwordField, "Enter your password");

        userTypeComboBox = new JComboBox<>(new String[] { "admin", "vendor", "customer", "driver" });

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 5);
        add(usernameLabel, gbc);

        gbc.gridx++;
        gbc.insets = new Insets(5, 5, 5, 20);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 5);
        add(passwordLabel, gbc);

        gbc.gridx++;
        gbc.insets = new Insets(5, 5, 5, 20);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 5);
        add(userTypeLabel, gbc);

        gbc.gridx++;
        gbc.insets = new Insets(5, 5, 5, 20);
        add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        add(loginButton, gbc);

        // Set background color and line border for text fields
        Color bgColor = new Color(240, 240, 240);
        usernameField.setBackground(bgColor);
        usernameField.setBorder(new LineBorder(Color.GRAY));

        passwordField.setBackground(bgColor);
        passwordField.setBorder(new LineBorder(Color.GRAY));
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void login() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        String userType = (String) userTypeComboBox.getSelectedItem();

        // Check credentials
        loggedUser = authenticateUser(username, password, userType);
        if (loggedUser != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");

            // Use switch statement for userType
            switch (userType) {
                case "admin":
                    AdminPage();
                    break;
                case "vendor":
                    VendorPage();
                    break;
                case "customer":
                    CustomerPage();
                    break;
                case "driver":
                    RunnerPage();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Wrong input for userType!");
                    break;
            }

            // Add code to open the main application window or perform other actions
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username, password, or user type. Please try again.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }

        // Clear password field after login attempt
        passwordField.setText("");
    }

    private User authenticateUser(String username, String password, String userType) {
        // Check credentials against stored data
        try (BufferedReader reader = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\users.txt"))) {
            String line;
            // Skip the first line (header)
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                System.out.println("@@@@====> " + line);
                if (parts.length == 5 && parts[0].equals(username) && parts[2].equals(password)
                        && parts[3].equals(userType)) {
                    // Create a User object with the matching data and return it
                    return new User(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return null if no match is found
        return null;
    }

    // Function to set the minimum width of a JTextField
    private static void setMinWidth(JTextField textField, int minWidth) {
        FontMetrics fontMetrics = textField.getFontMetrics(textField.getFont());
        int charWidth = fontMetrics.charWidth('W');

        int columns = Math.max(minWidth / charWidth, 1);
        textField.setColumns(columns);
    }

    private void AdminPage() {
        // Create and show the UserManagementGUI
        AdminScreen adminScreen = new AdminScreen(loggedUser);
        adminScreen.setSize(970, 650);
        adminScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminScreen.setLocationRelativeTo(null);
        adminScreen.setVisible(true);
        // Hide the current frame
        this.setVisible(false);
    }

    private void VendorPage() {
        // Create and show the UserManagementGUI
        VendorScreen vendorScreen = new VendorScreen(loggedUser);
        vendorScreen.setSize(970, 650);
        vendorScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vendorScreen.setLocationRelativeTo(null);
        vendorScreen.setVisible(true);
        // Hide the current frame
        this.setVisible(false);
    }

    private void CustomerPage() {
        // Create and show the UserManagementGUI
        CustomerScreen customerScreen = new CustomerScreen(loggedUser);
        customerScreen.setSize(980, 650);
        customerScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        customerScreen.setLocationRelativeTo(null);
        customerScreen.setVisible(true);
        // Hide the current frame
        this.setVisible(false);
    }

    private void RunnerPage() {
        // Create and show the UserManagementGUI
        RunnerScreen runnerScreen = new RunnerScreen(loggedUser);
        runnerScreen.setSize(1080, 650);
        runnerScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        runnerScreen.setLocationRelativeTo(null);
        runnerScreen.setVisible(true);
        // Hide the current frame
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App().setVisible(true);
            }
        });
    }
}
