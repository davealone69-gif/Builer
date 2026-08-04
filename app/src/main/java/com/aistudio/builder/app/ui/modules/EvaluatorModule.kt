package com.aistudio.builder.app.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EvaluatorModule(
    expression: String,
    result: String,
    onExpressionChanged: (String) -> Unit,
    onEvaluate: () -> Unit
) {
    val scrollState = rememberScrollState()
    var benchResult by remember { mutableStateOf("Ready to benchmark") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("evaluator_module"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Evaluator — Code & Math Benchmarker",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Math & Logic Evaluator Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Expression Solver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Real-time Expression Evaluator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedTextField(
                    value = expression,
                    onValueChange = onExpressionChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expr_input"),
                    label = { Text("Mathematical Expression") },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
                )

                // Quick Operator Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("+", "-", "*", "/", "(", ")").forEach { op ->
                        OutlinedButton(
                            onClick = { onExpressionChanged(expression + op) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = op, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = onEvaluate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("eval_btn")
                ) {
                    Text("Evaluate Expression")
                }

                // Output Result Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Result:", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = result,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Algorithmic Benchmark Suite
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Algorithmic Performance Benchmark",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val start = System.nanoTime()
                            var fib = 0L
                            var a = 0L
                            var b = 1L
                            for (i in 2..40) {
                                fib = a + b
                                a = b
                                b = fib
                            }
                            val durationUs = (System.nanoTime() - start) / 1000
                            benchResult = "Fibonacci(40) = $fib\nExecution Time: ${durationUs} µs"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fib_bench_btn")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Fibonacci(40)")
                    }

                    OutlinedButton(
                        onClick = {
                            val start = System.nanoTime()
                            val list = (1..5000).map { (0..10000).random() }.sorted()
                            val durationUs = (System.nanoTime() - start) / 1000
                            benchResult = "Sorted 5,000 Random Integers\nExecution Time: ${durationUs} µs"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sort Benchmark")
                    }
                }

                Text(
                    text = benchResult,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(12.dp)
                )
            }
        }
    }
}
