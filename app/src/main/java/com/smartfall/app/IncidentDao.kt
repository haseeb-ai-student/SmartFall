package com.smartfall.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncidentDao {
    @Insert
    suspend fun insertIncident(incident: IncidentEntity)

    @Query("SELECT * FROM incidents ORDER BY id DESC")
    suspend fun getAllIncidents(): List<IncidentEntity>
}