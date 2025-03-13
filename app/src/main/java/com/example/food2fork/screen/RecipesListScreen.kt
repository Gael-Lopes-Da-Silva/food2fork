package com.example.food2fork.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.food2fork.activity.RecipeDetailsActivity
import com.example.food2fork.data.RecipeEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesListScreen(
    navController: NavController,
    viewModel: RecipesListViewModel,
    modifier: Modifier = Modifier
) {
    val recipes by viewModel.recipes.collectAsState()
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Poulet", "Bœuf", "Poisson", "Végétarien", "Pâtes", "Curry", "Soupe")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liste des recettes") },
                actions = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { newValue ->
                            searchText = newValue
                            scope.launch { viewModel.searchRecipes(newValue) }
                        },
                        label = { Text("Rechercher...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            LazyRow(modifier = Modifier.padding(8.dp)) {
                items(categories) { category ->
                    Button(
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                            scope.launch { viewModel.searchRecipes(searchText) }
                        },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == category) Color.Gray else Color.LightGray
                        )
                    ) {
                        Text(category)
                    }
                }
            }


            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {

                LazyColumn {
                    items(recipes) { recipe ->
                        RecipeItem(recipe, navController)
                    }
                }
            }
        }
    }
}




@Composable
fun RecipeItem(recipe: RecipeEntity, navController: NavController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                context.startActivity(
                    Intent(context, RecipeDetailsActivity::class.java).apply {
                        putExtra("recipe_id", recipe.pk)
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(recipe.featuredImage),
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
