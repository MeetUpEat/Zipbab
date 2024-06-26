package com.bestapp.zipbab.data.notification.preference

import android.content.Context
import android.content.SharedPreferences


class SharedPreference(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
    fun saveData(data: String) {
        pref.edit().putString("user_access_token", data).apply()
    }

    fun loadData(): String? {
        return pref.getString("user_access_token", "")
    }

    fun saveRefresh(data: String) {
        pref.edit().putString("user_refresh_token", data).apply()
    }

    fun loadRefresh(): String? {
        return pref.getString("user_refresh_token", "")
    }
}