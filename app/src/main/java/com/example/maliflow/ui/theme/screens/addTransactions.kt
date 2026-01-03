package com.example.maliflow.ui.theme.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.maliflow.DarajaApiService
import com.example.maliflow.DarajaSTKPush
import com.example.maliflow.view_models.TransactionViewModel
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SwapHoriz


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController) {
    val transactionViewModel: TransactionViewModel = viewModel()

    var amount by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("C2B") }
    var phoneNumber by remember { mutableStateOf("254708374149") } // Sandbox test number

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Add New Transaction",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = {
                Text(
                    "Amount",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Amount"
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline
//            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = customer,
            onValueChange = { customer = it },
            label = {
                Text(
                    "Customer Name",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Customer Name"
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline
//            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = {
                Text(
                    "Type (C2B/B2C)",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Type"
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
//            colors = TextFieldDefaults.textFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline
//            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = {
                Text(
                    "Phone Number (e.g., 254708374149)",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone Number"
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline
//            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Save Transaction to Firestore
        Button(
            onClick = {
                if (amount.isNotEmpty()) {
                    transactionViewModel.addDummyTransaction(
                        amount.toDoubleOrNull() ?: 0.0,
                        customer,
                        type
                    )
                    amount = ""
                    customer = ""
                }
                navController.navigate("transaction_list")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Transaction")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Trigger STK Push via M-Pesa
        Button(
            onClick = {
                DarajaApiService.generateAccessToken { token ->
                    if (token != null) {
                        DarajaSTKPush.initiateSTKPush(
                            accessToken = token,
                            amount = amount,
                            phoneNumber = phoneNumber
                        ) { success, response ->
                            if (success) {
                                println("✅ Payment initiated: $response")
                            } else {
                                println("❌ Payment failed: $response")
                            }
                        }
                    } else {
                        println("❌ Could not get access token")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay with M-Pesa")
        }
    }
}
