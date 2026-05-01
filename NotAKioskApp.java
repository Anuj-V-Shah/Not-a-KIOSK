import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import java.util.ArrayList;

public class NotAKioskApp extends Application {

    private Stage mainStage;
    private ArrayList<Department> departments = new ArrayList<Department>();
    private Cart cart = new Cart();
    private ArrayList<String> feedbackRecords = new ArrayList<String>();

    private final String MANAGER_PASSWORD = "admin123";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        mainStage.setTitle("Not a Kiosk");
        loadSampleData();
        showWelcomeScreen();
        mainStage.show();
    }

    private void loadSampleData() {
        Department d1 = new Department("D1", "Fast Food");
        d1.addProduct(new Product("P1", "Crunchy Vegan Wrap", "Mild-spiced wrap with crunchy vegetables.", 8.99, 20));
        d1.addProduct(new Product("P2", "Seed-Oil-Free Fries", "Crispy fries prepared without seed oil.", 4.49, 35));
        d1.addProduct(new Product("P3", "Spicy Burger", "Hot burger with house sauce.", 10.99, 14));
        d1.addProduct(new Product("P4", "Cold Lemon Drink", "Fresh lemon drink.", 2.99, 50));

        Department d2 = new Department("D2", "Clothing");
        d2.addProduct(new Product("P5", "Vegan Leather Jacket", "Black vegan leather jacket.", 59.99, 6));
        d2.addProduct(new Product("P6", "Cotton Shirt", "Comfortable cotton shirt.", 19.99, 30));
        d2.addProduct(new Product("P7", "Winter Hoodie", "Warm hoodie for cold weather.", 29.99, 12));
        d2.addProduct(new Product("P8", "Canvas Bag", "Reusable daily bag.", 9.99, 40));

        Department d3 = new Department("D3", "Grocery");
        d3.addProduct(new Product("P9", "Family CocaCola Pack", "Family-sized drink pack.", 7.99, 22));
        d3.addProduct(new Product("P10", "Snickers Bar", "Chocolate bar.", 1.99, 203));
        d3.addProduct(new Product("P11", "Organic Rice", "Large rice bag.", 12.99, 11));
        d3.addProduct(new Product("P12", "Apple Pack", "Fresh apple pack.", 5.99, 0));

        departments.add(d1);
        departments.add(d2);
        departments.add(d3);
    }

    private void showWelcomeScreen() {
        VBox root = new VBox();
        root.setSpacing(15);

        Label title = new Label("Not a Kiosk");
        Label welcome = new Label("Welcome Msg.");

        Button guestLogin = new Button("GUEST LOGIN");
        Button managerLogin = new Button("MANAGER LOGIN");
        Button exit = new Button("EXIT");

        guestLogin.setOnAction(e -> showCustomerStartScreen());
        managerLogin.setOnAction(e -> showManagerLoginScreen());
        exit.setOnAction(e -> Platform.exit());

        root.getChildren().add(title);
        root.getChildren().add(welcome);
        root.getChildren().add(guestLogin);
        root.getChildren().add(managerLogin);
        root.getChildren().add(exit);

        setScene(root, 500, 420);
    }

    private void showCustomerStartScreen() {
        VBox root = new VBox();
        root.setSpacing(15);

        Label hello = new Label("Hello, Guest");
        Label question = new Label("Choose service type:");

        Button dineIn = new Button("DINE IN");
        Button takeOut = new Button("TAKE OUT");
        Button language = new Button("LANGUAGE");
        Button next = new Button("NEXT");
        Button back = new Button("BACK");

        dineIn.setOnAction(e -> showDepartmentSelection("Dine In"));
        takeOut.setOnAction(e -> showDepartmentSelection("Take Out"));
        language.setOnAction(e -> showMessageAndReturn("Language option can be expanded later.", "customerStart"));
        next.setOnAction(e -> showDepartmentSelection("Guest Session"));
        back.setOnAction(e -> showWelcomeScreen());

        root.getChildren().add(back);
        root.getChildren().add(hello);
        root.getChildren().add(question);
        root.getChildren().add(dineIn);
        root.getChildren().add(takeOut);
        root.getChildren().add(language);
        root.getChildren().add(next);

        setScene(root, 500, 420);
    }

    private void showDepartmentSelection(String sessionType) {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label("Department Selection - " + sessionType);
        Label instruction = new Label("Choose a department:");
        Button checkout = new Button("CHECKOUT");

        back.setOnAction(e -> showCustomerStartScreen());
        next.setOnAction(e -> showProductGrid(departments.get(0)));
        checkout.setOnAction(e -> showCheckoutPage());

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(instruction);

        for (int i = 0; i < departments.size(); i++) {
            Department currentDepartment = departments.get(i);
            Button deptButton = new Button(currentDepartment.getDepartmentCode() + " - " + currentDepartment.getDepartmentName());
            deptButton.setOnAction(e -> showProductGrid(currentDepartment));
            root.getChildren().add(deptButton);
        }

        root.getChildren().add(checkout);
        root.getChildren().add(next);

        setScene(root, 500, 420);
    }

    private void showProductGrid(Department department) {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label(department.getDepartmentCode() + " - " + department.getDepartmentName());
        Label tabs = new Label("Tabs: TAB 1 | TAB 2");
        Button checkout = new Button("CHECKOUT");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ArrayList<Product> products = department.getProducts();

        int row = 0;
        int col = 0;

        for (int i = 0; i < products.size(); i++) {
            Product currentProduct = products.get(i);
            Button productButton = new Button(currentProduct.getProductCode());
            productButton.setOnAction(e -> showProductDetailPage(department, currentProduct));
            grid.add(productButton, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        back.setOnAction(e -> showDepartmentSelection("Guest Session"));
        next.setOnAction(e -> showProductDetailPage(department, products.get(0)));
        checkout.setOnAction(e -> showCheckoutPage());

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(tabs);
        root.getChildren().add(grid);
        root.getChildren().add(checkout);
        root.getChildren().add(next);

        setScene(root, 550, 430);
    }

    private void showProductDetailPage(Department department, Product product) {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label title = new Label(product.getProductCode());
        Label name = new Label(product.getName());
        Label description = new Label(product.getDescription());
        Label price = new Label("Price: $" + product.getPrice());
        Label available = new Label("Available: " + product.getInventoryText());

        TextField qtyField = new TextField();
        qtyField.setText("1");

        Button addToCart = new Button("ADD TO CART");
        Button checkout = new Button("CHECKOUT");

        back.setOnAction(e -> showProductGrid(department));
        next.setOnAction(e -> showCheckoutPage());
        checkout.setOnAction(e -> showCheckoutPage());

        addToCart.setOnAction(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                product.validateOrderQuantity(qty);
                cart.addItem(product, qty);
                showMessageAndReturn(product.getName() + " added to cart. Quantity: " + qty, "productDetail");
            }
            catch (NumberFormatException ex) {
                showMessageAndReturn("Please enter a valid whole number for quantity.", "productDetail");
            }
            catch (InvalidQuantityException ex) {
                showMessageAndReturn(ex.getMessage(), "productDetail");
            }
        });

        root.getChildren().add(back);
        root.getChildren().add(title);
        root.getChildren().add(name);
        root.getChildren().add(description);
        root.getChildren().add(price);
        root.getChildren().add(available);
        root.getChildren().add(new Label("Quantity:"));
        root.getChildren().add(qtyField);
        root.getChildren().add(addToCart);
        root.getChildren().add(checkout);
        root.getChildren().add(next);

        setScene(root, 500, 470);
    }

    private void showCheckoutPage() {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label("CHECKOUT");
        TextArea cartSummary = new TextArea();
        cartSummary.setText(cart.getReceipt());

        Label subtotal = new Label("Subtotal: $" + cart.getSubtotal());
        Label tax = new Label("Tax: $" + cart.getTax());
        Label total = new Label("Total: $" + cart.getTotal());

        Button payNow = new Button("PAY NOW");

        back.setOnAction(e -> showDepartmentSelection("Guest Session"));
        next.setOnAction(e -> showReviewPage());
        payNow.setOnAction(e -> {
            cart.completeOrder();
            showReviewPage();
        });

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(cartSummary);
        root.getChildren().add(subtotal);
        root.getChildren().add(tax);
        root.getChildren().add(total);
        root.getChildren().add(payNow);
        root.getChildren().add(next);

        setScene(root, 550, 520);
    }

    private void showReviewPage() {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label("OPTIONAL REVIEW");
        Label stars = new Label("Choose stars:");

        HBox starBox = new HBox();
        starBox.setSpacing(6);

        TextField selectedStars = new TextField();
        selectedStars.setText("5");

        for (int i = 1; i <= 5; i++) {
            int starNumber = i;
            Button starButton = new Button(starNumber + " STAR");
            starButton.setOnAction(e -> selectedStars.setText("" + starNumber));
            starBox.getChildren().add(starButton);
        }

        Label feedback = new Label("Feedback tags:");
        HBox tagBox1 = new HBox();
        tagBox1.setSpacing(5);
        HBox tagBox2 = new HBox();
        tagBox2.setSpacing(5);

        TextField selectedTag = new TextField();
        selectedTag.setText("Smooth");

        Button smooth = new Button("SMOOTH");
        Button laggy = new Button("LAGGY");
        Button friendly = new Button("FRIENDLY");
        Button easy = new Button("EASY");
        Button difficult = new Button("DIFFICULT");
        Button clutter = new Button("CLUTTER");

        smooth.setOnAction(e -> selectedTag.setText("Smooth"));
        laggy.setOnAction(e -> selectedTag.setText("Laggy"));
        friendly.setOnAction(e -> selectedTag.setText("Friendly"));
        easy.setOnAction(e -> selectedTag.setText("Easy"));
        difficult.setOnAction(e -> selectedTag.setText("Difficult"));
        clutter.setOnAction(e -> selectedTag.setText("Clutter"));

        tagBox1.getChildren().add(smooth);
        tagBox1.getChildren().add(laggy);
        tagBox1.getChildren().add(friendly);
        tagBox2.getChildren().add(easy);
        tagBox2.getChildren().add(difficult);
        tagBox2.getChildren().add(clutter);

        Button submit = new Button("SUBMIT REVIEW");
        Button finish = new Button("FINISH");

        submit.setOnAction(e -> {
            String record = "Stars: " + selectedStars.getText() + ", Tag: " + selectedTag.getText();
            feedbackRecords.add(record);
            showMessageAndReturn("Review recorded: " + record, "review");
        });

        finish.setOnAction(e -> {
            cart = new Cart();
            showWelcomeScreen();
        });

        back.setOnAction(e -> showCheckoutPage());
        next.setOnAction(e -> showWelcomeScreen());

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(stars);
        root.getChildren().add(starBox);
        root.getChildren().add(new Label("Selected stars:"));
        root.getChildren().add(selectedStars);
        root.getChildren().add(feedback);
        root.getChildren().add(tagBox1);
        root.getChildren().add(tagBox2);
        root.getChildren().add(new Label("Selected tag:"));
        root.getChildren().add(selectedTag);
        root.getChildren().add(submit);
        root.getChildren().add(finish);
        root.getChildren().add(next);

        setScene(root, 600, 520);
    }

    private void showManagerLoginScreen() {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Label heading = new Label("MANAGER LOGIN");
        Label instruction = new Label("Enter manager password:");
        PasswordField passwordField = new PasswordField();
        Button login = new Button("LOGIN");

        back.setOnAction(e -> showWelcomeScreen());

        login.setOnAction(e -> {
            if (passwordField.getText().equals(MANAGER_PASSWORD)) {
                showManagerSessionScreen();
            }
            else {
                showMessageAndReturn("Incorrect password.", "managerLogin");
            }
        });

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(instruction);
        root.getChildren().add(passwordField);
        root.getChildren().add(login);

        setScene(root, 500, 360);
    }

    private void showManagerSessionScreen() {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label("MANAGER SESSION");

        Button edit = new Button("EDIT");
        Button editHome = new Button("EDIT HOME");
        Button editProduct = new Button("EDIT PROD.");
        Button reports = new Button("REPORTS");
        Button generateSummary = new Button("GENERATE SUMMARY");
        Button inventory = new Button("INVENTORY");
        Button moreFunctions = new Button("MORE FUNCTIONS");

        back.setOnAction(e -> showWelcomeScreen());
        next.setOnAction(e -> showMoreFunctionsScreen());
        edit.setOnAction(e -> showMessageAndReturn("Editor mode enabled.", "manager"));
        editHome.setOnAction(e -> showMessageAndReturn("Edit Home screen can be expanded next.", "manager"));
        editProduct.setOnAction(e -> showMessageAndReturn("Edit Product screen can be expanded next.", "manager"));
        reports.setOnAction(e -> showMessageAndReturn("Report options opened.", "manager"));
        generateSummary.setOnAction(e -> showReportSummary());
        inventory.setOnAction(e -> showInventoryScreen());
        moreFunctions.setOnAction(e -> showMoreFunctionsScreen());

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(edit);
        root.getChildren().add(editHome);
        root.getChildren().add(editProduct);
        root.getChildren().add(reports);
        root.getChildren().add(generateSummary);
        root.getChildren().add(inventory);
        root.getChildren().add(moreFunctions);
        root.getChildren().add(next);

        setScene(root, 500, 520);
    }

    private void showInventoryScreen() {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label("INVENTORY");
        Label subheading = new Label("All products A-Z:");

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(subheading);

        for (int i = 0; i < departments.size(); i++) {
            Department d = departments.get(i);
            ArrayList<Product> products = d.getProducts();

            for (int j = 0; j < products.size(); j++) {
                Product p = products.get(j);
                HBox line = new HBox();
                line.setSpacing(8);

                Label item = new Label(p.getName() + " : " + p.getInventoryText());
                Button toggle = new Button("TOGGLE N/A");

                toggle.setOnAction(e -> {
                    p.toggleAvailable();
                    showInventoryScreen();
                });

                line.getChildren().add(item);
                line.getChildren().add(toggle);
                root.getChildren().add(line);
            }
        }

        back.setOnAction(e -> showManagerSessionScreen());
        next.setOnAction(e -> showMoreFunctionsScreen());

        root.getChildren().add(next);

        setScene(root, 650, 600);
    }

    private void showMoreFunctionsScreen() {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Button next = new Button("NEXT");
        Label heading = new Label("MORE FUNCTIONS");

        Button openSimulation = new Button("OPEN SIMULATION");
        Button openFeedback = new Button("OPEN FEEDBACK");
        Button changePassword = new Button("CHANGE PASSWORD");

        back.setOnAction(e -> showManagerSessionScreen());
        next.setOnAction(e -> showWelcomeScreen());
        openSimulation.setOnAction(e -> runSimulation());
        openFeedback.setOnAction(e -> showFeedbackRecords());
        changePassword.setOnAction(e -> showMessageAndReturn("Password tool can be added in the next phase.", "more"));

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(openSimulation);
        root.getChildren().add(openFeedback);
        root.getChildren().add(changePassword);
        root.getChildren().add(next);

        setScene(root, 500, 420);
    }

    private void showReportSummary() {
        String summary = "Sales Summary\n";
        summary += "Cart subtotal from current session: $" + cart.getSubtotal() + "\n";
        summary += "Feedback records: " + feedbackRecords.size() + "\n";
        summary += "Departments loaded: " + departments.size() + "\n";

        showTextScreen("GENERATED SUMMARY", summary, "manager");
    }

    private void runSimulation() {
        Cart testCart = new Cart();

        for (int i = 0; i < departments.size(); i++) {
            Department d = departments.get(i);
            ArrayList<Product> products = d.getProducts();

            for (int j = 0; j < products.size(); j++) {
                Product p = products.get(j);

                if (p.isAvailable() && p.getQuantityInStock() > 0) {
                    try {
                        p.validateOrderQuantity(1);
                        testCart.addItem(p, 1);
                    }
                    catch (InvalidQuantityException ex) {
                        // Simulation skips invalid products.
                    }
                    finally {
                        // Placeholder for cleanup-style logic from the lecture files.
                    }
                }
            }
        }

        String result = "Simulation completed.\n";
        result += "Random-style transaction test created using all available products.\n";
        result += testCart.getReceipt();

        showTextScreen("SIMULATION", result, "more");
    }

    private void showFeedbackRecords() {
        String text = "Customer Feedback Records\n";

        if (feedbackRecords.size() == 0) {
            text += "No feedback recorded yet.";
        }
        else {
            for (int i = 0; i < feedbackRecords.size(); i++) {
                text += (i + 1) + ". " + feedbackRecords.get(i) + "\n";
            }
        }

        showTextScreen("FEEDBACK", text, "more");
    }

    private void showTextScreen(String title, String text, String returnPlace) {
        VBox root = new VBox();
        root.setSpacing(10);

        Button back = new Button("BACK");
        Label heading = new Label(title);
        TextArea area = new TextArea();
        area.setText(text);

        if (returnPlace.equals("manager")) {
            back.setOnAction(e -> showManagerSessionScreen());
        }
        else {
            back.setOnAction(e -> showMoreFunctionsScreen());
        }

        root.getChildren().add(back);
        root.getChildren().add(heading);
        root.getChildren().add(area);

        setScene(root, 600, 520);
    }

    private void showMessageAndReturn(String message, String returnPlace) {
        VBox root = new VBox();
        root.setSpacing(10);

        Label label = new Label(message);
        Button ok = new Button("OK");

        if (returnPlace.equals("manager")) {
            ok.setOnAction(e -> showManagerSessionScreen());
        }
        else if (returnPlace.equals("managerLogin")) {
            ok.setOnAction(e -> showManagerLoginScreen());
        }
        else if (returnPlace.equals("more")) {
            ok.setOnAction(e -> showMoreFunctionsScreen());
        }
        else if (returnPlace.equals("review")) {
            ok.setOnAction(e -> showWelcomeScreen());
        }
        else if (returnPlace.equals("customerStart")) {
            ok.setOnAction(e -> showCustomerStartScreen());
        }
        else {
            ok.setOnAction(e -> showWelcomeScreen());
        }

        root.getChildren().add(label);
        root.getChildren().add(ok);

        setScene(root, 450, 250);
    }

    private void setScene(VBox root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        mainStage.setScene(scene);
    }
}