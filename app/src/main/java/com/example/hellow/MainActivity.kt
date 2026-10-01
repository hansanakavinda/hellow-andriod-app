package com.example.hellow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.example.hellow.model.Result
import com.example.hellow.model.RickCharacter
import com.example.hellow.ui.theme.HellowTheme
import com.example.hellow.viewmodel.CharacterViewModel
import kotlinx.serialization.Serializable

// --- Type-Safe Navigation Routes ---
@Serializable
object HomeRoute

@Serializable
data class GreetingRoute(val name: String)

@Serializable
data class DetailRoute(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HellowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HellowNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HellowNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            MainScreen(
                onNextClicked = { name ->
                    navController.navigate(GreetingRoute(name = name))
                }
            )
        }
        composable<GreetingRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<GreetingRoute>()
            GreetingScreen(
                name = route.name,
                onCharacterClick = { character ->
                    navController.navigate(
                        DetailRoute(
                            id = character.id,
                            name = character.name,
                            status = character.status,
                            species = character.species,
                            imageUrl = character.imageUrl
                        )
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            val character = RickCharacter(
                id = route.id,
                name = route.name,
                status = route.status,
                species = route.species,
                imageUrl = route.imageUrl
            )
            DetailScreen(
                character = character,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    onNextClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Hellow",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Enter your name",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onNextClicked(name.trim())
                }
            },
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFFA5D6A7),
                disabledContentColor = Color.DarkGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Next",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GreetingScreen(
    name: String,
    onCharacterClick: (RickCharacter) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Hellow $name",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Search Rick & Morty Characters (Tap for details):",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search character (e.g. Rick)...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is Result.Loading -> {
                    CircularProgressIndicator()
                }
                is Result.Error -> {
                    Text(
                        text = "Error: ${state.exception.localizedMessage ?: "Unknown error"}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Result.Success -> {
                    val characters = state.data
                    if (characters.isEmpty()) {
                        Text(text = "No characters found.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(characters) { character ->
                                Card(
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onCharacterClick(character) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = character.imageUrl,
                                            contentDescription = character.name,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = character.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${character.species} • ${character.status}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Back",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailScreen(
    character: RickCharacter,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Character Details",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )

        AsyncImage(
            model = character.imageUrl,
            contentDescription = character.name,
            modifier = Modifier
                .size(180.dp)
                .clip(MaterialTheme.shapes.extraLarge)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow(label = "Status", value = character.status)
                DetailRow(label = "Species", value = character.species)
                DetailRow(label = "ID", value = character.id.toString())
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Back to List",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HellowTheme {
        MainScreen(onNextClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    HellowTheme {
        DetailScreen(
            character = RickCharacter(
                id = 1,
                name = "Rick Sanchez",
                status = "Alive",
                species = "Human",
                imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
            ),
            onBack = {}
        )
    }
}
