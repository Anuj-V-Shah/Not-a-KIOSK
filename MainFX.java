import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MainFX extends Application {

    private enum ThemeMode {
        LIGHT,
        DARK;

        static ThemeMode fromPref(String value) {
            if (value == null) return LIGHT;
            try {
                return ThemeMode.valueOf(value);
            } catch (IllegalArgumentException ignored) {
                return LIGHT;
            }
        }
    }

    private static final Preferences PREFS = Preferences.userNodeForPackage(MainFX.class);
    private static final String PREF_KEY_THEME = "themeModeFx";

    private static final String DEMO_ADMIN_USERNAME = "manager";
    private static final String DEMO_ADMIN_PASSWORD = "password";

    private final Order order = new Order();
    private final Inventory inventory = new Inventory();
    private final SalesLedger salesLedger = new SalesLedger();
    private final Admin adminUser = new Admin(DEMO_ADMIN_USERNAME, "Manager", DEMO_ADMIN_PASSWORD.toCharArray());

    private ThemeMode themeMode;
    private Stage stage;

    private Scene homeScene;
    private Scene customerScene;
    private Scene managerLoginScene;
    private Scene managerScene;
    private ImageView homeBackgroundView;

    private static final class ThemeToggleRef {
        private final ToggleButton toggle;
        private final ImageView view;
        private final Image sun;
        private final Image moon;

        private ThemeToggleRef(ToggleButton toggle, ImageView view, Image sun, Image moon) {
            this.toggle = toggle;
            this.view = view;
            this.sun = sun;
            this.moon = moon;
        }
    }

    private final List<ThemeToggleRef> themeToggles = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        themeMode = ThemeMode.fromPref(PREFS.get(PREF_KEY_THEME, ThemeMode.LIGHT.name()));
        seedInventory();

        homeScene = new Scene(buildHomeRoot(), 1000, 650);
        customerScene = new Scene(buildCustomerRoot(), 1000, 650);
        managerLoginScene = new Scene(buildManagerLoginRoot(), 1000, 650);
        managerScene = new Scene(buildManagerRoot(), 1100, 680);

        applyTheme(homeScene);
        applyTheme(customerScene);
        applyTheme(managerLoginScene);
        applyTheme(managerScene);
        syncThemeControls();

        stage.setTitle("Not-A-KIOSK");
        stage.setScene(homeScene);
        stage.show();
    }

    private void seedInventory() {
        if (!inventory.getEntriesView().isEmpty()) return;
        inventory.addItem("Rainbow Bowl", 1299, 25);
        inventory.addItem("Spicy Tofu Bowl", 1399, 25);
        inventory.addItem("Falafel Wrap", 999, 30);
        inventory.addItem("Veggie Wrap", 949, 30);
        inventory.addItem("Green Smoothie", 699, 40);
        inventory.addItem("Mango Smoothie", 699, 40);
        inventory.addItem("Vegan Brownie", 399, 50);
        inventory.addItem("Chia Pudding", 499, 50);
    }

    private void setTheme(ThemeMode mode) {
        if (mode == themeMode) return;
        themeMode = mode;
        PREFS.put(PREF_KEY_THEME, mode.name());
        applyTheme(homeScene);
        applyTheme(customerScene);
        applyTheme(managerLoginScene);
        applyTheme(managerScene);
        syncThemeControls();
    }

    private void applyTheme(Scene scene) {
        if (scene == null) return;
        boolean dark = themeMode == ThemeMode.DARK;
        String bg = dark ? "#1e2023" : "#f5f8f2";
        String fg = dark ? "#e6e6e6" : "#282828";
        scene.getRoot().setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";");
    }

    private void syncThemeControls() {
        boolean dark = themeMode == ThemeMode.DARK;
        for (ThemeToggleRef ref : themeToggles) {
            ref.toggle.setSelected(dark);
            if (ref.view != null) {
                ref.view.setImage(dark ? ref.moon : ref.sun);
            }
        }
        if (homeBackgroundView != null) {
            homeBackgroundView.setOpacity(dark ? 0.16 : 0.22);
        }
    }

    private Parent buildHomeRoot() {
        boolean isHome = true;

        StackPane root = new StackPane();
        root.setPadding(new Insets(0));

        Image bg = tryLoadImage("bg1.png");
        if (bg != null) {
            homeBackgroundView = new ImageView(bg);
            homeBackgroundView.setPreserveRatio(false);
            homeBackgroundView.fitWidthProperty().bind(root.widthProperty());
            homeBackgroundView.fitHeightProperty().bind(root.heightProperty());
            homeBackgroundView.setOpacity(themeMode == ThemeMode.DARK ? 0.16 : 0.22);
            root.getChildren().add(homeBackgroundView);
        }

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10, 15, 15, 15));
        layout.setPickOnBounds(false);

        // Top-right theme toggle
        HBox topRight = new HBox();
        topRight.setAlignment(Pos.TOP_RIGHT);
        topRight.getChildren().add(createThemeToggle(isHome));
        layout.setTop(topRight);
        BorderPane.setAlignment(topRight, Pos.TOP_RIGHT);

        // Center "tap to order"
        VBox center = new VBox(10);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(60, 40, 60, 40));

        Label payment = new Label("CREDIT  •  DEBIT  •  GIFT CARD");
        payment.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

        Label cash = new Label("CASH NOT ACCEPTED");
        cash.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        cash.setTextFill(Color.GRAY);

        Label tap = new Label("TAP TO ORDER");
        tap.setFont(Font.font("SansSerif", FontWeight.EXTRA_BOLD, 84));
        tap.setTextFill(Color.web("#4CAF50"));

        Label hint = new Label("Tap anywhere to begin");
        hint.setFont(Font.font("SansSerif", FontWeight.NORMAL, 18));

        center.getChildren().addAll(payment, cash, new Region(), tap, hint);
        VBox.setVgrow(center.getChildren().get(2), Priority.NEVER);

        layout.setCenter(center);

        // Bottom bar with Help + Manager Login
        Button helpButton = new Button("Help");
        helpButton.setPrefSize(140, 44);
        helpButton.setOnAction(e -> showHelpDialog());

        Button managerButton = new Button("Manager Login");
        managerButton.setPrefSize(180, 44);
        managerButton.setOnAction(e -> stage.setScene(managerLoginScene));

        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bottomBar.getChildren().addAll(helpButton, spacer, managerButton);
        layout.setBottom(bottomBar);

        // Tap anywhere to continue (except clicking buttons)
        EventHandler<MouseEvent> tapHandler = e -> stage.setScene(customerScene);
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, tapHandler);
        helpButton.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
        managerButton.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);

        root.getChildren().add(layout);
        return root;
    }

    private Parent buildCustomerRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        HBox header = buildHeader("Customer Ordering Screen", false);
        root.setTop(header);

        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(15);
        menuGrid.setVgap(15);
        menuGrid.setPadding(new Insets(15));

        ScrollPane menuScroll = new ScrollPane(menuGrid);
        menuScroll.setFitToWidth(true);
        menuScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(menuScroll);

        TextArea orderArea = new TextArea(order.summary());
        orderArea.setEditable(false);
        orderArea.setWrapText(true);
        orderArea.setPrefWidth(360);

        Label selectedLabel = new Label("(none)");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 20, 1);
        qtySpinner.setEditable(true);
        TextField instructionsField = new TextField();
        instructionsField.setPromptText("Instructions (optional)");
        instructionsField.setDisable(true);

        Button addToOrder = new Button("Add to Order");
        addToOrder.setDisable(true);
        addToOrder.setPrefWidth(220);

        final String[] selectedItemName = {null};

        Runnable refreshMenu = () -> {
            menuGrid.getChildren().clear();
            int col = 0;
            int row = 0;
            for (Inventory.Entry entry : inventory.getEntriesView()) {
                MenuItem item = entry.getMenuItem();
                int stock = entry.getStock();
                Button itemButton = new Button(item.getName() + "\n" + item.getFormattedPrice());
                itemButton.setPrefSize(220, 90);
                itemButton.setWrapText(true);
                itemButton.setDisable(stock <= 0);
                itemButton.setOnAction(e -> {
                    selectedItemName[0] = item.getName();
                    selectedLabel.setText(item.getName());
                    qtySpinner.getValueFactory().setValue(1);
                    instructionsField.clear();
                    instructionsField.setDisable(false);
                    addToOrder.setDisable(false);
                });
                menuGrid.add(itemButton, col, row);
                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }
        };
        refreshMenu.run();

        addToOrder.setOnAction(e -> {
            if (selectedItemName[0] == null) return;
            int qty = qtySpinner.getValue();
            int available = inventory.getStock(selectedItemName[0]);
            if (qty > available) {
                showAlert(Alert.AlertType.WARNING, "Out of Stock", "Not enough stock. Available: " + available);
                return;
            }
            MenuItem currentItem = inventory.getMenuItem(selectedItemName[0]);
            order.addItem(currentItem, qty, instructionsField.getText());
            inventory.adjustStock(selectedItemName[0], -qty);
            orderArea.setText(order.summary());
            qtySpinner.getValueFactory().setValue(1);
            instructionsField.clear();
            refreshMenu.run();
        });

        Button clear = new Button("Clear");
        Button checkout = new Button("Checkout");
        Button back = new Button("Back");

        clear.setOnAction(e -> {
            for (java.util.Map.Entry<MenuItem, Integer> entry : order.getItemsView().entrySet()) {
                inventory.adjustStock(entry.getKey().getName(), entry.getValue());
            }
            order.clear();
            orderArea.setText(order.summary());
            selectedItemName[0] = null;
            selectedLabel.setText("(none)");
            qtySpinner.getValueFactory().setValue(1);
            instructionsField.clear();
            instructionsField.setDisable(true);
            addToOrder.setDisable(true);
            refreshMenu.run();
        });

        checkout.setOnAction(e -> {
            if (order.getItemsView().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Nothing to Checkout", "Add at least one item before checkout.");
                return;
            }
            salesLedger.recordCompletedOrder(order);
            showAlert(Alert.AlertType.INFORMATION, "Checkout", "Thanks! Your total is " + order.getFormattedTotal());
            order.clear();
            orderArea.setText(order.summary());
            stage.setScene(homeScene);
        });

        back.setOnAction(e -> stage.setScene(homeScene));

        VBox right = new VBox(10);
        right.setPadding(new Insets(15));
        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));

        GridPane input = new GridPane();
        input.setHgap(10);
        input.setVgap(10);
        input.addRow(0, new Label("Selected:"), selectedLabel, new Label("Qty:"), qtySpinner);
        input.add(instructionsField, 1, 1, 3, 1);
        input.add(addToOrder, 4, 0);
        GridPane.setMargin(addToOrder, new Insets(0, 0, 0, 10));

        HBox buttons = new HBox(10, clear, checkout, back);
        buttons.setAlignment(Pos.CENTER);

        right.getChildren().addAll(summaryTitle, orderArea, input, buttons);
        root.setRight(right);

        return root;
    }

    private Parent buildManagerLoginRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        root.setTop(buildHeader("Manager Login", false));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        form.addRow(0, new Label("Username:"), usernameField);
        form.addRow(1, new Label("Password:"), passwordField);

        Button login = new Button("Login");
        Button back = new Button("Back");
        Label note = new Label("Demo login: manager / password");
        note.setTextFill(Color.GRAY);

        HBox buttons = new HBox(10, login, back);
        buttons.setAlignment(Pos.CENTER);
        form.add(buttons, 0, 2, 2, 1);
        form.add(note, 0, 3, 2, 1);

        login.setOnAction(e -> {
            String username = usernameField.getText();
            char[] pass = passwordField.getText().toCharArray();
            boolean ok = adminUser.authenticate(username, pass) && adminUser.canAccessManagerDashboard();
            passwordField.clear();
            if (ok) {
                stage.setScene(managerScene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });
        back.setOnAction(e -> stage.setScene(homeScene));

        root.setCenter(form);
        return root;
    }

    private Parent buildManagerRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        root.setTop(buildHeader("Manager Dashboard", false));

        TableView<Inventory.Entry> table = new TableView<>();
        TableColumn<Inventory.Entry, String> nameCol = new TableColumn<>("Item");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        TableColumn<Inventory.Entry, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMenuItem().getFormattedPrice()));
        TableColumn<Inventory.Entry, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()));
        table.getColumns().addAll(nameCol, priceCol, stockCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        Runnable refreshTable = () -> {
            table.getItems().setAll(inventory.getEntriesView());
        };
        refreshTable.run();

        Spinner<Integer> deltaSpinner = new Spinner<>(1, 500, 5);
        TextField priceField = new TextField();
        priceField.setPromptText("New price (e.g. 12.99)");

        Button addStock = new Button("Add Stock");
        Button removeStock = new Button("Remove Stock");
        Button setPrice = new Button("Set Price");
        Button refresh = new Button("Refresh");

        addStock.setOnAction(e -> {
            Inventory.Entry selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            inventory.adjustStock(selected.getName(), deltaSpinner.getValue());
            refreshTable.run();
            table.getSelectionModel().select(selected);
        });
        removeStock.setOnAction(e -> {
            Inventory.Entry selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                inventory.adjustStock(selected.getName(), -deltaSpinner.getValue());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.WARNING, "Cannot Remove Stock", ex.getMessage());
            }
            refreshTable.run();
            table.getSelectionModel().select(selected);
        });
        setPrice.setOnAction(e -> {
            Inventory.Entry selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                int cents = parsePriceCents(priceField.getText());
                inventory.setPriceCents(selected.getName(), cents);
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.WARNING, "Invalid Price", ex.getMessage());
            }
            refreshTable.run();
            table.getSelectionModel().select(selected);
        });
        refresh.setOnAction(e -> refreshTable.run());

        VBox inventoryBox = new VBox(10, new Label("Inventory / Prices"), table);
        inventoryBox.setPrefWidth(650);

        HBox controls1 = new HBox(10, new Label("Stock Δ:"), deltaSpinner, addStock, removeStock);
        controls1.setAlignment(Pos.CENTER_LEFT);
        HBox controls2 = new HBox(10, new Label("New Price:"), priceField, setPrice, refresh);
        controls2.setAlignment(Pos.CENTER_LEFT);
        inventoryBox.getChildren().addAll(controls1, controls2);

        TextArea salesArea = new TextArea(salesLedger.summary());
        salesArea.setEditable(false);
        salesArea.setWrapText(true);

        Button refreshSales = new Button("Refresh Sales");
        Button resetSales = new Button("Reset Sales");
        refreshSales.setOnAction(e -> salesArea.setText(salesLedger.summary()));
        resetSales.setOnAction(e -> {
            salesLedger.reset();
            salesArea.setText(salesLedger.summary());
        });

        VBox salesBox = new VBox(10, new Label("Sales Summary"), salesArea, new HBox(10, refreshSales, resetSales));
        salesBox.setPrefWidth(380);

        HBox content = new HBox(15, inventoryBox, salesBox);
        root.setCenter(content);

        Button logout = new Button("Logout");
        logout.setOnAction(e -> stage.setScene(homeScene));
        root.setBottom(new HBox(logout));
        BorderPane.setAlignment(logout, Pos.CENTER);
        BorderPane.setMargin(logout, new Insets(10, 0, 0, 0));

        // Populate price field on selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return;
            priceField.setText(formatDollarsFromCents(newV.getMenuItem().getPriceCents()));
        });

        return root;
    }

    private HBox buildHeader(String title, boolean isHome) {
        Label label = new Label(title);
        label.setFont(Font.font("SansSerif", FontWeight.BOLD, 28));
        label.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, spacer, label, spacer, createThemeToggle(isHome));
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setBackground(new Background(new BackgroundFill(Color.web("#2E7D32"), CornerRadii.EMPTY, Insets.EMPTY)));
        return header;
    }

    private ToggleButton createThemeToggle(boolean isHomeScreen) {
        Image sun = tryLoadImage(isHomeScreen ? "sun_black.png" : "sun_white.png");
        Image moon = tryLoadImage(isHomeScreen ? "moon_white.png" : "moon_black.png");
        Image initial = themeMode == ThemeMode.DARK ? moon : sun;

        ImageView view = new ImageView(initial);
        view.setFitWidth(48);
        view.setFitHeight(48);
        view.setPreserveRatio(true);

        ToggleButton toggle = new ToggleButton();
        toggle.setGraphic(view);
        toggle.setSelected(themeMode == ThemeMode.DARK);
        toggle.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        themeToggles.add(new ThemeToggleRef(toggle, view, sun, moon));
        toggle.setOnAction(e -> {
            boolean dark = toggle.isSelected();
            if (dark) {
                if (moon != null) view.setImage(moon);
                setTheme(ThemeMode.DARK);
            } else {
                if (sun != null) view.setImage(sun);
                setTheme(ThemeMode.LIGHT);
            }
        });
        return toggle;
    }

    private void showHelpDialog() {
        showAlert(Alert.AlertType.INFORMATION,
                "Help",
                "How to use the kiosk:\n\n" +
                        "1) Tap anywhere to begin ordering\n" +
                        "2) Select an item, choose Qty, add optional instructions\n" +
                        "3) Add to Order, then Checkout\n\n" +
                        "Manager login demo:\n" +
                        "Username: manager\n" +
                        "Password: password");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private static Image tryLoadImage(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                return new Image(new FileInputStream(file));
            }
            java.net.URL url = MainFX.class.getResource("/" + fileName);
            if (url != null) return new Image(url.toExternalForm());
        } catch (Exception ignored) {
            // ignore
        }
        return null;
    }

    private static int parsePriceCents(String input) {
        if (input == null) throw new IllegalArgumentException("price is required");
        String normalized = input.trim().replace("$", "");
        if (normalized.isBlank()) throw new IllegalArgumentException("price is required");
        try {
            java.math.BigDecimal dollars = new java.math.BigDecimal(normalized).setScale(2, java.math.RoundingMode.HALF_UP);
            return dollars.movePointRight(2).intValueExact();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid price. Use formats like 12.99 or 12", e);
        }
    }

    private static String formatDollarsFromCents(int cents) {
        return String.format("%.2f", cents / 100.0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
