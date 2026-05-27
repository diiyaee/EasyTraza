package cat.copernic.easytrazaapp.core.utils

import android.content.Context
import android.content.SharedPreferences
import cat.copernic.easytrazaapp.core.DTOs.Usuari
import cat.copernic.easytrazaapp.core.network.UsuariRetrofitInstance
import com.google.gson.Gson

// Al ser un 'object', se instancia una sola vez y su valor persiste mientras la app está abierta
object SessionManager {
    private const val PREFS_NAME = "EasyTrazaPrefs"
    private const val USER_KEY = "current_user"

    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    var currentUser: Usuari? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadUserFromDisk()
    }

    private fun loadUserFromDisk() {
        val json = prefs?.getString(USER_KEY, null)
        if (json != null) {
            currentUser = gson.fromJson(json, Usuari::class.java)
        }
    }

    fun login(user: Usuari) {
        currentUser = user
        val json = gson.toJson(user)
        prefs?.edit()?.putString(USER_KEY, json)?.apply()
    }

    fun logout() {
        currentUser = null
        prefs?.edit()?.remove(USER_KEY)?.apply()

        UsuariRetrofitInstance.cookieJar.clear()
    }
}