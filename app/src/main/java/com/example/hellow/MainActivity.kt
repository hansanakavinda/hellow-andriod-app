package com.example.hellow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hellow.ui.theme.HellowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HellowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HellowApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HellowApp(modifier: Modifier = Modifier) {
    var submittedName by remember { mutableStateOf<String?>(null) }

    if (submittedName == null) {
        MainScreen(
            modifier = modifier,
            onNextClicked = { name ->
                submittedName = name
            }
        )
    } else {
        GreetingScreen(
            name = submittedName!!,
            modifier = modifier,
            onBack = {
                submittedName = null
            }
        )
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
        // App name header at the top
        Text(
            text = "Hellow",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Label above input field
        Text(
            text = "Enter your name",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Input field for user's name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Spacer pushes the Next button to the bottom of the screen
        Spacer(modifier = Modifier.weight(1f))

        // Next button at the bottom
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onNextClicked(name.trim())
                }
            },
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50), // Green background
                contentColor = Color.Black,          // Black text
                disabledContainerColor = Color(0xFFA5D6A7), // Light green when disabled
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
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Greeting with user's name
        Text(
            text = "Hellow $name",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle below greeting
        Text(
            text = "Welcome to the team",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        // Back button to return to the input screen
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HellowTheme {
        MainScreen(onNextClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    HellowTheme {
        GreetingScreen(name = "Hansana", onBack = {})
    }
}