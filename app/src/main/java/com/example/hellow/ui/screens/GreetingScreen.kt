package com.example.hellow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hellow.model.Result
import com.example.hellow.model.RickCharacter
import com.example.hellow.viewmodel.CharacterViewModel
import com.example.hellow.viewmodel.MainViewModel

@Composable
fun GreetingScreen(
    onCharacterClick: (RickCharacter) -> Unit,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    characterViewModel: CharacterViewModel = viewModel()
) {
    val currentUserName by mainViewModel.userName.collectAsState()
    val searchQuery by characterViewModel.searchQuery.collectAsState()
    val uiState by characterViewModel.uiState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var newNameInput by remember { mutableStateOf(currentUserName ?: "") }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Your Name") },
            text = {
                OutlinedTextField(
                    value = newNameInput,
                    onValueChange = { newNameInput = it },
                    singleLine = true,
                    placeholder = { Text("Name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newNameInput.isNotBlank()) {
                            mainViewModel.saveUserName(newNameInput.trim())
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hellow ${currentUserName ?: ""}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = {
                    newNameInput = currentUserName ?: ""
                    showEditDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Edit Profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Search Rick & Morty Characters (Tap for details):",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { characterViewModel.onSearchQueryChanged(it) },
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
    }
}
