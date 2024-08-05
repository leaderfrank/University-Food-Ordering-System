import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RunnerScreen extends JFrame {
    // -- Panels
    private JPanel taskPanel, tasksHistoryPanel, revenuPanel;
    // -- Buttons
    private JButton taskButton, taskHistoryButton, revenuButton, acceptButton, declineButton, deliveredButton,
            refreshRevenueButton;
    // -- Labels
    private JLabel lblWelcome, currentTasksLabel, orderLabel, usernameLabel, vendorLabel, itemsLabel, costLabel,
            deliveryLabel, lblTotalEarnings;
    // -- Fields
    private JTextField orderField, usernameField, vendorField, itemsField, costField;
    // -- Panel View Controllers
    private boolean isTaskPanelVisible = true;
    private boolean isTasksHistoryPanelVisible = false;
    private boolean isRevenuPanelVisible = false;
    // -- Text Area
    private JTextArea receiptDetailsTextArea;
    // -- Logged Runner Data
    private User runnerUser;
    // -- Tables
    private JTable currentTasksTable, tasksHistoryTable, revenueTable;
    // -- Table Models
    private DefaultTableModel currentTasksTableModel, tasksHistoryTableModel, revenueTableModel;
    // -- Orders
    private ArrayList<Order> orders;
    // -- Tasks
    private ArrayList<Task> tasks;
    private int availableTasksIndexer = 0;
    private Runner runner;

    public RunnerScreen(User loggedUser) {
        // Initialize runner
        super("Runner - Food Ordering");
        runnerUser = loggedUser;
        runner = new Runner();
        setLayout(null);
        // -- panels
        taskPanel = createTaskPanel();
        tasksHistoryPanel = createTasksHistoryPanel();
        revenuPanel = createRevenuePanel();
        // -- buttons
        taskButton = new JButton("Tasks");
        taskHistoryButton = new JButton("Tasks History");
        revenuButton = new JButton("Revenue");
        // -- labels
        lblWelcome = new JLabel("Welcome");
        // add components
        add(taskButton);
        add(taskHistoryButton);
        add(revenuButton);
        add(taskPanel);
        add(tasksHistoryPanel);
        add(revenuPanel);
        add(lblWelcome);
        lblWelcome.setBounds(345, 10, 300, 25);
        taskButton.setBounds(30, 50, 150, 30);
        taskHistoryButton.setBounds(30, 100, 150, 30);
        revenuButton.setBounds(30, 150, 150, 30);
        // Update welcome label text if loggedUser is not null
        if (runnerUser != null) {
            lblWelcome.setText("Welcome " + runnerUser.getUsername() + " - " +
                    runnerUser.getType());
        }
        // Add action listeners to buttons
        taskButton.addActionListener(e -> showTaskPanel());
        taskHistoryButton.addActionListener(e -> showTaskHistoryPanel());
        revenuButton.addActionListener(e -> showRevenuePanel());

        // Initialize tables & registeration panel
        tasks = loadAvailableTasks();
        orders = readOrdersFromFile();
        showTaskPanel();
        updateTasksTable();
        updateTaskHistoryTable();
        updateRevenueTable();
    }

    private void updateRevenueTable() {
        revenueTableModel.setRowCount(0);
        tasks = loadAvailableTasks();
        System.out.println("###=> Tasks: " + tasks);
        for (Task task : tasks) {
            if (task != null && task.getRunnerId().equalsIgnoreCase(runnerUser
                    .getUsername()) && task.getTaskStatus() != TaskStatus.PENDING) {
                revenueTableModel.addRow(new Object[] {
                        task.getOrderId(),
                        task.getCustomerId(),
                        task.getVendorId(),
                        task.getEarning(),
                });
            }
        }
    }

    private void updateTaskHistoryTable() {
        // Clear the existing table data
        tasksHistoryTableModel.setRowCount(0);
        tasks = loadAvailableTasks();
        System.out.println("###=> Tasks: " + tasks);
        for (Task task : tasks) {
            if (task != null && task.getRunnerId().equalsIgnoreCase(runnerUser
                    .getUsername()) && task.getTaskStatus() != TaskStatus.PENDING) {
                tasksHistoryTableModel.addRow(new Object[] {
                        task.getOrderId(),
                        task.getCustomerId(),
                        task.getVendorId(),
                        task.getTaskStatus(),
                });
            }
        }
    }

    private void updateTasksTable() {
        // Clear the existing table data
        currentTasksTableModel.setRowCount(0);
        tasks = loadAvailableTasks();
        System.out.println("###=> Tasks: " + tasks);
        for (Task task : tasks) {
            if (task != null && task.getRunnerId().equalsIgnoreCase(runnerUser
                    .getUsername())) {
                currentTasksTableModel.addRow(new Object[] {
                        task.getOrderId(),
                        task.getCustomerId(),
                        task.getVendorId(),
                        task.getTaskStatus(),
                });
            }

        }

    }

    private void showTaskPanel() {
        // --
        taskPanel.setVisible(true);
        tasksHistoryPanel.setVisible(false);
        revenuPanel.setVisible(false);
        isTaskPanelVisible = true;
        isTasksHistoryPanelVisible = false;
        isRevenuPanelVisible = false;
        updateSingleAvailableTask();
    }

    private void updateSingleAvailableTask() {
        // Create a filtered copy of tasks with TaskStatus.PENDING
        List<Task> pendingTasks = tasks.stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.PENDING)
                .collect(Collectors.toList());

        if (pendingTasks.size() > 0 && availableTasksIndexer < pendingTasks.size()) {
            Order order = findOrder(pendingTasks.get(availableTasksIndexer).getOrderId());
            orderField.setText(String.valueOf(pendingTasks.get(availableTasksIndexer).getOrderId()));
            usernameField.setText(String.valueOf(pendingTasks.get(availableTasksIndexer).getCustomerId()));
            vendorField.setText(String.valueOf(pendingTasks.get(availableTasksIndexer).getVendorId()));
            itemsField.setText(String.valueOf(pendingTasks.get(availableTasksIndexer).getTaskStatus()));
            costField.setText(String.valueOf(order.getTotal()));
            deliveryLabel.setText(String.valueOf(pendingTasks.get(availableTasksIndexer).getEarning()));
            availableTasksIndexer++;
        } else if (pendingTasks.size() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No Available Tasks Yet!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // If we reach the end of the list, display a message with a "Refresh" button
            int choice = JOptionPane.showOptionDialog(this,
                    "You have reached the last task available.",
                    "End of Task List",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[] { "Refresh" },
                    "Refresh");

            if (choice == 0) {
                refreshTasks();
            }
        }
    }

    private void refreshTasks() {
        // Reset the indexer to 0 and update the task fields
        availableTasksIndexer = 0;
        updateSingleAvailableTask();
    }

    private ArrayList<Order> readOrdersFromFile() {
        ArrayList<Order> orderList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\orders.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int orderId = Integer.parseInt(data[0].trim());
                String customerId = data[1].trim();
                String vendorId = data[2].trim();
                String[] itemsData = data[3].split("\\|");
                List<Item> items = new ArrayList<>();
                for (String itemDatum : itemsData) {
                    String[] itemParts = itemDatum.split(":");
                    String itemId = itemParts[0].trim();
                    int quantity = Integer.parseInt(itemParts[1].trim());
                    items.add(new Item(itemId, quantity));
                }
                String timestamp = data[5].trim();
                DeliveryOption deliveryOption = DeliveryOption.valueOf(data[6].trim());
                OrderStatus status = OrderStatus.valueOf(data[7].trim());
                Order order = new Order(orderId, customerId, vendorId, timestamp, items, deliveryOption);
                order.setStatus(status);
                orderList.add(order);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    private void handleDeliveredButton() {
        Task taskToUpdate = findTask(Integer.parseInt(orderField.getText()));
        Order orderToUpdate = findOrder(Integer.parseInt(orderField.getText()));
        if (orderToUpdate != null && taskToUpdate != null) {
            taskToUpdate.setTaskStatus(TaskStatus.COMPLETED);
            orderToUpdate.deliverOrder(); // Update the order status to "DELIVERED"
            writeOrdersAndTasksToFile();
            JOptionPane.showMessageDialog(this, "Task marked as delivered.");
            // Update the table with the new data
            updateTasksTable();
            deliveredButton.setEnabled(false); // Disable the "DELIVERED" button after marking as delivered
            refreshTasks(); // Move to the next available task
        } else {
            JOptionPane.showMessageDialog(this, "Task not found");
        }
    }

    private ArrayList<Task> loadAvailableTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\tasks.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int orderId = Integer.parseInt(data[0].trim());
                String customerId = data[1].trim();
                String vendorId = data[2].trim();
                TaskStatus taskStatus = TaskStatus.valueOf(data[3].trim());
                double earning = Double.parseDouble(data[4].trim());
                String runnerId = data[5].trim();
                Task task = new Task(orderId, customerId, vendorId, taskStatus, earning);
                task.setRunnerId(runnerId);
                taskList.add(task);
                return taskList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskList;
    }

    private void showTaskHistoryPanel() {
        taskPanel.setVisible(false);
        tasksHistoryPanel.setVisible(true);
        revenuPanel.setVisible(false);
        isTaskPanelVisible = false;
        isTasksHistoryPanelVisible = true;
        isRevenuPanelVisible = false;
    }

    private void showRevenuePanel() {
        taskPanel.setVisible(false);
        tasksHistoryPanel.setVisible(false);
        revenuPanel.setVisible(true);
        isTaskPanelVisible = false;
        isTasksHistoryPanelVisible = false;
        isRevenuPanelVisible = true;
    }

    private JPanel createTasksHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Tasks History"));
        panel.setLayout(null);
        // - Labels
        lblTotalEarnings = new JLabel("Total Earnings: $0.00");
        // - Buttons
        refreshRevenueButton = new JButton("Refresh Revenue");
        // - Models
        tasksHistoryTableModel = new DefaultTableModel();
        // - Tables
        tasksHistoryTable = new JTable(tasksHistoryTableModel);
        // -- Table Components
        tasksHistoryTableModel.addColumn("Order ID");
        tasksHistoryTableModel.addColumn("Customer ID");
        tasksHistoryTableModel.addColumn("Vendor ID");
        tasksHistoryTableModel.addColumn("Status");
        // - ScrollPane
        JScrollPane taskHistoryScrollPane = new JScrollPane(tasksHistoryTable);
        // - add
        panel.add(taskHistoryScrollPane);
        // - Bounds
        taskHistoryScrollPane.setBounds(35, 35, 765, 400);
        panel.setBounds(195, 35, 850, 560);
        return panel;
    }

    private void showTasksHistoryDetails(int selectedRowIndex) {
    }

    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Revenue"));
        panel.setLayout(null);

        // - Label
        lblTotalEarnings = new JLabel("Total Earnings: $0.00");
        // - Button
        refreshRevenueButton = new JButton("Refresh Revenue");
        // -- Revenue Table Components
        revenueTableModel = new DefaultTableModel();
        revenueTable = new JTable(revenueTableModel);

        // -- Table Columns
        revenueTableModel.addColumn("Order ID");
        revenueTableModel.addColumn("Customer ID");
        revenueTableModel.addColumn("Vendor ID");
        revenueTableModel.addColumn("Earning");

        // - ScrollPane
        JScrollPane revenueScrollPane = new JScrollPane(revenueTable);

        // - add components to panel
        panel.add(revenueScrollPane);
        panel.add(lblTotalEarnings);
        panel.add(refreshRevenueButton);

        // - set bounds for components
        revenueScrollPane.setBounds(35, 35, 765, 400);
        lblTotalEarnings.setBounds(35, 35, 200, 25);
        refreshRevenueButton.setBounds(250, 35, 150, 25);
        panel.setBounds(195, 35, 850, 560);

        refreshRevenueButton.addActionListener(e -> updateRevenuePanel());
        return panel;
    }

    private void updateRevenuePanel() {
        double totalEarnings = calculateTotalEarnings();
        lblTotalEarnings.setText("Total Earnings: $" + String.format("%.2f", totalEarnings));
    }

    private double calculateTotalEarnings() {
        double totalEarnings = 0.0;

        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\tasks.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                TaskStatus taskStatus = TaskStatus.valueOf(data[3].trim());
                double earning = Double.parseDouble(data[4].trim());
                String runnerId = data[5].trim();

                // Assuming only completed tasks contribute to earnings
                if (taskStatus == TaskStatus.COMPLETED && runnerUser.getUsername().equals(runnerId)) {
                    totalEarnings += earning;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalEarnings;
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Available Tasks"));
        panel.setLayout(null);
        // construct components
        // - Buttons
        acceptButton = new JButton("ACCEPT TASK");
        declineButton = new JButton("DECLINE TASK");
        deliveredButton = new JButton("DELIVERED");
        deliveredButton.setEnabled(false);
        // - Labels
        currentTasksLabel = new JLabel("CURRENT TASKS");
        orderLabel = new JLabel("Order ID:");
        usernameLabel = new JLabel("Username:");
        vendorLabel = new JLabel("Vendor:");
        itemsLabel = new JLabel("Items:");
        costLabel = new JLabel("Cost:");
        deliveryLabel = new JLabel("DELIVERY CHARGE:");
        // - Models
        currentTasksTableModel = new DefaultTableModel();
        // - Tables
        currentTasksTable = new JTable(currentTasksTableModel);
        // -- Table Components
        currentTasksTableModel.addColumn("Order ID");
        currentTasksTableModel.addColumn("Customer ID");
        currentTasksTableModel.addColumn("Vendor ID");
        currentTasksTableModel.addColumn("Status");
        // - ScrollPane
        JScrollPane currenttasksScrollPane = new JScrollPane(currentTasksTable);
        // - Fields
        orderField = new JTextField(5);
        orderField.setEditable(false);

        usernameField = new JTextField(5);
        usernameField.setEditable(false);

        vendorField = new JTextField(5);
        vendorField.setEditable(false);

        itemsField = new JTextField(20);
        itemsField.setEditable(false);

        costField = new JTextField(5);
        costField.setEditable(false);
        // add components
        panel.add(acceptButton);
        panel.add(declineButton);
        panel.add(currentTasksLabel);
        panel.add(currenttasksScrollPane);
        panel.add(deliveredButton);
        panel.add(orderLabel);
        panel.add(orderField);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(vendorLabel);
        panel.add(vendorField);
        panel.add(itemsLabel);
        panel.add(itemsField);
        panel.add(costLabel);
        panel.add(costField);
        panel.add(deliveryLabel);
        // set component bounds
        acceptButton.setBounds(545, 225, 120, 40);
        declineButton.setBounds(680, 225, 120, 40);
        currentTasksLabel.setBounds(35, 300, 100, 25);
        currenttasksScrollPane.setBounds(35, 330, 765, 150);
        deliveredButton.setBounds(680, 495, 120, 40);
        orderLabel.setBounds(35, 85, 100, 30);
        orderField.setBounds(90, 85, 120, 30);
        usernameLabel.setBounds(220, 85, 100, 30);
        usernameField.setBounds(290, 85, 200, 30);
        vendorLabel.setBounds(500, 85, 100, 30);
        vendorField.setBounds(550, 85, 250, 30);
        itemsLabel.setBounds(35, 150, 100, 30);
        itemsField.setBounds(90, 150, 500, 30);
        costLabel.setBounds(650, 150, 100, 30);
        costField.setBounds(690, 150, 110, 30);
        deliveryLabel.setBounds(30, 220, 200, 35);
        panel.setBounds(195, 35, 850, 560);
        // Action Listener
        declineButton.addActionListener(e -> updateSingleAvailableTask());
        acceptButton.addActionListener(e -> addTaskToDriver());
        deliveredButton.addActionListener(e -> handleDeliveredButton());
        return panel;
    }

    private Task findTask(int taskId) {
        for (Task task : tasks) {
            if (task != null && task.getOrderId() == taskId) {
                return task;
            }
        }
        return null;
    }

    private Order findOrder(int orderId) {
        for (Order order : orders) {
            if (order != null && order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

    private void addTaskToDriver() {
        Task taskToUpdate = findTask(Integer.parseInt(orderField.getText()));
        Order orderToUpdate = findOrder(Integer.parseInt(orderField.getText()));
        if (orderToUpdate != null && taskToUpdate != null) {
            taskToUpdate.setRunnerId(runnerUser.getUsername());
            taskToUpdate.setTaskStatus(TaskStatus.ACCEPTED);
            orderToUpdate.setStatus(OrderStatus.UNDER_DELIVERY);
            notifyCustomerOrderStatus(orderToUpdate);
            writeOrdersAndTasksToFile();
            JOptionPane.showMessageDialog(this, "Task added successfully.");
            // Update the table with the new data
            updateTasksTable();
            updateSingleAvailableTask();
        } else {
            JOptionPane.showMessageDialog(this, "Task not found");
        }
    }

    private void notifyCustomerOrderStatus(Order order) {
        // Create a Notification object
        if (order.getStatus() == OrderStatus.UNDER_DELIVERY) {
            Notification notification = new Notification("Order", runnerUser.getUsername(), order.getCustomerId(),
                    "under delivery",
                    order.getTimestamp(), "Your order has been accepted by driver " + runnerUser.getName()
                            + " and will be delivered shortly.");
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

    private void writeOrdersAndTasksToFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\orders.txt"))) {
            bw.write("orderId,username,items,total,timestamp,deliveryOptions,status,deliveryCost\n");
            for (Order order : orders) {
                bw.write(order.toString());
                bw.newLine();
            }

            try (BufferedWriter tk = new BufferedWriter(
                    new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\tasks.txt"))) {
                tk.write("orderId,customerId,vendorId,taskStatus,earning,runnerId\n");
                for (Task task : tasks) {
                    {
                        tk.write(task.getOrderId() + "," + task.getCustomerId() + "," + task.getVendorId() + ","
                                + task.getTaskStatus() + "," + task.getEarning() + ","
                                + task.getRunnerId());
                        tk.newLine();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
