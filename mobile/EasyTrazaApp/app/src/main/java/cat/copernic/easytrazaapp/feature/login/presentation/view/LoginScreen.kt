package cat.copernic.easytrazaapp.feature.login.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.easytrazaapp.core.DTOs.Usuari
import cat.copernic.easytrazaapp.feature.login.presentation.viewmodel.LoginState
import cat.copernic.easytrazaapp.feature.login.presentation.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onUserSelected: (Usuari) -> Unit,
    onConfigClick: () -> Unit, // <-- NUEVO
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchUsuaris(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona tu perfil") },
                actions = {

                    // =========================================
                    // BOTÓN CONFIGURAR IP
                    // =========================================
                    IconButton(onClick = onConfigClick) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Configurar IP",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            when (val currentState = state) {

                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }

                is LoginState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is LoginState.Success -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(currentState.usuaris) { usuari ->

                            UserProfileCard(
                                usuari = usuari
                            ) {
                                onUserSelected(usuari)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileCard(usuari: Usuari, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // 1. Muestra el Nombre y Apellido
                val fullName = "${usuari.nom ?: ""} ${usuari.cognoms ?: ""}".trim()
                Text(
                    text = if (fullName.isNotEmpty()) fullName else "Usuario sin nombre",
                    style = MaterialTheme.typography.titleLarge
                )

                // 2. Muestra el Correo debajo
                Text(
                    text = usuari.email ?: "Sin correo registrado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 3. Muestra si es Admin o Empleado
                val rolText = if (usuari.isEsAdmin) "Administrador" else "Empleado"
                Text(
                    text = rolText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}