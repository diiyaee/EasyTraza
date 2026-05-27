package cat.copernic.easytrazaapp.core.network

import cat.copernic.easytrazaapp.core.DTOs.Usuari
import retrofit2.Response
import retrofit2.http.GET

interface UsuariApiRest {

    @GET("api/usuaris")
    suspend fun findAll(): Response<List<Usuari>>

}