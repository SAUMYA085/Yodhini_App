package com.shakti.yodhini_2o

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.shakti.yodhini_2o.ui.theme.Yodhini_2OTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions for location, SMS, and audio recording
        requestPermissions()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SpeechRecognizer
        setupSpeechRecognizer()

        setContent {
            Yodhini_2OTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthenticationScreen(
                        modifier = Modifier.padding(innerPadding),
                        onRegister = { email, password -> registerUser(email, password) },
                        onLogin = { email, password -> loginUser(email, password) },
                        onSpeak = { startSpeechRecognition() }
                    )
                }
            }
        }

        // Set up panic button functionality
        val panicButton: Button = findViewById(R.id.panicButton)
        panicButton.setOnClickListener {
            sendSosMessage()
        }
    }

    // Function to request necessary permissions
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO
        )
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    // Set up SpeechRecognizer
    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(this@MainActivity, "Ready to listen", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Toast.makeText(this@MainActivity, "Processing...", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "Error recognizing speech", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.get(0) ?: "No speech recognized"
                Toast.makeText(this@MainActivity, "You said: $spokenText", Toast.LENGTH_SHORT).show()
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    // Function to start speech recognition
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now...")
        }
        speechRecognizer.startListening(intent)
    }

    // Function to send an SOS message
    private fun sendSosMessage() {
        val phoneNumber = "1234567890" // Trusted contact's number
        val message = "SOS! I need help. My location is: [location]"

        val smsUri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, smsUri)
        intent.putExtra("sms_body", message)
        try {
            startActivity(intent)
            Toast.makeText(this, "SOS message sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SOS message", Toast.LENGTH_SHORT).show()
        }
    }

    // Firebase authentication functions remain the same
    private fun registerUser(email: String, password: String) { /*...*/ }
    private fun loginUser(email: String, password: String) { /*...*/ }
}
@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    onRegister: (String, String) -> Unit,
    onLogin: (String, String) -> Unit,
    onSpeak: () -> Unit  // New parameter for speech recognition
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onRegister(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSpeak,  // Button to trigger speech recognition
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Speak")
        }
    }
}
