import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentView = "HOME";

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
    private JRadioButtonMenuItem dayMenuItem;
    private JRadioButtonMenuItem nightMenuItem;
    private final Admin adminUser;

    public MainGUI(ThemeMode initialTheme) {
        this.themeMode = initialTheme;
        applyPalette(initialTheme);
        this.adminUser = new Admin(DEMO_ADMIN_USERNAME, "Manager", DEMO_ADMIN_PASSWORD);

        setTitle("Not-A-KIOSK");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setJMenuBar(createMenuBar());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        rebuildCards();

        add(mainPanel);
        showView("HOME");
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
        cardLayout.show(mainPanel, name);
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
        if (dayMenuItem != null) dayMenuItem.setSelected(themeMode == ThemeMode.LIGHT);
        if (nightMenuItem != null) nightMenuItem.setSelected(themeMode == ThemeMode.DARK);
        revalidate();
        repaint();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu viewMenu = new JMenu("View");
        JMenu themeMenu = new JMenu("Theme");

        ButtonGroup group = new ButtonGroup();
        dayMenuItem = new JRadioButtonMenuItem("Day (Light)");
        nightMenuItem = new JRadioButtonMenuItem("Night (Dark)");
        group.add(dayMenuItem);
        group.add(nightMenuItem);

        dayMenuItem.setSelected(themeMode == ThemeMode.LIGHT);
        nightMenuItem.setSelected(themeMode == ThemeMode.DARK);

        dayMenuItem.addActionListener(e -> setTheme(ThemeMode.LIGHT));
        nightMenuItem.addActionListener(e -> setTheme(ThemeMode.DARK));

        themeMenu.add(dayMenuItem);
        themeMenu.add(nightMenuItem);

        JMenuItem toggleItem = new JMenuItem("Toggle Day/Night");
        toggleItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
        ));
        toggleItem.addActionListener(e ->
                setTheme(themeMode == ThemeMode.DARK ? ThemeMode.LIGHT : ThemeMode.DARK)
        );

        viewMenu.add(themeMenu);
        viewMenu.addSeparator();
        viewMenu.add(toggleItem);

        menuBar.add(viewMenu);
        return menuBar;
    }

    private JPanel createHeaderPanel(String title, int verticalPadding, int fontSize) {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        titleLabel.setForeground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(verticalPadding, 20, verticalPadding, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JToggleButton themeToggle = new JToggleButton(themeMode == ThemeMode.DARK ? "Night" : "Day");
        themeToggle.setSelected(themeMode == ThemeMode.DARK);
        themeToggle.setFocusPainted(false);
        themeToggle.addItemListener(e -> {
            themeToggle.setText(themeToggle.isSelected() ? "Night" : "Day");
            setTheme(themeToggle.isSelected() ? ThemeMode.DARK : ThemeMode.LIGHT);
        });
        headerPanel.add(themeToggle, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        JPanel headerPanel = createHeaderPanel("Welcome to Not-A-KIOSK", 25, 34);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(backgroundColor);
        centerPanel.setLayout(new GridBagLayout());

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(panelColor);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        // Don't force a small preferred size; it can clip components (like the Customer/Manager buttons).
        cardPanel.setMinimumSize(new Dimension(500, 280));

        JLabel welcomeLabel = new JLabel("Main Menu");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subText = new JLabel("<html><div style='text-align: center;'>Choose how you want to use the kiosk.<br><br>" +
                "Customer: browse menu and place an order.<br>" +
                "Manager: sign in to access admin features.</div></html>");
        subText.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subText.setForeground(textColor);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionsLabel = new JLabel("<html><div style='text-align: left;'>" +
                "<b>Instructions</b><br>" +
                "- Use the <b>Day/Night</b> toggle in the header or the View menu.<br>" +
                "- Customer: select items, set Qty, optionally add Special notes.<br>" +
                "- Manager: demo login is <b>manager</b> / <b>password</b>." +
                "</div></html>");
        instructionsLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        instructionsLabel.setForeground(textColor);
        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton customerButton = createStyledButton("Customer");
        JButton managerButton = createStyledButton("Manager Login");

        customerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        managerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        customerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showView("CUSTOMER");
            }
        });

        managerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showView("LOGIN");
            }
        });

        cardPanel.add(welcomeLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        cardPanel.add(subText);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(instructionsLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        cardPanel.add(customerButton);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(managerButton);

        GridBagConstraints homeGbc = new GridBagConstraints();
        homeGbc.gridx = 0;
        homeGbc.gridy = 0;
        homeGbc.insets = new Insets(10, 10, 10, 10);
        homeGbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(cardPanel, homeGbc);

        panel.add(headerPanel, BorderLayout.NORTH);
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

        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setForeground(textColor);
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

        JLabel specialLabel = new JLabel("Special:");
        specialLabel.setForeground(textColor);
        JTextField specialField = new JTextField();
        JButton applyNoteButton = createSecondaryButton("Apply");

        inputGbc.gridx = 0;
        inputGbc.gridy = 0;
        inputGbc.weightx = 0;
        inputPanel.add(qtyLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.gridy = 0;
        inputGbc.weightx = 1;
        inputPanel.add(qtySpinner, inputGbc);

        inputGbc.gridx = 0;
        inputGbc.gridy = 1;
        inputGbc.weightx = 0;
        inputPanel.add(specialLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.gridy = 1;
        inputGbc.weightx = 1;
        inputPanel.add(specialField, inputGbc);

        inputGbc.gridx = 2;
        inputGbc.gridy = 1;
        inputGbc.weightx = 0;
        inputPanel.add(applyNoteButton, inputGbc);

        JButton clearButton = createSecondaryButton("Clear");
        JButton checkoutButton = createStyledButton("Checkout");
        JButton backButton = createSecondaryButton("Back");

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);

        // Core menu items (domain model) wired to the GUI.
        MenuItem[] menuItems = new MenuItem[] {
                new MenuItem("Rainbow Bowl", 1299),
                new MenuItem("Spicy Tofu Bowl", 1399),
                new MenuItem("Falafel Wrap", 999),
                new MenuItem("Veggie Wrap", 949),
                new MenuItem("Green Smoothie", 699),
                new MenuItem("Mango Smoothie", 699),
                new MenuItem("Vegan Brownie", 399),
                new MenuItem("Chia Pudding", 499)
        };

        for (MenuItem item : menuItems) {
            JButton itemButton = new JButton("<html><b>" + item.getName() + "</b><br>" + item.getFormattedPrice() + "</html>");
            itemButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            itemButton.setFocusPainted(false);
            itemButton.setBackground(panelColor);
            itemButton.setForeground(textColor);
            itemButton.setPreferredSize(new Dimension(180, 80));
            itemButton.addActionListener(e -> {
                int qty = (Integer) qtySpinner.getValue();
                order.addItem(item, qty);
                orderArea.setText(order.summary());
            });
            menuPanel.add(itemButton);
        }

        applyNoteButton.addActionListener(e -> {
            order.setSpecialInstructions(specialField.getText());
            orderArea.setText(order.summary());
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                order.clear();
                specialField.setText("");
                qtySpinner.setValue(1);
                orderArea.setText(order.summary());
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        MainGUI.this,
                        "Thanks! Your total is " + order.getFormattedTotal(),
                        "Checkout",
                        JOptionPane.INFORMATION_MESSAGE
                );
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

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);

        JTextArea managerArea = new JTextArea();
        managerArea.setEditable(false);
        managerArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        managerArea.setText(
                "Welcome to the Manager Dashboard.\n\n" +
                "This screen is a draft.\n\n" +
                "Later you can add:\n" +
                "- View inventory\n" +
                "- Update menu\n" +
                "- Check orders"
        );
        managerArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        managerArea.setBackground(panelColor);
        managerArea.setForeground(textColor);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(500, 280));
        contentPanel.setBackground(backgroundColor);
        contentPanel.add(managerArea, BorderLayout.CENTER);

        JButton logoutButton = createSecondaryButton("Logout");
        contentPanel.add(logoutButton, BorderLayout.SOUTH);

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showView("HOME");
            }
        });

        centerPanel.add(contentPanel);

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
