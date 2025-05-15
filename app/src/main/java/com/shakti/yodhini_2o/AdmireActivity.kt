package com.shakti.yodhini_2o

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdmireActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admire)

        // Buttons
        val sosButton: Button = findViewById(R.id.sosButton)
        val panicButton: Button = findViewById(R.id.panicButton)
        val womenProfileButton: Button = findViewById(R.id.womenProfileButton)
        val emergencyContactButton: Button = findViewById(R.id.emergencyContactButton)
        val googleMapsButton: Button = findViewById(R.id.googleMapsButton)

        // SOS Button Click
        sosButton.setOnClickListener {
            // Add SOS Functionality
        }

        // Panic Button Click
        panicButton.setOnClickListener {
            // Add Panic Functionality
        }

        // Women Profile Navigation
        womenProfileButton.setOnClickListener {
            startActivity(Intent(this, WomenProfileActivity::class.java))
        }

        // Emergency Contact Navigation
        emergencyContactButton.setOnClickListener {
            startActivity(Intent(this, EmergencyContactActivity::class.java))
        }

        // Google Maps Navigation
        googleMapsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
    }
}
