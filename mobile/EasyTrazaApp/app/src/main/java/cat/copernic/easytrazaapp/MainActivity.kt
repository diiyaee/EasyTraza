package cat.copernic.easytrazaapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.easytrazaapp.core.ipConfig.ConfigIpScreen
import cat.copernic.easytrazaapp.core.DTOs.MateriaDTO
import cat.copernic.easytrazaapp.core.DTOs.ProveidorDTO
import cat.copernic.easytrazaapp.core.network.UsuariRetrofitInstance
import cat.copernic.easytrazaapp.core.network.MateriaApiRest
import cat.copernic.easytrazaapp.core.network.ProveidorApiRest
import cat.copernic.easytrazaapp.core.screens.HomeScreen
import cat.copernic.easytrazaapp.core.utils.SessionManager
import cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.view.AlbaraListScreen
import cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.view.CreateAlbaraScreen
import cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.viewmodel.AlbaraViewModel
import cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.viewmodel.CreateAlbaraViewModel
import cat.copernic.easytrazaapp.feature.login.presentation.view.LoginScreen
import cat.copernic.easytrazaapp.feature.lot.presentation.view.LotsScreen
import cat.copernic.easytrazaapp.feature.lot.presentation.viewmodel.LotViewModel
import cat.copernic.easytrazaapp.ui.theme.EasyTrazaAppTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // =========================================
        // 🌟 1. DESPERTAMOS AL SESSION MANAGER ANTES DE NADA
        // =========================================
        SessionManager.init(applicationContext)

        setContent {
            EasyTrazaAppTheme {
                val navController = rememberNavController()

                val albaraViewModel: AlbaraViewModel = viewModel()
                val createViewModel: CreateAlbaraViewModel = viewModel()
                val lotViewModel: LotViewModel = viewModel()

                val context = LocalContext.current
                val retrofit = UsuariRetrofitInstance.getRetrofit(context)
                val proveidorApi = retrofit.create(ProveidorApiRest::class.java)
                val materiaApi = retrofit.create(MateriaApiRest::class.java)

                val rutaInicial = if (SessionManager.currentUser != null) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }

                NavHost(
                    navController = navController,
                    startDestination = rutaInicial
                ) {

                    // =====================================
                    // LOGIN
                    // =====================================
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onUserSelected = { selectedUser ->

                                SessionManager.login(selectedUser)

                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onConfigClick = {
                                navController.navigate(Screen.ConfigIp.route)
                            }
                        )
                    }

                    // =====================================
                    // HOME
                    // =====================================
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onAlbaranesClick = {
                                navController.navigate(Screen.List.route)
                            },
                            onLotesClick = {
                                navController.navigate(Screen.Lotes.route)
                            },
                            onLogoutClick = {
                                SessionManager.logout()

                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // =====================================
                    // LISTA ALBARANES
                    // =====================================

                    composable(Screen.List.route) {
                        AlbaraListScreen(
                            viewModel = albaraViewModel,
                            navController = navController,
                            onCreateClick = {
                                navController.navigate(Screen.Create.route)
                            }
                        )
                    }

                    // =====================================
                    // CREATE
                    // =====================================

                    composable(Screen.Create.route) {
                        CreateAlbaraScreen(
                            viewModel = createViewModel,
                            navController = navController,
                            onSaved = {
                                albaraViewModel.loadAlbaranes()
                                navController.popBackStack()
                            }
                        )
                    }

                    // =====================================
                    // LOTES
                    // =====================================

                    composable(Screen.Lotes.route) {
                        LotsScreen(
                            viewModel = lotViewModel,
                            navController = navController
                        )
                    }

                    // =====================================
                    // CONFIG IP
                    // =====================================

                    composable(Screen.ConfigIp.route) {

                        ConfigIpScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Home : Screen("home")

    object List : Screen("list")

    object Create : Screen("create")

    object Lotes : Screen("lotes")

    object ConfigIp : Screen("config_ip")
}