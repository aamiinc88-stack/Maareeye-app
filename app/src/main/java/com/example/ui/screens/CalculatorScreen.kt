package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.i18n.LocalAppStrings
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun CalculatorScreen(viewModel: FinanceViewModel) {
    val strings = LocalAppStrings.current
    var input by remember { mutableStateOf("0") }
    var previousInput by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }
    var showGuideDialog by remember { mutableStateOf(false) }

    fun onAction(action: String) {
        when (action) {
            "C" -> {
                input = "0"
                previousInput = ""
                operation = ""
            }
            "⌫" -> {
                input = if (input.length > 1) input.dropLast(1) else "0"
            }
            "+", "-", "*", "/" -> {
                if (operation.isNotEmpty() && previousInput.isNotEmpty() && input != "0") {
                    val p = previousInput.toDoubleOrNull() ?: 0.0
                    val c = input.toDoubleOrNull() ?: 0.0
                    val result = when (operation) {
                        "+" -> p + c
                        "-" -> p - c
                        "*" -> p * c
                        "/" -> if (c != 0.0) p / c else 0.0
                        else -> 0.0
                    }
                    previousInput = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
                } else if (previousInput.isEmpty()) {
                    previousInput = input
                }
                operation = action
                input = "0"
            }
            "=" -> {
                if (operation.isNotEmpty() && previousInput.isNotEmpty()) {
                    val p = previousInput.toDoubleOrNull() ?: 0.0
                    val c = input.toDoubleOrNull() ?: 0.0
                    val result = when (operation) {
                        "+" -> p + c
                        "-" -> p - c
                        "*" -> p * c
                        "/" -> if (c != 0.0) p / c else 0.0
                        else -> 0.0
                    }
                    input = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
                    previousInput = ""
                    operation = ""
                }
            }
            "." -> {
                if (!input.contains(".")) input += "."
            }
            else -> {
                if (input == "0") input = action else input += action
            }
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(strings.calculator) },
                actions = {
                    IconButton(onClick = { showGuideDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Help")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (previousInput.isNotEmpty() && operation.isNotEmpty()) {
                    Text(
                        text = "$previousInput $operation",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = input,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Keypad
            val buttons = listOf(
                listOf("C", "⌫", "/", "*"),
                listOf("7", "8", "9", "-"),
                listOf("4", "5", "6", "+"),
                listOf("1", "2", "3", "="),
                listOf("0", ".", "")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { btn ->
                        if (btn.isEmpty()) {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(if (row.size == 3) 1f else 1f))
                        } else {
                            val isAction = btn in listOf("C", "⌫", "/", "*", "-", "+", "=")
                            val isEquals = btn == "="
                            val weight = if (btn == "0") 2f else 1f
                            val color = when {
                                isEquals -> MaterialTheme.colorScheme.primaryContainer
                                isAction -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                            val textColor = when {
                                isEquals -> MaterialTheme.colorScheme.onPrimaryContainer
                                isAction -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .aspectRatio(if (btn == "0") 2f else 1f)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(color)
                                    .clickable { onAction(btn) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = btn,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showGuideDialog) {
        Dialog(onDismissRequest = { showGuideDialog = false }) {
            Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(24.dp).fillMaxHeight(0.7f)) {
                    Text(text = strings.calcGuideTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        Text(text = strings.calcGuide1, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = strings.calcGuide2, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = strings.calcGuide3, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = strings.calcGuide4, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showGuideDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = strings.cancel)
                    }
                }
            }
        }
    }
}
