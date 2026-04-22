import java.util.Objects;

public final class MenuItem implements Priced {
    private final String name;
    private final int priceCents;

    public MenuItem(String name, int priceCents) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("priceCents must be >= 0");
        }
        this.name = name.trim();
        this.priceCents = priceCents;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getPriceCents() {
        return priceCents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        MenuItem menuItem = (MenuItem) o;
        return priceCents == menuItem.priceCents && name.equals(menuItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, priceCents);
    }
}
