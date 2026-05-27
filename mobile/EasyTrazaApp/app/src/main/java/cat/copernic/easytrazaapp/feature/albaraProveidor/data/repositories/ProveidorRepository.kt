package cat.copernic.easytrazaapp.feature.albaraProveidor.data.repositories

import cat.copernic.easytrazaapp.core.DTOs.ProveidorDTO
import cat.copernic.easytrazaapp.core.network.ProveidorApiRest

class ProveidorRepository(
    private val api: ProveidorApiRest
) {

    suspend fun getAll(): List<ProveidorDTO> {
        return api.getAll()
    }
}