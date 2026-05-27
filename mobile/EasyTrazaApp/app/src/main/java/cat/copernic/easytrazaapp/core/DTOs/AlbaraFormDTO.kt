package cat.copernic.easytrazaapp.core.DTOs

data class AlbaraFormDTO(
    val numAlbara: String,
    val proveidorId: Long,
    val dataRecepcio: String,
    val usuariId: Long,
    val linies: List<LiniaDTO>
)