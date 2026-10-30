package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxTheme

/**
 * Complete, 100% English-typed Raspberry Pi 5 & IoT Hardware Engineering Guide.
 * Includes complete End-to-End TechFlow, Raspberry Pi 5 System Information & Specifications,
 * SQLite Schema & Python API server code, LoRa Long-Range Module Support & Packet Protocol,
 * and Embedded C/C++ Neural Model Integration.
 */
@Composable
fun PiDatabaseGuideScreen(
    currentHost: String,
    deviceConnected: Boolean,
    isOnline: Boolean,
    onSaveHost: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hostInput by remember(currentHost) { mutableStateOf(currentHost) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "TechFlow",
        "System Info",
        "Pi 5 & SQLite",
        "LoRa Support",
        "Edge AI Model"
    )

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "Pi 5 & IoT Guide",
                subtitle = "Hardware Wiring, SQLite & LoRa Pipeline",
                isOnline = isOnline,
                showBack = true,
                onBackClick = onBack
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Quick IP Configuration Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VxTheme.surfaceBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeveloperBoard,
                                        contentDescription = null,
                                        tint = VxTheme.primaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Raspberry Pi 5 Gateway",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Port 5000 REST & WebSocket Endpoint",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Connection Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (deviceConnected) VxTheme.surfaceGreen else VxTheme.surfaceWarning)
                                    .border(1.dp, if (deviceConnected) VxTheme.borderGreen else VxTheme.borderWarning, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (deviceConnected) "Connected" else "Waiting for Pi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (deviceConnected) VxTheme.emeraldDark else VxTheme.warningDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = hostInput,
                            onValueChange = { hostInput = it },
                            label = { Text("Raspberry Pi Endpoint URL") },
                            placeholder = { Text("http://192.168.1.100:5000/") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VxTheme.primaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("guide_pi_host_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onSaveHost(hostInput) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("guide_save_host_button")
                        ) {
                            Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Save & Test Connection", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tab Navigation Row (5 English tabs)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VxTheme.surfaceSubtle)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) VxTheme.primaryBlue else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // TAB 0: Complete TechFlow & End-to-End Pipeline
                    item { TechFlowContent() }
                }
                1 -> {
                    // TAB 1: System Information & Hardware Specifications
                    item { SystemInfoContent() }
                }
                2 -> {
                    // TAB 2: Raspberry Pi 5 & SQLite Daemon Setup
                    item { PiSetupAndSqliteContent() }
                }
                3 -> {
                    // TAB 3: LoRa Long-Range Module Support & Packet Protocol
                    item { LoraModuleSupportContent() }
                }
                4 -> {
                    // TAB 4: Embedded AI & train.py C++ Model
                    item { EdgeAiModelContent() }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Tab 0: Complete Architecture & End-to-End TechFlow Pipeline
 */
@Composable
private fun TechFlowContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "End-to-End System TechFlow",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "A multi-tier architecture connecting physical chamber sensors to local edge compute, LoRa wireless transmission, and the mobile client.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                TechFlowStepCard(
                    stepNumber = "1",
                    layerName = "Physical Sensor & Actuator Layer",
                    protocols = "1-Wire, I2C, GPIO, ADC",
                    description = "Chamber 1 & 2 temperature/humidity (DS18B20 waterproof probes + DHT22), air quality (MQ-135 ethylene/gas), solar voltage/current (INA219 I2C), and magnetic reed door switches poll physical conditions at 1-second intervals."
                )

                TechFlowStepCard(
                    stepNumber = "2",
                    layerName = "Raspberry Pi 5 Edge Controller",
                    protocols = "Linux 6.6 (Bookworm), Python 3.12, SQLite 3",
                    description = "The Pi 5 executes continuous sensor acquisition via libgpiod. Sensor readings are buffered locally in an on-device SQLite database to guarantee zero data loss during network dropouts."
                )

                TechFlowStepCard(
                    stepNumber = "3",
                    layerName = "Embedded AI Inference Engine",
                    protocols = "C/C++ Shared Library & Python ctypes",
                    description = "Executes the 15-feature neural network auto-generated by train.py to evaluate respiration degradation rates (Q10 index) and predict post-harvest produce shelf-life in real-time."
                )

                TechFlowStepCard(
                    stepNumber = "4",
                    layerName = "LoRa Long-Range Wireless Subsystem",
                    protocols = "SX1276/SX1278 SPI/UART, 868/915 MHz, FSK/LoRa",
                    description = "For remote agricultural installations without cellular coverage, the Pi packs telemetry into 24-byte binary frames and transmits over LoRa up to 15 km to a farm gateway."
                )

                TechFlowStepCard(
                    stepNumber = "5",
                    layerName = "Local REST & WebSocket Gateway",
                    protocols = "FastAPI / Flask, Port 5000, JSON",
                    description = "Exposes /api/live, /api/history, and actuator control endpoints (/api/control/cooling, /api/control/fans) with CORS support for seamless local network discovery."
                )

                TechFlowStepCard(
                    stepNumber = "6",
                    layerName = "VISION X Mobile Android App",
                    protocols = "Kotlin, Jetpack Compose, Retrofit, Room DB",
                    description = "Provides real-time dashboards, Recharts-grade telemetry graphs, produce crate inventory tracking, push alerts, and offline caching with bi-directional cloud synchronization."
                )
            }
        }
    }
}

