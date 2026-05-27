package cat.copernic.easytrazaapp.feature.albaraProveidor.data.repositories

import cat.copernic.easytrazaapp.core.DTOs.AlbaraFormDTO
import cat.copernic.easytrazaapp.core.DTOs.AlbaraResponseDTO
import cat.copernic.easytrazaapp.core.DTOs.OcrAlbaraDTO
import cat.copernic.easytrazaapp.core.network.AlbaraApiRest
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class AlbaraRepository(
    private val api: AlbaraApiRest
) {

    suspend fun getAll(): List<AlbaraResponseDTO> {
        return api.getAll()
    }

    suspend fun create(dto: AlbaraFormDTO): AlbaraResponseDTO {

        try {

            return api.create(dto)

        } catch (e: retrofit2.HttpException) {

            val errorBody = e.response()
                ?.errorBody()
                ?.string()

            val message = try {

                val json = JSONObject(errorBody ?: "{}")

                json.optString("error").ifBlank {

                    when (e.code()) {

                        400 -> "Datos inválidos"

                        404 -> "Endpoint no encontrado"

                        500 -> "Error interno del servidor"

                        else -> "Error (${e.code()})"
                    }
                }

            } catch (_: Exception) {

                when (e.code()) {

                    400 -> "Datos inválidos"

                    404 -> "Endpoint no encontrado"

                    500 -> "Error interno del servidor"

                    else -> "Error (${e.code()})"
                }
            }

            throw Exception(message)

        } catch (e: Exception) {

            throw Exception(
                e.message ?: "Error creando albarán"
            )
        }
    }

    suspend fun processOcr(file: File): OcrAlbaraDTO {

        val requestFile = file
            .asRequestBody("image/*".toMediaTypeOrNull())

        val body = MultipartBody.Part.createFormData(
            "image", // ⚠️ debe coincidir con backend
            file.name,
            requestFile
        )

        try {

            return api.processarImatge(body)

        } catch (e: retrofit2.HttpException) {

            val errorBody = e.response()
                ?.errorBody()
                ?.string()

            val message = try {

                val json = JSONObject(errorBody ?: "{}")

                json.optString("error").ifBlank {

                    when (e.code()) {

                        400 -> "El OCR no pudo interpretar el albarán"

                        404 -> "Endpoint OCR no encontrado"

                        500 -> "Error interno del servidor"

                        else -> "Error OCR (${e.code()})"
                    }
                }

            } catch (_: Exception) {

                when (e.code()) {

                    400 -> "El OCR no pudo interpretar el albarán"

                    404 -> "Endpoint OCR no encontrado"

                    500 -> "Error interno del servidor"

                    else -> "Error OCR (${e.code()})"
                }
            }

            throw Exception(message)

        } catch (e: Exception) {

            throw Exception(
                e.message ?: "Error procesando OCR"
            )
        }
    }
}