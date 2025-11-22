package com.example.dulceapp.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREFS_NAME = "DulceAppSession"
    private const val KEY_IS_GUEST = "is_guest"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setGuestMode(context: Context, isGuest: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_GUEST, isGuest)
        editor.apply()
    }

    fun isGuest(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_GUEST, false)
    }

    fun clearSession(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}
