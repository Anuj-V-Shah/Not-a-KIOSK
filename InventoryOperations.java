import java.util.List;

public interface InventoryOperations {
    List<Inventory.Entry> getEntriesView();
    int getStock(String itemName);
    MenuItem getMenuItem(String itemName);
    void setPriceCents(String itemName, int newPriceCents);
    void adjustStock(String itemName, int delta);
}
