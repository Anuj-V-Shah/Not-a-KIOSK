import java.util.ArrayList;

public class Department extends KioskEntity implements Reportable {
    private ArrayList<Product> products = new ArrayList<Product>();

    public Department(String departmentCode, String departmentName) {
        super(departmentCode, departmentName);
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getDepartmentCode() {
        return getCode();
    }

    public String getDepartmentName() {
        return getName();
    }

    public String getReportText() {
        return getDisplayText() + " has " + products.size() + " products.";
    }
}