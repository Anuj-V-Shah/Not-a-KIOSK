public interface OrderOperations {
    void addItem(MenuItem item, int quantity);
    void clear();
    int getTotalCents();
}
