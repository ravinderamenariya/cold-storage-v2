package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Crate
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxDangerRed
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCrateScreen(
    nextCrateNumber: Int = 5,
    isOnline: Boolean,
    onSaveCrate: (Crate) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var crateId by remember { mutableStateOf("CR00$nextCrateNumber") }
    var selectedProduct by remember { mutableStateOf("Tomato") }
    var isProductDropdownOpen by remember { mutableStateOf(false) }

    var quantityStr by remember { mutableStateOf("150") }
    var selectedUnit by remember { mutableStateOf("kg") }
    var selectedChamber by remember { mutableStateOf("Chamber 2") } // 4°C
    var expectedShelfLifeDaysStr by remember { mutableStateOf("14") }
    var targetTempStr by remember { mutableStateOf("4.0") }
    var targetHumidityStr by remember { mutableStateOf("75.0") }
    var notes by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val productsList = listOf("Tomato", "Mango", "Banana", "Flower", "Apple", "Grapes", "Potato", "Spinach", "Strawberry")
    val unitsList = listOf("kg", "boxes", "crates", "tons")
    val chambersList = listOf("Chamber 1", "Chamber 2")

    fun onProductChanged(product: String) {
        selectedProduct = product
        when (product) {
            "Mango", "Banana" -> {
                selectedChamber = "Chamber 1"
                targetTempStr = "12.0"
                targetHumidityStr = "85.0"
                expectedShelfLifeDaysStr = if (product == "Mango") "21" else "12"
            }
            "Tomato", "Flower", "Strawberry", "Spinach" -> {
                selectedChamber = "Chamber 2"
                targetTempStr = "4.0"
                targetHumidityStr = "70.0"
                expectedShelfLifeDaysStr = if (product == "Flower") "7" else "14"
            }
            else -> {
                selectedChamber = "Chamber 2"
                targetTempStr = "4.0"
                targetHumidityStr = "80.0"
                expectedShelfLifeDaysStr = "20"
            }
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "Add Crate",
                subtitle = "Register Produce Batch",
                isOnline = isOnline,
                showBack = true,
                onBackClick = onCancel
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
            if (validationError != null) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError!!,
                        color = VxDangerRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Crate ID
            OutlinedTextField(
                value = crateId,
                onValueChange = { crateId = it },
                label = { Text("Crate ID *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VxPrimaryBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_crate_id_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Product Dropdown
            ExposedDropdownMenuBox(
                expanded = isProductDropdownOpen,
                onExpandedChange = { isProductDropdownOpen = !isProductDropdownOpen }
            ) {
                OutlinedTextField(
                    value = selectedProduct,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Product / Produce *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProductDropdownOpen) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VxPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("add_crate_product_dropdown")
                )

                ExposedDropdownMenu(
                    expanded = isProductDropdownOpen,
                    onDismissRequest = { isProductDropdownOpen = false }
                ) {
                    productsList.forEach { prod ->
                        DropdownMenuItem(
                            text = { Text(prod) },
                            onClick = {
                                onProductChanged(prod)
                                isProductDropdownOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity & Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VxPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_crate_quantity_input")
                )

                OutlinedTextField(
                    value = selectedUnit,
                    onValueChange = { selectedUnit = it },
                    label = { Text("Unit") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VxPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_crate_unit_input")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Storage Chamber Selector (Chamber 1 vs Chamber 2)
            Text(
                text = "Storage Chamber *",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                chambersList.forEach { chamber ->
                    val isSelected = chamber == selectedChamber
                    Surface(
                        onClick = {
                            selectedChamber = chamber
                            targetTempStr = if (chamber == "Chamber 1") "12.0" else "4.0"
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) VxPrimaryBlue else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VxPrimaryBlue else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("select_chamber_$chamber")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = chamber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (chamber == "Chamber 1") "Target: 12°C" else "Target: 4°C",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Target Temp & Target Humidity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = targetTempStr,
                    onValueChange = { targetTempStr = it },
                    label = { Text("Target Temp (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VxPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = targetHumidityStr,
                    onValueChange = { targetHumidityStr = it },
                    label = { Text("Target Humidity (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VxPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expected Shelf Life Days
            OutlinedTextField(
                value = expectedShelfLifeDaysStr,
                onValueChange = { expectedShelfLifeDaysStr = it },
                label = { Text("Expected Shelf Life (Days)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VxPrimaryBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_crate_shelf_life_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Batch / Storage Notes (Optional)") },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VxPrimaryBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_crate_notes_input")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Add Crate Button
            Button(
                onClick = {
                    val qty = quantityStr.toDoubleOrNull()
                    if (crateId.isBlank()) {
                        validationError = "Please specify a Crate ID"
                        return@Button
                    }
                    if (qty == null || qty <= 0) {
                        validationError = "Please enter a valid numeric quantity"
                        return@Button
                    }

                    val targetTemp = targetTempStr.toDoubleOrNull() ?: 12.0
                    val targetHum = targetHumidityStr.toDoubleOrNull() ?: 80.0
                    val shelfLife = expectedShelfLifeDaysStr.toIntOrNull() ?: 14

                    val newCrate = Crate(
                        id = crateId.trim().uppercase(),
                        product = selectedProduct,
                        chamber = selectedChamber,
                        quantity = qty,
                        unit = selectedUnit.trim(),
                        storageTimestamp = System.currentTimeMillis(),
                        expectedShelfLifeDays = shelfLife,
                        targetTemp = targetTemp,
                        targetHumidity = targetHum,
                        freshnessPercent = 100,
                        status = "Healthy",
                        notes = notes.trim()
                    )

                    onSaveCrate(newCrate)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VxNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_add_crate_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Crate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
