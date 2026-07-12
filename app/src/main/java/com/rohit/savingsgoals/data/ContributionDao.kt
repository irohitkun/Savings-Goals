package com.rohit.savingsgoals.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao {

    @Insert
    suspend fun insert(contribution: Contribution): Long

    @Delete
    suspend fun delete(contribution: Contribution)

    @Query("SELECT * FROM contributions WHERE goalId = :goalId ORDER BY timestamp DESC")
    fun getContributionsForGoal(goalId: Long): Flow<List<Contribution>>

    @Query("SELECT * FROM contributions ORDER BY timestamp DESC")
    fun getAllContributions(): Flow<List<Contribution>>
}
