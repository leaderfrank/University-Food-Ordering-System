import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class CustomerScreen extends JFrame {
    // -- Panels
    private JPanel menuPanel, orderHistoryPanel, transHistoryPanel;
    // -- Buttons
    private JButton menuButton, historyButton, transactionButton, addButton, deleteButton, updateButton, orderButton,
            findOrderButton, findButton, reviewButton, cancelOrderButton, reOrderButton;
    // -- Labels
    private JLabel lblWelcome, vendorLabel, itemLabel, priceLabel, jcomp7, jcomp12, jcomp17, jcomp18, codeLabel,
            orderIdLabel, vendorIdLabel, itemsLabel, totalLabel, timestampLabel, deliveryOptionsLabel, statusLabel,
            deliveryCostLabel, filterStatusLabel, searchLabel;
    // -- Table Models
    private DefaultTableModel menuTableModel, itemTableModel, orderTableModel, transactionTableModel;
    // -- Tables
    private JTable menuTable, selectedItemsTable, ordersTable, transactionTable;
    // -- Text Fields
    private JTextField qtyField, searchField, searchOrdersField;
    // -- Combo Boxes
    private JComboBox categoriesComboBox, filterStatusComboBox;
    // -- Panel View Controllers
    private boolean isMenuPanelVisible = true;
    private boolean isOrderHistoryPanelVisible = false;
    private boolean isTransHistoryPanelVisible = false;
    // -- Text Area
    private JTextArea receiptDetailsTextArea;
    // -- Logged Customer Data
    private User customerUser;
    private ArrayList<User> users;
    // -- Categories
    private ArrayList<Category> categories;
    // -- Food Items
    private ArrayList<FoodItem> foodItems;
    // -- Orders
    private ArrayList<Order> orders;
    // -- Notifications
    private ArrayList<Notification> notifications;
    // -- Selected items
    private List<Item> selectedItems;

    public CustomerScreen(User loggedUser) {
        // construct preComponents
        super("Customer - Food Ordering");
        customerUser = loggedUser;
        users = readUsersFromFile();
        categories = loadCategories(
                "C:\\UniversityFoodOrderingSystem\\src\\db\\categories.txt");
        foodItems = readFoodItemFromFile();
        orders = readOrdersFromFile();
        selectedItems = new ArrayList<>();
        // construct components
        // -- panels
        menuPanel = createMenuPanel();
        orderHistoryPanel = createOrderHistoryPanel();
        transHistoryPanel = createTransHistoryPanel();
        // -- buttons
        menuButton = new JButton("Menu");
        historyButton = new JButton("Order History");
        transactionButton = new JButton("Transaction History");
        // -- Labels
        lblWelcome = new JLabel("Welcome");
        // adjust size and set layout
        setPreferredSize(new Dimension(980, 650));
        setLayout(null);
        // add components
        add(menuButton);
        add(historyButton);
        add(transactionButton);
        add(menuPanel);
        add(orderHistoryPanel);
        add(transHistoryPanel);
        add(lblWelcome);
        lblWelcome.setBounds(345, 10, 300, 25);
        menuButton.setBounds(30, 50, 150, 30);
        historyButton.setBounds(30, 100, 150, 30);
        transactionButton.setBounds(30, 150, 150, 30);

        // Update welcome label text if loggedUser is not null
        if (customerUser != null) {
            lblWelcome.setText("Welcome " + customerUser.getUsername() + " - " +
                    customerUser.getType());
            JOptionPane.showMessageDialog(
                    null,
                    "After you add the vendor item we will show only\nthat vendor food items the system allows\none order for one vendor only",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Add action listeners to buttons
        menuButton.addActionListener(e -> {
            showMenuPanel();
            checkUnreadMessages();
        });
        historyButton.addActionListener(e -> {
            showOrderHistoryPanel();
            checkUnreadMessages();
        });
        transactionButton.addActionListener(e -> {
            showTransHistoryPanel();
            checkUnreadMessages();
        });

        // Initialize tables & registeration panel
        showMenuPanel();
        updateMenuTable();
        updateOrderHistoryTable();
        updateTransHistoryTable();
        checkUnreadMessages();
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

    private ArrayList<Notification> readAvailableNotifications() {
        ArrayList<Notification> notificationList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\notification.txt"))) {
            String line;
            // Skip the first line (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                // type,from,to,status,timestamp,message,isRead
                String[] data = line.split(",");
                String type = data[0].trim();
                String from = data[1].trim();
                String to = data[2].trim();
                String status = data[3].trim();
                String timestamp = data[4].trim();
                String message = data[5].trim();
                int isRead = Integer.parseInt(data[6].trim());
                Notification notification = new Notification(type, from, to, status, timestamp, message);
                notification.setIsRead(isRead);
                notificationList.add(notification);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notificationList;
    }

    private void checkUnreadMessages() {
        notifications = readAvailableNotifications();
        String username = customerUser.getUsername();
        for (Notification notification : notifications) {
            if (notification.getTo().equals(username) && notification.getIsRead() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "You have an unread message:\n" + notification.getMessage(),
                        "Unread Message",
                        JOptionPane.INFORMATION_MESSAGE);
                notification.setIsRead(1);
            }
        }
        writeNotificationsToFile();
    }

    private User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
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

    private void updateTransHistoryTable() {
    }

    private void updateOrderHistoryTable() {
        // Clear the existing table data
        orderTableModel.setRowCount(0);

        // Get the selected status from the JComboBox
        String selectedStatus = (String) filterStatusComboBox.getSelectedItem();

        // Get the search keyword from the search field
        String searchKeyword = searchOrdersField.getText().trim().toLowerCase();

        // Populate the table with the current user data based on the selected status
        // and search keyword
        for (Order order : orders) {
            if (order.getCustomerId().equalsIgnoreCase(customerUser.getUsername())) {
                if ((selectedStatus.equalsIgnoreCase("All")
                        || selectedStatus.equalsIgnoreCase(order.getStatus().toString()))
                        && (searchKeyword.isEmpty()
                                || order.getVendorId().toLowerCase().contains(searchKeyword)
                                || order.getItems().toLowerCase().contains(searchKeyword)
                                || String.valueOf(order.getTotal()).toLowerCase().contains(searchKeyword)
                                || order.getTimestamp().toLowerCase().contains(searchKeyword))) {
                    orderTableModel.addRow(new Object[] {
                            order.getOrderId(),
                            order.getVendorId(),
                            order.getItems(),
                            order.getTotal(),
                            order.getTimestamp(),
                            order.getDeliveryOption(),
                            order.getStatus()
                    });
                }
            }
        }
    }

    private void updateMenuTable() {
        // Clear the existing table data
        menuTableModel.setRowCount(0);
        // Populate the table with the current user data
        System.out.println(foodItems);
        for (FoodItem item : foodItems) {
            if (selectedItems.isEmpty()) {
                menuTableModel
                        .addRow(new Object[] { item.getCode(), item.getName(), item.getVendor(), item.getPrice(),
                                item.getCategory() });
            } else if (item.getVendor().equalsIgnoreCase(findFoodItem(selectedItems.get(0).getItemId()).getVendor())) {
                menuTableModel
                        .addRow(new Object[] { item.getCode(), item.getName(), item.getVendor(), item.getPrice(),
                                item.getCategory() });
            }
        }
    }

    private void showMenuPanel() {
        menuPanel.setVisible(true);
        orderHistoryPanel.setVisible(false);
        transHistoryPanel.setVisible(false);
        isMenuPanelVisible = true;
        isOrderHistoryPanelVisible = false;
        isTransHistoryPanelVisible = false;
    }

    private void showOrderHistoryPanel() {
        menuPanel.setVisible(false);
        orderHistoryPanel.setVisible(true);
        transHistoryPanel.setVisible(false);
        isMenuPanelVisible = false;
        isOrderHistoryPanelVisible = true;
        isTransHistoryPanelVisible = false;
    }

    private void showTransHistoryPanel() {
        menuPanel.setVisible(false);
        orderHistoryPanel.setVisible(false);
        transHistoryPanel.setVisible(true);
        isMenuPanelVisible = false;
        isOrderHistoryPanelVisible = false;
        isTransHistoryPanelVisible = true;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Menu Food Items"));
        panel.setLayout(null);
        // - Construct components
        // -- Buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        orderButton = new JButton("Place an Order");
        findButton = new JButton("Find");
        // -- Labels
        vendorLabel = new JLabel("Vendor:");
        codeLabel = new JLabel();
        itemLabel = new JLabel("Item:");
        priceLabel = new JLabel("Price:");
        jcomp7 = new JLabel("Qty:");
        jcomp12 = new JLabel("Items");
        jcomp17 = new JLabel("Category:");
        jcomp18 = new JLabel("Search:");
        // -- TextFields
        qtyField = new JTextField(5);
        searchField = new JTextField(5);
        // -- Combo Boxes
        String[] categoryComboBoxItems = getCategoryNames(categories);
        String[] categoriesComboBoxItems = getFilterComboBoxItems(categoryComboBoxItems);
        categoriesComboBox = new JComboBox(categoriesComboBoxItems);
        // -- Table Models
        menuTableModel = new DefaultTableModel();
        itemTableModel = new DefaultTableModel();
        // -- Tables
        menuTable = new JTable(menuTableModel);
        selectedItemsTable = new JTable(itemTableModel);
        JScrollPane menuScrollPane = new JScrollPane(menuTable);
        JScrollPane selectedItemsScrollPane = new JScrollPane(selectedItemsTable);
        // -- Table Components
        menuTableModel.addColumn("Code");
        menuTableModel.addColumn("Name");
        menuTableModel.addColumn("Vendor");
        menuTableModel.addColumn("Price");
        menuTableModel.addColumn("Category");
        // --- Select by row on the table
        menuTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = menuTable.getSelectedRow();
                    if (selectedRow != -1) {
                        qtyField.setText("1");
                        updateButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        codeLabel.setText(menuTable.getValueAt(selectedRow, 0).toString());
                        itemLabel.setText("Item:  " + menuTable.getValueAt(selectedRow, 1).toString());
                        vendorLabel.setText("Vendor:  " + menuTable.getValueAt(selectedRow, 2).toString());
                        priceLabel.setText("Price:  " + menuTable.getValueAt(selectedRow, 3).toString());
                    }
                }
            }
        });
        // -
        itemTableModel.addColumn("Code");
        itemTableModel.addColumn("Name");
        itemTableModel.addColumn("Vendor");
        itemTableModel.addColumn("Price");
        itemTableModel.addColumn("Quantity");
        // --- Select by row on the table
        selectedItemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = selectedItemsTable.getSelectedRow();
                    if (selectedRow != -1) {
                        addButton.setEnabled(false);
                        updateButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        codeLabel.setText(selectedItemsTable.getValueAt(selectedRow, 0).toString());
                        itemLabel.setText("Item:  " + selectedItemsTable.getValueAt(selectedRow, 1).toString());
                        vendorLabel.setText("Vendor:  " + selectedItemsTable.getValueAt(selectedRow, 2).toString());
                        priceLabel.setText("Price:  " + selectedItemsTable.getValueAt(selectedRow, 3).toString());
                        qtyField.setText(selectedItemsTable.getValueAt(selectedRow, 4).toString());
                    }
                }
            }
        });
        // Add Components
        panel.add(vendorLabel);
        panel.add(itemLabel);
        panel.add(priceLabel);
        panel.add(jcomp7);
        panel.add(qtyField);
        panel.add(menuScrollPane);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(updateButton);
        panel.add(jcomp12);
        panel.add(selectedItemsScrollPane);
        panel.add(orderButton);
        panel.add(categoriesComboBox);
        panel.add(jcomp17);
        panel.add(jcomp18);
        panel.add(searchField);
        panel.add(findButton);
        // Adjusted bounds
        vendorLabel.setBounds(20, 35, 150, 25);
        itemLabel.setBounds(20, 60, 150, 25);
        priceLabel.setBounds(20, 85, 150, 25);
        jcomp7.setBounds(20, 290, 50, 25);
        menuScrollPane.setBounds(20, 115, 710, 165);
        addButton.setBounds(375, 290, 80, 25);
        deleteButton.setBounds(650, 290, 80, 25);
        updateButton.setBounds(465, 290, 80, 25);
        jcomp12.setBounds(20, 350, 100, 25);
        selectedItemsScrollPane.setBounds(20, 385, 710, 115);
        orderButton.setBounds(610, 520, 120, 30);
        qtyField.setBounds(45, 290, 100, 25);
        categoriesComboBox.setBounds(420, 35, 100, 25);
        jcomp17.setBounds(355, 35, 60, 25);
        jcomp18.setBounds(355, 70, 60, 25);
        searchField.setBounds(420, 70, 225, 25);
        findButton.setBounds(660, 70, 70, 25);
        panel.setBounds(195, 35, 745, 560);
        // -- Action Listeners
        findButton.addActionListener(e -> readFoodItem());
        addButton.addActionListener(e -> addFoodItem());
        updateButton.addActionListener(e -> updateSelectedFoodItem());
        deleteButton.addActionListener(e -> deleteSelectedFoodItem());
        orderButton.addActionListener(e -> makeOrder());
        return panel;
    }

    private void makeOrder() {
        int orderId = orders.size();
        String username = customerUser.getUsername();

        // Show pop-up to select delivery option
        DeliveryOption selectedDeliveryOption = showDeliveryOptions();

        if (selectedDeliveryOption != null) {
            Order newOrder = new Order(orderId, username, findFoodItem(selectedItems.get(0).getItemId()).getVendor(),
                    null, selectedItems, selectedDeliveryOption);

            // Display order details
            String orderDetails = "Order Details:\n\n" +
                    "Items: " + newOrder.getItems() + "\n" +
                    "Total: $" + newOrder.getTotal() + "\n" +
                    "Delivery Option: " + newOrder.getDeliveryOption() + "\n" +
                    "Delivery Cost: $" + newOrder.getDeliveryCost() + "\n" +
                    "Total Cost with Delivery: $" + (newOrder.getTotal() + newOrder.getDeliveryCost());

            // Show option dialog to ask the user to proceed or cancel
            int choice = JOptionPane.showOptionDialog(
                    null,
                    orderDetails,
                    "Order Summary",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[] { "Proceed", "Cancel" },
                    "Proceed");

            if (choice == JOptionPane.YES_OPTION) {
                // Check if customer's balance is sufficient
                double orderCost = newOrder.getTotal() + newOrder.getDeliveryCost();
                if (customerUser.getCredit() >= orderCost) {
                    // Deduct the order cost from customer's credit
                    User customerWithinList = findUser(customerUser.getUsername());
                    customerUser.setCredit(customerUser.getCredit() - orderCost);
                    customerWithinList.setCredit(customerWithinList.getCredit() - orderCost);
                    // Update order status and perform other order processing steps
                    newOrder.setStatus(OrderStatus.PENDING);
                    orders.add(newOrder);
                    writeOrdersToFile();
                    writeUsersToFile();
                    JOptionPane.showMessageDialog(this, "You have placed your order successfully!");
                    selectedItems.clear();
                    updateMenuTable();
                    updateSelectedItemsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance. Please add funds to your account.");
                }
            }
        }
    }

    private void writeOrdersToFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\orders.txt"))) {
            bw.write("orderId,username,vendor,items,total,timestamp,deliveryOptions,status,deliveryCost\n");
            for (Order order : orders) {
                bw.write(order.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeNotificationsToFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\notification.txt"))) {
            bw.write("type,from,to,status,timestamp,message,isRead\n");
            for (Notification notification : notifications) {
                bw.write(notification.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DeliveryOption showDeliveryOptions() {
        Object[] options = { DeliveryOption.OPT_IN, DeliveryOption.TAKEAWAY, DeliveryOption.DELIVERY };
        int choice = JOptionPane.showOptionDialog(null, "Select Delivery Option", "Delivery Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice >= 0 && choice < options.length) {
            return (DeliveryOption) options[choice];
        } else {
            return null;
        }
    }

    private void addFoodItem() {
        String itemCode = codeLabel.getText();
        try {
            double qtyDouble = Double.parseDouble(qtyField.getText());
            int qty = (int) Math.floor(qtyDouble);

            // Check if the quantity is within the valid range (1 to 100)
            if (qty < 1 || qty > 100) {
                JOptionPane.showMessageDialog(this, "Quantity must be an integer between 1 and 100.");
                return;
            }

            // Check if the food item already exists
            if (findFoodItemInCart(itemCode) != null) {
                JOptionPane.showMessageDialog(this, "Item code already exists. Please choose a different food item.");
                return;
            }

            Item newItem = new Item(itemCode, qty);
            selectedItems.add(newItem);
            JOptionPane.showMessageDialog(this, "Food Item added to the cart successfully.");
            // Update the table with the new data
            updateSelectedItemsTable();
            updateMenuTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity. Please enter a valid integer.");
        }
    }

    private void deleteSelectedFoodItem() {
        // Get the selected row from the table
        int selectedRow = selectedItemsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item to delete.");
            return;
        }

        // Get the item code of the selected row
        String itemCodeToDelete = itemTableModel.getValueAt(selectedRow, 0).toString();

        // Check if the item is in the selected items list
        Item itemToDelete = findFoodItemInCart(itemCodeToDelete);

        if (itemToDelete != null) {
            selectedItems.remove(itemToDelete);
            JOptionPane.showMessageDialog(this, "Food Item deleted from the cart successfully.");
            // Update the table with the new data
            updateSelectedItemsTable();
            updateMenuTable();
        } else {
            JOptionPane.showMessageDialog(this, "Selected food item not found in the cart.");
        }
    }

    private void updateSelectedFoodItem() {
        // Get the selected row from the table
        int selectedRow = selectedItemsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item to update.");
            return;
        }

        // Get the item code and quantity from the selected row
        String itemCodeToUpdate = itemTableModel.getValueAt(selectedRow, 0).toString();
        int newQty;

        try {
            double qtyDouble = Double.parseDouble(qtyField.getText());
            newQty = (int) Math.floor(qtyDouble);

            if (newQty < 1 || newQty > 100) {
                JOptionPane.showMessageDialog(this, "Quantity must be an integer between 1 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity. Please enter a valid integer.");
            return;
        }

        // Check if the item is in the selected items list
        Item itemToUpdate = findFoodItemInCart(itemCodeToUpdate);

        if (itemToUpdate != null) {
            // Update the quantity of the selected item
            itemToUpdate.setQuantity(newQty);
            JOptionPane.showMessageDialog(this, "Food Item updated successfully.");
            // Update the table with the new data
            updateSelectedItemsTable();
        } else {
            JOptionPane.showMessageDialog(this, "Selected food item not found in the cart.");
        }
    }

    private void updateSelectedItemsTable() {
        // Clear the existing table data
        itemTableModel.setRowCount(0);
        // Populate the table with the current user data
        System.out.println("@=> SELECTED ITEMS @=>" + selectedItems.toString());
        for (Item item : selectedItems) {
            FoodItem selectedFoodItem = findFoodItem(item.getItemId());
            itemTableModel
                    .addRow(new Object[] { selectedFoodItem.getCode(), selectedFoodItem.getName(),
                            selectedFoodItem.getVendor(), selectedFoodItem.getPrice(),
                            item.getQuantity() });
        }
    }

    private FoodItem findFoodItem(String itemCodeOrName) {
        for (FoodItem foodItem : foodItems) {
            if (foodItem != null
                    && (foodItem.getCode().equals(itemCodeOrName) || foodItem.getName().equals(itemCodeOrName))) {
                return foodItem;
            }
        }
        return null;
    }

    private Item findFoodItemInCart(String itemCode) {
        for (Item item : selectedItems) {
            if (item != null && item.getItemId().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Order History"));
        panel.setLayout(null);
        // Construct preComponents
        String[] itemsListViewItems = new String[1];
        String[] filterStatusComboBoxItems = new String[OrderStatus.values().length + 1];
        filterStatusComboBoxItems[0] = "All";
        for (int i = 0; i < OrderStatus.values().length; i++) {
            filterStatusComboBoxItems[i + 1] = OrderStatus.values()[i].toString();
        }
        // Construct components
        // - Labels
        orderIdLabel = new JLabel("Order ID: ");
        vendorIdLabel = new JLabel("Vendor ID: ");
        itemsLabel = new JLabel("Items:");
        totalLabel = new JLabel("Total: ");
        timestampLabel = new JLabel("Timestamp: ");
        deliveryOptionsLabel = new JLabel("Delivery Option: ");
        statusLabel = new JLabel("Status:");
        deliveryCostLabel = new JLabel("Delivery Cost: ");
        filterStatusLabel = new JLabel("Filter Status: ");
        searchLabel = new JLabel("Search:");
        // - Combo Box
        filterStatusComboBox = new JComboBox<>(filterStatusComboBoxItems);
        // - Text Field
        searchOrdersField = new JTextField(5);
        // - Button
        reviewButton = new JButton("Review");
        cancelOrderButton = new JButton("Cancel Order");
        reOrderButton = new JButton("Re Order");
        reOrderButton.setEnabled(false);
        reviewButton.setEnabled(false);
        cancelOrderButton.setEnabled(false);
        findOrderButton = new JButton("Find");
        // - Table Model
        orderTableModel = new DefaultTableModel();
        orderTableModel.addColumn("Order");
        orderTableModel.addColumn("Vendor");
        orderTableModel.addColumn("Items");
        orderTableModel.addColumn("Total");
        orderTableModel.addColumn("Time");
        orderTableModel.addColumn("Delivery Options");
        orderTableModel.addColumn("Status");
        // - Table
        ordersTable = new JTable(orderTableModel);
        // -- Select by row on the table
        ordersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = ordersTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Order orderDetails = findOrder((int) ordersTable.getValueAt(selectedRow, 0));
                        if (orderDetails.getStatus() == OrderStatus.DELIVERED
                                || orderDetails.getStatus() == OrderStatus.ACCEPTED) {
                            reOrderButton.setEnabled(true);
                            reviewButton.setEnabled(true);
                        } else {
                            reOrderButton.setEnabled(false);
                            reviewButton.setEnabled(false);
                        }
                        if (orderDetails.getStatus() == OrderStatus.PENDING) {
                            cancelOrderButton.setEnabled(true);
                        } else {
                            cancelOrderButton.setEnabled(false);
                        }

                        orderIdLabel.setText("Order ID: " + orderDetails.getOrderId());
                        vendorIdLabel.setText("Vendor ID:   " + orderDetails.getVendorId());
                        totalLabel.setText("Total:  " + orderDetails.getTotal());
                        timestampLabel.setText("Timestamp:  " + orderDetails.getTimestamp());
                        statusLabel.setText("Status:" + orderDetails.getStatus());
                        deliveryOptionsLabel.setText("Delivery Option: " + orderDetails.getDeliveryOption());
                        deliveryCostLabel.setText("Delivery Cost: " + orderDetails.getDeliveryCost());
                        itemsLabel.setText("Items: " + orderDetails.getItems().toString());
                    }
                }
            }
        });
        // - ScrollPane
        JScrollPane orderScrollPane = new JScrollPane(ordersTable);
        // Add Components
        panel.add(orderIdLabel);
        panel.add(vendorIdLabel);
        panel.add(itemsLabel);
        panel.add(totalLabel);
        panel.add(timestampLabel);
        panel.add(deliveryOptionsLabel);
        panel.add(statusLabel);
        panel.add(deliveryCostLabel);
        panel.add(filterStatusComboBox);
        panel.add(filterStatusLabel);
        panel.add(searchLabel);
        panel.add(searchOrdersField);
        panel.add(findOrderButton);
        panel.add(orderScrollPane);
        panel.add(reviewButton);
        panel.add(cancelOrderButton);
        panel.add(reOrderButton);
        // Adjusted bounds
        orderIdLabel.setBounds(20, 40, 200, 30);
        vendorIdLabel.setBounds(20, 80, 200, 30);
        itemsLabel.setBounds(20, 120, 200, 120);
        totalLabel.setBounds(360, 40, 200, 30);
        timestampLabel.setBounds(360, 80, 200, 30);
        deliveryOptionsLabel.setBounds(360, 120, 200, 30);
        statusLabel.setBounds(360, 160, 200, 30);
        deliveryCostLabel.setBounds(360, 200, 200, 30);
        filterStatusComboBox.setBounds(110, 285, 200, 30);
        filterStatusLabel.setBounds(20, 285, 90, 30);
        searchLabel.setBounds(360, 285, 60, 30);
        searchOrdersField.setBounds(420, 285, 150, 30);
        findOrderButton.setBounds(600, 285, 120, 30);
        orderScrollPane.setBounds(20, 340, 700, 140);
        reviewButton.setBounds(20, 500, 100, 30);
        cancelOrderButton.setBounds(600, 500, 120, 30);
        reOrderButton.setBounds(135, 500, 120, 30);
        panel.setBounds(195, 35, 745, 560);
        // -- Action Listeners
        cancelOrderButton.addActionListener(e -> cancelOrder());
        findOrderButton.addActionListener(e -> updateOrderHistoryTable());
        reviewButton.addActionListener(e -> reviewOrder());
        reOrderButton.addActionListener(e -> reOrderButton());
        return panel;
    }

    private void reOrderButton() {
        // Check if a table row is selected
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            // No row selected, show a message or handle accordingly
            JOptionPane.showMessageDialog(this, "Please select a row to reorder.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Retrieve the Order from the selected row
        Order selectedOrder = orders.get(selectedRow);
        // Show pop-up to select delivery option
        DeliveryOption selectedDeliveryOption = showDeliveryOptions();
        if (selectedDeliveryOption != null) {
            Order newOrder = new Order(orders.size(), customerUser.getUsername(),
                    selectedOrder.getVendorId(), null, selectedOrder.getItemsList(), selectedDeliveryOption);
            // Display order details
            String orderDetails = "Order Details:\n\n" +
                    "Items: " + newOrder.getItems() + "\n" +
                    "Total: $" + newOrder.getTotal() + "\n" +
                    "Delivery Option: " + newOrder.getDeliveryOption() + "\n" +
                    "Delivery Cost: $" + newOrder.getDeliveryCost() + "\n" +
                    "Total Cost with Delivery: $" + (newOrder.getTotal() + newOrder.getDeliveryCost());
            // Show option dialog to ask user to proceed or cancel
            int choice = JOptionPane.showOptionDialog(
                    null,
                    orderDetails,
                    "Order Summary",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[] { "Proceed", "Cancel" },
                    "Proceed");

            if (choice == JOptionPane.YES_OPTION) {
                orders.add(newOrder);
                writeOrdersToFile();
                JOptionPane.showMessageDialog(this, "You have placed your reorder successfully!");
                updateOrderHistoryTable();
            }
        }
    }

    private void reviewOrder() {
        // Get the selected row from the table
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to review.");
            return;
        }

        // Check if the item is in the selected items list
        Order orderToReview = findOrder((int) ordersTable.getValueAt(selectedRow, 0));
        if (orderToReview != null) {
            showReviewDialog(orderToReview);
        } else {
            JOptionPane.showMessageDialog(this, "Selected order not found.");
        }
    }

    private void showReviewDialog(Order orderToReview) {
        // Use JOptionPane to get user's review input
        String comment = JOptionPane.showInputDialog(this, "Enter your review comment:");

        if (comment != null) {
            try {
                int rating = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter your rating (1-5):"));
                if (rating >= 1 && rating <= 5) {
                    OrderReview newReview = new OrderReview(orderToReview.getOrderId(),
                            orderToReview.getCustomerId(), rating, comment);
                    if (checkExistReview(orderToReview)) {
                        // A review from this customer for this order already exists
                        int choice = JOptionPane.showOptionDialog(this,
                                "You have already submitted a review for this order. What do you want to do?",
                                "Review Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                new Object[] { "View Review", "Update Review" }, "View Review");

                        if (choice == JOptionPane.YES_OPTION) {
                            // View existing review
                            viewExistingReview(orderToReview);
                        } else if (choice == JOptionPane.NO_OPTION) {
                            // Update the existing review
                            orderToReview.updateReview(newReview);
                            JOptionPane.showMessageDialog(this, "Review updated successfully!");
                        }
                        return;
                    } else {
                        orderToReview.addReview(newReview);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid rating between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric rating.");
            }
        }
    }

    private boolean checkExistReview(Order selectedOrderToReview) {
        List<OrderReview> reviews = selectedOrderToReview.getReviews();
        for (OrderReview review : reviews) {
            if (review.getCustomerId().equals(customerUser.getUsername())
                    && review.getOrderId() == selectedOrderToReview.getOrderId()) {
                return true;
            }
        }
        return false;
    }

    private void viewExistingReview(Order selectedOrderToReview) {
        List<OrderReview> reviews = selectedOrderToReview.getReviews();
        for (OrderReview review : reviews) {
            if (review.getCustomerId().equals(customerUser.getUsername())
                    && review.getOrderId() == selectedOrderToReview.getOrderId()) {
                // Display the existing review
                JOptionPane.showMessageDialog(this, "Existing Review:\nRating: " + review.getRating()
                        + "\nComment: " + review.getComment());
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Review not found.");
    }

    private void cancelOrder() {
        // Get the selected row from the table
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.");
            return;
        }
        // Check if the item is in the selected items list
        Order orderToCancel = findOrder((int) ordersTable.getValueAt(selectedRow, 0));
        if (orderToCancel != null) {
            orders.remove(orderToCancel);
            JOptionPane.showMessageDialog(this, "Order Canceled Successfully!");
            // Update the table with the new data
            updateOrderHistoryTable();
            writeOrdersToFile();
        } else {
            JOptionPane.showMessageDialog(this, "Selected order not found.");
        }
    }

    private JPanel createTransHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Transactions History"));
        panel.setLayout(null);
        // Construct components
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

        // Add components to the panel
        panel.add(receiptScrollPane);
        panel.add(receiptTableScrollPane);

        // Set component bounds
        receiptScrollPane.setBounds(30, 60, 675, 150);
        receiptTableScrollPane.setBounds(30, 220, 675, 330);
        panel.setBounds(195, 35, 745, 560);

        // Set ActionListeners
        updateTransactionTable();
        return panel;
    }

    private void updateTransactionTable() {
        // Clear the existing table data
        transactionTableModel.setRowCount(0);

        // Read receipt data from the file and populate the table
        ArrayList<TransactionReceipt> receipts = readReceiptsFromFile();
        for (TransactionReceipt receipt : receipts) {
            if (receipt.getUsername().equalsIgnoreCase(customerUser.getUsername())) {
                transactionTableModel.addRow(new Object[] {
                        receipt.getUsername(),
                        receipt.getName(),
                        receipt.getAmount(),
                        receipt.getCredit(),
                        receipt.getTimestamp()
                });
            }
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

    private Order findOrder(int orderId) {
        System.out.println("--> ORDER ID IS ---> " + orderId);
        for (Order order : orders) {
            if (order != null && order.getOrderId() == orderId
                    && order.getCustomerId().equalsIgnoreCase(customerUser.getUsername())) {
                return order;
            }
        }
        return null;
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

    private void readFoodItem() {
        // Clear the existing table data
        menuTableModel.setRowCount(0);

        String filter = categoriesComboBox.getSelectedItem().toString().toLowerCase();
        String searchKeyword = searchField.getText().toLowerCase();

        for (FoodItem item : foodItems) {
            boolean matchesFilter = filter.equals("all")
                    || item.getCategory().toLowerCase().equals(filter);
            boolean matchesSearch = item.getCode().toLowerCase().contains(searchKeyword)
                    || item.getName().toLowerCase().contains(searchKeyword);

            if (matchesFilter && matchesSearch) {

                if (selectedItems.isEmpty()) {
                    menuTableModel
                            .addRow(new Object[] { item.getCode(), item.getName(), item.getVendor(), item.getPrice(),
                                    item.getCategory() });
                } else if (item.getVendor()
                        .equalsIgnoreCase(findFoodItem(selectedItems.get(0).getItemId()).getVendor())) {
                    menuTableModel
                            .addRow(new Object[] { item.getCode(), item.getName(), item.getVendor(), item.getPrice(),
                                    item.getCategory() });
                }
            }
        }
    }

}
