package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Purchase
import com.example.i18n.LocalAppStrings
import com.example.ui.components.CalculatorDialog
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(viewModel: FinanceViewModel) {
    val strings = LocalAppStrings.current
    val purchases by viewModel.purchases.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = strings.addPurchase)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = strings.purchasesTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (purchases.isEmpty()) {
                item {
                    Text(text = strings.noPurchases, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                items(purchases) { purchase ->
                    PurchaseItem(purchase, viewModel, strings)
                }
            }
        }
    }

    if (showAddDialog) {
        AddPurchaseDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, cat, qty, price ->
                viewModel.addPurchase(name, cat, qty, price)
                showAddDialog = false
            },
            strings = strings
        )
    }
}

@Composable
fun PurchaseItem(purchase: Purchase, viewModel: FinanceViewModel, strings: com.example.i18n.AppStrings) {
    val currency by viewModel.userPreferences.currencyFlow.collectAsState("")
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = purchase.productName, fontWeight = FontWeight.Bold)
                Text(text = "${purchase.category} • ${strings.quantity}: ${purchase.quantity}", style = MaterialTheme.typography.bodySmall)
                Text(text = dateFormat.format(Date(purchase.date)), style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = format.format(purchase.price).replace("$", "$currency "),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = { viewModel.deletePurchase(purchase) }) {
                    Icon(Icons.Default.Delete, contentDescription = strings.deletePurchase, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Double) -> Unit,
    strings: com.example.i18n.AppStrings
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(strings.categoryFood) }
    var quantityStr by remember { mutableStateOf("1") }
    var priceStr by remember { mutableStateOf("") }
    var showCalc by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        strings.categoryVegetables, strings.categoryFruits, strings.categoryHousehold,
        strings.categoryFood, strings.categoryClothing, strings.categoryOther
    )

    if (showCalc) {
        CalculatorDialog(
            onDismiss = { showCalc = false },
            onResult = { res ->
                priceStr = res
                showCalc = false
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = strings.addPurchase, style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.productName) },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text(strings.quantity) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text(strings.price) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f),
                        trailingIcon = {
                            IconButton(onClick = { showCalc = true }) {
                                Icon(Icons.Default.Calculate, contentDescription = strings.calculator)
                            }
                        }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(strings.cancel) }
                    Button(
                        onClick = {
                            val q = quantityStr.toIntOrNull() ?: 1
                            val p = priceStr.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && p > 0) {
                                onSave(name, category, q, p)
                            }
                        }
                    ) { Text(strings.save) }
                }
            }
        }
    }
}
