package com.duoc.mobile_01_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.mobile_01_android.ui.navigation.AppNavigation
import com.duoc.mobile_01_android.ui.screens.HomeScreen
import com.duoc.mobile_01_android.ui.theme.Mobile_01_androidTheme
import com.duoc.mobile_01_android.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_01_androidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VeterinariaApp()
                }
            }
        }
    }
}

@Composable
fun VeterinariaApp() {
    val viewModel: MainViewModel = viewModel()
    AppNavigation(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun VeterinariaAppPreview() {
    Mobile_01_androidTheme {
        VeterinariaApp()
    }
}
