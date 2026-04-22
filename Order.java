import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Order implements OrderOperations, Summarizable {
    private final Map<MenuItem, Integer> items = new LinkedHashMap<>();
    private String specialInstructions = "";

    @Override
    public void addItem(MenuItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("item is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be >= 1");
        }
        items.merge(item, quantity, Integer::sum);
    }

    public void setSpecialInstructions(String instructions) {
        specialInstructions = instructions == null ? "" : instructions.trim();
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public Map<MenuItem, Integer> getItemsView() {
        return Collections.unmodifiableMap(items);
    }

    @Override
    public void clear() {
        items.clear();
        specialInstructions = "";
    }

    @Override
    public int getTotalCents() {
        int total = 0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPriceCents() * entry.getValue();
        }
        return total;
    }

    public String getFormattedTotal() {
        return String.format("$%.2f", getTotalCents() / 100.0);
    }

    @Override
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order Summary\n\n");
        if (items.isEmpty()) {
            sb.append("(No items yet)\n");
        } else {
            for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
                MenuItem item = entry.getKey();
                int qty = entry.getValue();
                int lineTotal = item.getPriceCents() * qty;
                sb.append("- ")
                        .append(item.getName())
                        .append("  x")
                        .append(qty)
                        .append("  ")
                        .append(String.format("$%.2f", lineTotal / 100.0))
                        .append("\n");
            }
        }
        sb.append("\nTotal: ").append(getFormattedTotal()).append("\n");
        if (!specialInstructions.isBlank()) {
            sb.append("\nSpecial instructions:\n").append(specialInstructions).append("\n");
        }
        return sb.toString();
    }
}
