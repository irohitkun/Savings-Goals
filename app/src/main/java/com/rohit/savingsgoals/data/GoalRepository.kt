package com.rohit.savingsgoals.data

class GoalRepository(private val db: AppDatabase) {

    private val goalDao = db.goalDao()
    private val contributionDao = db.contributionDao()

    fun activeGoals() = goalDao.getActiveGoalsWithProgress()

    fun goal(goalId: Long) = goalDao.getGoalWithProgress(goalId)

    fun contributionsFor(goalId: Long) = contributionDao.getContributionsForGoal(goalId)

    fun allContributions() = contributionDao.getAllContributions()

    suspend fun addGoal(
        name: String,
        emoji: String,
        imagePath: String?,
        category: String,
        targetAmount: Double,
        targetDateMillis: Long?
    ): Long {
        return goalDao.insert(
            Goal(
                name = name,
                emoji = emoji,
                imagePath = imagePath,
                category = category,
                targetAmount = targetAmount,
                targetDateMillis = targetDateMillis
            )
        )
    }

    suspend fun updateGoal(
        goalId: Long,
        name: String,
        emoji: String,
        imagePath: String?,
        category: String,
        targetAmount: Double,
        targetDateMillis: Long?,
        createdAt: Long
    ) {
        goalDao.update(
            Goal(
                id = goalId,
                name = name,
                emoji = emoji,
                imagePath = imagePath,
                category = category,
                targetAmount = targetAmount,
                createdAt = createdAt,
                targetDateMillis = targetDateMillis
            )
        )
    }

    suspend fun deleteGoal(goal: GoalWithSaved) {
        goalDao.delete(
            Goal(
                id = goal.id,
                name = goal.name,
                emoji = goal.emoji,
                imagePath = goal.imagePath,
                category = goal.category,
                targetAmount = goal.targetAmount,
                createdAt = goal.createdAt,
                targetDateMillis = goal.targetDateMillis,
                isArchived = goal.isArchived
            )
        )
    }

    suspend fun addContribution(goalId: Long, amount: Double, note: String) {
        contributionDao.insert(Contribution(goalId = goalId, amount = amount, note = note))
    }

    suspend fun removeContribution(contribution: Contribution) {
        contributionDao.delete(contribution)
    }
}
