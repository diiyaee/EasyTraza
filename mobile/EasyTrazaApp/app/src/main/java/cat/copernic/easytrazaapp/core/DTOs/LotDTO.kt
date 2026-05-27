package cat.copernic.easytrazaapp.core.DTOs

data class LotDTO(
    val id: Long,
    val numLot: String,
    val estatLot: EstatLot,
    val materiaNom: String,
    val proveidorNom: String
)