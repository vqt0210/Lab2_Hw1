package com.example.lab2_hw1

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var submitButton: Button
    private lateinit var emojiView: TextView
    private lateinit var layout: LinearLayout
    private val sentimentAnalyzer = SentimentAnalyzer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing views
        inputText = findViewById(R.id.inputText)
        submitButton = findViewById(R.id.submitButton)
        emojiView = findViewById(R.id.emojiView)
        layout = findViewById(R.id.layout) // Ensure 'layout' is properly initialized

        submitButton.setOnClickListener {
            val text = inputText.text.toString()
            if (text.isNotBlank()) {
                analyzeSentiment(text)
            }
        }
    }

    private fun analyzeSentiment(text: String) {
        sentimentAnalyzer.analyze(text) { sentiment ->
            runOnUiThread {
                Log.d("Sentiment Debug", "Raw Output: '$sentiment'")

                val cleanSentiment = sentiment.trim().lowercase()
                Log.d("Sentiment Debug", "Processed Sentiment: '$cleanSentiment'")

                when (cleanSentiment) {
                    "pos" -> {
                        layout.setBackgroundColor(Color.GREEN)
                        emojiView.text = "😃"
                    }
                    "neg" -> {
                        layout.setBackgroundColor(Color.RED)
                        emojiView.text = "😞"
                    }
                    "neu" -> {
                        layout.setBackgroundColor(Color.GRAY)
                        emojiView.text = "😐"
                    }
                    else -> {
                        layout.setBackgroundColor(Color.YELLOW) // fallback
                        emojiView.text = "❓"
                        Log.e("Sentiment Error", "Unexpected sentiment output: '$cleanSentiment'")
                    }
                }
            }
        }
    }




}
