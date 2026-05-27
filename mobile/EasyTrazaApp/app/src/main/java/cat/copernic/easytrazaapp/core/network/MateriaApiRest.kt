package cat.copernic.easytrazaapp.core.network

import cat.copernic.easytrazaapp.core.DTOs.MateriaDTO
import retrofit2.http.GET

interface MateriaApiRest {

    @GET("api/materies")
    suspend fun getAll(): List<MateriaDTO>
}