package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionMode
import com.example.ui.AppDestination
import com.example.ui.theme.VxTheme

/**
 * Navigation item definition for the adaptive sidebar.
 */
data class SidebarNavItem(
    val destination: AppDestination,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

/**
 * Adaptive navigation sidebar for tablets, iPads, MacBooks, iMacs, and desktop computers.
 * Switches between an expanded sidebar (240dp) and a compact navigation rail (76dp).
 */
@Composable
fun VisionXAdaptiveSidebar(
    currentScreen: AppDestination,
    unreadAlertCount: Int,
    isOnline: Boolean,
    connectionMode: ConnectionMode,
    isExpanded: Boolean,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        SidebarNavItem(AppDestination.HOME, "Dashboard", Icons.Default.Home, "sidebar_nav_home"),
        SidebarNavItem(AppDestination.CRATES, "Crates & Stock", Icons.Default.Inventory2, "sidebar_nav_crates"),
        SidebarNavItem(AppDestination.VOICE_ASSISTANT, "Kisan Voice AI", Icons.Default.Mic, "sidebar_nav_voice"),
        SidebarNavItem(AppDestination.HISTORY, "24h Trends & History", Icons.Default.ShowChart, "sidebar_nav_history"),
        SidebarNavItem(AppDestination.ALERTS, "Alerts", Icons.Default.Notifications, "sidebar_nav_alerts"),
        SidebarNavItem(AppDestination.LIVE_MONITORING, "Live Telemetry", Icons.Default.Sensors, "sidebar_nav_live"),
        SidebarNavItem(AppDestination.SOLAR_POWER, "Solar & Battery", Icons.Default.SolarPower, "sidebar_nav_solar"),
        SidebarNavItem(AppDestination.AI_PREDICTION, "AI Shelf-Life", Icons.Default.AutoAwesome, "sidebar_nav_prediction"),
        SidebarNavItem(AppDestination.PI_GUIDE, "Raspberry Pi 5", Icons.Default.DeveloperBoard, "sidebar_nav_pi_guide"),
        SidebarNavItem(AppDestination.SETTINGS, "Settings", Icons.Default.Settings, "sidebar_nav_settings")
    )

    Surface(
        modifier = modifier
            .width(if (isExpanded) 240.dp else 76.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = if (isExpanded) 12.dp else 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: App Branding
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isExpanded) Alignment.Start else Alignment.CenterHorizontally
            ) {
                if (isExpanded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .clickable { onNavigate(AppDestination.HOME) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VxTheme.surfaceBlue)
                                .border(1.dp, VxTheme.border, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = "VISION X Logo",
                                tint = VxTheme.primaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VISION X",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Cold Storage Control",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VxTheme.surfaceBlue)
                            .border(1.dp, VxTheme.border, RoundedCornerShape(10.dp))
                            .clickable { onNavigate(AppDestination.HOME) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AcUnit,
                            contentDescription = "VISION X Logo",
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Items List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    navItems.forEach { item ->
                        val isSelected = when (item.destination) {
                            AppDestination.HOME -> currentScreen == AppDestination.HOME
                            AppDestination.CRATES -> currentScreen in listOf(AppDestination.CRATES, AppDestination.ADD_CRATE, AppDestination.CRATE_DETAIL)
                            AppDestination.VOICE_ASSISTANT -> currentScreen == AppDestination.VOICE_ASSISTANT
                            AppDestination.HISTORY -> currentScreen == AppDestination.HISTORY
                            AppDestination.ALERTS -> currentScreen == AppDestination.ALERTS
                            AppDestination.LIVE_MONITORING -> currentScreen == AppDestination.LIVE_MONITORING
                            AppDestination.SOLAR_POWER -> currentScreen == AppDestination.SOLAR_POWER
                            AppDestination.AI_PREDICTION -> currentScreen == AppDestination.AI_PREDICTION
                            AppDestination.PI_GUIDE -> currentScreen == AppDestination.PI_GUIDE
                            AppDestination.SETTINGS -> currentScreen == AppDestination.SETTINGS
                            else -> false
                        }

                        SidebarNavItemRow(
                            item = item,
                            isSelected = isSelected,
                            isExpanded = isExpanded,
                            unreadCount = if (item.destination == AppDestination.ALERTS) unreadAlertCount else 0,
                            onClick = { onNavigate(item.destination) }
                        )
                    }
                }
            }

            // Bottom Section: Status & Profile
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                if (isExpanded) {
                    // Status Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOnline) VxTheme.surfaceGreen else VxTheme.surfaceSubtle)
                            .border(1.dp, VxTheme.border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) VxTheme.success else MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) "System Online" else "Offline Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isOnline) VxTheme.success else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (connectionMode == ConnectionMode.RASPBERRY_PI) "RPi 5" else "Demo",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxTheme.primaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // User Profile Snippet
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigate(AppDestination.SETTINGS) }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VxTheme.surfaceSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = VxTheme.primaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Admin Engineer",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "admin@coldchain.iot",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    // Compact Profile icon button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VxTheme.surfaceSubtle)
                            .clickable { onNavigate(AppDestination.SETTINGS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarNavItemRow(
    item: SidebarNavItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    unreadCount: Int,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) VxTheme.surfaceBlue else Color.Transparent
    val contentColor = if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Surface(
        onClick = onClick,
        color = containerColor,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(item.testTag)
    ) {
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = fontWeight,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(VxTheme.errorRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$unreadCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (unreadCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = VxTheme.errorRed,
                                contentColor = Color.White
                            ) {
                                Text(text = "$unreadCount", fontSize = 9.sp)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
