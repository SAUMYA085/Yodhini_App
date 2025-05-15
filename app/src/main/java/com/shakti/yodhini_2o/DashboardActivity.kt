package com.shakti.yodhini_2o

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class DashboardActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // SOS Button - Dials 100
        val sosButton: ImageView = findViewById(R.id.btn_sos)
        sosButton.setOnClickListener {
            dialNumber("100")
        }

        // Share Live Location
        val routeButton: ImageView = findViewById(R.id.ic_route)
        routeButton.setOnClickListener {
            shareLiveLocation()
        }

        // Click listeners for nearby services
        findViewById<ImageView>(R.id.ic_police).setOnClickListener {
            openMapsWithQuery("nearby police stations")
        }

        findViewById<ImageView>(R.id.ic_hospital).setOnClickListener {
            openMapsWithQuery("nearby hospitals")
        }

        findViewById<ImageView>(R.id.ic_pharmacy).setOnClickListener {
            openMapsWithQuery("nearby pharmacies")
        }

        // Bottom Navigation
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true // Already on home
                R.id.call -> {
                    startActivity(Intent(this, EmergencyContactActivity::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Opens dialer with given number
    private fun dialNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        if (callIntent.resolveActivity(packageManager) != null) {
            startActivity(callIntent)
        }
    }

    // Opens Google Maps with a search query
    private fun openMapsWithQuery(query: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    // Shares live location
    private fun shareLiveLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener<Location> { location ->
                if (location != null) {
                    val uri = Uri.parse("geo:${location.latitude},${location.longitude}")
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "My current location: $uri")
                    startActivity(Intent.createChooser(intent, "Share Location"))
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                shareLiveLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
