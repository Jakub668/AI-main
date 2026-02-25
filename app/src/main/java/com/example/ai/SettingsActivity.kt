package com.example.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai.ui.theme.AiAppTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiAppTheme {
                val username = remember { mutableStateOf("") }
                val enableDarkMode = remember { mutableStateOf(false) }

                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(text = "Personalization & Settings", style = MaterialTheme.typography.titleLarge)
                    // placeholder switches/fields
                    Text(text = "Username (for display): $${username.value}")
                    Text(text = "Dark mode: $${enableDarkMode.value}")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Enabled features:")
                    FeatureRegistry.all().forEach { feat ->
                        Text(text = "• ${feat.name}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // button to open data directory for mods
                    Button(onClick = {
                        // this will be wired up via activity result launcher defined above
                    }) {
                        Text("Open AI files")
                    }

                    Button(onClick = {
                        // save settings to shared preferences
                    }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
