package cat.copernic.easytrazaapp.core.DTOs

data class OcrAlbaraDTO(
    val numAlbara: String?,
    val proveidor: String?,
    val dataRecepcio: String?,
    val linies: List<OcrLiniaDTO>?
)