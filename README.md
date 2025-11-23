# 🚛 Tracker: Driver Fleet Application

This repository contains the source code for the Tracker Driver App, an essential tool for logistics personnel to manage and report their daily trips, relay real-time location data, and ensure continuity of tracking even without an internet connection.

## ✨ Features

The Tracker Driver App is designed for robustness and ease of use in a fast-paced logistics environment.

- **Real-time Location Relay**: Continuously transmits the device's GPS coordinates to the Admin Dashboard, providing fleet managers with up-to-the-second location data.
- **Trip Logging & Management**: Drivers can officially start, pause, and end logistics trips directly within the app, logging key metadata (e.g., trip destination, cargo type).
- **Offline Tracking Capability**: Ensures critical location data is cached locally when the device loses network connectivity. Data is automatically synchronized and uploaded to the server once the connection is restored, preventing data loss.
- **Daily Route Visualization**: Displays the planned route for the day and the driver's current progress against it.
- **Secure Driver Login**: Dedicated login using secure Firebase or API authentication to ensure only authorized drivers can access the system and log trips.
- **Battery and Connectivity Awareness**: Optimized to minimize battery drain while maintaining accurate GPS sampling and providing visual indicators for network status.

## 🛠 Technology Stack

- **Platform**: Android (Java/Kotlin)
- **Location Services**: Android Location API (Fused Location Provider)
- **Offline Storage**: Room Persistence Library (SQLite) for caching trip data and location points.
- **Networking**: Retrofit / OkHttp for API communication.
- **State Management**: ViewModels and LiveData (or Kotlin Flows).

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest version)
- Android device or emulator running API Level 21+
- Access to the Tracker API endpoint (configured in app/build.gradle or constants file).

### Installation

1. Clone the repository:

   ```
   git clone https://github.com/your-org/tracker-android-app.git
   cd tracker-android-app
   ```

2. Open in Android Studio:  
   Open the cloned directory in Android Studio.

3. Configure API Keys:  
   Ensure your Google Maps API Key and the backend API endpoint are correctly configured in the project's configuration files (e.g., local.properties or a dedicated config.kt file).

4. Build and Run:  
   Sync the Gradle project, select your desired device/emulator, and click the 'Run' button.

## 📝 Usage Guide

1. **Login**: Enter your assigned driver credentials to gain access.
2. **Start a Trip**: Navigate to the "Trips" section and tap the 'Start New Trip' button. Enter the required details (e.g., manifest ID, estimated destination).
3. **Real-time Tracking**: Once a trip is active, the app will automatically begin background location tracking. A persistent notification will indicate that tracking is active.
4. **Offline Mode**: If network connectivity is lost, the app will display an "Offline" indicator. All subsequent location points will be saved locally.
5. **End a Trip**: Tap the 'End Trip' button to conclude the trip. The app will finalize all data and upload any remaining offline points.
