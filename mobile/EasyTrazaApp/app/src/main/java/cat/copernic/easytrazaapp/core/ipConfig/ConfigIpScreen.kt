package cat.copernic.easytrazaapp.core.ipConfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cat.copernic.easytrazaapp.core.network.ConfigManager
import cat.copernic.easytrazaapp.core.network.UsuariRetrofitInstance

@Composable
fun ConfigIpScreen(onNavigateBack: () -> Unit) {

    val context = LocalContext.current

    var ip by remember {
        mutableStateOf(ConfigManager.getIp(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =========================================
            // TÍTULO Y DESCRIPCIÓN
            // =========================================
            Text(
                text = "Configuración IP",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Introduce la dirección IP de tu servidor para conectar la aplicación.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================
            // INPUT FIELD
            // =========================================
            OutlinedTextField(
                value = ip,
                onValueChange = { newValue -> ip = newValue },
                label = { Text("IP del servidor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true // Evita que se haga multilinea por error
            )

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================
            // BOTÓN GUARDAR
            // =========================================
            Button(
                onClick = {
                    ConfigManager.saveIp(context, ip)

                    // 🔥 Recrear retrofit inmediatamente
                    UsuariRetrofitInstance.refresh(context)

                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Altura estandarizada con el resto de la app
            ) {
                Text("Guardar configuración")
            }
        }

        // =========================================
        // BOTÓN FLOTANTE PARA VOLVER
        // =========================================
        FloatingActionButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            // Utilizamos el icono nativo de flecha hacia atrás
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }
    }
}