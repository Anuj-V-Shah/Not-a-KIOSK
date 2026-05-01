public class Product extends KioskEntity implements Sellable, Reportable {
    private String description;
    private double price;
    private int quantityInStock;
    private boolean available;

    public Product(String productCode, String name, String description, double price, int quantityInStock) {
        super(productCode, name);
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.available = true;
    }

    public String getProductCode() {
        return getCode();
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public boolean isAvailable() {
        return available;
    }

    public void toggleAvailable() {
        available = !available;
    }

    public String getInventoryText() {
        if (!available) {
            return "N/A";
        }
        return "" + quantityInStock;
    }

    public void validateOrderQuantity(int qty) throws InvalidQuantityException {
        if (!available) {
            throw new InvalidQuantityException("This product is marked N/A and cannot be sold.");
        }

        if (qty <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than 0.");
        }

        if (qty > quantityInStock) {
            throw new InvalidQuantityException("Not enough inventory for this order.");
        }
    }

    public void reduceInventory(int qty) {
        quantityInStock = quantityInStock - qty;
    }

    public String getReportText() {
        return getDisplayText() + ", Stock: " + getInventoryText() + ", Price: $" + price;
    }
}