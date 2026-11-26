# 💊 Pill Reminder – Medication Reminder App

**Pill Reminder** is an Android application designed to help users manage their medications and never miss a scheduled dose.

The application is developed using **Java** and **MVVM architecture**, with **Room Database** for local data storage and reliable medication management.

---

## 📱 Features

- **💊 Medication Management:** Add, edit, and delete medication reminders with detailed information.
- **⏰ Advanced Scheduling:** Configure weekdays, months, start times, and medication repeat intervals.
- **🔔 Smart Alarm & Notification System:** Play alarm sounds and display persistent notifications through a Foreground Service, with interactive **"Taken"** and **"Snooze"** actions.
- **📩 Emergency SMS Notifications:** Automatically send SMS messages to emergency contacts when medication is taken.
- **🎨 Modern User Interface:** Material 3 components, elegant cards, and full Right-to-Left (RTL) layout support.

---

## 🖼️ Screenshots

<p align="center">
  <img src="images/main.jpg" width="260" height="460" alt="Main Screen" />
  <img src="images/add.jpg" width="260" height="460" alt="Add Medication Screen" />
  <img src="images/setting.jpg" width="260" height="460" alt="Settings Screen" />
</p>

---

## 🛠️ Tech Stack & Architecture

| Category | Technology |
|---|---|
| Programming Language | Java |
| Architecture | MVVM (Model - View - ViewModel) |
| Local Database | Room Database + TypeConverters |
| Alarm Scheduling | AlarmManager + BroadcastReceiver |
| Background Services | Foreground Service + MediaPlayer |
| UI Components | Material 3 |
| View Binding | ViewBinding |
| Layout System | ConstraintLayout |
| Observable Data | LiveData |

---

## ⚡ Optimizations & Reliability

- **Android Version Compatibility:** Supports Foreground Service Types required for Android 14 and above.
- **Reliable Alarm Scheduling:** Implements AlarmManager with system permission checks to improve alarm reliability.
- **Lifecycle-Aware Data Management:** Uses LiveData and Repository patterns to keep the database and UI synchronized.
- **Background Execution:** Uses Foreground Services and MediaPlayer for alarm playback and persistent notifications.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Iguana or newer.
- Android SDK.
- Java Development Kit (JDK) compatible with the project's Gradle configuration.

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/USERNAME/Pill-Reminder.git
   ```

2. Open the project in Android Studio.

3. Wait for Gradle synchronization to complete.

4. Run the application on an Android Emulator or a physical device running **Android 8.0 (API 26) or newer**.

---

## 🔒 Permissions

Depending on the Android version and enabled features, the application may require permissions for:

- Notifications.
- Exact alarm scheduling.
- Foreground services.
- Sending SMS messages.

Grant the required permissions through Android system settings when prompted.
