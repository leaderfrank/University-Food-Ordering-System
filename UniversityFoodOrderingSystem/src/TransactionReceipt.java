public class TransactionReceipt {
    private String username;
    private String name;
    private double amount;
    private double credit;
    private String timestamp;

    public TransactionReceipt(String username, String name, double amount, double credit, String timestamp) {
        this.username = username;
        this.name = name;
        this.amount = amount;
        this.credit = credit;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public double getCredit() {
        return credit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return username + "," + name + "," + amount + "," + credit + "," + timestamp;
    }
}
