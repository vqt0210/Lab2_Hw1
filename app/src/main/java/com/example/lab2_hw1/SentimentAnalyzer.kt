package com.example.lab2_hw1

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class SentimentAnalyzer {
    private val client = OkHttpClient()
    private val API_URL = "http://10.0.2.2:8000/predict/"

    fun analyze(text: String, callback: (String) -> Unit) {
        // Prepare JSON request body
        val json = JSONObject().apply {
            put("text", text)
        }

        // Define content type
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)

        // Prepare HTTP request
        val request = Request.Builder()
            .url(API_URL)
            .post(body)
            .build()

        // Make async HTTP call using OkHttpClient
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Callback with error message in case of failure
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle successful response
                if (!response.isSuccessful) {
                    callback("Error: ${response.code}")
                    return
                }

                // Parse the response body and extract sentiment
                response.body?.string()?.let { responseBody ->
                    Log.d("API_RESPONSE", responseBody)
                    try {
                        val jsonResponse = JSONObject(responseBody)
                        var sentimentResult = jsonResponse.getString("sentiment")
                        sentimentResult = sentimentResult.toLowerCase() // Convert to lowercase to avoid the issue

                        // Log processed sentiment to ensure it matches expected values
                        Log.d("Sentiment Debug", "Processed Sentiment: '$sentimentResult'")

                        // Handle each sentiment case more flexibly
                        if (sentimentResult == "pos" || sentimentResult == "neg" || sentimentResult == "neu") {
                            callback(sentimentResult)
                        } else {
                            callback("Unexpected sentiment result")
                        }
                    } catch (e: Exception) {
                        callback("Could not analyze")
                    }
                } ?: callback("Empty response")

            }
        })
    }
}
