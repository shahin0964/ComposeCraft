package com.composecraft.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.composecraft.app.ui.screens.DesignerScreen
import com.composecraft.app.ui.screens.HomeScreen
import com.composecraft.app.ui.theme.ComposeCraftTheme
import com.composecraft.app.viewmodel.DesignerViewModel

class MainActivity : ComponentActivity() {

    private val designerViewModel: DesignerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCraftTheme {
                val currentRoute by designerViewModel.currentRoute.collectAsState()

                // Intercept back button when in editor to return to home dashboard
                BackHandler(enabled = currentRoute == "editor") {
                    designerViewModel.navigateToHome()
                }

                // Smooth crossfade between Android Studio Welcome Dashboard and Canvas Editor
                Crossfade(targetState = currentRoute, label = "ScreenTransition") { route ->
                    when (route) {
                        "home" -> HomeScreen(viewModel = designerViewModel)
                        "editor" -> DesignerScreen(viewModel = designerViewModel)
                        else -> HomeScreen(viewModel = designerViewModel)
                    }
                }
            }
        }
    }
}
