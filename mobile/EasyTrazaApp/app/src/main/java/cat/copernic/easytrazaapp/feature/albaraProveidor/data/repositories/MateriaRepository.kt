package cat.copernic.easytrazaapp.feature.albaraProveidor.data.repositories

import cat.copernic.easytrazaapp.core.DTOs.MateriaDTO
import cat.copernic.easytrazaapp.core.network.MateriaApiRest

class MateriaRepository(
    private val api: MateriaApiRest
) {

    suspend fun getAll(): List<MateriaDTO> {
        return api.getAll()
    }
}