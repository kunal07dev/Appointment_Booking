package com.dr.booking_client.ui.navigation


import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.dr.booking_client.ui.screens.*
import com.dr.booking_client.ui.screens.admin.AdminDashboardScreen
import com.dr.booking_client.ui.screens.appointments.MyAppointmentsScreen
import com.dr.booking_client.ui.screens.auth.LoginScreen
import com.dr.booking_client.ui.screens.booking.BookingScreen
//import com.dr.booking_client.ui.screens.auth.RegisterScreen
import com.dr.booking_client.viewmodel.AuthViewModel
import com.dr.booking_client.viewmodel.BookingViewModel

object Routes {
    const val HOME           = "home"
    const val BOOKING        = "booking"
    const val LOGIN          = "login"
    const val REGISTER       = "register"
    const val MY_APPOINTMENTS = "my_appointments"
    const val ADMIN          = "admin"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val bookingViewModel: BookingViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                currentUser  = authViewModel.currentUser,
                onBookClick  = { navController.navigate(Routes.BOOKING) },
                onMyApptClick = {
                    if (authViewModel.currentUser != null)
                        navController.navigate(Routes.MY_APPOINTMENTS)
                    else
                        navController.navigate(Routes.LOGIN + "?next=my_appointments")
                },
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onLogout     = { authViewModel.logout() },
                onAdminClick = { navController.navigate(Routes.ADMIN) }
            )
        }

        composable(Routes.BOOKING) {
            BookingScreen(
                viewModel = bookingViewModel,
                currentUser = authViewModel.currentUser,
                onBack    = { navController.popBackStack() },
                onLoginRequest = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN + "?next={next}") { backStack ->
            val next = backStack.arguments?.getString("next")
            LoginScreen(
                viewModel = authViewModel,
                onSuccess = {
                    if (next == "my_appointments") {
                        navController.navigate(Routes.MY_APPOINTMENTS) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                },

                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onSuccess = { navController.popBackStack() },
                onBack    = { navController.popBackStack() }
            )
        }

//        composable(Routes.REGISTER) {
//            RegisterScreen(
//                viewModel = authViewModel,
//                onSuccess = { navController.popBackStack() },
//                onBack = { navController.popBackStack() }
//            )
//        }

        composable(Routes.MY_APPOINTMENTS) {
            MyAppointmentsScreen(
                userId = authViewModel.currentUser?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN) {
            AdminDashboardScreen(
                currentUser = authViewModel.currentUser,
                onBack = { navController.popBackStack() },
                onLoginRequired = { navController.navigate(Routes.LOGIN) }
            )
        }
    }
}