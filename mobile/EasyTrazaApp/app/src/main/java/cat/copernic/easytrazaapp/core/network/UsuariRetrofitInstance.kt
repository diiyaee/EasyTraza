package cat.copernic.easytrazaapp.core.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsuariRetrofitInstance {

    // =========================================
    // 🌟 1. EL TARRO DE GALLETAS (Memoria de Sesión)
    // =========================================
    class SessionCookieJar : CookieJar {
        private val cookieStore = HashMap<String, MutableList<Cookie>>()

        // Guarda el JSESSIONID que devuelve el backend al hacer Login
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies.toMutableList()
        }

        // Envía el JSESSIONID en cada petición (ej: al cargar albaranes)
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }

        // Permite borrar la sesión manualmente
        fun clear() {
            cookieStore.clear()
        }
    }

    val cookieJar = SessionCookieJar()

    // 🌟 2. CREAMOS EL MOTOR CON EL TARRO INTEGRADO
    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var currentIp: String? = null

    fun getRetrofit(context: Context): Retrofit {
        val ip = ConfigManager.getIp(context) // Ajusta si tu clase se llama distinto

        if (retrofit == null || currentIp != ip) {
            currentIp = ip
            retrofit = Retrofit.Builder()
                .baseUrl("http://$ip:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(
                    com.google.gson.GsonBuilder().setLenient().create()
                ))
                .build()
        }
        return retrofit!!
    }

    fun refresh(context: Context) {
        currentIp = ConfigManager.getIp(context)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$currentIp:8080/")
            .client(client) // 🌟 Y AQUÍ TAMBIÉN
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}