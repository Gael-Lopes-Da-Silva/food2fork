package com.example.food2fork

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.food2fork.ui.theme.Food2forkTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import kotlinx.coroutines.launch

class LoadingScreenActivity : ComponentActivity() {
    private suspend fun getRecipes(): String {
        val client = HttpClient()

        val response = client.get("https://food2fork.ca/api/recipe/search/?page=2&query=beef%20carrot%20potato%20onion") {
            headers {
                append(HttpHeaders.Authorization, "Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
            }
        }

        return response.bodyAsText()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Food2forkTheme {
                val scope = rememberCoroutineScope()

                LaunchedEffect(true) {
                    scope.launch {
                        val response = try {
                            getRecipes()
                        } catch (error: Exception) {
                            error.localizedMessage ?: "error"
                        }

                        val intent = Intent(this@LoadingScreenActivity, RecipesListActivity::class.java)
                        intent.putExtra("request_data", response)
                        startActivity(intent)
                    }
                }

                LoadingScreen();
            }
        }
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
            contentDescription = "",
            modifier = Modifier.size(300.dp)
        )
    }
}