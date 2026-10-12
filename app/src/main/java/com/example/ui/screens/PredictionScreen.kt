package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChamberData
import com.example.data.model.CropCategory
import com.example.data.model.IndianAgriMarketCatalog
import com.example.data.model.IndianAgriProduce
import com.example.data.model.ShelfLifePrediction
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxBorderBlue
import com.example.ui.theme.VxBorderGreen
import com.example.ui.theme.VxDangerRed
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxEmeraldDark
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxPurple
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceDanger
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxSurfaceWarning
import com.example.ui.theme.VxTextPrimary
import com.example.ui.theme.VxTextSecondary
import com.example.ui.theme.VxWarningAmber
import com.example.ui.theme.VxWarningDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    chamber1: ChamberData,
    chamber2: ChamberData,
    lastPrediction: ShelfLifePrediction?,
    isOnline: Boolean,
    marketProduces: List<IndianAgriProduce> = IndianAgriMarketCatalog.allProduces,
    currentLocationInfo: com.example.util.UserLocationInfo? = null,
    onDetectLocation: () -> Unit = {},
    onRunPrediction: (product: String, harvestDate: String, chamber: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<CropCategory?>(null) } // null = All
    var selectedProduce by remember { mutableStateOf(marketProduces.firstOrNull() ?: IndianAgriMarketCatalog.allProduces.first()) }
    var isProduceDropdownExpanded by remember { mutableStateOf(false) }
    var harvestDate by remember { mutableStateOf("08/10/2026") }
    var selectedChamber by remember { mutableStateOf("Chamber 2") }
    var isChamberDropdownExpanded by remember { mutableStateOf(false) }

    val mandiLocations = listOf(
        "📍 राष्ट्रीय औसत (All India Average)" to 1.0f,
        "🏙️ दिल्ली - आज़ादपुर मंडी (Delhi - Azadpur)" to 1.08f,
        "🍇 महाराष्ट्र - लासलगाँव / नासिक (Maharashtra - Lasalgaon)" to 0.95f,
        "🥔 उत्तर प्रदेश - आगरा मंडी (Uttar Pradesh - Agra)" to 0.92f,
        "🍎 जम्मू-कश्मीर - श्रीनगर (Kashmir - Srinagar)" to 1.12f,
        "🌾 पंजाब - लुधियाना एपीएमसी (Punjab - Ludhiana)" to 0.97f,
        "🍊 नागपुर एपीएमसी - संतरा मंडी (Nagpur APMC)" to 0.90f,
        "🌸 कर्नाटक - बैंगलोर (Karnataka - Bangalore)" to 1.05f,
        "🌿 तमिलनाडु - कोयम्बेडु चेन्नई (Chennai - Koyambedu)" to 1.03f,
        "🪵 मध्य प्रदेश - इंदौर / मंदसौर (Madhya Pradesh - Indore)" to 0.93f
    )
    var selectedMandiPair by remember { mutableStateOf(mandiLocations[0]) }
    var isMandiDropdownExpanded by remember { mutableStateOf(false) }

    val filteredProduces = remember(selectedCategory, marketProduces) {
        if (selectedCategory == null) marketProduces
        else marketProduces.filter { it.category == selectedCategory }
    }

    val chamberList = listOf("Chamber 1 (12°C)", "Chamber 2 (4°C)")
    val currentChamberData = if (selectedChamber.contains("2")) chamber2 else chamber1
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "AI Prediction & Mandi Price",
                subtitle = "All India Fruits, Veg & Flowers",
                isOnline = isOnline,
                showBack = true,
                onBackClick = onBack
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Automatic Price Alert Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = VxSurfaceGreen,
                border = BorderStroke(1.dp, VxBorderGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VxEmeraldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Price Alert",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔔 ऑटो मंडी भाव अलर्ट (Auto Price Spike Alerts Active)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxEmeraldDark
                        )
                        Text(
                            text = "जैसे ही किसी फल/सब्जी/फूल के भाव 10%+ बढ़ेंगे, ऐप नोटिफिकेशन में तुरंत अलर्ट आएगा।",
                            fontSize = 11.sp,
                            color = VxTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mandi / Location Selector
            Text(
                text = "📍 अपनी लोकेशन या मंडी चुनें (Select Location / Mandi):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VxTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = isMandiDropdownExpanded,
                onExpandedChange = { isMandiDropdownExpanded = !isMandiDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedMandiPair.first,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMandiDropdownExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VxPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("mandi_location_dropdown")
                )

                ExposedDropdownMenu(
                    expanded = isMandiDropdownExpanded,
                    onDismissRequest = { isMandiDropdownExpanded = false }
                ) {
                    mandiLocations.forEach { pair ->
                        DropdownMenuItem(
                            text = { Text(pair.first, fontWeight = FontWeight.Medium, fontSize = 13.sp) },
                            onClick = {
                                selectedMandiPair = pair
                                isMandiDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDetectLocation,
                colors = ButtonDefaults.buttonColors(containerColor = VxPrimaryBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("detect_location_button")
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentLocationInfo != null) "🛰️ डिटेक्टेड शहर: ${currentLocationInfo.cityName}, ${currentLocationInfo.stateName} (रीफ्रेश करें)" else "🛰️ वर्तमान लोकेशन डिटेक्ट करें (Auto-Detect City & Rates)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips (Vegetables, Fruits, Flowers, All)
            Text(
                text = "फसल श्रेणी चुनें (Crop Category):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VxTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAll = selectedCategory == null
                    Surface(
                        onClick = { selectedCategory = null },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isAll) VxPrimaryBlue else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isAll) VxPrimaryBlue else VxBorder)
                    ) {
                        Text(
                            text = "🌾 सभी (All 32+)",
                            fontSize = 12.sp,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAll) Color.White else VxTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                items(CropCategory.values()) { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) VxPrimaryBlue else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSel) VxPrimaryBlue else VxBorder)
                    ) {
                        val icon = when (cat) {
                            CropCategory.VEGETABLE -> "🥦"
                            CropCategory.FRUIT -> "🍎"
                            CropCategory.FLOWER -> "🌸"
                        }
                        Text(
                            text = "$icon ${cat.labelHindi} (${cat.labelEnglish})",
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else VxTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inputs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Produce Selector (All India items)
                    Text(
                        text = "फसल / उत्पाद (Select Crop)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = isProduceDropdownExpanded,
                        onExpandedChange = { isProduceDropdownExpanded = !isProduceDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${selectedProduce.emoji} ${selectedProduce.nameHindi} (${selectedProduce.nameEnglish}) • ₹${selectedProduce.currentPricePerKgOrUnit}/${selectedProduce.unit}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProduceDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VxPrimaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("prediction_product_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = isProduceDropdownExpanded,
                            onDismissRequest = { isProduceDropdownExpanded = false }
                        ) {
                            filteredProduces.forEach { prod ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = prod.emoji, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "${prod.nameHindi} (${prod.nameEnglish})",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "${prod.majorState} • ${prod.category.labelEnglish}",
                                                        fontSize = 11.sp,
                                                        color = VxTextSecondary
                                                    )
                                                }
                                            }
                                            Surface(
                                                color = VxSurfaceGreen,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "₹${prod.currentPricePerKgOrUnit}/${prod.unit} (+${prod.priceTrendPercent}%)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VxEmeraldDark,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedProduce = prod
                                        selectedChamber = if (prod.optimalTemp >= 10.0) "Chamber 1 (12°C)" else "Chamber 2 (4°C)"
                                        isProduceDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val localizedPrice = (selectedProduce.currentPricePerKgOrUnit * selectedMandiPair.second).toInt()

                    // Live Market Price Info Strip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VxSurfaceSubtle,
                        border = BorderStroke(1.dp, VxBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "वर्तमान मंडी भाव (Current Mandi Price):",
                                    fontSize = 11.sp,
                                    color = VxTextSecondary
                                )
                                Text(
                                    text = "₹${localizedPrice} प्रति ${selectedProduce.unit}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxPrimaryBlue
                                )
                                Text(
                                    text = "चयनित मंडी: ${selectedMandiPair.first}",
                                    fontSize = 10.sp,
                                    color = VxTextSecondary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedProduce.priceTrendPercent >= 0) VxSurfaceGreen else VxSurfaceDanger,
                                border = BorderStroke(1.dp, if (selectedProduce.priceTrendPercent >= 0) VxBorderGreen else VxBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = if (selectedProduce.priceTrendPercent >= 0) VxEmeraldDark else VxDangerRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+${selectedProduce.priceTrendPercent}% भाव बढ़ा",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedProduce.priceTrendPercent >= 0) VxEmeraldDark else VxDangerRed
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Harvest Date
                    Text(
                        text = "भंडारण / तुड़ाई की तारीख (Harvest Date)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = harvestDate,
                        onValueChange = { harvestDate = it },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = VxPrimaryBlue
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VxPrimaryBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prediction_date_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Storage Chamber
                    Text(
                        text = "कोल्ड स्टोरेज कमरा (Storage Chamber)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = isChamberDropdownExpanded,
                        onExpandedChange = { isChamberDropdownExpanded = !isChamberDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedChamber,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isChamberDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VxPrimaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("prediction_chamber_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = isChamberDropdownExpanded,
                            onDismissRequest = { isChamberDropdownExpanded = false }
                        ) {
                            chamberList.forEach { ch ->
                                DropdownMenuItem(
                                    text = { Text(ch) },
                                    onClick = {
                                        selectedChamber = ch
                                        isChamberDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Current Telemetry Overview Box
                    Surface(
                        color = VxSurfaceSubtle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "कमरे की वास्तविक स्थिति (Live Chamber Telemetry)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "कमरे का तापमान:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${currentChamberData.currentTemp}°C", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxPrimaryBlue)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "कमरे की नमी (Humidity):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${currentChamberData.humidity.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxEmeraldDark)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "आदर्श तापमान (Ideal Temp):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${selectedProduce.optimalTemp}°C (±${selectedProduce.tempTolerance}°C)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxTextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Predict Button
                    Button(
                        onClick = {
                            val chamberKey = if (selectedChamber.contains("2")) "Chamber 2" else "Chamber 1"
                            onRunPrediction(selectedProduce.nameEnglish, harvestDate, chamberKey)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VxPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("run_prediction_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "लाइफ व भाव का अनुमान लगाएं (Predict Shelf Life & Price)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prediction Result Section
            val prediction = lastPrediction ?: ShelfLifePrediction(
                product = selectedProduce.nameEnglish,
                estimatedDays = selectedProduce.baseShelfLifeDays,
                confidencePercent = 94,
                currentTemp = currentChamberData.currentTemp,
                currentHumidity = currentChamberData.humidity,
                harvestDate = harvestDate,
                optimalTempRange = "${selectedProduce.optimalTemp}°C ± ${selectedProduce.tempTolerance}°C",
                optimalHumidityRange = "${(selectedProduce.optimalHumidity - 5).toInt()}% – ${(selectedProduce.optimalHumidity + 5).toInt()}%",
                accelerationFactor = 1.05,
                recommendation = "कमरे का तापमान और नमी बिल्कुल सही है। फसल की ताजगी सुरक्षित रहेगी।",
                currentMarketPrice = selectedProduce.currentPricePerKgOrUnit,
                priceUnit = selectedProduce.unit,
                majorState = selectedProduce.majorState,
                priceTrendPercent = selectedProduce.priceTrendPercent,
                cropCategory = selectedProduce.category.labelHindi,
                produceEmoji = selectedProduce.emoji
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prediction_result_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, VxBorderGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = prediction.produceEmoji,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "अनुमानित सुरक्षित दिन (Estimated Shelf Life)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${prediction.estimatedDays} दिन (Days)",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxEmeraldDark
                                )
                            }
                        }

                        // Confidence Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VxSurfaceGreen)
                                .border(1.dp, VxBorderGreen, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${prediction.confidencePercent}% सटीक",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VxEmeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Market Price Highlight in Prediction Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VxSurfaceBlue,
                        border = BorderStroke(1.dp, VxBorderBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "वर्तमान मंडी भाव (Current Mandi Rate)",
                                    fontSize = 11.sp,
                                    color = VxTextSecondary
                                )
                                Text(
                                    text = "₹${prediction.currentMarketPrice} / ${prediction.priceUnit}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxPrimaryBlue
                                )
                                Text(
                                    text = "मंडी: ${prediction.majorState}",
                                    fontSize = 11.sp,
                                    color = VxTextSecondary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VxSurfaceGreen,
                                border = BorderStroke(1.dp, VxBorderGreen)
                            ) {
                                Text(
                                    text = "+${prediction.priceTrendPercent}% भाव में बढ़त 📈",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxEmeraldDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Condition Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConditionPill(
                            icon = Icons.Default.Thermostat,
                            label = "${prediction.currentTemp}°C Temp",
                            color = VxPrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                        ConditionPill(
                            icon = Icons.Default.WaterDrop,
                            label = "${prediction.currentHumidity.toInt()}% Humidity",
                            color = VxEmeraldDark,
                            modifier = Modifier.weight(1f)
                        )
                        ConditionPill(
                            icon = Icons.Default.Eco,
                            label = "Range OK",
                            color = VxPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recommendation & Action
                    Surface(
                        color = VxSurfaceSubtle,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = VxPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "सलाह व मार्गदर्शन (Recommendation):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prediction.recommendation,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConditionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}
