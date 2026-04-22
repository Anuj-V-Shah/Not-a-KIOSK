import java.util.LinkedHashMap;
import java.util.Map;

public final class SalesLedger implements Summarizable {
    private int completedOrders = 0;
    private int revenueCents = 0;
    private final Map<String, Integer> itemSales = new LinkedHashMap<>();

    public void recordCompletedOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("order is required");
        completedOrders++;
        revenueCents += order.getTotalCents();
        for (Map.Entry<MenuItem, Integer> entry : order.getItemsView().entrySet()) {
            String itemName = entry.getKey().getName();
            int qty = entry.getValue();
            itemSales.merge(itemName, qty, Integer::sum);
        }
    }

    public int getCompletedOrders() {
        return completedOrders;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public String getFormattedRevenue() {
        return String.format("$%.2f", revenueCents / 100.0);
    }

    public void reset() {
        completedOrders = 0;
        revenueCents = 0;
        itemSales.clear();
    }

    @Override
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sales Summary\n\n");
        sb.append("Completed orders: ").append(completedOrders).append("\n");
        sb.append("Revenue: ").append(getFormattedRevenue()).append("\n\n");
        if (itemSales.isEmpty()) {
            sb.append("(No sales yet)\n");
        } else {
            sb.append("Items sold:\n");
            for (Map.Entry<String, Integer> entry : itemSales.entrySet()) {
                sb.append("- ").append(entry.getKey()).append("  x").append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }
}
