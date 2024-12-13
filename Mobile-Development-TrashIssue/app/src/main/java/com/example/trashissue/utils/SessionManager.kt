package com.example.trashissue.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
    }

    // Simpan token ke SharedPreferences
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()  // apply() untuk menyimpan data secara asinkron
    }

    // Ambil token dari SharedPreferences
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    // Hapus token dari SharedPreferences
    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)  // Menghapus hanya token, bukan seluruh data sesi
        editor.apply()  // apply() untuk menyimpan data secara asinkron
    }
}
