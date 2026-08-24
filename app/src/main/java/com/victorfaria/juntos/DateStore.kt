package com.victorfaria.juntos

import android.content.Context

/**
 * A data especial é única e global para o app: todos os widgets mostram a mesma contagem.
 */
object DateStore {
    private const val PREFS_NAME = "juntos_prefs"
    private const val KEY_TARGET_MILLIS = "target_millis"
    private const val KEY_DISPLAY_MODE = "display_mode"

    const val MODE_DAYS = 0
    const val MODE_FULL = 1

    fun getTargetMillis(context: Context): Long? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val value = prefs.getLong(KEY_TARGET_MILLIS, -1L)
        return if (value == -1L) null else value
    }

    fun setTargetMillis(context: Context, millis: Long) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_TARGET_MILLIS, millis)
            .apply()
    }

    fun getDisplayMode(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DISPLAY_MODE, MODE_DAYS)
    }

    fun toggleDisplayMode(context: Context) {
        val next = if (getDisplayMode(context) == MODE_DAYS) MODE_FULL else MODE_DAYS
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_DISPLAY_MODE, next)
            .apply()
    }
}
