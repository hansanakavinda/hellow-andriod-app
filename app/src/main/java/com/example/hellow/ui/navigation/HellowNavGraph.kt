package com.example.hellow.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hellow.model.RickCharacter
import com.example.hellow.ui.screens.DetailScreen
import com.example.hellow.ui.screens.GreetingScreen
import com.example.hellow.ui.screens.MainScreen
import com.example.hellow.viewmodel.MainViewModel
import com.example.hellow.viewmodel.UserPreferenceState
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object GreetingRoute

@Serializable
data class DetailRoute(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String
)

@Composable
fun HellowNavHost(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    val userState by mainViewModel.userState.collectAsState()

    when (val state = userState) {
        is UserPreferenceState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UserPreferenceState.Ready -> {
            val navController = rememberNavController()
            val startDestination = if (!state.name.isNullOrBlank()) {
                GreetingRoute
            } else {
                HomeRoute
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
            ) {
                composable<HomeRoute> {
                    MainScreen(
                        onNextClicked = { name ->
                            mainViewModel.saveUserName(name) {
                                navController.navigate(GreetingRoute) {
                                    popUpTo(HomeRoute) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                composable<GreetingRoute> {
                    GreetingScreen(
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
    }
}
