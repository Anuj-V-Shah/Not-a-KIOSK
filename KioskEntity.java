public abstract class KioskEntity implements Displayable {
    private String code;
    private String name;

    public KioskEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayText() {
        return code + " - " + name;
    }
}