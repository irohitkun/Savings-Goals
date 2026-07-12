package com.rohit.savingsgoals.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohit.savingsgoals.data.AppDatabase
import com.rohit.savingsgoals.data.Contribution
import com.rohit.savingsgoals.data.GoalRepository
import com.rohit.savingsgoals.data.GoalWithSaved
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GoalRepository(AppDatabase.getInstance(application))

    val goals: StateFlow<List<GoalWithSaved>> = repository.activeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContributions: StateFlow<List<Contribution>> = repository.allContributions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGoal(
        name: String,
        emoji: String,
        imagePath: String?,
        category: String,
        targetAmount: Double,
        targetDateMillis: Long?
    ) {
        viewModelScope.launch {
            repository.addGoal(name, emoji, imagePath, category, targetAmount, targetDateMillis)
        }
    }

    fun updateGoal(
        goalId: Long,
        name: String,
        emoji: String,
        imagePath: String?,
        category: String,
        targetAmount: Double,
        targetDateMillis: Long?,
        createdAt: Long
    ) {
        viewModelScope.launch {
            repository.updateGoal(goalId, name, emoji, imagePath, category, targetAmount, targetDateMillis, createdAt)
        }
    }

    fun deleteGoal(goal: GoalWithSaved) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun addContribution(goalId: Long, amount: Double, note: String) {
        viewModelScope.launch {
            repository.addContribution(goalId, amount, note)
        }
    }

    fun removeContribution(contribution: Contribution) {
        viewModelScope.launch {
            repository.removeContribution(contribution)
        }
    }

    fun goalFlow(goalId: Long) = repository.goal(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun contributionsFlow(goalId: Long) = repository.contributionsFor(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
