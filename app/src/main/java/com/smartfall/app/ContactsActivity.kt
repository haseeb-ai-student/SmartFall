package com.smartfall.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContactsActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        prefs = getSharedPreferences("SmartFallPrefs", MODE_PRIVATE)

        val etName = findViewById<EditText>(R.id.etContactName)
        val etPhone = findViewById<EditText>(R.id.etContactPhone)
        val btnSave = findViewById<Button>(R.id.btnSaveContact)
        val tvSaved = findViewById<TextView>(R.id.tvSavedContact)

        // Load existing saved contact
        val savedName = prefs.getString("contact_name", "None")
        val savedPhone = prefs.getString("contact_phone", "")
        tvSaved.text = "Saved Contact: $savedName ($savedPhone)"

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                prefs.edit().putString("contact_name", name).putString("contact_phone", phone).apply()
                tvSaved.text = "Saved Contact: $name ($phone)"
                Toast.makeText(this, "Emergency Contact Saved Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}