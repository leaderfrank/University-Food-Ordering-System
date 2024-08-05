import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

enum DeliveryOption {
    OPT_IN,
    TAKEAWAY,
    DELIVERY
}

enum OrderStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    UNDER_DELIVERY,
    DELIVERED
}

public class Order {
    private int orderId;
    private String customerId;
    private String vendorId;
    private List<Item> items;
    private double total;
    private String timestamp;
    private DeliveryOption deliveryOption;
    private OrderStatus status;
    private double deliveryCost;
    private List<OrderReview> reviews;

    public Order(int orderId, String customerId, String vendorId, String timestamp, List<Item> items,
            DeliveryOption deliveryOption) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.items = items;
        this.total = calculateTotal(items);
        this.timestamp = (timestamp != null) ? timestamp : getCurrentTimestamp();
        this.deliveryOption = deliveryOption;
        this.status = OrderStatus.PENDING;
        this.deliveryCost = calculateDeliveryCost(deliveryOption);
        this.reviews = new ArrayList<>();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getItems() {
        return items.toString();
    }

    public List<Item> getItemsList() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        this.total = calculateTotal(items);
    }

    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
        this.deliveryCost = calculateDeliveryCost(deliveryOption);
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    private double calculateTotal(List<Item> items) {
        return items.stream().mapToDouble(item -> item.getQuantity() * getItemPrice(item.getItemId())).sum();
    }

    private double getItemPrice(String itemId) {
        // You can implement logic to get the price of an item from a database or other
        // source
        return 10.0; // Replace this with actual logic
    }

    private double calculateDeliveryCost(DeliveryOption deliveryOption) {
        switch (deliveryOption) {
            case OPT_IN:
                return 0.0;
            case TAKEAWAY:
                return 0.0;
            case DELIVERY:
                return 10.0;
            default:
                return 0.0;
        }
    }

    public void acceptOrder() {
        this.status = OrderStatus.ACCEPTED;
        notifyCustomer();
    }

    public void rejectOrder() {
        this.status = OrderStatus.REJECTED;
        notifyCustomer();
    }

    public void deliverOrder() {
        this.status = OrderStatus.DELIVERED;
    }

    private void notifyCustomer() {
        System.out.println("Customer notified about order status: " + this.status);
    }

    public void addReview(OrderReview review) {
        reviews.add(review);
        writeReviewsToFile();
        JOptionPane.showMessageDialog(null, "Review added successfully!");
    }

    public List<OrderReview> getReviews() {
        return reviews;
    }

    private void writeReviewsToFile() {
        try (FileWriter writer = new FileWriter("C:\\UniversityFoodOrderingSystem\\src\\db\\reviews.txt",
                true)) {
            for (OrderReview review : reviews) {
                writer.write(review.toFileString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadReviewsFromFile() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("C:\\UniversityFoodOrderingSystem\\src\\db\\reviews.txt"))) {
            String line;
            // Skip the first line (header)
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                OrderReview review = OrderReview.fromFileString(line);
                reviews.add(review);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateReview(OrderReview newReview) {
        for (OrderReview review : reviews) {
            if (review.getCustomerId().equals(newReview.getCustomerId())
                    && review.getOrderId() == newReview.getOrderId()) {
                review.setRating(newReview.getRating());
                review.setComment(newReview.getComment());
                writeReviewsToFile();
                return;
            }
        }
        // If the review is not found, add the new review
        addReview(newReview);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(orderId).append(",");
        builder.append(customerId).append(",");
        builder.append(vendorId).append(",");
        builder.append(itemsToString()).append(",");
        builder.append(total).append(",");
        builder.append(timestamp).append(",");
        builder.append(deliveryOption).append(",");
        builder.append(status).append(",");
        builder.append(deliveryCost);

        return builder.toString();
    }

    private String itemsToString() {
        StringBuilder itemsBuilder = new StringBuilder();
        for (Item item : items) {
            itemsBuilder.append(item.getItemId()).append(":").append(item.getQuantity()).append("|");
        }
        // Remove the trailing comma
        return itemsBuilder.substring(0, itemsBuilder.length() - 1);
    }

}
