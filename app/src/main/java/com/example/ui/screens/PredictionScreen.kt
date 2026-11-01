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
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber
import com.example.ui.theme.VxWarningDark
import com.example.data.ai.GeminiMarketPrediction
import com.example.data.model.MandiCatalog
import com.example.data.model.MandiLocation
import com.example.ui.components.GoogleMandiMapView
import androidx.compose.material3.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    chamber1: ChamberData,
    chamber2: ChamberData,
    lastPrediction: ShelfLifePrediction?,
    geminiPrediction: GeminiMarketPrediction? = null,
    isAiPredicting: Boolean = false,
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
        "🏰 उदयपुर - सविना कृषि उपज मंडी (Udaipur - Savina Mandi)" to 0.96f,
        "🌾 फतहनगर मंडी - उदयपुर (Fatehnagar Mandi - Udaipur)" to 0.94f,
        "🍅 मावली / डबोक हब (Mavli & Dabok Hub - Udaipur)" to 0.95f,
        "🌼 नाथद्वारा व राजसमंद मंडी (Nathdwara & Rajsamand)" to 0.97f,
        "🧄 चित्तौड़गढ़ व बड़ी सादड़ी (Chittorgarh & Bari Sadri)" to 0.95f,
        "🏜️ राजस्थान - जयपुर मुहाना मंडी (Jaipur - Muhana Mandi)" to 0.96f,
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
                color = VxTheme.surfaceGreen,
                border = BorderStroke(1.dp, VxTheme.borderGreen),
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
                            .background(VxTheme.emeraldDark),
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
                            color = VxTheme.emeraldDark
                        )
                        Text(
                            text = "जैसे ही किसी फल/सब्जी/फूल के भाव 10%+ बढ़ेंगे, ऐप नोटिफिकेशन में तुरंत अलर्ट आएगा।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
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
                color = MaterialTheme.colorScheme.onSurface
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
                        focusedBorderColor = VxTheme.primaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                            text = { Text(pair.first, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface) },
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
                colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("detect_location_button")
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentLocationInfo != null) "🛰️ डिटेक्टेड शहर: ${currentLocationInfo.cityName}, ${currentLocationInfo.stateName} (रीफ्रेश करें)" else "🛰️ वर्तमान लोकेशन डिटेक्ट करें (Auto-Detect City & Rates)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips (Vegetables, Fruits, Flowers, All)
            Text(
                text = "फसल श्रेणी चुनें (Crop Category):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                        color = if (isAll) VxTheme.primaryBlue else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isAll) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "🌾 सभी (All 32+)",
                            fontSize = 12.sp,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAll) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                items(CropCategory.values()) { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) VxTheme.primaryBlue else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSel) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline)
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
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
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
                                focusedBorderColor = VxTheme.primaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "${prod.majorState} • ${prod.category.labelEnglish}",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            Surface(
                                                color = VxTheme.surfaceGreen,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "₹${prod.currentPricePerKgOrUnit}/${prod.unit} (+${prod.priceTrendPercent}%)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VxTheme.emeraldDark,
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
                        color = VxTheme.surfaceSubtle,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${localizedPrice} प्रति ${selectedProduce.unit}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.primaryBlue
                                )
                                Text(
                                    text = "चयनित मंडी: ${selectedMandiPair.first}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedProduce.priceTrendPercent >= 0) VxTheme.surfaceGreen else MaterialTheme.colorScheme.errorContainer,
                                border = BorderStroke(1.dp, if (selectedProduce.priceTrendPercent >= 0) VxTheme.borderGreen else MaterialTheme.colorScheme.error)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = if (selectedProduce.priceTrendPercent >= 0) VxTheme.emeraldDark else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+${selectedProduce.priceTrendPercent}% भाव बढ़ा",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedProduce.priceTrendPercent >= 0) VxTheme.emeraldDark else MaterialTheme.colorScheme.error
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
                                tint = VxTheme.primaryBlue
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VxTheme.primaryBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                                focusedBorderColor = VxTheme.primaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                                    text = { Text(ch, color = MaterialTheme.colorScheme.onSurface) },
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
                        color = VxTheme.surfaceSubtle,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                                Text(text = "${currentChamberData.currentTemp}°C", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxTheme.primaryBlue)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "कमरे की नमी (Humidity):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${currentChamberData.humidity.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VxTheme.emeraldDark)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "आदर्श तापमान (Ideal Temp):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${selectedProduce.optimalTemp}°C (±${selectedProduce.tempTolerance}°C)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
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
                        colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("run_prediction_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
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
                border = BorderStroke(1.dp, VxTheme.borderGreen),
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
                                    color = VxTheme.emeraldDark
                                )
                            }
                        }

                        // Confidence Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VxTheme.surfaceGreen)
                                .border(1.dp, VxTheme.borderGreen, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${prediction.confidencePercent}% सटीक",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VxTheme.emeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Market Price Highlight in Prediction Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VxTheme.surfaceBlue,
                        border = BorderStroke(1.dp, VxTheme.borderBlue),
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${prediction.currentMarketPrice} / ${prediction.priceUnit}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.primaryBlue
                                )
                                Text(
                                    text = "मंडी: ${prediction.majorState}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VxTheme.surfaceGreen,
                                border = BorderStroke(1.dp, VxTheme.borderGreen)
                            ) {
                                Text(
                                    text = "+${prediction.priceTrendPercent}% भाव में बढ़त 📈",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.emeraldDark,
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
                            color = VxTheme.primaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                        ConditionPill(
                            icon = Icons.Default.WaterDrop,
                            label = "${prediction.currentHumidity.toInt()}% Humidity",
                            color = VxTheme.emeraldDark,
                            modifier = Modifier.weight(1f)
                        )
                        ConditionPill(
                            icon = Icons.Default.Eco,
                            label = "Range OK",
                            color = VxTheme.purple,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recommendation & Action
                    Surface(
                        color = VxTheme.surfaceSubtle,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = VxTheme.primaryBlue,
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

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 1. GEMINI 3.8 FLASH AI ADVISORY CARD
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_ai_advisory_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, VxTheme.purple.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VxTheme.purple.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gemini AI",
                                    tint = VxTheme.purple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Gemini 3.8 Flash AI Advisory",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Real-time Agronomic & Market Intelligence",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isAiPredicting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = VxTheme.purple
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = VxTheme.purple.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, VxTheme.purple.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = geminiPrediction?.spoilageRiskLevel?.let { "$it Risk" } ?: "Low Risk",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.purple,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val advisoryText = geminiPrediction?.aiAnalysis
                        ?: "फसल की जैविक श्वसन दर स्थिर है। वर्तमान तापमान और आर्द्रता स्तर के तहत यह उपज अगले 5-7 दिनों तक उच्च गुणवत्ता बनाए रखेगी।"

                    Text(
                        text = advisoryText,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selling Window & Price Trajectory Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VxTheme.surfaceSubtle,
                        border = BorderStroke(1.dp, VxTheme.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = VxTheme.primaryBlue,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "सर्वोत्तम बिक्री समय (Best Selling Window):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = geminiPrediction?.recommendedSellingWindow ?: "अगले 4 से 6 दिनों के भीतर मंडी में बिक्री करें।",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTheme.primaryBlue
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = VxTheme.emerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "मंडी भाव रुझान (Mandi Price Forecast):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = geminiPrediction?.expectedPriceTrend ?: "निकटवर्ती थोक मंडी में मांग स्थिर व भाव में 5-8% वृद्धि संभावित है।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (!geminiPrediction?.storageAdjustmentAdvice.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 सुझाव: ${geminiPrediction?.storageAdjustmentAdvice}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 2. GOOGLE MAPS & APMC MANDI ROUTE CARD
            // ==========================================
            val targetMandi = MandiCatalog.allMandis.find {
                it.name.contains(currentLocationInfo?.nearestMandi ?: "", ignoreCase = true) ||
                it.city.contains(currentLocationInfo?.cityName ?: "", ignoreCase = true)
            } ?: MandiCatalog.findNearestMandi(
                currentLocationInfo?.latitude ?: 24.5854,
                currentLocationInfo?.longitude ?: 73.7125
            )

            GoogleMandiMapView(
                mandi = targetMandi,
                storageLat = currentLocationInfo?.latitude ?: 24.5854,
                storageLng = currentLocationInfo?.longitude ?: 73.7125,
                storageName = "VisionX Solar Cold Storage Hub - Dabok / Savina, Udaipur, Rajasthan",
                produceName = selectedProduce.nameEnglish,
                cropPrice = prediction.currentMarketPrice.toDouble(),
                priceUnit = selectedProduce.unit
            )

            Spacer(modifier = Modifier.height(28.dp))
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
