package cat.copernic.easytrazaapp.core.network

import android.content.Context

object ConfigManager {

    private const val PREFS_NAME = "config"
    private const val KEY_IP = "server_ip"

    fun saveIp(context: Context, ip: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_IP, ip)
            .apply()
    }

    fun getIp(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_IP, "10.0.2.2") ?: "10.0.2.2"
    }
}