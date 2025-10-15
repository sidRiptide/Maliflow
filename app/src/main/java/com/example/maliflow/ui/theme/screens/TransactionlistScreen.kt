package com.example.maliflow.ui.theme.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.maliflow.view_models.TransactionViewModel

@Composable
fun TransactionListScreen(navController: NavController) {
    val transactionViewModel: TransactionViewModel = viewModel()

    // Start listening when screen loads
    LaunchedEffect(Unit) {
        transactionViewModel.startListening()
    }

    val transactions = transactionViewModel.transactions

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Transactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (transactions.isEmpty()) {
            Text("No transactions yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions) { t ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Customer: ${t.customer}")
                            Text("Amount: ${t.amount}")
                            Text("Type: ${t.type}")
                            Text("Date: ${t.date}")
                        }
                    }
                }
            }
        }
    }
}
