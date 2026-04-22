import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Order implements OrderOperations, Summarizable {
    // Each "add to order" action becomes a line-group keyed by item + instruction text.
    // This lets the UI show instructions under the item in the cart summary.
    private final Map<MenuItem, Map<String, Integer>> itemsByInstruction = new LinkedHashMap<>();

    @Override
    public void addItem(MenuItem item, int quantity) {
        addItem(item, quantity, "");
    }

    public void addItem(MenuItem item, int quantity, String instructions) {
        if (item == null) throw new IllegalArgumentException("item is required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be >= 1");
        String key = instructions == null ? "" : instructions.trim();
        Map<String, Integer> perInstruction = itemsByInstruction.computeIfAbsent(item, k -> new LinkedHashMap<>());
        perInstruction.merge(key, quantity, Integer::sum);
    }

    public Map<MenuItem, Integer> getItemsView() {
        Map<MenuItem, Integer> aggregated = new LinkedHashMap<>();
        for (Map.Entry<MenuItem, Map<String, Integer>> entry : itemsByInstruction.entrySet()) {
            int totalQty = 0;
            for (int qty : entry.getValue().values()) {
                totalQty += qty;
            }
            aggregated.put(entry.getKey(), totalQty);
        }
        return Collections.unmodifiableMap(aggregated);
    }

    @Override
    public void clear() {
        itemsByInstruction.clear();
    }

    @Override
    public int getTotalCents() {
        int total = 0;
        for (Map.Entry<MenuItem, Map<String, Integer>> entry : itemsByInstruction.entrySet()) {
            MenuItem item = entry.getKey();
            for (int qty : entry.getValue().values()) {
                total += item.getPriceCents() * qty;
            }
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
        if (itemsByInstruction.isEmpty()) {
            sb.append("(No items yet)\n");
        } else {
            for (Map.Entry<MenuItem, Map<String, Integer>> entry : itemsByInstruction.entrySet()) {
                MenuItem item = entry.getKey();
                for (Map.Entry<String, Integer> group : entry.getValue().entrySet()) {
                    String instructions = group.getKey();
                    int qty = group.getValue();
                    int lineTotal = item.getPriceCents() * qty;
                    sb.append("- ")
                            .append(item.getName())
                            .append("  x")
                            .append(qty)
                            .append("  ")
                            .append(String.format("$%.2f", lineTotal / 100.0))
                            .append("\n");
                    if (!instructions.isBlank()) {
                        sb.append("  instructions: ").append(instructions).append("\n");
                    }
                }
            }
        }
        sb.append("\nTotal: ").append(getFormattedTotal()).append("\n");
        return sb.toString();
    }
}
