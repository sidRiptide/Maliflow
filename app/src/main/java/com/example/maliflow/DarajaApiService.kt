package com.example.maliflow



import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import org.json.JSONObject
import java.util.*

object DarajaApiService {

    private const val CONSUMER_KEY = "Ad0uq4CODgAcAipAkcRZsSwZr68Gr10i4ADDwrG6dQvG2ZXQ"
    private const val CONSUMER_SECRET = "yN60hIF2ADttN8IHkhdCXAcwNhHotUtlwZUGp4uyXF8Dg8bLS48uwAkruubPwaLI"
    private const val BASE_URL = "https://sandbox.safaricom.co.ke"

    private val client = OkHttpClient()

    // --- Generate OAuth Token ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateAccessToken(callback: (String?) -> Unit) {
        val credentials = "$CONSUMER_KEY:$CONSUMER_SECRET"
        val auth = Base64.getEncoder().encodeToString(credentials.toByteArray())

        val request = Request.Builder()
            .url("$BASE_URL/oauth/v1/generate?grant_type=client_credentials")
            .addHeader("Authorization", "Basic $auth")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback(null)
                    } else {
                        val responseBody = response.body?.string()
                        val json = JSONObject(responseBody)
                        val token = json.getString("access_token")
                        callback(token)
                    }
                }
            }
        })
    }
}
