# Connecting Raspberry Pi Hardware to GREENCOLD App

Solve the "Waiting for Pi" status by addressing the two root causes identified during diagnostics:
1. **Pi Script**: Starting the integrated API server (`green_cold_server.py`) so Port 5000 is open and listening.
2. **Network Route**: Bridging the cloud-based browser emulator to the local Raspberry Pi using an instant free tunnel (ngrok) for PC testing, or testing directly on a physical phone on the same Wi-Fi.

## User Review & Critical Decisions

> [!IMPORTANT]
> **Why it showed "Waiting for Pi":**
> 1. `green_cold_b405ffc1.py` does not have a web server listening on Port 5000. It must be run with the integrated server (`green_cold_server.py`).
> 2. The browser preview runs on Google Cloud servers, which cannot directly dial into a private home IP address (`192.168.X.X`).
> 3. To connect to the browser emulator right now on your PC, a simple free 1-line tunnel command (`ngrok http 5000`) bridges the Pi directly to the cloud emulator.
> 4. To connect directly via `http://192.168.X.X:5000/`, use the installed APK on a physical Android smartphone connected to your home Wi-Fi.

- **Confirmed Decision 1**: Pi script will be upgraded to `green_cold_server.py` to open the port 5000 REST API.
- **Confirmed Decision 2**: Enable both direct local Wi-Fi (for physical phone) and instant cloud tunnel (for PC browser preview).

---

## 1. Overview & Core Concept

- **What It Delivers**: Real-time two-way synchronization between your physical DHT11 sensors, MQ137/MQ2 gas sensors, relays, and servos on the Raspberry Pi 5 and the GREENCOLD Android dashboard.
- **Dual Testing Paths**:
  - **Path A (Browser Emulator on PC)**: Connects via an instant HTTPS tunnel so you can test and demonstrate directly in the AI Studio browser window.
  - **Path B (Physical Android Phone)**: Connects directly to `http://192.168.x.x:5000/` over local Wi-Fi without needing any internet connection.

---

## 2. Implementation Steps

### Step 1: Start the API Server on Raspberry Pi
1. Stop the current script (`Ctrl + C` in the Pi terminal).
2. Save and run `green_cold_server.py`:
   ```bash
   pip install flask flask-cors
   python3 green_cold_server.py
   ```
3. Verify the server is running by testing in another terminal tab on the Pi:
   ```bash
   curl http://localhost:5000/api/live
   ```

### Step 2: Connect from PC Browser Emulator (Option A - Instant)
Because the browser emulator runs in Google Cloud:
1. On the Pi, start an instant tunnel:
   ```bash
   # Run free ngrok or localtunnel on the Pi:
   npx localtunnel --port 5000
   # OR:
   ngrok http 5000
   ```
2. Copy the generated public URL (e.g. `https://greencold-test.loca.lt/` or `https://xxxx.ngrok-free.app/`).
3. Paste this URL into the GREENCOLD app in Settings -> **Configure Raspberry Pi IP**.
4. The badge will instantly change to **🟢 Connected** right inside your PC browser emulator!

### Step 3: Connect from Physical Phone on Wi-Fi (Option B - Direct Local)
1. On your phone connected to the same Wi-Fi, open the app.
2. In Settings -> **Configure Raspberry Pi IP**, enter `http://<PI_IP>:5000/` (e.g., `http://192.168.1.45:5000/`).
3. Tap **Save & Test Connection**.

### Step 4: App Code Enhancements (`SettingsScreen.kt`, `PiDatabaseGuideScreen.kt`, `ColdStorageRepository.kt`)
1. **Auto-Sanitization**: Automatically format entered IPs (prepend `http://` if missing, ensure `:5000/` is properly formatted).
2. **Auto-Switch Mode**: Saving an IP automatically sets `ConnectionMode.RASPBERRY_PI` so users don't have to manually hunt for mode toggle buttons.
3. **In-App Diagnostic Guidance**: Add a clear note explaining the difference between Local Wi-Fi (for phone APK) and Tunnel URL (for PC browser preview), with copyable Pi commands.

---

## 3. Technical Architecture & Communication Flow

```
┌─────────────────────────────────────────────────────────────┐
│                   Testing In PC Browser                     │
│  (AI Studio Cloud Preview asia-east1.run.app)               │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS (e.g. https://xyz.ngrok-free.app/)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    Raspberry Pi 5 Server                    │
│   (Flask Port 5000 + Background DHT11 / MQ / TFT Loop)      │
└──────────────────────────────▲──────────────────────────────┘
                               │ HTTP (e.g. http://192.168.1.45:5000/)
┌──────────────────────────────┴──────────────────────────────┐
│                Testing on Physical Mobile Phone             │
│               (Connected to same Home Wi-Fi)                │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Verification

1. Verify `curl http://localhost:5000/api/live` outputs valid JSON on Raspberry Pi.
2. Enter the endpoint in the app settings: status indicator changes from "Waiting for Pi" to "Connected".
3. Verify live temperature from physical DHT11 and gas alert status from MQ137/MQ2 update on the Dashboard.
4. Run `compile_applet` and test suite (`gradle :app:testDebugUnitTest`).
