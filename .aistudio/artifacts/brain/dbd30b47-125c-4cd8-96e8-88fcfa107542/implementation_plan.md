# Global Firebase Cloud Sync for GREENCOLD Smart Cold Storage

Connect Raspberry Pi 5 hardware directly to Google Firebase Cloud so farmers, mandi traders, and cold storage operators can monitor chambers, gas levels, and alerts in real time from anywhere in the world via phone or web.

## User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed that telemetry should be uploaded directly from the Raspberry Pi to Firebase Cloud, and that users worldwide will access live data through Firebase Cloud Sync.

- **Confirmed Decision 1 (Access Anywhere)**: Global cloud sync via Firebase Firestore so anyone on mobile (4G/5G/any Wi-Fi) or web preview can monitor chambers without needing to be on the local Wi-Fi.
- **Confirmed Decision 2 (Raspberry Pi Cloud Push)**: The Raspberry Pi Python script will push real-time sensor readings directly to Firebase Firestore, eliminating port forwarding, static IP, or localtunnel/ngrok requirements.
- **Recommended Default (Zero-Dependency Pi REST Pipeline)**: Use Python `requests` against Firestore REST API for the Pi script so the Raspberry Pi requires zero heavy C-extensions or complex gRPC dependencies, guaranteeing smooth execution on Raspberry Pi OS.

---

## 1. Overview & Core Concept

- **What It Does**: Transforms the GREENCOLD control system from a local-only LAN setup into a globally connected IoT cloud solution. The Raspberry Pi continuously reads physical sensors (3x DHT11, MQ-137 ammonia, MQ-2 smoke/flammable gas) and publishes telemetry to Firebase Firestore every 2–3 seconds. The Android app and web preview listen to real-time snapshot streams and instantly reflect live temperature, humidity, gas PPM, and fan/servo status.
- **Target Audience / Persona**:
  - Cold storage operators monitoring temperature stability across Chamber 1 (Garlic/Onion) and Chamber 2 (Potato/Produce) from home or the mandi.
  - Remote inspectors checking ammonia gas spikes or ventilation safety in real time.
  - Multi-device users accessing the web preview link or Android APK without configuring router ports.
- **Key Value**: True "access from anywhere" reliability. Physical cold storage hardware in Udaipur or anywhere else streams directly to Google Cloud, with sub-second sync to mobile devices worldwide.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Global Live Dashboard**: User opens the app anywhere (cellular data or home Wi-Fi). The app immediately connects to Firebase Cloud Sync. A live cloud badge (`☁️ Cloud Live • Pi Connected`) displays the hardware heartbeat timestamp (e.g., "Updated 2s ago").
  2. **Seamless Mode Selector**: In Settings and the top app bar, users can seamlessly toggle between:
     - **☁️ Cloud Sync (Firebase)**: Default global mode for remote monitoring.
     - **📡 Direct Local Wi-Fi**: LAN mode (`http://192.168.x.x:5000/`) for zero-internet local facility control.
     - **🧪 Hardware Simulation**: Offline demonstration mode.
  3. **Raspberry Pi Cloud Setup Hub**: A dedicated tab in the guide showing the ready-to-run Python script (`green_cold_cloud.py`) preconfigured with the Firebase project ID, single-command installation (`pip install requests Adafruit-DHT`), and a live cloud verification ping.
  4. **Bidirectional Control Sync**: Toggling fan relays or adjusting servo vent angles from the app writes control flags to Firestore, which the Raspberry Pi reads and enacts on physical GPIO pins.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Industrial IoT Control Dashboard, high-contrast agricultural tech with dark teal accents and crisp M3 cards.
  - *Status Indicators*: Glowing green pulsing dot for active cloud stream (`Live < 5s`), amber badge for intermittent network (`Last seen 30s ago`), and clean diagnostic warning if the Pi is powered off.
  - *Chamber Visuals*: Visual cards with real-time temperature gauges, humidity levels, and threshold alert badges.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct Firestore REST vs. gRPC Admin SDK on Raspberry Pi**
  - *Chosen Approach*: Lightweight HTTP REST with Firebase API Key / Firestore REST endpoint in Python.
  - *Why*: Raspberry Pi OS often encounters compilation and wheel incompatibility issues with large gRPC / protobuf dependencies in `firebase-admin`. Standard HTTP `requests` is lightweight, robust, and works instantly without crashing the Pi.
  - *Alternatives Considered*: Native `firebase-admin` SDK (heavy, frequently breaks on ARM32/ARM64 Python environments); Cloud Run intermediate bridge (extra complexity and hosting configuration).
