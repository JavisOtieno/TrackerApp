# Tracker – Android Driver App

> Native Android app for drivers to log in, record trips, and provide real-time & offline GPS tracking for the Tracker fleet management system.

---

## Features
- Driver authentication (login / logout)
- Start / pause / stop trips with automatic metadata (start/end time, distance)
- Continuous background location capture (foreground & background)
- Offline-first: caches GPS points while offline and syncs when online
- Trip history (local + server)
- Automatic reconnection & conflict-resilient sync
- Low-battery / adaptive sampling to conserve power
- Optional photo/cargo proof upload per trip or delivery
- Error reporting & logs

---

## Quick Start (Android Studio)
1. Clone the repo:  
   ```bash
   git clone https://github.com/yourusername/tracker-android.git
   cd tracker-android
Open the project in Android Studio.

Configure local.properties and update app/src/main/res/values/strings.xml with:

API_BASE_URL — Tracker backend API

MAPS_API_KEY — Google Maps / Mapbox key

Build and run on a device (recommended) or emulator (location mocking required).

Requirements & Permissions
Android SDK 29+ (Android 11+ recommended)

Permissions:

ACCESS_FINE_LOCATION

ACCESS_COARSE_LOCATION

ACCESS_BACKGROUND_LOCATION (if tracking in background)

INTERNET

FOREGROUND_SERVICE

Optional: Storage permissions for photo uploads

Architecture
MVVM / Clean Architecture

Modules:

AuthModule — login, token refresh

LocationService — foreground service for GPS

LocalCache — Room DB for offline GPS storage

SyncManager — WorkManager for reliable uploads

Network — Retrofit / OkHttp

MapScreen — display current route & past trips

UploadManager — handles photos / POD uploads

Offline & Sync Behavior
GPS points cached locally when offline

SyncManager uploads when network is available

Batched sync to reduce API calls

Conflict handling ensures no duplicate points

API Examples
POST /api/auth/login — { email, password }

POST /api/trips/start — { driver_id, vehicle_id, started_at }

POST /api/trips/{trip_id}/points — { points: [{lat, lng, timestamp}] }

POST /api/trips/{trip_id}/end — { ended_at, total_distance }

POST /api/uploads/photo — multipart upload

Security
HTTPS for all API calls

Tokens stored securely (EncryptedSharedPreferences / Keystore)

Validate server certificates

Testing
Unit tests: ViewModel & repositories

Integration: Robolectric & instrumentation for LocationService

Manual QA: offline/online sync, map rendering, push notifications

Build & Release
Configure signing in Gradle

Produce release APK / AAB

Provide background location justification for Play Store

Contributing
Fork repo

Create branch feature/xyz

Submit Pull Request

License
MIT License

yaml
Copy code

---

If you want, I can now prepare the **full Laravel Blade Admin Dashboard README.md** in
