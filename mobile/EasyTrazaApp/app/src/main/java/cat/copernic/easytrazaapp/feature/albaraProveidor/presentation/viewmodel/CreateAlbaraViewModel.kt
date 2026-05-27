package cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytrazaapp.core.DTOs.AlbaraFormDTO
import cat.copernic.easytrazaapp.core.DTOs.LiniaDTO
import cat.copernic.easytrazaapp.core.DTOs.MateriaDTO
import cat.copernic.easytrazaapp.core.DTOs.ProveidorDTO
import cat.copernic.easytrazaapp.core.error.ErrorMapper
import cat.copernic.easytrazaapp.core.network.ApiClient
import cat.copernic.easytrazaapp.core.utils.SessionManager
import cat.copernic.easytrazaapp.feature.albaraProveidor.data.repositories.AlbaraRepository
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import kotlin.collections.map

class CreateAlbaraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AlbaraRepository(
        ApiClient.getAlbaraApi(application)
    )

    var numAlbara by mutableStateOf("")
    var proveidorId by mutableStateOf<Long?>(null)
    var dataRecepcio by mutableStateOf("")

    var proveidors by mutableStateOf<List<ProveidorDTO>>(emptyList())
    var materies by mutableStateOf<List<MateriaDTO>>(emptyList())

    var linies = mutableStateListOf(LiniaFormState())

    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // =====================================================
    // LOAD DATA
    // =====================================================
    fun loadData() {
        viewModelScope.launch {
            try {
                proveidors = ApiClient.getProveidorApi(getApplication()).getAll()
                materies = ApiClient.getMateriaApi(getApplication()).getAll()
            } catch (e: Exception) {
                error = ErrorMapper.getFriendlyMessage(e)
            }
        }
    }

    // =====================================================
    // LÍNEAS
    // =====================================================
    fun addLinia() {
        linies.add(LiniaFormState())
    }

    fun removeLinia(index: Int) {
        if (index in linies.indices) {
            linies.removeAt(index)
        }
    }

    // =====================================================
    // CREATE NORMAL
    // =====================================================
    @RequiresApi(Build.VERSION_CODES.O)
    fun create(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                loading = true
                error = null

                // =========================
                // VALIDACIONES GENERALES
                // =========================
                if (numAlbara.isBlank()) throw IllegalStateException("El número de albarán es obligatorio")

                val recepcio = try {
                    LocalDate.parse(dataRecepcio)
                } catch (e: Exception) {
                    throw IllegalStateException("Fecha de recepción inválida")
                }

                if (recepcio.isAfter(LocalDate.now())) throw IllegalStateException("La fecha de recepción no puede ser futura")

                val provId = proveidorId ?: throw IllegalStateException("Selecciona un proveedor")

                if (linies.isEmpty()) throw IllegalStateException("Añade al menos una línea")

                // =========================
                // LOTES DUPLICADOS
                // =========================
                val lotsDuplicats = linies.groupBy { it.numLot.trim() }.filter { it.key.isNotBlank() && it.value.size > 1 }
                if (lotsDuplicats.isNotEmpty()) throw IllegalStateException("Hay lotes duplicados en el albarán")

                // =========================
                // VALIDACIÓN LÍNEAS
                // =========================
                val validLinies = linies.map { l ->
                    val materiaId = l.materiaId ?: throw IllegalStateException("La materia prima es obligatoria")
                    val quantitat = l.quantitat.toDoubleOrNull() ?: throw IllegalStateException("Cantidad inválida")
                    if (quantitat <= 0) throw IllegalStateException("Cantidad debe ser > 0")
                    if (l.numLot.isBlank()) throw IllegalStateException("El lote es obligatorio")

                    if (l.dataCaducitat.isNotBlank()) {
                        val caducitat = try {
                            LocalDate.parse(l.dataCaducitat)
                        } catch (e: Exception) {
                            throw IllegalStateException("Fecha de caducidad inválida")
                        }
                        if (caducitat.isBefore(LocalDate.now())) {
                            throw IllegalStateException("La fecha de caducidad del lote ${l.numLot} ya ha pasado")
                        }
                    }

                    LiniaDTO(
                        numLot = l.numLot,
                        materiaId = materiaId,
                        quantitat = quantitat,
                        unitats = l.unitats,
                        dataCaducitat = l.dataCaducitat
                    )
                }

                // =========================
                // DTO
                // =========================
                val userId = SessionManager.currentUser?.id ?: throw IllegalStateException("No hay usuario en sesión")

                val dto = AlbaraFormDTO(
                    numAlbara = numAlbara,
                    proveidorId = provId,
                    dataRecepcio = dataRecepcio,
                    usuariId = userId,
                    linies = validLinies
                )

                repository.create(dto)

                resetForm()
                onSuccess()

            } catch (e: Exception) {
                // Al ser validaciones IllegalStateException o errores de red, el Mapper los tratará correctamente
                error = ErrorMapper.getFriendlyMessage(e)
            } finally {
                loading = false
            }
        }
    }

    // =====================================================
    // OCR
    // =====================================================
    fun processOcr(file: File) {
        viewModelScope.launch {
            try {
                loading = true
                error = null

                val result = repository.processOcr(file)

                numAlbara = result.numAlbara ?: ""

                val proveidor = proveidors.find { it.nom == result.proveidor }
                if (proveidor == null) throw IllegalStateException("No existe el proveedor: ${result.proveidor}")
                proveidorId = proveidor.id

                linies.clear()

                result.linies?.forEach { l ->
                    val materia = materies.find { it.nom == l.materia }
                    if (materia == null) throw IllegalStateException("No existe la materia: ${l.materia}")

                    linies.add(
                        LiniaFormState(
                            numLot = l.numLot ?: "",
                            materiaId = materia.id,
                            quantitat = l.quantitat?.toString() ?: "",
                            unitats = l.unitats ?: "",
                            dataCaducitat = l.dataCaducitat ?: ""
                        )
                    )
                }

                if (linies.isEmpty()) linies.add(LiniaFormState())

            } catch (e: Exception) {
                error = ErrorMapper.getFriendlyMessage(e)
            } finally {
                loading = false
            }
        }
    }

    // =====================================================
    // RESET Y STATE (Se mantienen igual)
    // =====================================================
    fun resetForm() {
        numAlbara = ""
        proveidorId = null
        dataRecepcio = ""

        linies.clear()
        linies.add(LiniaFormState())
    }

    data class LiniaFormState(
        var numLot: String = "",
        var materiaId: Long? = null,
        var quantitat: String = "",
        var unitats: String = "",
        var dataCaducitat: String = ""
    )
}