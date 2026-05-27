package cat.copernic.easytrazaapp.core.DTOs

data class LiniaDTO(
    val numLot: String,
    val materiaId: Long,
    val quantitat: Double,
    val unitats: String,
    val dataCaducitat: String
)