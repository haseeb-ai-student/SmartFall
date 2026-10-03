package com.smartfall.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: String,
    val type: String, // "Fall" ya "Accident"
    val severity: String, // "Low", "Medium", "High"
    val location: String
)