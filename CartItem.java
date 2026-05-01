public class CartItem implements Reportable {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        quantity = quantity + amount;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }

    public String getReportText() {
        return product.getName() + "  Qty x" + quantity + "  $" + getLineTotal();
    }
}