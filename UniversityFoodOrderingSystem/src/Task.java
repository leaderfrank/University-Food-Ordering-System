enum TaskStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    COMPLETED
}

public class Task {
    private int orderId;
    private String customerId;
    private String vendorId;
    private TaskStatus taskStatus;
    private double earning;
    private String runnerId;

    public Task(int orderId, String customerId, String vendorId, TaskStatus taskStatus, double earning) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.taskStatus = taskStatus;
        this.earning = earning;
        this.runnerId = "null";
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

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public String getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(String runnerId) {
        this.runnerId = runnerId;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public double getEarning() {
        return earning;
    }

    @Override
    public String toString() {
        return "Task{" +
                "orderId=" + orderId +
                ", customerId='" + customerId + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", taskStatus=" + taskStatus +
                ", earning=" + earning +
                '}';
    }
}
