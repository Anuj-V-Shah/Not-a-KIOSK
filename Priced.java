public interface Priced {
    int getPriceCents();

    default String getFormattedPrice() {
        return String.format("$%.2f", getPriceCents() / 100.0);
    }
}
