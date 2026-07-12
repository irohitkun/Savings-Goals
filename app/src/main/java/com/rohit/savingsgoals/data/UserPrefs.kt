package com.rohit.savingsgoals.data

import android.content.Context

class UserPrefs(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun getName(): String? = prefs.getString(KEY_NAME, null)

    fun setName(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    fun hasOnboarded(): Boolean = getName() != null

    companion object {
        private const val KEY_NAME = "user_name"
    }
}
