package com.example.food2fork.screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.food2fork.data.AppDatabase
import com.example.food2fork.data.RecipeEntity
import com.example.food2fork.data.RecipeResponse
import com.example.food2fork.data.toEntity
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)


    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val _recipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val recipes: StateFlow<List<RecipeEntity>> = _recipes.asStateFlow()

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        viewModelScope.launch {

            val localRecipes = database.recipeDao().getAllRecipes()
            _recipes.value = localRecipes

            Log.d("RoomDatabase", "Recettes locales chargées: ${localRecipes.size}")


            val apiRecipes = fetchRecipesFromApi()
            if (apiRecipes != null && apiRecipes.isNotEmpty()) {
                database.recipeDao().deleteAllRecipes()
                database.recipeDao().insertRecipes(apiRecipes)
                _recipes.value = apiRecipes
                Log.d("API", "Recettes mises à jour depuis l'API: ${apiRecipes.size}")
            } else {
                Log.e("API", "Échec de l'API, on conserve les recettes locales.")
            }
        }
    }

    private suspend fun fetchRecipesFromApi(query: String = "") : List<RecipeEntity>? {
        return try {
            val url = "https://food2fork.ca/api/recipe/search/?page=1&query=$query"

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

            val recipeResponse = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                .decodeFromString<RecipeResponse>(response.bodyAsText())

            recipeResponse.results.map { it.toEntity() }

        } catch (e: Exception) {
            Log.e("API", "Erreur lors de la récupération des recettes: ${e.localizedMessage}", e)
            null
        }
    }


    fun searchRecipes(query: String) {
        viewModelScope.launch {
            val apiRecipes = fetchRecipesFromApi(query)
            if (apiRecipes != null && apiRecipes.isNotEmpty()) {
                database.recipeDao().deleteAllRecipes()
                database.recipeDao().insertRecipes(apiRecipes)
                _recipes.value = apiRecipes
                Log.d("API", "Résultats de la recherche: ${apiRecipes.size} recettes trouvées.")
            } else {
                Log.e("API", "Aucune recette trouvée pour '$query', affichage des recettes locales.")
            }
        }
    }
}
