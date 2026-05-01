import java.util.ArrayList;

public class Cart implements Reportable {
    private ArrayList<CartItem> items = new ArrayList<CartItem>();
    private final double TAX_RATE = 0.08875;

    public void addItem(Product product, int qty) {
        CartItem found = null;

        for (int i = 0; i < items.size(); i++) {
            CartItem current = items.get(i);

            if (current.getProduct().getProductCode().equals(product.getProductCode())) {
                found = current;
            }
        }

        if (found == null) {
            items.add(new CartItem(product, qty));
        }
        else {
            found.addQuantity(qty);
        }
    }

    public double getSubtotal() {
        double subtotal = 0;

        for (int i = 0; i < items.size(); i++) {
            subtotal = subtotal + items.get(i).getLineTotal();
        }

        return roundMoney(subtotal);
    }

    public double getTax() {
        return roundMoney(getSubtotal() * TAX_RATE);
    }

    public double getTotal() {
        return roundMoney(getSubtotal() + getTax());
    }

    public String getReceipt() {
        String receipt = "";

        if (items.size() == 0) {
            receipt += "Cart is empty.\n";
        }
        else {
            for (int i = 0; i < items.size(); i++) {
                receipt += items.get(i).getReportText() + "\n";
            }
        }

        return receipt;
    }

    public String getReportText() {
        return getReceipt();
    }

    public void completeOrder() {
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            item.getProduct().reduceInventory(item.getQuantity());
        }
    }

    private double roundMoney(double value) {
        int cents = (int)(value * 100 + 0.5);
        return cents / 100.0;
    }
}