package cat.copernic.easytrazaapp.feature.lot.data.repository

import cat.copernic.easytrazaapp.core.DTOs.LotDTO
import cat.copernic.easytrazaapp.core.network.LotApiRest

class LotRepository(
    private val api: LotApiRest
) {

    suspend fun getLots(): List<LotDTO> {
        return api.getLots()
    }

    suspend fun canviarEstat(id: Long, forcar: Boolean = false) {
        api.canviarEstat(id, forcar)
    }
}