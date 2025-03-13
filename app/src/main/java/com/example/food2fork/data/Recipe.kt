package com.example.food2fork.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val pk: Int,
    val title: String,
    val featuredImage: String,
    val sourceUrl: String,
    val description: String?,
    val cookingInstructions: String?,
    val ingredients: String?
)

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        pk = this.pk,
        title = this.title,
        featuredImage = this.featured_image,
        sourceUrl = this.source_url,
        description = this.description ?: "Aucune description",
        cookingInstructions = this.cooking_instructions ?: "Instructions non disponibles",
        ingredients = this.ingredients.joinToString(",")
    )
}



@Serializable
data class RecipeResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Recipe>
)

@Serializable
data class Recipe(
    val pk: Int,
    val title: String,
    val featured_image: String,
    val source_url: String,
    val description: String?,
    val cooking_instructions: String?,
    val ingredients: List<String>,
    val date_added: String,
    val date_updated: String,
    val publisher: String,
    val rating: Int,
    val long_date_added: Long,
    val long_date_updated: Long
)

@Serializable
data class RecipeDetails(
    val pk: Int,
    val title: String,
    val featured_image: String,
    val description: String?,
    val cooking_instructions: String?,
    val ingredients: List<String>,
    val date_added: String,
    val date_updated: String,
    val source_url: String
)