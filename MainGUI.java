import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.prefs.Preferences;

public class MainGUI extends JFrame {

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

    private static final Preferences PREFS = Preferences.userNodeForPackage(MainGUI.class);
    private static final String PREF_KEY_THEME = "themeMode";

    private static final String DEMO_ADMIN_USERNAME = "manager";
    private static final char[] DEMO_ADMIN_PASSWORD = "password".toCharArray();

    private final Order order = new Order();
    private final Inventory inventory = new Inventory();
    private final SalesLedger salesLedger = new SalesLedger();
    private final Icon sunBlackIcon;
    private final Icon sunWhiteIcon;
    private final Icon moonBlackIcon;
    private final Icon moonWhiteIcon;
    private static final int THEME_ICON_SIZE_PX = 48;
    private final Image welcomeBackgroundImage;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentView = "HOME";
  // nothing man
    // Colors
    private Color backgroundColor;
    private Color headerColor;
    private Color buttonColor;
    private Color buttonTextColor;
    private Color panelColor;
    private Color textColor;
    private Color secondaryButtonColor;
    private Color borderColor;

    private ThemeMode themeMode;
    private final Admin adminUser;

    public MainGUI(ThemeMode initialTheme) {
        this.themeMode = initialTheme;
        applyPalette(initialTheme);
        this.adminUser = new Admin(DEMO_ADMIN_USERNAME, "Manager", DEMO_ADMIN_PASSWORD);
        seedInventory();
        this.sunBlackIcon = loadScaledIcon("sun_black.png", THEME_ICON_SIZE_PX);
        this.sunWhiteIcon = loadScaledIcon("sun_white.png", THEME_ICON_SIZE_PX);
        this.moonBlackIcon = loadScaledIcon("moon_black.png", THEME_ICON_SIZE_PX);
        this.moonWhiteIcon = loadScaledIcon("moon_white.png", THEME_ICON_SIZE_PX);
        this.welcomeBackgroundImage = tryLoadImage("bg1.png");

        setTitle("Not-A-KIOSK");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        rebuildCards();

        add(mainPanel);
        showView("HOME");
    }

    private void seedInventory() {
        // Seed once; keep state across theme rebuilds.
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

    private static ThemeMode loadThemePreference() {
        return ThemeMode.fromPref(PREFS.get(PREF_KEY_THEME, ThemeMode.LIGHT.name()));
    }

    private static void saveThemePreference(ThemeMode mode) {
        PREFS.put(PREF_KEY_THEME, mode.name());
    }

    private static void applyLookAndFeel(ThemeMode mode) {
        try {
            if (mode == ThemeMode.DARK) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // ignore
            }
        }
    }

    private void applyPalette(ThemeMode mode) {
        if (mode == ThemeMode.DARK) {
            backgroundColor = new Color(30, 32, 35);
            panelColor = new Color(45, 47, 50);
            textColor = new Color(230, 230, 230);
            headerColor = new Color(27, 94, 32);
            buttonColor = new Color(56, 142, 60);
            buttonTextColor = Color.WHITE;
            secondaryButtonColor = new Color(70, 70, 70);
            borderColor = new Color(85, 85, 85);
        } else {
            backgroundColor = new Color(245, 248, 242);
            headerColor = new Color(46, 125, 50);
            buttonColor = new Color(76, 175, 80);
            buttonTextColor = Color.WHITE;
            panelColor = Color.WHITE;
            textColor = new Color(40, 40, 40);
            secondaryButtonColor = new Color(230, 230, 230);
            borderColor = new Color(220, 220, 220);
        }
    }

    private void showView(String name) {
        currentView = name;
        // Inventory/prices can change in the Manager screen; rebuild so Customer view always reflects latest state.
        if ("CUSTOMER".equals(name)) {
            rebuildCards();
        }
        cardLayout.show(mainPanel, name);
    }

    private static int parsePriceCents(String input) {
        if (input == null) throw new IllegalArgumentException("price is required");
        String normalized = input.trim().replace("$", "");
        if (normalized.isBlank()) throw new IllegalArgumentException("price is required");
        try {
            BigDecimal dollars = new BigDecimal(normalized).setScale(2, RoundingMode.HALF_UP);
            return dollars.movePointRight(2).intValueExact();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid price. Use formats like 12.99 or 12", e);
        }
    }

