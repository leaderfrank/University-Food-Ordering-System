import java.util.ArrayList;
import java.util.List;

public class Runner {
    private List<Task> tasks;
    private double earnings;

    public Runner() {
        this.tasks = new ArrayList<>();
        this.earnings = 0.0;
    }

    public void acceptTask() {
        if (!tasks.isEmpty()) {
            Task acceptedTask = tasks.remove(0);
            // Assuming each accepted task earns the runner a fixed amount
            earnings += acceptedTask.getEarning();
            acceptedTask.setTaskStatus(TaskStatus.ACCEPTED);
            // Notify customer about the order status (you may implement this)
            notifyCustomer(acceptedTask);
        }
    }

    public void declineTask() {
        if (!tasks.isEmpty()) {
            Task declinedTask = tasks.remove(0);
            // Allocate the next available runner
            allocateNextRunner(declinedTask);
            declinedTask.setTaskStatus(TaskStatus.DECLINED);
            // Notify customer about the order status (you may implement this)
            notifyCustomer(declinedTask);
        }
    }

    private void allocateNextRunner(Task declinedTask) {
        // Logic to allocate the next available runner
        // This could involve maintaining a list of available runners and assigning the
        // task to one of them.
        // You may need to implement this based on your application's design.
    }

    public double getEarnings() {
        return earnings;
    }

    public String getTasksAsString() {
        StringBuilder tasksString = new StringBuilder();
        for (Task task : tasks) {
            tasksString.append(task.toString()).append("\n");
        }
        return tasksString.toString();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    private void notifyCustomer(Task task) {
        System.out.println("Notifying customer about task status: " + task.getTaskStatus());

        // Simulate sending a notification (replace this with actual notification logic)
        switch (task.getTaskStatus()) {
            case ACCEPTED:
                System.out.println("Notification sent: Your order has been accepted!");
                break;
            case DECLINED:
                System.out.println("Notification sent: Your order has been declined. A new runner will be assigned.");
                break;
            case COMPLETED:
                System.out.println("Notification sent: Your order has been delivered.");
                break;
            default:
                System.out.println("Unknown task status.");
        }
    }
}
