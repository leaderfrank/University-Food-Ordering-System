class User {
    private String username;
    private String name;
    private String password;
    private String type;
    private double credit;

    public User(String username, String name, String password, String type, double credit) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.type = type;
        this.credit = credit;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public double getCredit() {
        return credit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return username + "," + name + "," + password + "," + type + "," + credit;
    }
}