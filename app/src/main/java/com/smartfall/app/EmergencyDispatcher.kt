package com.smartfall.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager

class EmergencyDispatcher(private val context: Context) {

    fun sendEmergencyAlerts(phoneNumber: String, latitude: Double, longitude: Double) {
        val prefs = context.getSharedPreferences("SmartFallPrefs", Context.MODE_PRIVATE)
        val userCustomMessage = prefs.getString("custom_sos_message", "Emergency! I need immediate help.") ?: "Emergency Fall Detected!"
        val googleMapsLink = "https://maps.google.com/?q=$latitude,$longitude"
        val fullMessageText = "$userCustomMessage
Location: $googleMapsLink"

        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, fullMessageText, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(fullMessageText)}")
                setPackage("com.whatsapp")
            }
            whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
