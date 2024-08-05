class Notification {
    private String type;
    private String from;
    private String to;
    private String status;
    private String timestamp;
    private String message;
    private int isRead;

    public Notification(String type, String from, String to, String status, String timestamp, String message) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.status = status;
        this.timestamp = timestamp;
        this.message = message.replace("\n", "\\n").replace("\t", "\\t").replace(",",
                "_x32_");
        this.isRead = 0;
        System.out.println(message);
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getIsRead() {
        return isRead;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message.replace("\\n", "\n").replace("\\t", "\t").replace("_x32_",
                ",");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setMessage(String msg) {
        this.message = msg.replace("\n", "\\n").replace("\t", "\\t").replace(",",
                "_x32_");
    }

    @Override
    public String toString() {
        return type + "," + from + "," + to + "," + status + "," + timestamp + "," + message + "," + isRead;
    }
}