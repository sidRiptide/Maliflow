package com.example.maliflow.ui.theme.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.maliflow.navigation.ROUTE_LOGIN
import com.example.maliflow.view_models.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var paybill by remember { mutableStateOf("") }
    val authViewModel: AuthViewModel = viewModel()



    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Business Name") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        OutlinedTextField(value = paybill, onValueChange = { paybill = it }, label = { Text("Paybill/Till") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Button(onClick = {
            authViewModel.signUp(email, password, name, phone, paybill)
            navController.navigate("login")
        }) {
            Text("Sign Up")
        }
        Text(
            "already have an account?Login Here!",
            modifier = Modifier.clickable{navController.navigate(ROUTE_LOGIN)})
    }
}