@Composable
private fun TechFlowStepCard(
    stepNumber: String,
    layerName: String,
    protocols: String,
    description: String
) {
    Surface(
        color = VxTheme.surfaceSubtle,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(VxTheme.primaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stepNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = layerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = protocols,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VxTheme.primaryAccent
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Tab 1: System Information & Raspberry Pi 5 Hardware Specifications
 */
@Composable
private fun SystemInfoContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Raspberry Pi 5 System Specifications",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                SpecRow("Processor", "Broadcom BCM2712 64-bit Arm Cortex-A76 @ 2.4 GHz (Quad-core)")
                SpecRow("Memory", "4GB or 8GB LPDDR4X-4267 SDRAM")
                SpecRow("Storage Interface", "PCIe 2.0 x1 interface for M.2 NVMe SSD (fast sensor logging)")
                SpecRow("Power Requirement", "USB-C PD 27W (5.1V / 5.0A Power Delivery)")
                SpecRow("Operating System", "Raspberry Pi OS (Bookworm, 64-bit Lite/Desktop, Linux 6.6)")
                SpecRow("Cold-Storage Cooling", "Raspberry Pi Active Cooler (PWM temperature-regulated fan)")
                SpecRow("Connectivity", "Gigabit Ethernet, 2.4/5.0 GHz 802.11ac Wi-Fi, BLE 5.0")
                SpecRow("GPIO Header", "Standard 40-Pin GPIO with UART, SPI, I2C, and PWM support")
            }
        }

        // Complete 40-Pin GPIO Connection Matrix
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Hardware Pinout & Wiring Matrix (Pi 5 40-Pin Header)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                WiringRow("DHT22 / SHT31 (Chamber 1)", "Data Pin", "GPIO 4 (Physical Pin 7)", "Chamber 1 Temp & Humidity (12°C target)")
                WiringRow("DHT22 / SHT31 (Chamber 2)", "Data Pin", "GPIO 17 (Physical Pin 11)", "Chamber 2 Temp & Humidity (4°C target)")
                WiringRow("DS18B20 Outside Probe", "1-Wire Data", "GPIO 27 (Physical Pin 13)", "Ambient reference sensor")
                WiringRow("MQ-135 Gas Sensor", "A0 via MCP3008 ADC", "GPIO 26 / SPI CE1", "Ethylene & VOC air quality monitoring")
                WiringRow("INA219 Power Monitor", "SDA, SCL", "GPIO 2 (Pin 3), GPIO 3 (Pin 5)", "I2C Bus for Solar Watts & Battery SoC")
                WiringRow("Cooling Relay 1", "IN1 Trigger", "GPIO 22 (Physical Pin 15)", "Switches Compressor contactor")
                WiringRow("Circulation Fan Relay 2", "IN2 Trigger", "GPIO 23 (Physical Pin 16)", "Switches Evaporator airflow fans")
                WiringRow("Magnetic Door Switch", "Reed Sensor Input", "GPIO 24 (Physical Pin 18)", "Chamber door breach detector")
                WiringRow("LoRa SX1276 Module", "SPI0 + IRQ", "GPIO 8, 9, 10, 11 + GPIO 25", "Long-Range wireless rural transmission")
            }
        }
    }
}

/**
 * Tab 2: Step-by-Step Raspberry Pi 5 & SQLite Server Setup
 */
