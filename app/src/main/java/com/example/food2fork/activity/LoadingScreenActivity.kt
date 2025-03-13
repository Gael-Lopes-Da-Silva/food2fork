package com.example.food2fork.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.food2fork.R
import com.example.food2fork.data.AppDatabase
import com.example.food2fork.data.RecipeEntity
import com.example.food2fork.data.RecipeResponse
import com.example.food2fork.ui.theme.Food2forkTheme
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingScreenActivity : ComponentActivity() {
    private val client = HttpClient()
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this)

        setContent {
            Food2forkTheme {
                LoadingScreen()
            }
        }

        lifecycleScope.launch {
            val response = fetchRecipesFromApi()

            if (response != null && response.isNotEmpty()) {
                saveRecipesToDatabase(response)
            } else {
                Log.e("API", "Échec de récupération des recettes, on garde la base locale.")
            }

            delay(1000)
            val intent = Intent(this@LoadingScreenActivity, RecipesListActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private suspend fun fetchRecipesFromApi(query: String = "") : List<RecipeEntity>? {
        return try {
            val baseUrl = "https://food2fork.ca/api/recipe/search/"
            val url = if (query.isNotEmpty()) "$baseUrl?page=1&query=$query" else "$baseUrl?page=1&query=chicken"

            Log.d("API", "Envoi de la requête à l'API : $url")

            val response = client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
                    contentType(ContentType.Application.Json)
                }
            }

            if (!response.status.isSuccess()) {
                Log.e("API", "Erreur HTTP ${response.status.value}: ${response.status.description}")
                return null
            }

            val jsonString = response.bodyAsText()
            Log.d("API", "Réponse de l'API : $jsonString")

            val recipeResponse = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                .decodeFromString<RecipeResponse>(jsonString)

            recipeResponse.results.map {
                RecipeEntity(it.pk, it.title, it.featured_image, it.source_url, it.description, it.cooking_instructions, it.ingredients.joinToString(","))
            }
        } catch (e: Exception) {
            Log.e("API", "Erreur lors de la récupération des recettes: ${e.localizedMessage}", e)
            null
        }
    }



    private suspend fun saveRecipesToDatabase(recipes: List<RecipeEntity>) {
        database.recipeDao().deleteAllRecipes()
        database.recipeDao().insertRecipes(recipes)
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(255, 155, 0)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )
    }
}
