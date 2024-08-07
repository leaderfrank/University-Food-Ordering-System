class Notification {
    private String type, from, to, status, timestamp, message;
    private int isRead;

    public Notification(String type, String from, String to, String status, String timestamp, String message) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.status = status;
        this.timestamp = timestamp;
        setMessage(message);
    }

    public String getType() { return type; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    public String getMessage() { return revertMessageFormatting(message); }
    public int getIsRead() { return isRead; }

    public void setStatus(String status) { this.status = status; }
    public void setIsRead(int isRead) { this.isRead = isRead; }
    public void setMessage(String message) { this.message = formatMessage(message); }

    private String formatMessage(String message) {
        return message.replace("\n", "\\n").replace("\t", "\\t").replace(",", "_x32_");
    }

    private String revertMessageFormatting(String message) {
        return message.replace("\\n", "\n").replace("\\t", "\t").replace("_x32_", ",");
    }

    @Override
    public String toString() {
        return String.join(",", type, from, to, status, timestamp, message, String.valueOf(isRead));
    }
}