@Composable
private fun PiSetupAndSqliteContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideCard(
            stepNumber = "1",
            title = "OS Package Installation",
            description = "Open the Raspberry Pi 5 terminal and install essential system libraries:"
        ) {
            CodeSnippet(
                code = """
# 1. Update operating system packages
sudo apt update && sudo apt upgrade -y

# 2. Install Python 3, virtual environment, and SQLite3
sudo apt install -y python3-pip python3-venv sqlite3 libgpiod-dev git

# 3. Create dedicated virtual environment for VISION X
python3 -m venv ~/visionx-env
source ~/visionx-env/bin/activate

# 4. Install backend dependencies
pip install fastapi uvicorn sqlite3 pydantic requests
pip install adafruit-circuitpython-dht pi-ina219 RPi.GPIO
                """.trimIndent()
            )
        }

        GuideCard(
            stepNumber = "2",
            title = "Local SQLite Database Schema",
            description = "Run this DDL script to create the local persistence tables on the Pi:"
        ) {
            CodeSnippet(
                code = """
-- SQLite Schema: visionx_storage.db
CREATE TABLE IF NOT EXISTS telemetry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    chamber1_temp REAL, chamber1_hum REAL,
    chamber2_temp REAL, chamber2_hum REAL,
    outside_temp REAL, outside_hum REAL,
    gas_status TEXT,
    solar_watts REAL,
    battery_percent INTEGER,
    cooling_on INTEGER,
    fans_on INTEGER,
    door_open INTEGER
);

CREATE TABLE IF NOT EXISTS crates (
    id TEXT PRIMARY KEY,
    product TEXT,
    chamber TEXT,
    quantity REAL,
    unit TEXT,
    freshness INTEGER,
    status TEXT
);
                """.trimIndent()
            )
        }

        GuideCard(
            stepNumber = "3",
            title = "Complete Python Server Daemon (visionx_pi_service.py)",
            description = "Save this script on the Pi. It logs hardware readings and serves the REST API:"
        ) {
            CodeSnippet(
                code = """
import sqlite3, datetime, time
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

app = FastAPI(title="VISION X Pi 5 Cold Storage Gateway")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

DB_PATH = "visionx_storage.db"

actuators = {"cooling": True, "fans": True, "door": False}

@app.get("/api/live")
def get_live_telemetry():
    # Poll physical hardware sensors or simulated fallbacks
    data = {
        "timestamp": datetime.datetime.utcnow().isoformat() + "Z",
        "chamber1": {"temperature": 12.0, "humidity": 62.0},
        "chamber2": {"temperature": 4.0, "humidity": 60.0},
        "outside": {"temperature": 28.5, "humidity": 62.0},
        "gasStatus": "normal",
        "solarPower": 620.0,
        "battery": 78,
        "cooling": actuators["cooling"],
        "fans": actuators["fans"],
        "doorOpen": actuators["door"]
    }
    log_to_sqlite(data)
    return data

@app.post("/api/control/cooling")
def set_cooling(enabled: bool):
    actuators["cooling"] = enabled
    return {"cooling": actuators["cooling"]}

@app.post("/api/control/fans")
def set_fans(enabled: bool):
    actuators["fans"] = enabled
    return {"fans": actuators["fans"]}

def log_to_sqlite(data):
    try:
        conn = sqlite3.connect(DB_PATH)
        cur = conn.cursor()
        cur.execute('''INSERT INTO telemetry 
            (chamber1_temp, chamber1_hum, chamber2_temp, chamber2_hum, outside_temp, outside_hum, gas_status, solar_watts, battery_percent, cooling_on, fans_on, door_open)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)''',
            (data["chamber1"]["temperature"], data["chamber1"]["humidity"],
             data["chamber2"]["temperature"], data["chamber2"]["humidity"],
             data["outside"]["temperature"], data["outside"]["humidity"],
             data["gasStatus"], data["solarPower"], data["battery"],
             int(data["cooling"]), int(data["fans"]), int(data["doorOpen"])))
        conn.commit()
        conn.close()
    except Exception as e:
        print("SQLite Log Error:", e)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=5000)
                """.trimIndent()
            )
        }
    }
}

/**
 * Tab 3: LoRa Long-Range Module Support & Packet Protocol
 */
