**Problem Statement**

Self-service kiosks are commonly used in quick-service restaurants to improve efficiency and reduce wait times. However, many customers experience confusion when using these systems, especially first-time users or individuals who are less comfortable with technology. Complex menu layouts, unclear navigation, and accidental item cancellations often lead to frustration and abandoned orders. Traditional kiosks do not respond when users struggle, treating all interactions the same regardless of difficulty. This project addresses the problem of unresponsive kiosk interfaces by introducing a system that detects user confusion and provides timely assistance during the ordering process.

**Target Users**

The primary users of this application are customers of quick-service restaurants who place orders through self-service kiosks. This includes first-time users, elderly customers, and individuals unfamiliar with digital ordering systems. Secondary beneficiaries include restaurant owners and staff, who benefit from fewer abandoned orders and reduced need for direct customer assistance during peak hours.

**Proposed Solution**

The proposed solution is a smart self-service ordering kiosk that monitors customer interaction patterns to identify signs of confusion. Instead of relying solely on static screens, the kiosk dynamically responds when customers appear stuck or unsure. By observing behaviors such as long pauses, repeated navigation between screens, and frequent item cancellations, the system determines when assistance may be helpful. When confusion is detected, the kiosk provides subtle guidance through prompts, recommendations, or the option to request staff support, improving usability without interrupting the ordering flow.

---

## Running (with FlatLaf Day/Night mode)

This GUI uses [FlatLaf](https://www.formdev.com/flatlaf/) for modern Swing theming + a built-in Day/Night toggle.

### Option A: Plain `javac` + downloaded jar (no build tool)

1. Download the FlatLaf jar from Maven Central and place it in a `lib/` folder (for example `lib/flatlaf-3.x.jar`).
2. Compile + run:

```bash
mkdir -p lib
javac -cp "lib/*" MainGUI.java
java -cp ".:lib/*" MainGUI
```

### Option B: Maven/Gradle dependency (if you add a build)

- Maven:
  - `com.formdev:flatlaf`
- Gradle:
  - `implementation("com.formdev:flatlaf:<version>")`
