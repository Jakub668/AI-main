package com.example.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.ai.ui.theme.AiAppTheme


data class ChatMessage(val user: String, val ai: String)

class MainActivity : ComponentActivity() {
    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            // TODO: handle selected file (send contents to AI, etc.)
        }
    }

    private fun saveHistory(history: List<ChatMessage>) {
        val prefs = getSharedPreferences("ai_prefs", MODE_PRIVATE)
        val json = com.google.gson.Gson().toJson(history)
        prefs.edit().putString("conversation", json).apply()
    }

    private fun loadHistory(): List<ChatMessage> {
        val prefs = getSharedPreferences("ai_prefs", MODE_PRIVATE)
        val json = prefs.getString("conversation", null) ?: return emptyList()
        return try {
            com.google.gson.Gson().fromJson(json, Array<ChatMessage>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiAppTheme {
                // state: conversation history stored as pairs
                val history = remember { mutableStateListOf<ChatMessage>() }
                // load persisted history once
                LaunchedEffect(Unit) {
                    history.addAll(loadHistory())
                }

                val prompt = remember { mutableStateOf("") }
                val response = remember { mutableStateOf("") }
                val languageHint = remember { mutableStateOf("") }
                val generatedImage = remember { mutableStateOf<android.graphics.Bitmap?>(null) }

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // conversation view
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(history) { msg ->
                            Text(text = "You: ${msg.user}")
                            Text(text = "AI: ${msg.ai}", modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    // top row: open file and settings
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = {
                            openDocumentLauncher.launch(arrayOf("*/*"))
                        }) {
                            Text(stringResource(id = R.string.open_file))
                        }
                        Button(onClick = {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }) {
                            Text(stringResource(id = R.string.settings))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = stringResource(id = R.string.language_label))
                    BasicTextField(
                        value = languageHint.value,
                        onValueChange = { languageHint.value = it },
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(text = "(type e.g. 'Python', 'Polski', or any other language or format')")

                    BasicTextField(
                        value = prompt.value,
                        onValueChange = { prompt.value = it },
                        modifier = Modifier.padding(8.dp)
                    )
                    Button(onClick = {
                        // TODO: call inference engine with prompt.value and languageHint.value
                        val aiResp = "(not implemented yet)"
                        response.value = aiResp
                        history.add(ChatMessage(prompt.value, aiResp))
                        saveHistory(history)
                    }) {
                        Text(stringResource(id = R.string.send_button))
                    }

                    Text(text = response.value, modifier = Modifier.padding(8.dp))

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        // TODO: generate an image via model and set generatedImage
                    }) {
                        Text(stringResource(id = R.string.generate_image))
                    }
                    generatedImage.value?.let { img ->
                        Image(bitmap = img.asImageBitmap(), contentDescription = null)
                    }
                }
            }
        }
    }
}
