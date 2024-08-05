public class OrderReview {
    private int orderId;
    private String customerId;
    private int rating;
    private String comment;

    public OrderReview(int orderId, String customerId, int rating, String comment) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String toFileString() {
        return orderId + "," + customerId + "," + rating + "," + comment;
    }

    public static OrderReview fromFileString(String line) {
        String[] parts = line.split(",");
        int orderId = Integer.parseInt(parts[0]);
        String customerId = parts[1];
        int rating = Integer.parseInt(parts[2]);
        String comment = parts[3];
        return new OrderReview(orderId, customerId, rating, comment);
    }

    public void setRating(int rating2) {
        rating = rating2;
    }

    public void setComment(String comment2) {
        comment = comment2;
    }
}