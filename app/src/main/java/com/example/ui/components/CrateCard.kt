package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Crate
import com.example.ui.theme.VxTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Returns distinct visual icon emoji for produce types.
 */
fun getProduceEmoji(product: String): String {
    return when (product.lowercase().trim()) {
        "mango" -> "🥭"
        "tomato" -> "🍅"
        "banana" -> "🍌"
        "flower", "flowers" -> "🌸"
        "apple", "apples" -> "🍎"
        "grapes" -> "🍇"
        "potato", "potatoes" -> "🥔"
        "spinach", "leafy greens" -> "🥬"
        "strawberry", "strawberries" -> "🍓"
        else -> "📦"
    }
}

/**
 * Interactive Crate Card item matching VISION X design.
 */
@Composable
fun CrateCard(
    crate: Crate,
    marketProduce: com.example.data.model.IndianAgriProduce? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val storedDateStr = dateFormat.format(Date(crate.storageTimestamp))

    val matchedProduce = marketProduce ?: com.example.data.model.IndianAgriMarketCatalog.findByName(crate.product)

    val progressColor = when {
        crate.freshnessPercent >= 70 -> VxTheme.emerald
        crate.freshnessPercent >= 40 -> VxTheme.warningAmber
        else -> VxTheme.errorRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("crate_item_${crate.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Produce visual avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VxTheme.surfaceSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getProduceEmoji(crate.product),
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = crate.id,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VxTheme.primaryBlue
                            )
                            Text(
                                text = " – ${crate.product}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${crate.chamber} • ${crate.targetTemp.toInt()}°C",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Chip (Healthy, Watch, Spoiled Risk)
                StatusBadge(status = crate.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-time APMC Market Price & Trend Badge
            if (matchedProduce != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(VxTheme.surfaceSubtle)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "APMC Market: ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${matchedProduce.currentPricePerKgOrUnit}/${matchedProduce.unit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val trend = matchedProduce.priceTrendPercent
                    val trendText = if (trend >= 0) "+$trend% ↗" else "$trend% ↘"
                    val trendColor = if (trend >= 0) VxTheme.emerald else VxTheme.errorRed
                    Text(
                        text = trendText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = trendColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Freshness Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Freshness: ${crate.freshnessPercent}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = progressColor
                )
                Text(
                    text = "Stored: $storedDateStr",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (crate.freshnessPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = VxTheme.surfaceSubtle
            )
        }
    }
}
