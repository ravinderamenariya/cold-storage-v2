package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import com.example.util.IndianLanguage
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertSeverity
import com.example.data.model.ConnectionMode
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxBorderBlue
import com.example.ui.theme.VxBorderDanger
import com.example.ui.theme.VxBorderGreen
import com.example.ui.theme.VxBorderPurple
import com.example.ui.theme.VxBorderSubtle
import com.example.ui.theme.VxBorderWarning
import com.example.ui.theme.VxCyanAccent
import com.example.ui.theme.VxDangerDark
import com.example.ui.theme.VxDangerRed
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxEmeraldAccent
import com.example.ui.theme.VxEmeraldDark
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxNavyDark
import com.example.ui.theme.VxPrimaryAccent
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxPurple
import com.example.ui.theme.VxSuccess
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceCard
import com.example.ui.theme.VxSurfaceDanger
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfacePurple
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxSurfaceWarning
import com.example.ui.theme.VxTextMuted
import com.example.ui.theme.VxTextPrimary
import com.example.ui.theme.VxTextSecondary
import com.example.ui.theme.VxWarningAmber
import com.example.ui.theme.VxWarningDark

/**
 * Modern Header Bar matching VISION X branding with statusBarsPadding,
 * language switcher chip, and audio speaker button.
 */
@Composable
fun VisionXHeader(
    title: String = "VISION X",
    subtitle: String = "Smart Cold Storage",
    isOnline: Boolean = true,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    language: IndianLanguage = IndianLanguage.HINDI,
    onLanguageClick: (() -> Unit)? = null,
    onSpeakStatusClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (showBack) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    // GREENCOLD Logo Emblem Badge
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VxSurfaceBlue)
                            .border(1.dp, VxBorder, RoundedCornerShape(8.dp))
                            .testTag("greencold_logo_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GC",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = VxPrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OnlineBadge(isOnline = isOnline)
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (trailingContent != null) {
                trailingContent()
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Speaker Button to listen to status
                    if (onSpeakStatusClick != null) {
                        IconButton(
                            onClick = onSpeakStatusClick,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(VxSurfaceGreen)
                                .border(1.dp, VxBorderGreen, CircleShape)
                                .testTag("header_speaker_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen status",
                                tint = VxSuccess,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Language Switcher Chip (Compact)
                    if (onLanguageClick != null) {
                        Surface(
                            onClick = onLanguageClick,
                            shape = RoundedCornerShape(12.dp),
                            color = VxSurfaceBlue,
                            border = BorderStroke(1.dp, VxBorder),
                            modifier = Modifier.testTag("header_lang_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = language.flagEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = language.nativeName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxPrimaryAccent,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Profile Avatar Button
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(VxSurfaceSubtle)
                            .testTag("header_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = VxPrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact Online / Offline status badge.
 */
@Composable
fun OnlineBadge(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isOnline) VxSurfaceGreen else VxSurfaceSubtle)
            .border(
                1.dp,
                if (isOnline) VxBorderGreen else VxBorderSubtle,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isOnline) VxEmerald.copy(alpha = alpha) else VxTextMuted
                    )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isOnline) "Online" else "Offline",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOnline) VxEmeraldDark else VxTextSecondary
            )
        }
    }
}

/**
 * Mode banner at top of dashboard (Demo Mode vs Raspberry Pi Live).
 */
@Composable
fun ConnectionModeBanner(
    mode: ConnectionMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, textColor, label) = when (mode) {
        ConnectionMode.DEMO -> BannerVisuals(
            VxSurfaceWarning,
            VxBorderWarning,
            VxWarningDark,
            "DEMO MODE • Real-time Simulation Active"
        )
        ConnectionMode.RASPBERRY_PI -> BannerVisuals(
            VxSurfaceGreen,
            VxBorderGreen,
            VxEmeraldDark,
            "CONNECTED • Raspberry Pi 5 Live Telemetry"
        )
        ConnectionMode.CLOUD_SYNC -> BannerVisuals(
            VxSurfaceBlue,
            VxBorderBlue,
            VxPrimaryBlue,
            "CLOUD SYNC • Realtime Storage Synchronization"
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("connection_mode_banner")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }
            Text(
                text = "Change",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

private data class BannerVisuals(
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val label: String
)

/**
 * Status Chip (Healthy, Watch, Spoiled Risk).
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status.lowercase()) {
        "healthy", "normal", "ok", "good" -> Triple(VxTheme.surfaceGreen, VxTheme.emerald, VxTheme.borderGreen)
        "watch", "warning", "elevated" -> Triple(VxTheme.surfaceWarning, VxTheme.warningAmber, VxTheme.borderWarning)
        "spoiled risk", "critical", "danger" -> Triple(VxTheme.surfaceDanger, VxTheme.errorRed, VxTheme.borderDanger)
        else -> Triple(VxTheme.surfaceSubtle, VxTheme.textSecondary, VxTheme.border)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

/**
 * Empty state container.
 */
@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(VxTheme.surfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = VxTheme.primaryBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    onClick = onAction,
                    shape = RoundedCornerShape(10.dp),
                    color = VxPrimaryBlue,
                    modifier = Modifier.testTag("empty_state_action_button")
                ) {
                    Text(
                        text = actionLabel,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