    private static String formatDollarsFromCents(int cents) {
        return String.format("%.2f", cents / 100.0);
    }

    private void rebuildCards() {
        if (mainPanel != null) {
            mainPanel.removeAll();
        }
        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createManagerLoginPanel(), "LOGIN");
        mainPanel.add(createCustomerPanel(), "CUSTOMER");
        mainPanel.add(createManagerPanel(), "MANAGER");
        if (mainPanel != null) {
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private void setTheme(ThemeMode mode) {
        if (mode == themeMode) return;

        themeMode = mode;
        saveThemePreference(mode);
        applyLookAndFeel(mode);
        applyPalette(mode);
        rebuildCards();
        showView(currentView);
        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel(String title, int verticalPadding, int fontSize) {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        titleLabel.setForeground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(verticalPadding, 20, verticalPadding, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        headerPanel.add(createThemeToggleButton(false), BorderLayout.EAST);

        return headerPanel;
    }

    private JToggleButton createThemeToggleButton(boolean isHomeScreen) {
        // Per request:
        // - Home: black sun (light), white moon (dark)
        // - Other screens: white sun (light), black moon (dark)
        Icon sun = isHomeScreen ? sunBlackIcon : sunWhiteIcon;
        Icon moon = isHomeScreen ? moonWhiteIcon : moonBlackIcon;

        Icon initialIcon = themeMode == ThemeMode.DARK ? moon : sun;
        JToggleButton themeToggle = new JToggleButton(initialIcon);
        themeToggle.setSelected(themeMode == ThemeMode.DARK);
        themeToggle.setText(null);
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setOpaque(false);
        themeToggle.setMargin(new Insets(0, 0, 0, 0));
        themeToggle.setToolTipText(themeMode == ThemeMode.DARK ? "Night mode" : "Day mode");
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        int buttonSize = THEME_ICON_SIZE_PX + 14;
        themeToggle.setPreferredSize(new Dimension(buttonSize, buttonSize));
        themeToggle.addItemListener(e -> {
            boolean dark = themeToggle.isSelected();
            if (dark) {
                if (moon != null) themeToggle.setIcon(moon);
                themeToggle.setToolTipText("Night mode");
                setTheme(ThemeMode.DARK);
            } else {
                if (sun != null) themeToggle.setIcon(sun);
                themeToggle.setToolTipText("Day mode");
                setTheme(ThemeMode.LIGHT);
            }
        });
        return themeToggle;
    }

    private static Icon loadScaledIcon(String fileName, int sizePx) {
        ImageIcon icon = tryLoadIcon(fileName);
        if (icon == null || icon.getIconWidth() <= 0) return null;
        Image scaled = icon.getImage().getScaledInstance(sizePx, sizePx, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static ImageIcon tryLoadIcon(String fileName) {
        java.net.URL url = MainGUI.class.getResource("/" + fileName);
        if (url != null) return new ImageIcon(url);
        java.io.File file = new java.io.File(fileName);
        if (file.exists()) return new ImageIcon(fileName);
        return null;
    }

    private static Image tryLoadImage(String fileName) {
        ImageIcon icon = tryLoadIcon(fileName);
        if (icon == null) return null;
        Image img = icon.getImage();
        return img == null ? null : img;
    }

    private static final class ImageBackgroundPanel extends JPanel {
        private final Image image;
        private final float alpha;

        private ImageBackgroundPanel(Image image, float alpha) {
            this.image = image;
            this.alpha = alpha;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // paints base color (white/black) first
            if (image == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                int panelW = getWidth();
                int panelH = getHeight();
                int imgW = image.getWidth(this);
                int imgH = image.getHeight(this);
                if (imgW <= 0 || imgH <= 0 || panelW <= 0 || panelH <= 0) return;

                // Cover the full panel while preserving aspect ratio (like CSS background-size: cover).
                double scale = Math.max(panelW / (double) imgW, panelH / (double) imgH);
                int drawW = (int) Math.ceil(imgW * scale);
                int drawH = (int) Math.ceil(imgH * scale);
                int x = (panelW - drawW) / 2;
                int y = (panelH - drawH) / 2;
                g2.drawImage(image, x, y, drawW, drawH, this);
            } finally {
                g2.dispose();
            }
        }
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        float bgAlpha = themeMode == ThemeMode.DARK ? 0.16f : 0.22f;
        JPanel centerPanel = new ImageBackgroundPanel(welcomeBackgroundImage, bgAlpha);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(backgroundColor); // base layer (white/black)

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRight.setOpaque(false);
        topRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 15));
        topRight.add(createThemeToggleButton(true));

        JPanel tapPanel = new JPanel();
        // Match the background so it feels like a full-screen "tap to order" prompt (no white box).
        tapPanel.setOpaque(false);
        tapPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        tapPanel.setLayout(new BoxLayout(tapPanel, BoxLayout.Y_AXIS));

        JLabel paymentLabel = new JLabel("CREDIT  •  DEBIT  •  GIFT CARD", SwingConstants.CENTER);
        paymentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        paymentLabel.setForeground(textColor);

        JLabel cashLabel = new JLabel("CASH NOT ACCEPTED", SwingConstants.CENTER);
        cashLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cashLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cashLabel.setForeground(new Color(120, 120, 120));

        JLabel tapLabel = new JLabel("TAP TO ORDER", SwingConstants.CENTER);
        tapLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tapLabel.setFont(new Font("SansSerif", Font.BOLD, 84));
        tapLabel.setForeground(buttonColor);

        JLabel hintLabel = new JLabel("Tap anywhere to begin", SwingConstants.CENTER);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        hintLabel.setForeground(textColor);

        tapPanel.add(paymentLabel);
        tapPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        tapPanel.add(cashLabel);
        tapPanel.add(Box.createRigidArea(new Dimension(0, 28)));
        tapPanel.add(tapLabel);
        tapPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        tapPanel.add(hintLabel);

        MouseAdapter tapToOrder = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showView("CUSTOMER");
            }
        };
        tapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        centerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tapPanel.addMouseListener(tapToOrder);
        tapLabel.addMouseListener(tapToOrder);
        hintLabel.addMouseListener(tapToOrder);
        paymentLabel.addMouseListener(tapToOrder);
        cashLabel.addMouseListener(tapToOrder);

        JPanel tapContainer = new JPanel(new GridBagLayout());
        tapContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        tapContainer.add(tapPanel, gbc);
        tapContainer.addMouseListener(tapToOrder);
        centerPanel.addMouseListener(tapToOrder);

        JButton helpButton = createSecondaryButton("Help");
        helpButton.setPreferredSize(new Dimension(140, 44));
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(
                MainGUI.this,
                "How to use the kiosk:\n\n" +
                        "1) Tap anywhere to begin ordering\n" +
                        "2) Select an item, choose Qty, add optional instructions\n" +
                        "3) Add to Order, then Checkout\n\n" +
                        "Manager login demo:\n" +
                        "Username: manager\n" +
                        "Password: password",
                "Help",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton managerButton = createSecondaryButton("Manager Login");
        managerButton.setPreferredSize(new Dimension(180, 44));
        managerButton.addActionListener(e -> showView("LOGIN"));

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        bottomBar.addMouseListener(tapToOrder);

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomLeft.setOpaque(false);
        bottomLeft.add(helpButton);
        bottomBar.add(bottomLeft, BorderLayout.WEST);

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRight.setOpaque(false);
        bottomRight.add(managerButton);
        bottomBar.add(bottomRight, BorderLayout.EAST);

        centerPanel.add(tapContainer, BorderLayout.CENTER);
        centerPanel.add(topRight, BorderLayout.NORTH);
        centerPanel.add(bottomBar, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createManagerLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        JPanel headerPanel = createHeaderPanel("Manager Login", 20, 30);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setPreferredSize(new Dimension(450, 280));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        usernameLabel.setForeground(textColor);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        passwordLabel.setForeground(textColor);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton loginButton = createStyledButton("Login");
        JButton backButton = createSecondaryButton("Back");

        JLabel noteLabel = new JLabel("Demo login: manager / password");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        noteLabel.setForeground(Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(backButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(noteLabel, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                boolean ok = adminUser.authenticate(username, password);
                passwordField.setText("");
                if (ok && adminUser.canAccessManagerDashboard()) {
                    showView("MANAGER");
                } else {
                    JOptionPane.showMessageDialog(
                            MainGUI.this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showView("HOME");
            }
        });

        centerPanel.add(formPanel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = createHeaderPanel("Customer Ordering Screen", 20, 28);

        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        menuPanel.setBackground(backgroundColor);

        JTextArea orderArea = new JTextArea();
        orderArea.setEditable(false);
        orderArea.setFont(new Font("SansSerif", Font.PLAIN, 17));
        orderArea.setText(order.summary());
        orderArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        orderArea.setBackground(panelColor);
        orderArea.setForeground(textColor);

        JScrollPane scrollPane = new JScrollPane(orderArea);
        scrollPane.setPreferredSize(new Dimension(280, 300));

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(backgroundColor);

        JLabel summaryLabel = new JLabel("Order Summary", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        summaryLabel.setForeground(textColor);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(backgroundColor);
        GridBagConstraints inputGbc = new GridBagConstraints();
        inputGbc.insets = new Insets(4, 4, 4, 4);
        inputGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectedLabel = new JLabel("Selected:");
        selectedLabel.setForeground(textColor);
        JLabel selectedValueLabel = new JLabel("(none)");
        selectedValueLabel.setForeground(textColor);

        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setForeground(textColor);
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        JButton addToOrderButton = createStyledButton("Add to Order");
        addToOrderButton.setEnabled(false);

        JLabel specialLabel = new JLabel("Instructions:");
        specialLabel.setForeground(textColor);
        JTextField specialField = new JTextField();
        specialField.setEnabled(false);

        inputGbc.gridx = 0;
        inputGbc.gridy = 0;
        inputGbc.weightx = 0;
        inputPanel.add(selectedLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.gridy = 0;
        inputGbc.weightx = 1.0;
        inputPanel.add(selectedValueLabel, inputGbc);

        inputGbc.gridx = 2;
        inputGbc.gridy = 0;
        inputGbc.weightx = 0;
        inputPanel.add(qtyLabel, inputGbc);

        inputGbc.gridx = 3;
        inputGbc.gridy = 0;
        inputGbc.weightx = 0;
        inputPanel.add(qtySpinner, inputGbc);

        inputGbc.gridx = 4;
        inputGbc.gridy = 0;
        inputGbc.weightx = 0;
        inputPanel.add(addToOrderButton, inputGbc);

        inputGbc.gridx = 0;
        inputGbc.gridy = 1;
        inputGbc.weightx = 0;
        inputPanel.add(specialLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.gridy = 1;
        inputGbc.weightx = 1.0;
        inputGbc.gridwidth = 4;
        inputPanel.add(specialField, inputGbc);
        inputGbc.gridwidth = 1;

        JButton clearButton = createSecondaryButton("Clear");
        JButton checkoutButton = createStyledButton("Checkout");
        JButton backButton = createSecondaryButton("Back");

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);

        java.util.Map<String, JButton> menuButtons = new java.util.HashMap<>();

        java.util.function.BiConsumer<MenuItem, JButton> renderMenuButton = (menuItem, button) -> {
            int stock = inventory.getStock(menuItem.getName());
            if (stock <= 0) {
                button.setText("<html><b>" + menuItem.getName() + "</b><br>" + menuItem.getFormattedPrice() +
                        "<br><i>Sold out</i></html>");
            } else {
                button.setText("<html><b>" + menuItem.getName() + "</b><br>" + menuItem.getFormattedPrice() + "</html>");
            }
            button.setEnabled(stock > 0);
        };

        final String[] selectedItemName = new String[] { null };
        final JButton[] selectedButton = new JButton[] { null };
        final Border defaultMenuBorder = BorderFactory.createLineBorder(borderColor, 1);
        final Border selectedMenuBorder = BorderFactory.createLineBorder(buttonColor, 3);

        for (Inventory.Entry entry : inventory.getEntriesView()) {
            MenuItem item = entry.getMenuItem();
            JButton itemButton = new JButton();
            itemButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            itemButton.setFocusPainted(false);
            itemButton.setBackground(panelColor);
            itemButton.setForeground(textColor);
            itemButton.setPreferredSize(new Dimension(180, 80));
            itemButton.setBorder(defaultMenuBorder);
            renderMenuButton.accept(item, itemButton);
            menuButtons.put(item.getName(), itemButton);
            itemButton.addActionListener(e -> {
                String name = item.getName();
                selectedItemName[0] = name;
                selectedValueLabel.setText(name);
                qtySpinner.setValue(1);
                specialField.setText("");
                specialField.setEnabled(true);
                addToOrderButton.setEnabled(true);
                if (selectedButton[0] != null) {
                    selectedButton[0].setBorder(defaultMenuBorder);
                }
                selectedButton[0] = itemButton;
                itemButton.setBorder(selectedMenuBorder);
            });
            menuPanel.add(itemButton);
        }

        addToOrderButton.addActionListener(e -> {
            if (selectedItemName[0] == null) {
                JOptionPane.showMessageDialog(MainGUI.this, "Select an item first.");
                return;
            }
            int qty = (Integer) qtySpinner.getValue();
            int available = inventory.getStock(selectedItemName[0]);
            if (qty > available) {
                JOptionPane.showMessageDialog(
                        MainGUI.this,
                        "Not enough stock. Available: " + available,
                        "Out of Stock",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            MenuItem currentItem = inventory.getMenuItem(selectedItemName[0]);
            order.addItem(currentItem, qty, specialField.getText());
            inventory.adjustStock(selectedItemName[0], -qty);
            orderArea.setText(order.summary());
            JButton button = menuButtons.get(selectedItemName[0]);
            if (button != null) {
                renderMenuButton.accept(inventory.getMenuItem(selectedItemName[0]), button);
            }
            qtySpinner.setValue(1);
            specialField.setText("");
            if (inventory.getStock(selectedItemName[0]) <= 0) {
                addToOrderButton.setEnabled(false);
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (java.util.Map.Entry<MenuItem, Integer> entry : order.getItemsView().entrySet()) {
                    inventory.adjustStock(entry.getKey().getName(), entry.getValue());
                }
                order.clear();
                specialField.setText("");
                specialField.setEnabled(false);
                qtySpinner.setValue(1);
                orderArea.setText(order.summary());
                selectedItemName[0] = null;
                selectedValueLabel.setText("(none)");
                addToOrderButton.setEnabled(false);
                if (selectedButton[0] != null) {
                    selectedButton[0].setBorder(defaultMenuBorder);
                    selectedButton[0] = null;
                }
                for (Inventory.Entry invEntry : inventory.getEntriesView()) {
                    JButton button = menuButtons.get(invEntry.getName());
                    if (button != null) {
                        renderMenuButton.accept(invEntry.getMenuItem(), button);
                    }
                }
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (order.getItemsView().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            MainGUI.this,
                            "Add at least one item before checkout.",
                            "Nothing to Checkout",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                salesLedger.recordCompletedOrder(order);
                JOptionPane.showMessageDialog(
                        MainGUI.this,
                        "Thanks! Your total is " + order.getFormattedTotal(),
                        "Checkout",
                        JOptionPane.INFORMATION_MESSAGE
                );
                order.clear();
                specialField.setText("");
                specialField.setEnabled(false);
                qtySpinner.setValue(1);
                orderArea.setText(order.summary());
                selectedItemName[0] = null;
                selectedValueLabel.setText("(none)");
                addToOrderButton.setEnabled(false);
                if (selectedButton[0] != null) {
                    selectedButton[0].setBorder(defaultMenuBorder);
                    selectedButton[0] = null;
                }
                showView("HOME");
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showView("HOME");
            }
        });

        rightPanel.add(summaryLabel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(backgroundColor);
        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        rightPanel.add(southPanel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(menuPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createManagerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        JPanel headerPanel = createHeaderPanel("Manager Dashboard", 20, 30);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(backgroundColor);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel inventoryLabel = new JLabel("Inventory / Prices");
        inventoryLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        inventoryLabel.setForeground(textColor);

        DefaultTableModel inventoryModel = new DefaultTableModel(new Object[] {"Item", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable inventoryTable = new JTable(inventoryModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane inventoryScroll = new JScrollPane(inventoryTable);
        inventoryScroll.setPreferredSize(new Dimension(520, 360));

        java.util.function.Supplier<String> getSelectedItemName = () -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) return null;
            return String.valueOf(inventoryModel.getValueAt(row, 0));
        };

        java.util.function.Consumer<String> refreshInventoryTable = (keepSelectedName) -> {
            inventoryModel.setRowCount(0);
            for (Inventory.Entry entry : inventory.getEntriesView()) {
                MenuItem item = entry.getMenuItem();
                inventoryModel.addRow(new Object[] {
                        entry.getName(),
                        item.getFormattedPrice(),
                        entry.getStock()
                });
            }
            if (keepSelectedName != null) {
                for (int i = 0; i < inventoryModel.getRowCount(); i++) {
                    String name = String.valueOf(inventoryModel.getValueAt(i, 0));
                    if (keepSelectedName.equals(name)) {
                        inventoryTable.setRowSelectionInterval(i, i);
                        inventoryTable.scrollRectToVisible(inventoryTable.getCellRect(i, 0, true));
                        break;
                    }
                }
            }
        };
        refreshInventoryTable.accept(null);

        JPanel inventoryControls = new JPanel(new GridBagLayout());
        inventoryControls.setBackground(backgroundColor);
        GridBagConstraints igbc = new GridBagConstraints();
        igbc.insets = new Insets(6, 6, 6, 6);
        igbc.fill = GridBagConstraints.HORIZONTAL;
        igbc.weightx = 1;

        JLabel stockDeltaLabel = new JLabel("Stock Δ:");
        stockDeltaLabel.setForeground(textColor);
        JSpinner stockDeltaSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 500, 1));
        JButton addStockButton = createSecondaryButton("Add Stock");
        JButton removeStockButton = createSecondaryButton("Remove Stock");

        JLabel priceLabel = new JLabel("New Price ($):");
        priceLabel.setForeground(textColor);
        JTextField priceField = new JTextField();
        JButton setPriceButton = createStyledButton("Set Price");

        igbc.gridx = 0;
        igbc.gridy = 0;
        igbc.weightx = 0;
        inventoryControls.add(stockDeltaLabel, igbc);
        igbc.gridx = 1;
        igbc.gridy = 0;
        igbc.weightx = 1;
        inventoryControls.add(stockDeltaSpinner, igbc);
        igbc.gridx = 2;
        igbc.gridy = 0;
        igbc.weightx = 0;
        inventoryControls.add(addStockButton, igbc);
        igbc.gridx = 3;
        igbc.gridy = 0;
        inventoryControls.add(removeStockButton, igbc);

        igbc.gridx = 0;
        igbc.gridy = 1;
        igbc.weightx = 0;
        inventoryControls.add(priceLabel, igbc);
        igbc.gridx = 1;
        igbc.gridy = 1;
        igbc.weightx = 1;
        inventoryControls.add(priceField, igbc);
        igbc.gridx = 2;
        igbc.gridy = 1;
        igbc.gridwidth = 2;
        igbc.weightx = 0;
        inventoryControls.add(setPriceButton, igbc);
        igbc.gridwidth = 1;

        JButton refreshButton = createSecondaryButton("Refresh");
        igbc.gridx = 0;
        igbc.gridy = 2;
        igbc.gridwidth = 4;
        inventoryControls.add(refreshButton, igbc);
        igbc.gridwidth = 1;

        JPanel inventoryPanel = new JPanel(new BorderLayout(10, 10));
        inventoryPanel.setBackground(backgroundColor);
        inventoryPanel.add(inventoryLabel, BorderLayout.NORTH);
        inventoryPanel.add(inventoryScroll, BorderLayout.CENTER);
        inventoryPanel.add(inventoryControls, BorderLayout.SOUTH);

        JLabel salesLabel = new JLabel("Sales Summary");
        salesLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        salesLabel.setForeground(textColor);

        JTextArea salesArea = new JTextArea();
        salesArea.setEditable(false);
        salesArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        salesArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        salesArea.setBackground(panelColor);
        salesArea.setForeground(textColor);
        salesArea.setText(salesLedger.summary());

        JScrollPane salesScroll = new JScrollPane(salesArea);
        salesScroll.setPreferredSize(new Dimension(360, 360));

        JButton refreshSalesButton = createSecondaryButton("Refresh Sales");
        JButton resetSalesButton = createSecondaryButton("Reset Sales");

        JPanel salesButtons = new JPanel(new GridLayout(1, 2, 10, 10));
        salesButtons.setBackground(backgroundColor);
        salesButtons.add(refreshSalesButton);
        salesButtons.add(resetSalesButton);

        JPanel salesPanel = new JPanel(new BorderLayout(10, 10));
        salesPanel.setBackground(backgroundColor);
        salesPanel.add(salesLabel, BorderLayout.NORTH);
        salesPanel.add(salesScroll, BorderLayout.CENTER);
        salesPanel.add(salesButtons, BorderLayout.SOUTH);

        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBackground(backgroundColor);
        mainContent.add(inventoryPanel, BorderLayout.CENTER);
        mainContent.add(salesPanel, BorderLayout.EAST);

        JButton logoutButton = createSecondaryButton("Logout");

        refreshButton.addActionListener(e -> refreshInventoryTable.accept(getSelectedItemName.get()));
        refreshSalesButton.addActionListener(e -> salesArea.setText(salesLedger.summary()));
        resetSalesButton.addActionListener(e -> {
            salesLedger.reset();
            salesArea.setText(salesLedger.summary());
        });

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = inventoryTable.getSelectedRow();
            if (row < 0) return;
            String itemName = String.valueOf(inventoryModel.getValueAt(row, 0));
            MenuItem item = inventory.getMenuItem(itemName);
            priceField.setText(formatDollarsFromCents(item.getPriceCents()));
        });

        addStockButton.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(MainGUI.this, "Select an item first.");
                return;
            }
            String itemName = String.valueOf(inventoryModel.getValueAt(row, 0));
            int delta = (Integer) stockDeltaSpinner.getValue();
            inventory.adjustStock(itemName, delta);
            refreshInventoryTable.accept(itemName);
        });

        removeStockButton.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(MainGUI.this, "Select an item first.");
                return;
            }
            String itemName = String.valueOf(inventoryModel.getValueAt(row, 0));
            int delta = (Integer) stockDeltaSpinner.getValue();
            try {
                inventory.adjustStock(itemName, -delta);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(MainGUI.this, ex.getMessage(), "Cannot Remove Stock", JOptionPane.WARNING_MESSAGE);
            }
            refreshInventoryTable.accept(itemName);
        });

        setPriceButton.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(MainGUI.this, "Select an item first.");
                return;
            }
            String itemName = String.valueOf(inventoryModel.getValueAt(row, 0));
            try {
                int cents = parsePriceCents(priceField.getText());
                inventory.setPriceCents(itemName, cents);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(MainGUI.this, ex.getMessage(), "Invalid Price", JOptionPane.WARNING_MESSAGE);
                return;
            }
            refreshInventoryTable.accept(itemName);
        });

        logoutButton.addActionListener(e -> showView("HOME"));

        centerPanel.add(mainContent, BorderLayout.CENTER);
        centerPanel.add(logoutButton, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(buttonColor);
        button.setForeground(buttonTextColor);
        button.setPreferredSize(new Dimension(220, 50));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(secondaryButtonColor);
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(220, 50));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemeMode initial = loadThemePreference();
            applyLookAndFeel(initial);
            MainGUI app = new MainGUI(initial);
            app.setVisible(true);
        });
    }
}
