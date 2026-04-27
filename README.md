# Not-a-KIOSK (Swing + FlatLaf)

## Purpose / Problem Statement

Self-service kiosks are commonly used in quick-service restaurants to improve efficiency and reduce wait times. However, many customers experience confusion when using these systems, especially first-time users or individuals who are less comfortable with technology. Complex menu layouts, unclear navigation, and accidental item cancellations often lead to frustration and abandoned orders. Traditional kiosks do not respond when users struggle, treating all interactions the same regardless of difficulty. This project addresses the problem of unresponsive kiosk interfaces by introducing a system that detects user confusion and provides timely assistance during the ordering process.

## Target Users

The primary users of this application are customers of quick-service restaurants who place orders through self-service kiosks. This includes first-time users, elderly customers, and individuals unfamiliar with digital ordering systems. Secondary beneficiaries include restaurant owners and staff, who benefit from fewer abandoned orders and reduced need for direct customer assistance during peak hours.

## Proposed Solution

The proposed solution is a smart self-service ordering kiosk that monitors customer interaction patterns to identify signs of confusion. Instead of relying solely on static screens, the kiosk dynamically responds when customers appear stuck or unsure. By observing behaviors such as long pauses, repeated navigation between screens, and frequent item cancellations, the system determines when assistance may be helpful. When confusion is detected, the kiosk provides subtle guidance through prompts, recommendations, or the option to request staff support, improving usability without interrupting the ordering flow.

---

## What’s Implemented

- **Welcome screen:** “Tap to order” start screen with background image (`bg1.png`).
- **Customer ordering:** select item → set quantity → add instructions → add to order → checkout.
- **Manager tools:** update inventory stock, change prices, and view/reset sales summary.
- **Themes:** day/night toggle using custom sun/moon icons.

## Tech Stack

- **UI:** Java **Swing** (`javax.swing.*`)
- **Theme:** FlatLaf (Light/Dark)
- **Core (OOP):** `BaseUser` (abstract), multiple interfaces, encapsulated domain classes

---

## Running (FlatLaf + Day/Night mode)

This GUI uses FlatLaf for modern Swing theming + a built-in Day/Night toggle.

### Requirements

- Java JDK **8+** (recommended: 17)

### Option A: Plain `javac` (no build tool)

This repo includes FlatLaf at `flat/flatlaf-3.7.1.jar`.

macOS / Linux:
```bash
javac -cp "flat/*" *.java
java -cp ".:flat/*" MainGUI
```

Windows (PowerShell / CMD):
```bat
javac -cp "flat/*" *.java
java -cp ".;flat/*" MainGUI
```

### Option B: Download/Update FlatLaf jar

If you want to download a newer FlatLaf version:

1. Go to [Maven Central](https://search.maven.org/) and search for `com.formdev flatlaf`.
2. Download the `flatlaf-<version>.jar`.
3. Replace the jar in `flat/` (keep only one FlatLaf jar there to avoid confusion).
4. Recompile/run using the commands above.

### Option C: Maven/Gradle (if you add a build)

- Maven dependency coordinates: `com.formdev:flatlaf:<version>`
- Gradle: `implementation("com.formdev:flatlaf:<version>")`

---

## How To Use

### Customer

1. On the welcome screen, **tap anywhere** to start ordering.
2. Select a menu item, adjust **Qty**, optionally type **Instructions**, then click **Add to Order**.
3. Use **Checkout** to complete the order (sales are recorded).

### Manager

1. Click **Manager Login** (welcome screen).
2. Demo credentials:
   - Username: `manager`
   - Password: `password`
3. In the Manager Dashboard:
   - Select an item in the table, then **Add/Remove Stock** or **Set Price**
   - Use **Sales Summary** refresh/reset buttons

---

## Project Files (Key Classes)

- UI: `MainGUI.java`
- Users/auth: `Authenticatable.java`, `BaseUser.java`, `Admin.java`, `Customer.java`
- Ordering: `MenuItem.java`, `Order.java`, `OrderOperations.java`
- Manager domain: `Inventory.java`, `InventoryOperations.java`, `SalesLedger.java`
- Interfaces: `Priced.java`, `Summarizable.java`

## Assets

- Welcome background: `bg1.png`
- Theme icons: `sun_black.png`, `sun_white.png`, `moon_black.png`, `moon_white.png`
