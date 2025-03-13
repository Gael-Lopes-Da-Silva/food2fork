package com.example.food2fork.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.food2fork.data.AppDatabase
import com.example.food2fork.data.RecipeDetails
import com.example.food2fork.data.RecipeEntity
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailsScreen(recipeId: Int, database: AppDatabase, modifier: Modifier = Modifier) {
    val client = HttpClient()
    val scope = rememberCoroutineScope()

    var recipe by remember { mutableStateOf<RecipeDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchRecipeDetails() {
        val url = "https://food2fork.ca/api/recipe/get/?id=$recipeId"
        isLoading = true
        try {
            val response = client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
                }
            }

            val fetchedRecipe = kotlinx.serialization.json.Json.decodeFromString<RecipeDetails>(
                response.bodyAsText()
            )


            database.recipeDao().insertRecipes(
                listOf(
                    RecipeEntity(
                        pk = fetchedRecipe.pk,
                        title = fetchedRecipe.title,
                        featuredImage = fetchedRecipe.featured_image,
                        sourceUrl = fetchedRecipe.source_url,
                        description = fetchedRecipe.description,
                        cookingInstructions = fetchedRecipe.cooking_instructions,
                        ingredients = fetchedRecipe.ingredients.joinToString(",")
                    )
                )
            )

            recipe = fetchedRecipe
        } catch (e: Exception) {

            recipe = database.recipeDao().getRecipeById(recipeId)?.let {
                RecipeDetails(
                    pk = it.pk,
                    title = it.title,
                    featured_image = it.featuredImage,
                    source_url = it.sourceUrl,
                    description = it.description,
                    cooking_instructions = it.cookingInstructions,
                    ingredients = it.ingredients?.split(",") ?: emptyList(),
                    date_added = "",
                    date_updated = ""
                )
            }
            errorMessage = if (recipe == null) "Impossible de charger la recette hors ligne." else null
        }
        isLoading = false
    }

    LaunchedEffect(recipeId) {
        scope.launch { fetchRecipeDetails() }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Erreur : $errorMessage", color = Color.Red)
        }
    } else if (recipe != null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Image(
                    painter = rememberAsyncImagePainter(recipe!!.featured_image),
                    contentDescription = recipe!!.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = recipe!!.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Ingrédients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            items(recipe!!.ingredients) { ingredient ->
                Text(text = "- $ingredient", fontSize = 16.sp)
            }
        }
    }
}

