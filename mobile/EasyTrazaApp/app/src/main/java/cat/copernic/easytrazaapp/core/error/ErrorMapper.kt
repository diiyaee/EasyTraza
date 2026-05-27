package cat.copernic.easytrazaapp.core.error

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

object ErrorMapper {
    fun getFriendlyMessage(error: Throwable?, statusCode: Int? = null): String {
        val code = statusCode ?: (error as? HttpException)?.code()

        return when (code) {
            400 -> "Los datos enviados no son correctos o están incompletos."
            401 -> "Sesión caducada o permisos insuficientes. Vuelve a iniciar sesión."
            404 -> "El servidor no encuentra el recurso solicitado."
            409 -> "Existe un conflicto con estos datos"
            500 -> "El servidor ha tenido un fallo interno. Avisa al administrador."
            else -> when (error) {
                is ConnectException -> "No se ha podido conectar al servidor. Comprueba tu IP o tu conexión a la red."
                is SocketTimeoutException -> "El servidor está tardando demasiado en responder. Inténtalo de nuevo."
                is IllegalStateException -> error.message ?: "Revisa los datos del formulario."
                else -> "Ha ocurrido un error inesperado: ${error?.localizedMessage ?: "Desconocido"}"
            }
        }
    }
}