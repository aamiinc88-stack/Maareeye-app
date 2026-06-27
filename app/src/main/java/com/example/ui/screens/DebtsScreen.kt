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
import com.example.data.model.Debt
import com.example.data.model.DebtType
import com.example.i18n.LocalAppStrings
import com.example.ui.components.CalculatorDialog
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtsScreen(viewModel: FinanceViewModel) {
    val strings = LocalAppStrings.current
    val debts by viewModel.debts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = strings.addDebt)
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
                    text = strings.debtsTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            items(debts) { debt ->
                DebtItem(debt, viewModel, strings)
            }
        }
    }

    if (showAddDialog) {
        AddDebtDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, amt, type, notes ->
                viewModel.addDebt(name, amt, type, notes)
                showAddDialog = false
            },
            strings = strings
        )
    }
}

@Composable
fun DebtItem(debt: Debt, viewModel: FinanceViewModel, strings: com.example.i18n.AppStrings) {
    val currency by viewModel.userPreferences.currencyFlow.collectAsState("")
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val isOwedToMe = debt.type == DebtType.OWED_TO_USER
    val color = if (isOwedToMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = debt.personName, fontWeight = FontWeight.Bold)
                Text(
                    text = if (isOwedToMe) strings.owedToMe else strings.owedByMe,
                    color = color,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = dateFormat.format(Date(debt.date)), style = MaterialTheme.typography.bodySmall)
                if (debt.notes.isNotBlank()) {
                    Text(text = debt.notes, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = debt.isPaid,
                        onCheckedChange = { viewModel.updateDebt(debt.copy(isPaid = it)) }
                    )
                    Text(if (debt.isPaid) strings.paid else strings.unpaid, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = format.format(debt.amount).replace("$", "$currency "),
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                IconButton(onClick = { viewModel.deleteDebt(debt) }) {
                    Icon(Icons.Default.Delete, contentDescription = strings.deleteDebt)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, DebtType, String) -> Unit,
    strings: com.example.i18n.AppStrings
) {
    var name by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isOwedToMe by remember { mutableStateOf(true) }
    var showCalc by remember { mutableStateOf(false) }

    if (showCalc) {
        CalculatorDialog(
            onDismiss = { showCalc = false },
            onResult = { res ->
                amountStr = res
                showCalc = false
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = strings.addDebt, style = MaterialTheme.typography.headlineSmall)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = isOwedToMe,
                        onClick = { isOwedToMe = true },
                        label = { Text(strings.owedToMe) }
                    )
                    FilterChip(
                        selected = !isOwedToMe,
                        onClick = { isOwedToMe = false },
                        label = { Text(strings.owedByMe) }
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.personName) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text(strings.amount) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showCalc = true }) {
                            Icon(Icons.Default.Calculate, contentDescription = strings.calculator)
                        }
                    }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(strings.cancel) }
                    Button(
                        onClick = {
                            val a = amountStr.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && a > 0) {
                                onSave(name, a, if (isOwedToMe) DebtType.OWED_TO_USER else DebtType.OWED_BY_USER, notes)
                            }
                        }
                    ) { Text(strings.save) }
                }
            }
        }
    }
}
