package cat.copernic.easytrazaapp.core.network

import cat.copernic.easytrazaapp.core.DTOs.ProveidorDTO
import retrofit2.http.GET

interface ProveidorApiRest {

    @GET("api/proveidors")
    suspend fun getAll(): List<ProveidorDTO>
}