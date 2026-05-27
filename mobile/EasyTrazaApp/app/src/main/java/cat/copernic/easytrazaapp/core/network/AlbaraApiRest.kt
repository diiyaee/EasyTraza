package cat.copernic.easytrazaapp.core.network

import cat.copernic.easytrazaapp.core.DTOs.AlbaraFormDTO
import cat.copernic.easytrazaapp.core.DTOs.AlbaraResponseDTO
import cat.copernic.easytrazaapp.core.DTOs.OcrAlbaraDTO
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AlbaraApiRest {

    @GET("api/albaranes")
    suspend fun getAll(): List<AlbaraResponseDTO>

    @POST("api/albaranes")
    suspend fun create(
        @Body dto: AlbaraFormDTO
    ): AlbaraResponseDTO

    @Multipart
    @POST("api/albaranes/ocr")
    suspend fun processarImatge(
        @Part image: MultipartBody.Part
    ): OcrAlbaraDTO
}