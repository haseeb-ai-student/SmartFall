package com.smartfall.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private val PICK_AUDIO_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etCustomMessage: EditText = findViewById(R.id.etCustomMessage)
        val btnSelectRingtone: Button = findViewById(R.id.btnSelectRingtone)
        val btnSave: Button = findViewById(R.id.btnSaveSettings)

        val prefs = getSharedPreferences("SmartFallPrefs", MODE_PRIVATE)
        etCustomMessage.setText(prefs.getString("custom_sos_message", "Emergency! I need immediate help."))

        btnSelectRingtone.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/*"
            }
            startActivityForResult(intent, PICK_AUDIO_REQUEST)
        }

        btnSave.setOnClickListener {
            val msg = etCustomMessage.text.toString()
            prefs.edit().putString("custom_sos_message", msg).apply()
            Toast.makeText(this, "Settings Saved Successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val prefs = getSharedPreferences("SmartFallPrefs", MODE_PRIVATE)
                prefs.edit().putString("custom_ringtone_uri", uri.toString()).apply()
                Toast.makeText(this, "Custom Ringtone Saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
