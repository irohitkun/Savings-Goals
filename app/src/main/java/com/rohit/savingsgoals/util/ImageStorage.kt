package com.rohit.savingsgoals.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object ImageStorage {

    /**
     * Copies the bytes behind a content:// Uri (from the system photo picker) into
     * app-private storage so it survives even if the original picker grant expires.
     * Returns the absolute file path to store in the DB, or null on failure.
     */
    suspend fun persist(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, "goal_images").apply { mkdirs() }
            val destFile = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).delete() }
    }
}
