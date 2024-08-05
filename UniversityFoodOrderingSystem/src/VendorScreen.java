import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VendorScreen extends JFrame {
    // -- Text Areas
    private JTextArea reviewDetailsTextArea;
    // -- Panels
    private JPanel foodItemPanel, orderPanel, reviewPanel;
    // -- Buttons
    private JButton foodItemsButton, orderButtons, reviewButton, findButton, createButton,
            updateButton, deleteButton;
    // -- Labels
    private JLabel lblWelcome, itemCodeLabel, nameLabel, categoryLabel,
            pricLabel, filterLabel;
    // -- Combo Boxes
    private JComboBox<String> categoryComboBox, filterComboBox;
    // -- Text Fields
    private JTextField itemCodeField, nameField, priceField,
            searchField;
    // -- Tables
    private JTable foodItemTable, orderTable, reviewOrderTable;
    // -- Table Models
    private DefaultTableModel tableModel, orderTableModel, reviewOrderTableModel;
    // -----------
    private ArrayList<FoodItem> foodItems;
    private ArrayList<Order> orders;
    private ArrayList<Category> categories;
    private User vendorUser;
    private boolean isFoodItemPanelVisible = true;
    private boolean isOrderPanelVisible = false;
    private boolean isReviewVisible = false;

    public VendorScreen(User loggedUser) {
        super("Vendor Management System");
        vendorUser = loggedUser;
        categories = loadCategories(
                "C:\\UniversityFoodOrderingSystem\\src\\db\\categories.txt");
        foodItems = readFoodItemFromFile();
        orders = readOrdersFromFile();
        setLayout(null);
        // - Construct components
        // -- Panels
        foodItemPanel = createFoodItemPanel();
        orderPanel = createOrderPanel();
        reviewPanel = createReviewPanel();
        // -- Buttons
        foodItemsButton = new JButton("Food items");
        orderButtons = new JButton("Orders");
        reviewButton = new JButton("Customer Reviews");
        // -- Labels
        lblWelcome = new JLabel("Welcome");
        // --------------------------------------------
        // Add components
        add(foodItemsButton);
        add(orderButtons);
        add(reviewButton);
        add(lblWelcome);
        add(foodItemPanel);
        add(orderPanel);
        add(reviewPanel);
        // Update welcome label text if loggedUser is not null
        if (vendorUser != null) {
            lblWelcome.setText("Welcome " + vendorUser.getUsername() + " - " + vendorUser.getType());
        }
        // Set component bounds
        foodItemsButton.setBounds(25, 35, 150, 30);
        orderButtons.setBounds(25, 75, 150, 30);
        reviewButton.setBounds(25, 115, 150, 30);
        lblWelcome.setBounds(345, 10, 300, 25);
        // Add action listeners to buttons
        foodItemsButton.addActionListener(e -> showFoodItemPanel());
        orderButtons.addActionListener(e -> showOrderPanel());
        reviewButton.addActionListener(e -> showReviewPanel());
        // Initialize tables & registeration panel
        showFoodItemPanel();
        updateTable();
        updateOrdersTable();
        updatereviewOrderTable();
    }

    private void showFoodItemPanel() {
        foodItemPanel.setVisible(true);
        orderPanel.setVisible(false);
        reviewPanel.setVisible(false);
        isFoodItemPanelVisible = true;
        isOrderPanelVisible = false;
        isReviewVisible = false;
    }

    private void showOrderPanel() {
        foodItemPanel.setVisible(false);
        orderPanel.setVisible(true);
        reviewPanel.setVisible(false);
        isFoodItemPanelVisible = false;
        isOrderPanelVisible = true;
        isReviewVisible = false;
    }

    private void showReviewPanel() {
        foodItemPanel.setVisible(false);
        orderPanel.setVisible(false);
        reviewPanel.setVisible(true);
        isFoodItemPanelVisible = false;
        isOrderPanelVisible = false;
        isReviewVisible = true;
    }

    private JPanel createFoodItemPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Food Items"));
        panel.setLayout(null);
        // - Construct components
        // -- Buttons
        createButton = new JButton("Create");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        findButton = new JButton("Find");
        // -- Labels
        filterLabel = new JLabel("Filter By");
        categoryLabel = new JLabel("Category");
        pricLabel = new JLabel("Price");
        itemCodeLabel = new JLabel("Item Code");
        itemCodeField = new JTextField(15);
        nameLabel = new JLabel("Name");
        // -- ComboBoxes
        String[] categoryComboBoxItems = getCategoryNames(categories);
        String[] filterComboBoxItems = getFilterComboBoxItems(categoryComboBoxItems);
        categoryComboBox = new JComboBox<>(categoryComboBoxItems);
        filterComboBox = new JComboBox<>(filterComboBoxItems);
        // -- Text Fields
        itemCodeField = new JTextField(15);
        nameField = new JTextField(15);
        priceField = new JTextField(15);
        searchField = new JTextField(15);
        // -- Tables
        tableModel = new DefaultTableModel();
        foodItemTable = new JTable(tableModel);
        tableModel.addColumn("Code");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Category");
        JScrollPane tableScrollPane = new JScrollPane(foodItemTable);
        // --- Select by row on the table
        foodItemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = foodItemTable.getSelectedRow();
                    if (selectedRow != -1) {
                        itemCodeField.setText(foodItemTable.getValueAt(selectedRow, 0).toString());
                        nameField.setText(foodItemTable.getValueAt(selectedRow, 1).toString());
                        priceField.setText(foodItemTable.getValueAt(selectedRow, 2).toString());
                        categoryComboBox.setSelectedItem(foodItemTable.getValueAt(selectedRow, 3).toString());
                    }
                }
            }
        });
        // --------------------------------------------
        // Add components
        panel.add(itemCodeLabel);
        panel.add(itemCodeField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(categoryLabel);
        panel.add(categoryComboBox);
        panel.add(pricLabel);
        panel.add(priceField);
        panel.add(createButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(filterLabel);
        panel.add(filterComboBox);
        panel.add(searchField);
        panel.add(findButton);
        panel.add(tableScrollPane);
        // Set component bounds
        itemCodeLabel.setBounds(30, 20, 100, 25);
        itemCodeField.setBounds(95, 20, 150, 25);
        nameLabel.setBounds(260, 20, 100, 25);
        nameField.setBounds(300, 20, 200, 25);
        categoryLabel.setBounds(510, 20, 100, 25);
        categoryComboBox.setBounds(565, 20, 140, 25);
        pricLabel.setBounds(30, 60, 100, 25);
        priceField.setBounds(95, 60, 150, 25);
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
        createButton.addActionListener(e -> createFoodItem());
        findButton.addActionListener(e -> readFoodItem());
        updateButton.addActionListener(e -> updateFoodItem());
        deleteButton.addActionListener(e -> deleteFoodItem());
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Orders"));
        panel.setLayout(null);
        // Construct components
        // -- Labels
        JLabel selectedOrderIdLable = new JLabel();
        // -- Text Areas
        JTextArea orderDetails = new JTextArea();
        orderDetails.setEditable(false);
        orderDetails.setOpaque(false);
        orderDetails.setFont(orderDetails.getFont().deriveFont(Font.BOLD, 14f));
        orderDetails.setText("No order has been selected");
        orderDetails.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // -- Buttons
        JButton acceptButton = new JButton("Accept");
        JButton rejectButton = new JButton("Reject");
        // -- Tables
        orderTableModel = new DefaultTableModel();
        orderTable = new JTable(orderTableModel);
        orderTableModel.addColumn("Order ID");
        orderTableModel.addColumn("Customer ID");
        orderTableModel.addColumn("Items");
        orderTableModel.addColumn("Total");
        orderTableModel.addColumn("Timestamp");
        orderTableModel.addColumn("Delivery Option");
        orderTableModel.addColumn("Status");
        orderTableModel.addColumn("Delivery Cost");
        JScrollPane orderTableScrollPane = new JScrollPane(orderTable);
        // --- Select by row on the table
        orderTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = orderTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedOrderIdLable.setText(orderTable.getValueAt(selectedRow, 0).toString());
                        orderDetails
                                .setText("Order Details\nOrder ID: " + orderTable.getValueAt(selectedRow, 0).toString()
                                        + " | Customer ID: " + orderTable.getValueAt(selectedRow, 1).toString()
                                        + " | Items: " + orderTable.getValueAt(selectedRow, 2).toString()
                                        + " | Total:" + orderTable.getValueAt(selectedRow, 3).toString()
                                        + "\nTimestamp: " + orderTable.getValueAt(selectedRow, 4).toString()
                                        + " | Delivery Option: " + orderTable.getValueAt(selectedRow, 5).toString()
                                        + "\nStatus: " + orderTable.getValueAt(selectedRow, 6).toString()
                                        + " | Delivery Cost: " + orderTable.getValueAt(selectedRow, 7).toString());
                    }
                }
            }
        });
        // Add components to the panel
        panel.add(orderTableScrollPane);
        panel.add(orderDetails);
        panel.add(acceptButton);
        panel.add(rejectButton);
        // Set component bounds
        orderTableScrollPane.setBounds(30, 30, 685, 400);
        orderDetails.setBounds(30, 450, 600, 80);
        acceptButton.setBounds(635, 450, 80, 25);
        rejectButton.setBounds(635, 490, 80, 25);
        panel.setBounds(195, 35, 745, 560);

        // Add action listeners
        acceptButton.addActionListener(
                e -> acceptOrder(Integer.parseInt(selectedOrderIdLable.getText())));
        rejectButton.addActionListener(
                e -> rejectOrder(Integer.parseInt(selectedOrderIdLable.getText())));
        return panel;
    }

    private JPanel createReviewPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Reviews"));
        panel.setLayout(null);

        // Construct components
        reviewDetailsTextArea = new JTextArea();
        reviewOrderTable = new JTable();
        reviewOrderTableModel = new DefaultTableModel();
        reviewOrderTable.setModel(reviewOrderTableModel);
        // orderId,customerId,rating,comment
        JScrollPane reviewDetailsTextAreaScrollPane = new JScrollPane(reviewDetailsTextArea);
        JScrollPane reviewTableScrollPane = new JScrollPane(reviewOrderTable);
        reviewOrderTableModel.addColumn("Order ID");
        reviewOrderTableModel.addColumn("Customer ID");
        reviewOrderTableModel.addColumn("Rating");
        reviewOrderTableModel.addColumn("Comment");
        // --- Select by row on the table
        reviewOrderTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = reviewOrderTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Order orderDtails = findOrder(
                                Integer.parseInt(reviewOrderTable.getValueAt(selectedRow, 0).toString()));
                        reviewDetailsTextArea
                                .setText("Customer Review\nOrder ID: "
                                        + reviewOrderTable.getValueAt(selectedRow, 0).toString()
                                        + " | Customer ID: " + reviewOrderTable.getValueAt(selectedRow, 1).toString()
                                        + "\nRating: " + reviewOrderTable.getValueAt(selectedRow, 2).toString()
                                        + " | Comment:" + reviewOrderTable.getValueAt(selectedRow, 3).toString()
                                        + "\n\nOrder Details:\nItems: " + orderDtails.getItems() + " | Total: "
                                        + orderDtails.getTotal() + " | Timestamp: " + orderDtails.getTimestamp()
                                        + "\nDelivery Option: " + orderDtails.getDeliveryOption() + " | Order Staus: "
                                        + orderDtails.getStatus() + " | Delivery Cost: "
                                        + orderDtails.getDeliveryCost());
                    }
                }
            }
        });
        // Add components to the panel
        panel.add(reviewDetailsTextAreaScrollPane);
        panel.add(reviewTableScrollPane);
        // Set component bounds
        reviewDetailsTextAreaScrollPane.setBounds(30, 60, 675, 150);
        reviewTableScrollPane.setBounds(30, 220, 675, 330);
        panel.setBounds(195, 35, 745, 560);
        return panel;
    }

    private void acceptOrder(int orderId) {
        Order orderToUpdate = findOrder(orderId);
        if (orderToUpdate != null) {
            orderToUpdate.setStatus(OrderStatus.ACCEPTED);
            writeOrdersToFile();
            JOptionPane.showMessageDialog(this, "Order accepted successfully.");
            // Update the table with the new data
            updateTable();
            notifyCustomerOrderStatus(orderToUpdate);
        } else {
            JOptionPane.showMessageDialog(this, "Order not found");
        }
        updateOrdersTable();
    }

    private void notifyCustomerOrderStatus(Order order) {
        // Create a Notification object
        Notification notification;
        if (order.getStatus() == OrderStatus.ACCEPTED) {
            notification = new Notification("Order", vendorUser.getUsername(), order.getCustomerId(),
                    "accepted",
                    order.getTimestamp(), "Your order has been accepted by the vendor");
        } else {
            notification = new Notification("Order", vendorUser.getUsername(), order.getCustomerId(),
                    "declined",
                    order.getTimestamp(), "Your order declined by the vendor");
        }
        // Append the notification to notification.txt
        appendToNotificationFile(notification);
        // Show pop-up message
        JOptionPane.showMessageDialog(null, "Customer notified successfully!");

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

    private void rejectOrder(int orderId) {
        Order orderToUpdate = findOrder(orderId);
        if (orderToUpdate != null) {
            orderToUpdate.setStatus(OrderStatus.REJECTED);

            writeOrdersToFile();
            JOptionPane.showMessageDialog(this, "Order rejected successfully.");
            // Update the table with the new data
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Order not found");
        }
        updateOrdersTable();
    }

    private void updateOrdersTable() {
        // Clear the existing table data
        orderTableModel.setRowCount(0);

        // Read receipt data from the file and populate the table
        ArrayList<Order> orders = readOrdersFromFile();
        for (Order order : orders) {
            if (order != null && order.getVendorId().equalsIgnoreCase(vendorUser
                    .getUsername())) {
                orderTableModel.addRow(new Object[] {
                        order.getOrderId(),
                        order.getCustomerId(),
                        order.getItems(),
                        order.getTotal(),
                        order.getTimestamp(),
                        order.getDeliveryOption(),
                        order.getStatus(),
                        order.getDeliveryCost()
                });
            }

        }
    }

    private void updatereviewOrderTable() {
        // Clear the existing table data
        reviewOrderTableModel.setRowCount(0);
        // Read reviews data
        ArrayList<OrderReview> reviews = readReviewsFromFile();
        for (OrderReview review : reviews) {
            for (Order order : orders) {
                if (order.getOrderId() == review.getOrderId()
                        && order.getVendorId().equalsIgnoreCase(vendorUser.getUsername())) {
                    reviewOrderTableModel.addRow(new Object[] {
                            review.getOrderId(),
                            review.getCustomerId(),
                            review.getRating(),
                            review.getComment(),
                    });
                }
            }
        }
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

    private ArrayList<OrderReview> readReviewsFromFile() {
        ArrayList<OrderReview> reviewList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\reviews.txt"))) {
            String line;
            // Skip the first line (header)
            // orderId,customerId,rating,comment
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int orderId = Integer.parseInt(data[0].trim());
                String customerId = data[1].trim();
                int rating = Integer.parseInt(data[2].trim());
                String comment = data[3].trim();
                OrderReview review = new OrderReview(orderId, customerId, rating, comment);
                reviewList.add(review);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    private void createFoodItem() {
        String itemCode = itemCodeField.getText();
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());

        // Get the selected item from the categoryComboBox
        String categoy = categoryComboBox.getSelectedItem().toString();

        // Check if the food item already exists
        if (findFoodItem(itemCode) != null) {
            JOptionPane.showMessageDialog(this, "Item code already exists. Please choose a different code.");
            return;
        }

        try {
            FoodItem newItem = new FoodItem(itemCode, name, price, vendorUser.getUsername(), categoy);
            foodItems.add(newItem);

            writeFoodItemsToFile();

            clearFields();
            JOptionPane.showMessageDialog(this, "Food Item created successfully.");

            // Update the table with the new data
            updateTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Data. Please enter a valid data.");
        }
    }

    private void readFoodItem() {
        // Clear the existing table data
        tableModel.setRowCount(0);

        String filter = filterComboBox.getSelectedItem().toString().toLowerCase();
        String searchKeyword = searchField.getText().toLowerCase();

        for (FoodItem item : foodItems) {
            boolean matchesFilter = filter.equals("all")
                    || item.getCategory().toLowerCase().equals(filter);
            boolean matchesSearch = item.getCode().toLowerCase().contains(searchKeyword)
                    || item.getName().toLowerCase().contains(searchKeyword);

            if (matchesFilter && matchesSearch) {
                // Add each user to the table
                tableModel.addRow(new Object[] { item.getCode(), item.getName(), item.getPrice(),
                        item.getCategory() });
            }
        }
    }

    private void updateTable() {
        // Clear the existing table data
        tableModel.setRowCount(0);

        // Populate the table with the current user data
        System.out.println(foodItems);
        for (FoodItem item : foodItems) {
            tableModel
                    .addRow(new Object[] { item.getCode(), item.getName(), item.getPrice(),
                            item.getCategory() });
        }
    }

    private void updateFoodItem() {
        String itemCode = itemCodeField.getText();
        FoodItem itemToUpdate = findFoodItem(itemCode);

        if (itemToUpdate != null) {
            itemToUpdate.setCode(itemCodeField.getText());
            itemToUpdate.setName(nameField.getText());
            itemToUpdate.setPrice(Double.parseDouble(priceField.getText()));
            itemToUpdate.setCategory(categoryComboBox.getSelectedItem().toString());
            itemToUpdate.setVendor(vendorUser.getUsername());

            writeFoodItemsToFile();

            clearFields();
            JOptionPane.showMessageDialog(this, "Food item updated successfully.");
            // Update the table with the new data
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Food item not found");
        }
    }

    private void deleteFoodItem() {
        String foodItemCode = itemCodeField.getText();
        FoodItem itemToDelete = findFoodItem(foodItemCode);

        if (itemToDelete != null) {
            foodItems.remove(itemToDelete);
            writeFoodItemsToFile();
            clearFields();
            JOptionPane.showMessageDialog(this, "Food item deleted successfully.");

            // Update the table with the new data
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Food Item not found.");
        }
    }

    private FoodItem findFoodItem(String itemCodeOrName) {
        for (FoodItem foodItem : foodItems) {
            // We need to filter by Vendor username
            if (foodItem != null && foodItem.getVendor().equalsIgnoreCase(vendorUser.getUsername())
                    && (foodItem.getCode().equals(itemCodeOrName) || foodItem.getName().equals(itemCodeOrName))) {
                return foodItem;
            }
        }
        return null;
    }

    private Order findOrder(int orderId) {
        for (Order order : orders) {
            // We need to filter by Vendor username
            if (order != null && order.getOrderId() == orderId && order.getVendorId().equalsIgnoreCase(vendorUser
                    .getUsername())) {
                return order;
            }
        }
        return null;
    }

    private Order findOrders() {
        for (Order order : orders) {
            // We need to filter by Vendor username
            if (order != null && order.getVendorId().equalsIgnoreCase(vendorUser
                    .getUsername())) {
                return order;
            }
        }
        return null;
    }

    private void clearFields() {
        itemCodeField.setText("");
        nameField.setText("");
        priceField.setText("");
        categoryComboBox.setSelectedIndex(0);
    }

    private ArrayList<FoodItem> readFoodItemFromFile() {
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\items.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String code = data[0];
                String name = data[1];
                double price = Double.parseDouble(data[2]);
                String vendor = data[3];
                String category = data[4];
                FoodItem item = new FoodItem(code, name, price, vendor, category);
                foodItems.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foodItems;
    }

    private void writeFoodItemsToFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\items.txt"))) {
            bw.write("code,name,price,vendor,category\n");
            for (FoodItem item : foodItems) {
                bw.write(item.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOrdersToFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\orders.txt"))) {
            bw.write("orderId,username,items,total,timestamp,deliveryOptions,status,deliveryCost\n");
            for (Order order : orders) {
                bw.write(order.toString());
                bw.newLine();
            }

            try (BufferedWriter tk = new BufferedWriter(
                    new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\tasks.txt"))) {
                tk.write("orderId,customerId,vendorId,taskStatus,earning,runnderId\n");
                for (Order order : orders) {
                    if (order.getDeliveryOption() == DeliveryOption.DELIVERY
                            && order.getStatus() == OrderStatus.ACCEPTED) {
                        tk.write(order.getOrderId() + "," + order.getCustomerId() + "," + order.getVendorId() + ","
                                + TaskStatus.PENDING + "," + order.getDeliveryCost() + ",null");
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

    private static ArrayList<Category> loadCategories(String filePath) {
        ArrayList<Category> categories = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each line in the file represents a category
                Category category = new Category(line.trim());
                categories.add(category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return categories;
    }

    private static String[] getCategoryNames(ArrayList<Category> categories) {
        String[] categoryNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryNames[i] = categories.get(i).getCategoryName();
        }
        return categoryNames;
    }

    private static String[] getFilterComboBoxItems(String[] categoryComboBoxItems) {
        String[] filterComboBoxItems = new String[categoryComboBoxItems.length + 1];
        filterComboBoxItems[0] = "all";
        System.arraycopy(categoryComboBoxItems, 0, filterComboBoxItems, 1, categoryComboBoxItems.length);
        return filterComboBoxItems;
    }
}