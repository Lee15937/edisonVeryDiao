package entity;

public class Medicine {
    private String medicineID;
    private String name;
    private double price;
    private int stock;

    public Medicine(String id, String name, double price, int stock) {
        this.medicineID = id;
        setName(name); // Use setter for validation
        this.price = price;
        this.stock = stock;
    }

    // Getters & Setters
    public String getMedicineID() { return medicineID; }

    public String getName() { return name; }

    public void setName(String name) {
        if (!name.matches("[A-Za-z0-9 ]+")) {
            throw new IllegalArgumentException("Medicine name can only contain letters, numbers, and spaces.");
        }
        this.name = name.trim();
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }

    public void reduceStock(int quantity) {
        if (stock >= quantity) stock -= quantity;
        else throw new RuntimeException("Not enough stock!");
    }

    public void addStock(int quantity) {
        stock += quantity;
    }

    // Save format: ID#Name#Stock#Price
    @Override
    public String toString() {
        return medicineID + "#" + name + "#" + stock + "#" + price;
    }

    // Parse from line in file
    public static Medicine fromString(String line) {
        String[] parts = line.split("#");
        if (parts.length != 4) return null;
        String id = parts[0].trim();
        String name = parts[1].trim();
        int stock = Integer.parseInt(parts[2].trim());
        double price = Double.parseDouble(parts[3].trim());
        return new Medicine(id, name, price, stock);
    }
}
