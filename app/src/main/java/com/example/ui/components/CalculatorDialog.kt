package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.i18n.LocalAppStrings

@Composable
fun CalculatorDialog(
    onDismiss: () -> Unit,
    onResult: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    var input by remember { mutableStateOf("0") }
    var previousInput by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }

    fun onAction(action: String) {
        when (action) {
            "C" -> {
                input = "0"
                previousInput = ""
                operation = ""
            }
            "DEL" -> {
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
                if (previousInput.isNotEmpty() && operation.isNotEmpty()) {
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
                if (!input.contains(".")) {
                    input += "."
                }
            }
            else -> {
                if (input == "0") input = action else input += action
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.calculator,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = if (previousInput.isNotEmpty()) "$previousInput $operation $input" else input,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    maxLines = 1,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )

                Spacer(modifier = Modifier.height(16.dp))

                val buttons = listOf(
                    listOf("7", "8", "9", "/"),
                    listOf("4", "5", "6", "*"),
                    listOf("1", "2", "3", "-"),
                    listOf("C", "0", "=", "+"),
                    listOf("DEL", ".")
                )

                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { btn ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable { onAction(btn) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = btn,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(strings.cancel)
                    }
                    Button(onClick = { onResult(input) }) {
                        Text(strings.useResult)
                    }
                }
            }
        }
    }
}