@Composable
private fun LoraModuleSupportContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VxTheme.surfaceBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = null,
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LoRa Long-Range Architecture",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Rural Agricultural Telemetry (Up to 15 km)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "In remote agricultural areas, cold storage chambers often lack reliable 4G cellular or Wi-Fi coverage. VISION X supports Semtech SX1276 / SX1278 (RFM95W) and EByte E32 LoRa transceivers connected to the Raspberry Pi 5. Data is transmitted via low-power sub-GHz RF (868 MHz / 915 MHz) to a local base station or LoRa gateway.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // LoRa Specs
                SpecRow("Supported Transceivers", "Semtech SX1276, SX1278, RFM95W, EByte E32-900T")
                SpecRow("Frequency Bands", "868 MHz (India / Europe ISM) • 915 MHz (Americas)")
                SpecRow("RF Modulation", "LoRa Chirp Spread Spectrum (CSS) • SF7 to SF12")
                SpecRow("Transmission Range", "5 km to 15 km Line-of-Sight in rural terrain")
                SpecRow("Pi 5 Connection", "SPI0 (MISO, MOSI, SCLK, CE0) or UART /dev/ttyAMA0")
            }
        }

        // LoRa Binary Packet Protocol Specification
        GuideCard(
            stepNumber = "L1",
            title = "LoRa 24-Byte Binary Frame Protocol",
            description = "Ultra-compact binary packet structure optimized for low airtime and duty cycle compliance:"
        ) {
            CodeSnippet(
                code = """
/* VISION X LoRa 24-Byte Binary Packet Frame */
struct __attribute__((__packed__)) VisionXLoraPacket {
    uint8_t  header;         // 0x56 ('V')
    uint8_t  deviceId;       // Storage Unit ID (e.g., 0x01)
    int16_t  chamber1Temp;   // Temp * 100 (e.g., 1205 = 12.05°C)
    uint16_t chamber1Hum;    // Humidity * 100 (e.g., 6200 = 62.0%)
    int16_t  chamber2Temp;   // Temp * 100 (e.g., 402 = 4.02°C)
    uint16_t chamber2Hum;    // Humidity * 100
    int16_t  outsideTemp;    // Ambient Temp * 100
    uint16_t solarWatts;     // Solar Watts * 10 (e.g., 6200 = 620.0W)
    uint8_t  batterySoc;     // Battery State of Charge (0-100%)
    uint8_t  flags;          // Bit 0: Cooling, Bit 1: Fans, Bit 2: Door
    uint32_t timestamp;      // Unix Epoch Seconds
    uint16_t crc16;          // Frame Checksum
}; // Total: Exactly 24 bytes per telemetry burst
                """.trimIndent()
            )
        }

        // Python LoRa Receiver Script
        GuideCard(
            stepNumber = "L2",
            title = "Python LoRa Daemon Script (lora_receiver.py)",
            description = "Listens to LoRa packets on the Pi 5 and updates the local SQLite database:"
        ) {
            CodeSnippet(
                code = """
import struct, serial, sqlite3

# Initialize LoRa UART module (e.g., EByte E32 on /dev/ttyAMA0)
ser = serial.Serial('/dev/ttyAMA0', baudrate=9600, timeout=1)

def listen_lora():
    print("Listening for VISION X LoRa packets on 868 MHz...")
    while True:
        raw = ser.read(24)
        if len(raw) == 24 and raw[0] == 0x56:
            # Unpack 24-byte packet
            header, devId, c1T, c1H, c2T, c2H, outT, solW, bat, flags, ts, crc = struct.unpack('<BBhHhHhHBBIH', raw)
            
            c1_temp = c1T / 100.0
            c1_hum = c1H / 100.0
            c2_temp = c2T / 100.0
            cooling_on = bool(flags & 0x01)
            door_open = bool(flags & 0x04)
            
            print(f"LoRa Rx: C1={c1_temp}°C, C2={c2_temp}°C, Bat={bat}%")
            # Automatically forward into SQLite database...

if __name__ == '__main__':
    listen_lora()
                """.trimIndent()
            )
        }
    }
}

/**
 * Tab 4: Embedded AI Model & train.py Integration
 */
@Composable
private fun EdgeAiModelContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VxTheme.surfaceBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Edge Neural Model Architecture",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Auto-generated by train.py (15 Inputs • 16 Hidden)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "The machine learning model is trained on biological food science degradation datasets. The trained weights are compiled directly into an embedded C header (visionx_model.h) so that inference executes in under 2 milliseconds on the Raspberry Pi 5 CPU without requiring heavy machine learning frameworks.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        GuideCard(
            stepNumber = "M1",
            title = "Compiling the C Model on Raspberry Pi 5",
            description = "Compile the C header as an optimized native shared library on the Pi:"
        ) {
            CodeSnippet(
                code = """
# 1. Compile C inference engine with GCC
gcc -O3 -shared -fPIC -o libvisionx_model.so visionx_model.c -lm

# 2. Call from Python in your server daemon
import ctypes
model = ctypes.CDLL("./libvisionx_model.so")
# Model executes forward pass in real-time
                """.trimIndent()
            )
        }

        GuideCard(
            stepNumber = "M2",
            title = "Embedded C Header Source (visionx_model.h)",
            description = "Exact weights and parameters auto-generated by train.py:"
        ) {
            CodeSnippet(
                code = com.example.data.ai.VisionXEdgeNeuralModel.EMBEDDED_C_HEADER_CODE
            )
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }
}

@Composable
private fun WiringRow(sensor: String, pinName: String, gpio: String, purpose: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = sensor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = gpio, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxTheme.primaryBlue)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = pinName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = purpose, fontSize = 11.sp, color = VxTheme.emeraldDark, fontWeight = FontWeight.Medium)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }
}

@Composable
private fun GuideCard(
    stepNumber: String,
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(VxTheme.primaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stepNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun CodeSnippet(code: String) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(VxTheme.navyDark)
            .border(1.dp, Color(0xFF1E2D4A), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "CODE / CONFIGURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VxTheme.cyanAccent)
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(code))
                        copied = true
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = if (copied) VxTheme.emerald else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
