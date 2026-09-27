package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainScaffold
import com.example.ui.theme.BizlyTheme
import com.example.ui.viewmodel.BizlyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BizlyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BizlyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScaffold(viewModel = viewModel)
                }
            }
        }
    }
}
