package cat.copernic.easytrazaapp.feature.lot.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.easytrazaapp.core.DTOs.EstatLot
import cat.copernic.easytrazaapp.feature.lot.presentation.viewmodel.LotViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import cat.copernic.easytrazaapp.Screen
import cat.copernic.easytrazaapp.core.utils.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotsScreen(
    viewModel: LotViewModel = viewModel(),
    navController: NavController
) {
    val lots = viewModel.lots
    val error = viewModel.error

    // =========================================
    // 🔄 ESTADO DEL PULL TO REFRESH (NUEVO)
    // =========================================
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // =========================================
    // 🛡️ VIGILANTE DE SESIÓN CADUCADA
    // =========================================
    LaunchedEffect(error) {
        if (error != null && error.contains("Sesión caducada")) {
            SessionManager.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Carga inicial
    LaunchedEffect(Unit) {
        viewModel.carregarLots()
    }

    var showDialog by remember { mutableStateOf(false) }
    var pendingId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🌟 BANNER DE ERROR
        if (error != null && !error.contains("Sesión caducada")) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
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

        // DIÁLOGO DE CONFLICTO
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Conflicto") },
                text = { Text("Ya hay un lote abierto con esta materia. ¿Quieres cerrarlo y abrir este?") },
                confirmButton = {
                    Button(
                        onClick = {
                            pendingId?.let { viewModel.canviarEstat(it, forcar = true) }
                            showDialog = false
                        }
                    ) { Text("Sí") }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) { Text("No") }
                }
            )
        }

        // =========================================
        // 📦 PULL TO REFRESH BOX (NUEVO)
        // Envuelve únicamente a la lista para que el banner de error quede por encima
        // =========================================
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                // Lanzamos la recarga de datos
                coroutineScope.launch {
                    isRefreshing = true
                    viewModel.carregarLots() // Llamamos a tu función
                    isRefreshing = false // Ocultamos la rueda al terminar
                }
            },
            modifier = Modifier.weight(1f) // Ocupa el espacio restante
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(lots) { lot ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(lot.numLot, style = MaterialTheme.typography.titleMedium)
                                Text("${lot.materiaNom}", style = MaterialTheme.typography.bodyMedium)

                                // 🔥 BADGE DE ESTADO
                                val (label, color) = when (lot.estatLot) {
                                    EstatLot.EN_ESTOC -> "EN STOCK" to Color(0xFF2E7D32)
                                    EstatLot.OBERT -> "ABIERTO" to Color(0xFFF9A825)
                                    EstatLot.ACABAT -> "ACABADO" to Color(0xFF212121)
                                }

                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .background(color = color, shape = RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            if (lot.estatLot != EstatLot.ACABAT) {
                                Button(
                                    onClick = {
                                        viewModel.canviarEstat(lot.id) {
                                            pendingId = lot.id
                                            showDialog = true
                                        }
                                    }
                                ) {
                                    Text(if (lot.estatLot == EstatLot.EN_ESTOC) "Abrir" else "Acabar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}