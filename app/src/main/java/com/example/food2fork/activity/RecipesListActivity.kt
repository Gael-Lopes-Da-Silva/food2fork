package com.example.food2fork.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.food2fork.screen.RecipesListScreen
import com.example.food2fork.screen.RecipesListViewModel
import com.example.food2fork.ui.theme.Food2forkTheme

class RecipesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Food2forkTheme {
                val navController = rememberNavController()
                val viewModel = ViewModelProvider(this)[RecipesListViewModel::class.java]

                Scaffold { innerPadding ->
                    RecipesListScreen(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
