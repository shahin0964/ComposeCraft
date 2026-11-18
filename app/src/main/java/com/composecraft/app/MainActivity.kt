package com.composecraft.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.composecraft.app.ui.screens.DesignerScreen
import com.composecraft.app.ui.theme.ComposeCraftTheme
import com.composecraft.app.viewmodel.DesignerViewModel

class MainActivity : ComponentActivity() {

    private val designerViewModel: DesignerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCraftTheme {
                DesignerScreen(viewModel = designerViewModel)
            }
        }
    }
}
