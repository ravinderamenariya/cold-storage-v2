package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Crate
import com.example.ui.components.CrateCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber

@Composable
fun CratesScreen(
    crates: List<Crate>,
    marketProduces: List<com.example.data.model.IndianAgriProduce> = emptyList(),
    isOnline: Boolean,
    onCrateClick: (String) -> Unit,
    onAddCrateClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Healthy", "Warning", "Spoiled"

    val filteredCrates = remember(crates, searchQuery, selectedFilter) {
        crates.filter { crate ->
            val matchesSearch = crate.id.contains(searchQuery, ignoreCase = true) ||
                    crate.product.contains(searchQuery, ignoreCase = true) ||
                    crate.chamber.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Healthy" -> crate.status.equals("Healthy", ignoreCase = true)
                "Warning" -> crate.status.equals("Watch", ignoreCase = true) || crate.status.equals("Warning", ignoreCase = true)
                "Spoiled" -> crate.status.equals("Spoiled Risk", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "Crates & Products",
                subtitle = "Manage Produce & Cold Storage",
                isOnline = isOnline,
                showBack = false,
                trailingContent = {
                    IconButton(
                        onClick = onAddCrateClick,
                        modifier = Modifier.testTag("top_add_crate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Crate",
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCrateClick,
                containerColor = VxTheme.primaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_crate")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Crate"
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by ID, produce, chamber...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                    .testTag("crates_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips: All, Healthy, Warning, Spoiled
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Healthy", "Warning", "Spoiled").forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.testTag("filter_chip_$filter")
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredCrates.isEmpty()) {
                EmptyStateCard(
                    icon = Icons.Default.Inventory2,
                    title = "No Crates Found",
                    description = if (searchQuery.isNotEmpty()) "No produce crate matches '$searchQuery'" else "No crates in the '$selectedFilter' category.",
                    actionLabel = "Add New Crate",
                    onAction = onAddCrateClick
                )
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWide = maxWidth >= 720.dp
                    if (isWide) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            val chunks = filteredCrates.chunked(2)
                            items(chunks) { rowCrates ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    rowCrates.forEach { crate ->
                                        CrateCard(
                                            crate = crate,
                                            onClick = { onCrateClick(crate.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowCrates.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCrates, key = { it.id }) { crate ->
                                CrateCard(
                                    crate = crate,
                                    onClick = { onCrateClick(crate.id) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
