package com.elliewonderland.achtsamkeit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
)

private val navItems = listOf(
    NavItem(Screen.Today,    "Heute",     Icons.Outlined.WbSunny, Icons.Filled.WbSunny),
    NavItem(Screen.Diary, "Tagebuch",  Icons.Outlined.Book,    Icons.Filled.Book),
    NavItem(Screen.Statistics,"Statistik", Icons.Outlined.BarChart, Icons.Filled.BarChart),
    NavItem(Screen.Profile,   "Profil",    Icons.Outlined.Person,  Icons.Filled.Person),
)

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    onTabClick: ((Screen) -> Unit)? = null
) {
    NavigationBar(
        containerColor = AppTheme.colors.surface,
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (onTabClick != null) {
                        onTabClick(item.screen)
                    } else {
                        navController.navigate(item.screen.route) {
                            popUpTo(Screen.Today.route) {
                                inclusive = item.screen == Screen.Today
                                saveState = item.screen != Screen.Today
                            }
                            launchSingleTop = item.screen != Screen.Today
                            restoreState    = item.screen != Screen.Today
                        }
                    }
                },
                icon  = {
                    Icon(
                        imageVector = if (selected) item.iconSelected else item.icon,
                        contentDescription = item.label,
                    )
                },
                label  = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor       = AppTheme.colors.accent,
                    selectedTextColor       = AppTheme.colors.accent,
                    indicatorColor          = AppTheme.colors.accent.copy(alpha = 0.12f),
                    unselectedIconColor     = AppTheme.colors.inkSoft,
                    unselectedTextColor     = AppTheme.colors.inkSoft,
                ),
            )
        }
    }
}
