package com.example.food2fork.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.food2fork.screen.RecipeDetailsScreen
import com.example.food2fork.data.AppDatabase
import com.example.food2fork.ui.theme.Food2forkTheme

class RecipeDetailsActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this)

        val recipeId = intent.getIntExtra("recipe_id", -1)

        setContent {
            Food2forkTheme {
                Scaffold { innerPadding ->
                    RecipeDetailsScreen(
                        recipeId = recipeId,
                        database = database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

