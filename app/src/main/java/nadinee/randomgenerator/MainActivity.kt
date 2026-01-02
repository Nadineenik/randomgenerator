package nadinee.randomgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nadinee.randomgenerator.data.Repository
import nadinee.randomgenerator.ui.GeneratorScreen
import nadinee.randomgenerator.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Repository.init(this)

        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "generator",
                    onClick = { navController.navigate("generator") { popUpTo("generator") { inclusive = true } } },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    label = { Text("Генератор") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "settings",
                    onClick = { navController.navigate("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Настройки") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "generator",
            modifier = Modifier.padding(padding)
        ) {
            composable("generator") { GeneratorScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}