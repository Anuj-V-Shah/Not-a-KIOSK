import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Inventory implements InventoryOperations, Summarizable {

    public static final class Entry {
        private final String name;
        private MenuItem menuItem;
        private int stock;

        private Entry(String name, MenuItem menuItem, int stock) {
            this.name = name;
            this.menuItem = menuItem;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getStock() {
            return stock;
        }
    }

    private final Map<String, Entry> entries = new LinkedHashMap<>();

    public void addItem(String name, int priceCents, int initialStock) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (priceCents < 0) throw new IllegalArgumentException("priceCents must be >= 0");
        if (initialStock < 0) throw new IllegalArgumentException("initialStock must be >= 0");
        String key = name.trim();
        if (entries.containsKey(key)) throw new IllegalArgumentException("item already exists: " + key);
        entries.put(key, new Entry(key, new MenuItem(key, priceCents), initialStock));
    }

    @Override
    public List<Entry> getEntriesView() {
        return Collections.unmodifiableList(new ArrayList<>(entries.values()));
    }

    @Override
    public int getStock(String itemName) {
        Entry entry = requireEntry(itemName);
        return entry.stock;
    }

    @Override
    public MenuItem getMenuItem(String itemName) {
        Entry entry = requireEntry(itemName);
        return entry.menuItem;
    }

    @Override
    public void setPriceCents(String itemName, int newPriceCents) {
        if (newPriceCents < 0) throw new IllegalArgumentException("newPriceCents must be >= 0");
        Entry entry = requireEntry(itemName);
        entry.menuItem = new MenuItem(entry.name, newPriceCents);
    }

    @Override
    public void adjustStock(String itemName, int delta) {
        Entry entry = requireEntry(itemName);
        int next = entry.stock + delta;
        if (next < 0) throw new IllegalArgumentException("not enough stock for " + entry.name);
        entry.stock = next;
    }

    private Entry requireEntry(String itemName) {
        if (itemName == null || itemName.isBlank()) throw new IllegalArgumentException("itemName is required");
        Entry entry = entries.get(itemName.trim());
        if (entry == null) throw new IllegalArgumentException("unknown item: " + itemName);
        return entry;
    }

    @Override
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inventory\n\n");
        for (Entry entry : entries.values()) {
            sb.append("- ")
                    .append(entry.name)
                    .append("  ")
                    .append(entry.menuItem.getFormattedPrice())
                    .append("  (stock: ")
                    .append(entry.stock)
                    .append(")\n");
        }
        return sb.toString();
    }
}
