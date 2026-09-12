package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppMode
import com.example.ui.NizamShopViewModel
import com.example.ui.OwnerScreen
import com.example.ui.screens.AnnouncementsScreen
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NizamShopApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NizamShopApp(viewModel: NizamShopViewModel = viewModel()) {
    val appMode by viewModel.appMode.collectAsState()
    val announcementState by viewModel.announcementState.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val stats by viewModel.stats.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (appMode == AppMode.CUSTOMER || currentScreen != OwnerScreen.DASHBOARD) {
                            IconButton(
                                onClick = {
                                    if (appMode == AppMode.CUSTOMER) {
                                        viewModel.setAppMode(AppMode.OWNER)
                                    } else {
                                        viewModel.navigateTo(OwnerScreen.DASHBOARD)
                                    }
                                },
                                modifier = Modifier.testTag("top_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Mode Toggle (Customer Mode vs Owner Mode)
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    if (appMode == AppMode.OWNER) viewModel.setAppMode(AppMode.CUSTOMER)
                                    else viewModel.setAppMode(AppMode.OWNER)
                                }
                                .testTag("mode_switch_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (appMode == AppMode.OWNER) Icons.Filled.SupervisorAccount else Icons.Filled.Person,
                                    contentDescription = "Toggle Mode",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (appMode == AppMode.OWNER) "Owner" else "Customer",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Nizam AI Shop 2",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = when {
                                announcementState.isSpeaking -> "Broadcasting Announcement"
                                appMode == AppMode.CUSTOMER -> "Customer Catalog Mode"
                                else -> "Owner Management Console"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (announcementState.isSpeaking) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Navigation Bar for Owner Mode screens
            if (appMode == AppMode.OWNER) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == OwnerScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(OwnerScreen.DASHBOARD) },
                        icon = {
                            Icon(
                                if (currentScreen == OwnerScreen.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard") },
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen == OwnerScreen.ANNOUNCEMENTS,
                        onClick = { viewModel.navigateTo(OwnerScreen.ANNOUNCEMENTS) },
                        icon = {
                            Icon(
                                if (currentScreen == OwnerScreen.ANNOUNCEMENTS) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                                contentDescription = "Announcements"
                            )
                        },
                        label = { Text("Announce") },
                        modifier = Modifier.testTag("nav_announcements")
                    )

                    NavigationBarItem(
                        selected = currentScreen == OwnerScreen.INVENTORY,
                        onClick = { viewModel.navigateTo(OwnerScreen.INVENTORY) },
                        icon = {
                            Icon(
                                if (currentScreen == OwnerScreen.INVENTORY) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                                contentDescription = "Inventory"
                            )
                        },
                        label = { Text("Inventory") },
                        modifier = Modifier.testTag("nav_inventory")
                    )

                    NavigationBarItem(
                        selected = currentScreen == OwnerScreen.ORDERS,
                        onClick = { viewModel.navigateTo(OwnerScreen.ORDERS) },
                        icon = {
                            Icon(
                                if (currentScreen == OwnerScreen.ORDERS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                contentDescription = "Orders"
                            )
                        },
                        label = { Text("Orders") },
                        modifier = Modifier.testTag("nav_orders")
                    )

                    NavigationBarItem(
                        selected = currentScreen == OwnerScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(OwnerScreen.SETTINGS) },
                        icon = {
                            Icon(
                                if (currentScreen == OwnerScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") },
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (appMode == AppMode.CUSTOMER) {
                CustomerScreen(
                    products = products
                )
            } else {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        OwnerScreen.DASHBOARD -> DashboardScreen(
                            stats = stats,
                            announcementState = announcementState,
                            recentOrders = orders,
                            onNavigateToAnnouncements = { viewModel.navigateTo(OwnerScreen.ANNOUNCEMENTS) },
                            onNavigateToInventory = { viewModel.navigateTo(OwnerScreen.INVENTORY) },
                            onStopAnnouncement = { viewModel.stopAnnouncement() }
                        )

                        OwnerScreen.ANNOUNCEMENTS -> AnnouncementsScreen(
                            announcementState = announcementState,
                            presets = viewModel.announcementManager.presets,
                            onBroadcast = { text -> viewModel.broadcastAnnouncement(text) },
                            onStopAnnouncement = { viewModel.stopAnnouncement() }
                        )

                        OwnerScreen.INVENTORY -> InventoryScreen(
                            products = products,
                            onAddStock = { id, qty -> viewModel.addStock(id, qty) }
                        )

                        OwnerScreen.ORDERS -> OrdersScreen(
                            orders = orders
                        )

                        OwnerScreen.SETTINGS -> SettingsScreen(
                            announcementState = announcementState,
                            onTestAnnouncement = { viewModel.broadcastAnnouncement("This is a test broadcast of the Nizam AI Shop 2 public address system.") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
