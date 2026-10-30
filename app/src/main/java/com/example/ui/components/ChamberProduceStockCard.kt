package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.util.AppTranslations
import com.example.util.IndianLanguage

/**
 * Clean card showing what crops/vegetables/fruits are stored in Chamber 1 & Chamber 2.
 */
@Composable
fun ChamberProduceStockCard(
    chamber1Crates: List<Crate>,
    chamber2Crates: List<Crate>,
    selectedLanguage: IndianLanguage,
    onSpeakStock: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("chamber_produce_stock_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📦 कमरों में रखी फसलें (Crops in Chambers)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Chamber 1 & Chamber 2 stored produce breakdown",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chamber 1 Items
            ChamberRow(
                chamberTitle = "कमरा 1 (Chamber 1 • 12°C)",
                badgeColor = VxTheme.surfaceGreen,
                borderColor = VxTheme.borderGreen,
                textColor = VxTheme.emerald,
                crates = chamber1Crates,
                onSpeakStock = onSpeakStock
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chamber 2 Items
            ChamberRow(
                chamberTitle = "कमरा 2 (Chamber 2 • 4°C)",
                badgeColor = VxTheme.surfaceBlue,
                borderColor = VxTheme.borderBlue,
                textColor = VxTheme.primaryBlue,
                crates = chamber2Crates,
                onSpeakStock = onSpeakStock
            )
        }
    }
}

@Composable
private fun ChamberRow(
    chamberTitle: String,
    badgeColor: Color,
    borderColor: Color,
    textColor: Color,
    crates: List<Crate>,
    onSpeakStock: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VxTheme.surfaceSubtle,
        border = BorderStroke(1.dp, VxTheme.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = chamberTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${crates.size} पेटियां (${crates.sumOf { it.quantity }.toInt()} kg)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (crates.isEmpty()) {
                Text(
                    text = "यह कमरा अभी खाली है (No crates stored)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    crates.forEach { crate ->
                        Surface(
                            onClick = {
                                onSpeakStock("${crate.chamber} में ${crate.product} रखा है, मात्रा ${crate.quantity.toInt()} ${crate.unit} है।")
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, VxTheme.border),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getProduceEmoji(crate.product),
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = crate.product,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${crate.quantity.toInt()} ${crate.unit}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
