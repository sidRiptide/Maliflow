package com.example.maliflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maliflow.ui.theme.screens.AddTransactionScreen
import com.example.maliflow.ui.theme.screens.LoginScreen
import com.example.maliflow.ui.theme.screens.SignupScreen
import com.example.maliflow.ui.theme.screens.TransactionListScreen
import com.example.maliflow.ui.theme.screens.TransactionScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_REGISTER
){
    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_REGISTER) { SignupScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_ADD_TRANSACTION) { AddTransactionScreen(navController) }
        composable(ROUTE_TRANSACTION_LIST) { TransactionListScreen(navController) }
        composable(ROUTE_DASHBOARD) { TransactionScreen(navController) }
    }
}
