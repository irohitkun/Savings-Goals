package com.rohit.savingsgoals.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String,
    val imagePath: String? = null,
    val category: String = "",
    val targetAmount: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val targetDateMillis: Long? = null,
    val isArchived: Boolean = false
)
