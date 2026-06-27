package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DebtType
import com.example.i18n.LocalAppStrings
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val strings = LocalAppStrings.current
    val purchases by viewModel.purchases.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val products by viewModel.products.collectAsState()
    val currency by viewModel.userPreferences.currencyFlow.collectAsState()
    
    val totalPurchases = purchases.sumOf { it.price }
    val owedToYou = debts.filter { it.type == DebtType.OWED_TO_USER && !it.isPaid }.sumOf { it.amount }
    val youOwe = debts.filter { it.type == DebtType.OWED_BY_USER && !it.isPaid }.sumOf { it.amount }
    
    // Quick today calculation
    val today = Date()
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val todayStr = sdf.format(today)
    val purchasesToday = purchases.filter { sdf.format(Date(it.date)) == todayStr }
    val totalPurchasesToday = purchasesToday.sumOf { it.price }
    val totalPurchasesOverall = purchases.sumOf { it.price }

    val format = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    fun fmt(amt: Double) = format.format(amt).replace("$", "$currency ")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = strings.tabDashboard,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = strings.purchasesToday, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = fmt(totalPurchasesToday),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = strings.totalPurchases, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = fmt(totalPurchasesOverall),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = strings.debtsOwedToYou, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = fmt(owedToYou),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = strings.debtsYouOwe, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = fmt(youOwe),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        val lowStockProducts = products.filter { it.stockQuantity <= 5 }
        if (lowStockProducts.isNotEmpty()) {
            item {
                Text(
                    text = strings.lowStockAlerts,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            items(lowStockProducts) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = p.name, fontWeight = FontWeight.Bold)
                        Text(text = "${strings.stockQuantity}: ${p.stockQuantity}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        item {
            Text(
                text = strings.recentTransactions,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val recentPurchases = purchases.take(5)
        if (recentPurchases.isEmpty()) {
            item {
                Text(text = strings.noPurchases)
            }
        } else {
            items(recentPurchases) { purchase ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = purchase.productName, fontWeight = FontWeight.Bold)
                            Text(text = purchase.category, style = MaterialTheme.typography.bodySmall)
                            Text(text = dateFormat.format(Date(purchase.date)), style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = fmt(purchase.price),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
