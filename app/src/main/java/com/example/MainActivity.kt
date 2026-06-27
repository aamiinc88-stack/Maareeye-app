package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.i18n.EnglishStrings
import com.example.i18n.LocalAppStrings
import com.example.i18n.SomaliStrings
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentLang by viewModel.userPreferences.languageFlow.collectAsState()
            val currentTheme by viewModel.userPreferences.themeFlow.collectAsState()
            val currentPin by viewModel.userPreferences.pinFlow.collectAsState()
            var authenticated by remember { mutableStateOf(currentPin.isEmpty()) }

            val appStrings = if (currentLang == "so") SomaliStrings else EnglishStrings
            val isDark = when (currentTheme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(LocalAppStrings provides appStrings) {
                MyApplicationTheme(darkTheme = isDark) {
                    if (!authenticated) {
                        PinAuthScreen(
                            savedPin = currentPin,
                            onAuthenticated = { authenticated = true }
                        )
                    } else {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    val items = listOf(
                                        Triple("dashboard", appStrings.tabDashboard, Icons.Default.Dashboard),
                                        Triple("purchases", appStrings.tabPurchases, Icons.Default.Payment),
                                        Triple("debts", appStrings.tabDebts, Icons.Default.Money),
                                        Triple("calculator", appStrings.tabCalculator, Icons.Default.Calculate),
                                        Triple("settings", appStrings.tabSettings, Icons.Default.Settings)
                                    )
                                    items.forEach { (route, label, icon) ->
                                        NavigationBarItem(
                                            icon = { Icon(icon, contentDescription = label) },
                                            label = { Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
                                            alwaysShowLabel = false,
                                            selected = currentRoute == route,
                                            onClick = {
                                                navController.navigate(route) {
                                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "dashboard",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("dashboard") { DashboardScreen(viewModel) }
                                composable("purchases") { PurchasesScreen(viewModel) }
                                composable("inventory") { InventoryScreen(viewModel) }
                                composable("customers") { CustomersScreen(viewModel) }
                                composable("debts") { DebtsScreen(viewModel) }
                                composable("calculator") { CalculatorScreen(viewModel) }
                                composable("settings") { SettingsScreen(viewModel) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinAuthScreen(savedPin: String, onAuthenticated: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Enter PIN to Access", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { 
                    input = it
                    error = false
                    if (input.length == 4) {
                        if (input == savedPin) {
                            onAuthenticated()
                        } else {
                            error = true
                            input = ""
                        }
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                isError = error,
                label = { Text("4-digit PIN") },
                singleLine = true
            )
            if (error) {
                Text("Incorrect PIN", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
