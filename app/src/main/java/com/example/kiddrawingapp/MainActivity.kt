package com.example.kiddrawingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var drawingView = findViewById<DrawingView>(R.id.drawing_view)
        drawingView.setBrushSize(20.toFloat())
    }
}