- **Decision 2: App Data Layer Architecture**
  - *Chosen Approach*: Add `FirebaseFirestore` snapshot listener in `ColdStorageRepository` that updates the existing `_telemetryState` StateFlow.
  - *Why*: All existing screens (Dashboard, Chamber 1, Chamber 2, Analytics, Voice Assistant, Agronomist AI) already consume `repository.telemetryFlow`. Updating the repository instantly illuminates the entire app with live cloud data without breaking any existing features.
- **Decision 3: Offline & Local Fallback Retention**
  - *Chosen Approach*: Keep the existing Direct Local Wi-Fi REST mode and Simulation mode alongside Cloud Sync.
  - *Why*: If internet connectivity at the physical cold storage drops, operators on-site can still switch to Local Wi-Fi mode directly over the facility router.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│             Physical Raspberry Pi 5                    │
│  Sensors: 3x DHT11, MQ-137, MQ-2, Relays, Servos       │
│  Script: green_cold_cloud.py                           │
└──────────────────────────┬─────────────────────────────┘
                           │ HTTP PATCH / POST (every 2.5s)
                           ▼
┌────────────────────────────────────────────────────────┐
│             Google Firebase Firestore                  │
│                                                        │
│  Collection: "cold_storage"                            │
│  ├── Document: "live_telemetry"                        │
│  │     { ch1_temp, ch1_hum, ch1_gas, fan1, ... }       │
│  └── Document: "remote_commands"                       │
│        { fan1_override, fan2_override, ... }           │
└──────────────────────────┬─────────────────────────────┘
                           │ Realtime Snapshot Stream
                           ▼
┌────────────────────────────────────────────────────────┐
│             GREENCOLD Android & Web App                │
│                                                        │
│  ColdStorageRepository                                 │
│  └── snapshotListener("live_telemetry")                │
│       └── emits to MutableStateFlow<TelemetryData>     │
│            ├── DashboardScreen                         │
│            ├── ChamberDetailsScreen                    │
│            ├── VoiceAssistant (Gemini Agronomist)      │
│            └── Analytics & Alert History               │
└────────────────────────────────────────────────────────┘
```

### Data Schema (`cold_storage/live_telemetry`)

```json
{
  "ch1_temp": 4.2,
  "ch1_humidity": 68.5,
  "ch1_gas_ppm": 12.4,
  "ch2_temp": 8.1,
  "ch2_humidity": 84.0,
  "ch2_gas_ppm": 18.0,
  "ambient_temp": 28.5,
  "ambient_humidity": 45.0,
  "fan1_state": true,
  "fan2_state": false,
  "servo1_angle": 45,
  "servo2_angle": 0,
  "power_saving_mode": false,
  "alert_count": 0,
  "source": "Raspberry Pi 5 Hardware",
  "updated_at": 1730450000000
}
```

### Planned Implementation Steps

1. **Gradle & Dependencies**: Uncomment `implementation(libs.firebase.firestore)` in `app/build.gradle.kts`.
2. **Repository Enhancement**: In `ColdStorageRepository.kt`, add Firestore snapshot listener listening to `cold_storage/live_telemetry` and command writer for `cold_storage/remote_commands`.
3. **Connection Mode Management**: Add `ConnectionMode.CLOUD_SYNC` as a first-class citizen with automatic fallback if cloud data is absent.
4. **Pi Cloud Script Generator in UI**: Update `PiDatabaseGuideScreen.kt` with the complete, copy-pasteable `green_cold_cloud.py` script containing sensor loops + Firebase REST publisher.
5. **UI Cloud Status Badging**: Display real-time cloud connection health and heartbeat indicators across the app header and settings.
6. **Build & Automated Testing**: Validate with `compile_applet` and local Robolectric / JUnit unit tests.
