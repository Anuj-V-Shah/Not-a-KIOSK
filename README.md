# Not-a-KIOSK

## Purpose / Problem Statement

Self-service kiosks are commonly used in quick-service restaurants to improve efficiency and reduce wait times. However, many customers experience confusion when using these systems, especially first-time users or individuals who are less comfortable with technology. Complex menu layouts, unclear navigation, and accidental item cancellations often lead to frustration and abandoned orders. Traditional kiosks do not respond when users struggle, treating all interactions the same regardless of difficulty. This project addresses the problem of unresponsive kiosk interfaces by introducing a system that detects user confusion and provides timely assistance during the ordering process.

## Target Users

The primary users of this application are customers of quick-service restaurants who place orders through self-service kiosks. This includes first-time users, elderly customers, and individuals unfamiliar with digital ordering systems. Secondary beneficiaries include restaurant owners and staff, who benefit from fewer abandoned orders and reduced need for direct customer assistance during peak hours.

## What’s Implemented

- **Welcome screen:** “Tap to order” start screen with background image (`bg1.png`)
- **Customer ordering:** select item → set quantity → add instructions → add to order → checkout
- **Manager tools:** update inventory stock, change prices, view/reset sales summary
- **Themes:** day/night toggle using custom sun/moon icons

## Tech Stack

- **UI (JavaFX):** `MainFX.java`
- **UI (Legacy Swing):** `MainGUI.java` (uses FlatLaf)
- **Core (OOP):** `BaseUser` (abstract), multiple interfaces, encapsulated domain classes

---

## Run (JavaFX)

JavaFX is **not** bundled with most modern JDK installs (including JDK 21), so you need the JavaFX SDK.

### 1) Download JavaFX SDK

1. Download the JavaFX SDK for your OS from Gluon.
2. Unzip it.
3. Locate its `lib/` directory.

### 2) Compile and run

macOS / Linux:
```bash
export JAVAFX_LIB="/path/to/javafx-sdk/lib"
javac --module-path "$JAVAFX_LIB" --add-modules javafx.controls,javafx.graphics Admin.java Authenticatable.java BaseUser.java Customer.java Inventory.java InventoryOperations.java MenuItem.java Order.java OrderOperations.java Priced.java SalesLedger.java Summarizable.java MainFX.java
java  --module-path "$JAVAFX_LIB" --add-modules javafx.controls,javafx.graphics MainFX
```

Windows (PowerShell):
```powershell
$env:JAVAFX_LIB="C:\path\to\javafx-sdk\lib"
javac --module-path "$env:JAVAFX_LIB" --add-modules javafx.controls,javafx.graphics Admin.java Authenticatable.java BaseUser.java Customer.java Inventory.java InventoryOperations.java MenuItem.java Order.java OrderOperations.java Priced.java SalesLedger.java Summarizable.java MainFX.java
java  --module-path "$env:JAVAFX_LIB" --add-modules javafx.controls,javafx.graphics MainFX
```

---

## Run (Legacy Swing + FlatLaf)

This repo includes FlatLaf at `flat/flatlaf-3.7.1.jar`.

Note: `MainFX.java` requires JavaFX, so don’t compile with `*.java` for the Swing version.

macOS / Linux:
```bash
javac -cp "flat/*" Admin.java Authenticatable.java BaseUser.java Customer.java Inventory.java InventoryOperations.java MainGUI.java MenuItem.java Order.java OrderOperations.java Priced.java SalesLedger.java Summarizable.java
java -cp ".:flat/*" MainGUI
```

Windows:
```bat
javac -cp "flat/*" Admin.java Authenticatable.java BaseUser.java Customer.java Inventory.java InventoryOperations.java MainGUI.java MenuItem.java Order.java OrderOperations.java Priced.java SalesLedger.java Summarizable.java
java -cp ".;flat/*" MainGUI
```

---

## FlatLaf (Download/Update)

If you want to download a newer FlatLaf version:

1. Go to Maven Central and search for `com.formdev flatlaf`
2. Download the `flatlaf-<version>.jar`
3. Replace the jar in `flat/` (keep only one FlatLaf jar there to avoid confusion)

---

## Project Files (Key Classes)

- UI (JavaFX): `MainFX.java`
- UI (Swing): `MainGUI.java`
- Users/auth: `Authenticatable.java`, `BaseUser.java`, `Admin.java`, `Customer.java`
- Ordering: `MenuItem.java`, `Order.java`, `OrderOperations.java`
- Manager domain: `Inventory.java`, `InventoryOperations.java`, `SalesLedger.java`
- Interfaces: `Priced.java`, `Summarizable.java`

## Assets

- Welcome background: `bg1.png`
- Theme icons: `sun_black.png`, `sun_white.png`, `moon_black.png`, `moon_white.png`
