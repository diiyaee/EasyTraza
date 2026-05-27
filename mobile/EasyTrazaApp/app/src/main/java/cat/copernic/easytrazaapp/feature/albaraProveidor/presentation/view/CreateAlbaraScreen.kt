package cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.view

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.easytrazaapp.Screen
import cat.copernic.easytrazaapp.core.utils.FileUtils.uriToFile
import cat.copernic.easytrazaapp.core.utils.SessionManager
import cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.viewmodel.CreateAlbaraViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbaraScreen(
    viewModel: CreateAlbaraViewModel = viewModel(),
    navController: NavController,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    val error = viewModel.error
    val loading = viewModel.loading

    LaunchedEffect(error) {
        if (error != null && error.contains("Sesión caducada")) {
            SessionManager.logout()

            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val expandedMateriaMap = remember { mutableStateMapOf<Int, Boolean>() }
    var expandedProveedor by remember { mutableStateOf(false) }

    val selectedProveedor = viewModel.proveidors.find { it.id == viewModel.proveidorId }

    // 📸 IMAGE PICKER
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.processOcr(file)
        }
    }

    // Usamos un Box principal para poder superponer la barra de carga sobre todo el formulario si está cargando
    Box(modifier = Modifier.fillMaxSize()) {

        // 🌟 COLUMNA PRINCIPAL (No scrolleable)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =========================================
            // 🚨 BANNER DE ERROR FIJO ARRIBA 🚨
            // (Siempre visible, no importa cuánto scroll hagas abajo)
            // =========================================
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // =========================================
            // 📝 FORMULARIO (Este sí tiene Scroll)
            // =========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(16.dp)
            ) {
                Text(
                    text = "➕ Crear albarán",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                // 📸 OCR BUTTON
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("📷 Importar con OCR")
                }

                Spacer(Modifier.height(16.dp))

                // 📄 NUM ALBARÁN
                OutlinedTextField(
                    value = viewModel.numAlbara,
                    onValueChange = { viewModel.numAlbara = it },
                    label = { Text("Número albarán") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // 🏢 PROVEEDOR
                ExposedDropdownMenuBox(
                    expanded = expandedProveedor,
                    onExpandedChange = { expandedProveedor = !expandedProveedor }
                ) {
                    OutlinedTextField(
                        value = selectedProveedor?.nom ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Proveedor") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedProveedor,
                        onDismissRequest = { expandedProveedor = false }
                    ) {
                        viewModel.proveidors.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.nom) },
                                onClick = {
                                    viewModel.proveidorId = p.id
                                    expandedProveedor = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 📅 FECHA
                DatePickerField(
                    value = viewModel.dataRecepcio,
                    label = "Fecha recepción"
                ) {
                    viewModel.dataRecepcio = it
                }

                Spacer(Modifier.height(16.dp))

                Text("📦 Líneas", style = MaterialTheme.typography.titleLarge)

                // 🔁 LÍNEAS
                viewModel.linies.forEachIndexed { index, linia ->
                    val expandedMateria = expandedMateriaMap[index] ?: false
                    val selectedMateria = viewModel.materies.find { it.id == linia.materiaId }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            // 🧾 LOTE
                            OutlinedTextField(
                                value = viewModel.linies[index].numLot,
                                onValueChange = { viewModel.linies[index] = viewModel.linies[index].copy(numLot = it) },
                                label = { Text("Num lote") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            // 🧪 MATERIA
                            ExposedDropdownMenuBox(
                                expanded = expandedMateria,
                                onExpandedChange = { expandedMateriaMap[index] = !expandedMateria }
                            ) {
                                OutlinedTextField(
                                    value = selectedMateria?.nom ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Materia") },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedMateria,
                                    onDismissRequest = { expandedMateriaMap[index] = false }
                                ) {
                                    viewModel.materies.forEach { m ->
                                        DropdownMenuItem(
                                            text = { Text(m.nom) },
                                            onClick = {
                                                viewModel.linies[index] = viewModel.linies[index].copy(materiaId = m.id)
                                                expandedMateriaMap[index] = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // 🔢 CANTIDAD
                            OutlinedTextField(
                                value = viewModel.linies[index].quantitat,
                                onValueChange = { viewModel.linies[index] = viewModel.linies[index].copy(quantitat = it) },
                                label = { Text("Cantidad") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            // 📦 UNIDADES
                            OutlinedTextField(
                                value = viewModel.linies[index].unitats,
                                onValueChange = { viewModel.linies[index] = viewModel.linies[index].copy(unitats = it) },
                                label = { Text("Unidades") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            // 📅 CADUCIDAD
                            DatePickerField(
                                value = viewModel.linies[index].dataCaducitat,
                                label = "Caducidad"
                            ) { value ->
                                viewModel.linies[index] = viewModel.linies[index].copy(dataCaducitat = value)
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { viewModel.removeLinia(index) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Eliminar línea")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ➕ ADD LÍNEA
                Button(
                    onClick = { viewModel.addLinia() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("+ Añadir línea")
                }

                Spacer(Modifier.height(24.dp))

                // 💾 SAVE
                Button(
                    onClick = { viewModel.create { onSaved() } },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !viewModel.loading
                ) {
                    Text("Guardar albarán")
                }

                // Añadimos un pequeño espacio extra al final para que el scroll llegue bien abajo
                Spacer(Modifier.height(30.dp))
            }
        }

        // =========================================
        // ⏳ PANTALLA DE CARGA FLOTANTE Y SEMITRANSPARENTE
        // =========================================
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // Fondo semitransparente oscuro
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Procesando...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------
// El DatePickerField y el uriToFile se quedan exactamente igual a los tuyos
// (Cópialos debajo de esta función si no los he incluido enteros aquí)
// -----------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    label: String,
    onDateSelected: (String) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { showModal = true }) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
            }
        }
    )

    if (showModal) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formattedDate = sdf.format(Date(millis))
                            onDateSelected(formattedDate)
                        }
                        showModal = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
