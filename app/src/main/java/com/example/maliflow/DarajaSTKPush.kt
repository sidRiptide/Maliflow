package com.example.maliflow


import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
object DarajaSTKPush {
    fun initiateSTKPush(
        accessToken: String,
        amount: String,
        phoneNumber: String,
        onResult: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val shortCode = "174379"
                val passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"

                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = Base64.encodeToString(
                    "$shortCode$passKey$timestamp".toByteArray(),
                    Base64.NO_WRAP
                )

                val jsonBody = JSONObject().apply {
                    put("BusinessShortCode", shortCode)
                    put("Password", password)
                    put("Timestamp", timestamp)
                    put("TransactionType", "CustomerPayBillOnline")
                    put("Amount", amount)
                    put("PartyA", phoneNumber)
                    put("PartyB", shortCode)
                    put("PhoneNumber", phoneNumber)
                    put("CallBackURL", "https://mydomain.com/callback")
                    put("AccountReference", "MaliFlowApp")
                    put("TransactionDesc", "Test payment")
                }

                val url = URL("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                connection.outputStream.use {
                    it.write(jsonBody.toString().toByteArray())
                }

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()

                if (responseCode == 200) {
                    Log.d("DARAJA_STK", "✅ STK Push Successful: $response")
                    onResult(true, response)
                } else {
                    Log.e("DARAJA_STK", "❌ STK Push Failed: $response")
                    onResult(false, response)
                }

            } catch (e: Exception) {
                Log.e("DARAJA_STK", "❌ Error: ${e.message}")
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }
}
