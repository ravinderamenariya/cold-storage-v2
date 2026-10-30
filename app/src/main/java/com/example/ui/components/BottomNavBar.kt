package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppDestination
import com.example.ui.theme.VxTheme
import com.example.util.AppTranslations
import com.example.util.IndianLanguage

sealed class NavItem(
    val destination: AppDestination,
    val translationKey: String,
    val defaultTitle: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : NavItem(
        AppDestination.HOME,
        "nav_home",
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home,
        "nav_item_home"
    )
    object Crates : NavItem(
        AppDestination.CRATES,
        "nav_crates",
        "Crates",
        Icons.Filled.Inventory2,
        Icons.Outlined.Inventory2,
        "nav_item_crates"
    )
    object Voice : NavItem(
        AppDestination.VOICE_ASSISTANT,
        "nav_voice",
        "Voice",
        Icons.Filled.Mic,
        Icons.Outlined.Mic,
        "nav_item_voice"
    )
    object Alerts : NavItem(
        AppDestination.ALERTS,
        "nav_alerts",
        "Alerts",
        Icons.Filled.Notifications,
        Icons.Outlined.Notifications,
        "nav_item_alerts"
    )
    object More : NavItem(
        AppDestination.SETTINGS,
        "nav_more",
        "More",
        Icons.Filled.MoreHoriz,
        Icons.Outlined.MoreHoriz,
        "nav_item_more"
    )
}

/**
 * VISION X standard bottom navigation bar.
 * Properly respects window insets without double-padding, displays 5 localized destinations,
 * and highlights active tabs with clear high-contrast indicators.
 */
@Composable
fun VisionXBottomNavBar(
    currentScreen: AppDestination,
    unreadAlertCount: Int,
    onNavigate: (AppDestination) -> Unit,
    onOpenMoreMenu: () -> Unit,
    language: IndianLanguage = IndianLanguage.HINDI,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem.Home,
        NavItem.Crates,
        NavItem.Voice,
        NavItem.Alerts,
        NavItem.More
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            items.forEach { item ->
                val isSelected = when (item) {
                    NavItem.Home -> currentScreen == AppDestination.HOME
                    NavItem.Crates -> currentScreen in listOf(AppDestination.CRATES, AppDestination.ADD_CRATE, AppDestination.CRATE_DETAIL)
                    NavItem.Voice -> currentScreen == AppDestination.VOICE_ASSISTANT
                    NavItem.Alerts -> currentScreen == AppDestination.ALERTS
                    NavItem.More -> currentScreen in listOf(
                        AppDestination.SETTINGS,
                        AppDestination.SOLAR_POWER,
                        AppDestination.AI_PREDICTION,
                        AppDestination.LIVE_MONITORING,
                        AppDestination.HISTORY,
                        AppDestination.PI_GUIDE
                    )
                }

                val title = AppTranslations.get(item.translationKey, language)

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (item == NavItem.More) {
                            onOpenMoreMenu()
                        } else {
                            onNavigate(item.destination)
                        }
                    },
                    icon = {
                        if (item == NavItem.Alerts && unreadAlertCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = VxTheme.errorRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$unreadAlertCount", fontSize = 10.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = title,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VxTheme.primaryBlue,
                        selectedTextColor = VxTheme.primaryBlue,
                        indicatorColor = VxTheme.surfaceBlue,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
