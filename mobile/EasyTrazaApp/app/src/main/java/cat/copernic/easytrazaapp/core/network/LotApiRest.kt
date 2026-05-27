package cat.copernic.easytrazaapp.core.network

import cat.copernic.easytrazaapp.core.DTOs.LotDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LotApiRest {

    @GET("api/lots")
    suspend fun getLots(): List<LotDTO>

    @POST("api/lots/canviar-estat/{id}")
    suspend fun canviarEstat(
        @Path("id") id: Long,
        @Query("forcar") forcar: Boolean = false
    )
}