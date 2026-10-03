package com.smartfall.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val tvLogs = findViewById<TextView>(R.id.tvHistoryLogs)
        
        // Fetch logs from SharedPreferences or Room DB
        val prefs = getSharedPreferences("SmartFallPrefs", MODE_PRIVATE)
        val lastIncident = prefs.getString("last_incident_log", null)

        if (lastIncident != null) {
            tvLogs.text = "Recent Incident Logs:\n\n$lastIncident"
        } else {
            tvLogs.text = "Incident History:\n- No falls recorded today.\n- System monitoring is fully operational."
        }
    }
}