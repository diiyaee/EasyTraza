package cat.copernic.easytrazaapp.core.DTOs

data class AlbaraResponseDTO(
    val numAlbara: String,
    val dataRecepcio: String,
    val proveidor: ProveidorDTO,
    val usuari: Usuari?,
    val linies: List<LiniaDTO>
)