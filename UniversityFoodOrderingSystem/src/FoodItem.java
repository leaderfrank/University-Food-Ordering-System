public class FoodItem {
    private String code;
    private String name;
    private double price;
    private String vendor;
    private String category;
    

    public FoodItem(String code, String name, double price, String vendor, String category) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.vendor = vendor;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getVendor() {
        return vendor;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return code + "," + name + "," + price + "," + vendor + "," + category;
    }
}
