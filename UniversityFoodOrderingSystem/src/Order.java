import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

enum DeliveryOption { OPT_IN, TAKEAWAY, DELIVERY }
enum OrderStatus { PENDING, ACCEPTED, REJECTED, UNDER_DELIVERY, DELIVERED }

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
    private List<OrderReview> reviews = new ArrayList<>();

    public Order(int orderId, String customerId, String vendorId, String timestamp, List<Item> items, DeliveryOption deliveryOption) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.items = items;
        this.total = calculateTotal();
        this.timestamp = timestamp != null ? timestamp : getCurrentTimestamp();
        this.deliveryOption = deliveryOption;
        this.deliveryCost = calculateDeliveryCost();
        this.status = OrderStatus.PENDING;
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private double calculateTotal() {
        return items.stream().mapToDouble(item -> item.getQuantity() * getItemPrice(item.getItemId())).sum();
    }

    private double getItemPrice(String itemId) {
        return 10.0; // Placeholder for actual price logic
    }

    private double calculateDeliveryCost() {
        return deliveryOption == DeliveryOption.DELIVERY ? 10.0 : 0.0;
    }

    public void acceptOrder() { updateStatus(OrderStatus.ACCEPTED); }
    public void rejectOrder() { updateStatus(OrderStatus.REJECTED); }
    public void deliverOrder() { updateStatus(OrderStatus.DELIVERED); }

    private void updateStatus(OrderStatus newStatus) {
        status = newStatus;
        notifyCustomer();
    }

    private void notifyCustomer() {
        System.out.println("Customer notified about order status: " + status);
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
        try (FileWriter writer = new FileWriter("reviews.txt", true)) {
            for (OrderReview review : reviews) {
                writer.write(review.toFileString() + "\n");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadReviewsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("reviews.txt"))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                reviews.add(OrderReview.fromFileString(line));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void updateReview(OrderReview newReview) {
        for (OrderReview review : reviews) {
            if (review.getCustomerId().equals(newReview.getCustomerId()) && review.getOrderId() == newReview.getOrderId()) {
                review.setRating(newReview.getRating());
                review.setComment(newReview.getComment());
                writeReviewsToFile();
                return;
            }
        }
        addReview(newReview);
    }

    @Override
    public String toString() {
        return String.join(",",
            String.valueOf(orderId),
            customerId,
            vendorId,
            itemsToString(),
            String.valueOf(total),
            timestamp,
            deliveryOption.name(),
            status.name(),
            String.valueOf(deliveryCost)
        );
    }

    private String itemsToString() {
        return items.stream()
                .map(item -> item.getItemId() + ":" + item.getQuantity())
                .reduce((a, b) -> a + "|" + b)
                .orElse("");
    }
}
