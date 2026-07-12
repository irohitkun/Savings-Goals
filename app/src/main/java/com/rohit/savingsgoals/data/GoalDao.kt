package com.rohit.savingsgoals.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class GoalWithSaved(
    val id: Long,
    val name: String,
    val emoji: String,
    val imagePath: String?,
    val category: String,
    val targetAmount: Double,
    val createdAt: Long,
    val targetDateMillis: Long?,
    val isArchived: Boolean,
    val savedAmount: Double
)

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query(
        """
        SELECT goals.id, goals.name, goals.emoji, goals.imagePath, goals.category, goals.targetAmount, goals.createdAt,
               goals.targetDateMillis, goals.isArchived,
               COALESCE(SUM(contributions.amount), 0.0) AS savedAmount
        FROM goals
        LEFT JOIN contributions ON contributions.goalId = goals.id
        WHERE goals.isArchived = 0
        GROUP BY goals.id
        ORDER BY goals.createdAt DESC
        """
    )
    fun getActiveGoalsWithProgress(): Flow<List<GoalWithSaved>>

    @Query(
        """
        SELECT goals.id, goals.name, goals.emoji, goals.imagePath, goals.category, goals.targetAmount, goals.createdAt,
               goals.targetDateMillis, goals.isArchived,
               COALESCE(SUM(contributions.amount), 0.0) AS savedAmount
        FROM goals
        LEFT JOIN contributions ON contributions.goalId = goals.id
        WHERE goals.id = :goalId
        GROUP BY goals.id
        """
    )
    fun getGoalWithProgress(goalId: Long): Flow<GoalWithSaved?>
}
