package com.shakti.yodhini_2o

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.telephony.SmsManager

class RegisterActivity : AppCompatActivity() {

    // Fused Location Provider for accessing current location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Constants for permissions
    private val smsPermissionCode = 100
    private val locationPermissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize UI components
        val nameEditText = findViewById<EditText>(R.id.et_name)
        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val registerButton = findViewById<Button>(R.id.btn_register)
        val sosButton = findViewById<Button>(R.id.btn_sos)

        // Check and request necessary permissions
        checkAndRequestPermissions()

        // Register Button click listener
        registerButton.setOnClickListener {
            handleRegistration(nameEditText, emailEditText, passwordEditText)
        }

        // SOS Button click listener
        sosButton.setOnClickListener {
            handleSOS()
        }
    }

    // Function to check and request permissions
    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), smsPermissionCode)
        }
    }

    // Handle registration logic
    private fun handleRegistration(nameEditText: EditText, emailEditText: EditText, passwordEditText: EditText) {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Simulate registration logic
            if (performRegistration(name, email, password)) {
                // Navigate to DashboardActivity after successful registration
                navigateToDashboardActivity()
            } else {
                Toast.makeText(this, "Registration failed. Try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Navigate to DashboardActivity
    private fun navigateToDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java) // Change from AdmireActivity to DashboardActivity
        startActivity(intent)
        finish() // Optional: Close RegisterActivity
    }

    // Simulated registration logic (replace with actual logic)
    private fun performRegistration(name: String, email: String, password: String): Boolean {
        // For now, return true to simulate successful registration
        return true
    }

    // Handle SOS functionality
    private fun handleSOS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        sendLocationSMS(location.latitude, location.longitude)
                    } else {
                        Toast.makeText(this, "Failed to retrieve location. Try again!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error retrieving location: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    // Send SMS with location
    private fun sendLocationSMS(latitude: Double, longitude: Double) {
        val emergencyContact = "1234567890" // Replace with the actual emergency contact number
        val message = "Emergency! My current location is: https://maps.google.com/?q=$latitude,$longitude"

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(emergencyContact, null, message, null, null)
                Toast.makeText(this, "SOS message sent!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            smsPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Some permissions were denied. Features may not work.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
