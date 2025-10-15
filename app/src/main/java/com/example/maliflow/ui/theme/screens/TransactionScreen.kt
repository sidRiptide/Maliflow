package com.example.maliflow.ui.theme.screens

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.maliflow.data.Transaction
import com.example.maliflow.view_models.TransactionViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.compose.ui.viewinterop.AndroidView
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(navController: NavController) {
    val transactionViewModel: TransactionViewModel = viewModel()

    // Start listening for transactions
    LaunchedEffect(Unit) {
        transactionViewModel.startListening()
    }

    val transactions = transactionViewModel.transactions

    // --- Basic stats ---
    val totalAmount = transactions.sumOf { it.amount }
    val averageAmount = if (transactions.isNotEmpty()) totalAmount / transactions.size else 0.0
    val totalTransactions = transactions.size

    // --- Monthly summary ---
    val currentMonth = LocalDate.now().monthValue
    val monthTransactions = transactions.filter {
        runCatching { LocalDate.parse(it.date).monthValue == currentMonth }.getOrDefault(false)
    }
    val monthlyTotal = monthTransactions.sumOf { it.amount }

    // --- Group by type ---
    val groupedByType = transactions.groupBy { it.type }
    val chartData = groupedByType.map { (type, list) ->
        type to list.sumOf { it.amount }
    }

    // --- Top customers ---
    val topCustomers = transactions
        .groupBy { it.customer }
        .mapValues { it.value.sumOf { t -> t.amount } }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

    // ✅ LazyColumn for full scrollable dashboard
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Dashboard Title ---
        item {
            Text("📊 Dashboard", style = MaterialTheme.typography.titleLarge)
        }

        // --- Summary card ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Transactions: $totalTransactions")
                    Text("Total Amount: Ksh ${"%.2f".format(totalAmount)}")
                    Text("Average Amount: Ksh ${"%.2f".format(averageAmount)}")
                }
            }
        }

        // --- Monthly Summary ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📅 Monthly Summary")
                    Text("Month Total: Ksh ${"%.2f".format(monthlyTotal)}")
                    Text("Transactions this month: ${monthTransactions.size}")
                }
            }
        }

        // --- Daily Trend Chart ---
        if (transactions.isNotEmpty()) {
            item {
                Text("📈 Daily Transaction Trend", style = MaterialTheme.typography.titleMedium)
                DailyTrendChart(transactions)
            }
        }

        // --- Income vs Expense Chart ---
        if (chartData.isNotEmpty()) {
            item {
                Text("💸 Expense vs Income", style = MaterialTheme.typography.titleMedium)
                IncomeExpenseBarChart(chartData)
            }
        }

        // --- Top Customers ---
        if (topCustomers.isNotEmpty()) {
            item {
                Text("🏆 Top Customers", style = MaterialTheme.typography.titleMedium)
                topCustomers.forEachIndexed { i, (name, total) ->
                    Text("${i + 1}. $name — Ksh ${"%.2f".format(total)}")
                }
            }
        }

        // --- All Transactions ---
        item {
            Text("All Transactions", style = MaterialTheme.typography.titleMedium)
        }

        if (transactions.isEmpty()) {
            item {
                Text("No transactions yet.")
            }
        } else {
            items(transactions) { t ->
                TransactionCard(t)
            }
        }

        // --- Add Transaction Button ---
        item {
            Button(
                onClick = { navController.navigate("add_transaction") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Transaction")
            }
        }
    }
}

// --- Transaction Card ---
@Composable
fun TransactionCard(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Customer: ${transaction.customer}")
            Text("Amount: Ksh ${transaction.amount}")
            Text("Type: ${transaction.type}")
            Text("Date: ${transaction.date}")
        }
    }
}

// --- Line Chart (Daily Trend) ---
@Composable
fun DailyTrendChart(transactions: List<Transaction>) {
    val groupedByDate = transactions
        .groupBy { it.date }
        .mapValues { it.value.sumOf { t -> t.amount } }
        .toSortedMap()

    val entries = groupedByDate.entries.mapIndexed { index, (date, total) ->
        Entry(index.toFloat(), total.toFloat())
    }

    val dataSet = LineDataSet(entries, "Daily Totals").apply {
        color = Color.BLUE
        valueTextColor = Color.BLACK
        lineWidth = 2f
        circleRadius = 3f
        setDrawValues(false)
        setDrawFilled(true)
    }

    val lineData = LineData(dataSet)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                legend.isEnabled = false
                animateX(800)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

// --- Simple Bar Chart ---
@Composable
fun IncomeExpenseBarChart(chartData: List<Pair<String, Double>>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        val barWidth = size.width / (chartData.size * 2)
        val maxAmount = chartData.maxOf { it.second }

        chartData.forEachIndexed { index, (type, amount) ->
            val barHeight = (amount / maxAmount * size.height).toFloat()
            drawRect(
                color = when (type.lowercase()) {
                    "c2b" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    "b2c" -> androidx.compose.ui.graphics.Color(0xFFF44336)
                    else -> androidx.compose.ui.graphics.Color(0xFF2196F3)
                },
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = (index * 2 * barWidth) + barWidth / 2,
                    y = size.height - barHeight
                ),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }
    }
}
