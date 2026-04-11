package com.dr.booking_client


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.dr.booking_client.ui.navigation.AppNavigation
import com.dr.booking_client.ui.theme.SharmaClinicTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharmaClinicTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}