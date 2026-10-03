package com.smartfall.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.concurrent.Executor

class CountdownActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnImOk: Button
    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var vibrator: Vibrator
    
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    
    private val SPEECH_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown)

        tvTimer = findViewById(R.id.tvTimer)
        btnImOk = findViewById(R.id.btnImOk)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        startAlarmAndVibration()
        startCountdown()
        startVoiceRecognition() // Microphone speech recognition start karne ke liye

        // Setup Biometric / Fingerprint Authentication for False Alarm Cancellation
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                stopAlertsAndFinish()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("False Alarm Verification")
            .setSubtitle("Scan your fingerprint to cancel emergency alert")
            .setNegativeButtonText("Use I am OK Button")
            .build()

        btnImOk.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'I am OK' to cancel alert")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0)?.lowercase(Locale.ROOT) ?: ""
            
            if (spokenText.contains("ok") || spokenText.contains("i am ok")) {
                stopAlertsAndFinish()
                Toast.makeText(this, "Alert Cancelled via Voice Command!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startAlarmAndVibration() {
        val prefs = getSharedPreferences("SmartFallPrefs", MODE_PRIVATE)
        val ringtoneUri = prefs.getString("custom_ringtone_uri", null)

        try {
            mediaPlayer = if (ringtoneUri != null) {
                MediaPlayer.create(this, Uri.parse(ringtoneUri))
            } else {
                MediaPlayer.create(this, android.R.raw.fallback_ringtone)
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            vibrator.vibrate(longArrayOf(0, 500, 300, 500), 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvTimer.text = "$secondsLeft s"
            }

            override fun onFinish() {
                triggerEmergencyAction()
            }
        }.start()
    }

    private fun triggerEmergencyAction() {
        stopAlertsAndFinish()
        val db = AppDatabase.getDatabase(applicationContext)
        val newIncident = IncidentEntity(
            dateTime = "2026-10-03 10:00:00",
            type = "Fall Detected",
            severity = "High",
            location = "Lat: 31.5204, Lng: 74.3587"
        )
        kotlinx.coroutines.GlobalScope.launch {
            db.incidentDao().insertIncident(newIncident)
        }
        val dispatcher = EmergencyDispatcher(this)
        dispatcher.sendEmergencyAlerts("+923001234567", 31.5204, 74.3587)
    }

    private fun stopAlertsAndFinish() {
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        vibrator.cancel()
        finish()
    }
}