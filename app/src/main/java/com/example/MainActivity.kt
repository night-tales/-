package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.chat.ChatViewModel
import com.example.ui.chat.HakayatChatScreen
import com.example.ui.create.CreateStoryScreen
import com.example.ui.home.HomeScreen
import com.example.ui.player.StoryPlayerScreen
import com.example.ui.theme.HakayatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HakayatTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
          composable("home") {
            HomeScreen(
              onCreateClick = { navController.navigate("create") },
              onProjectClick = { project ->
                if (project.status == "COMPLETED") {
                  // Pass a dummy video URI for preview purposes, or handle it via a viewmodel
                  navController.navigate("player")
                } else {
                  navController.navigate("chat")
                }
              },
              onLibraryClick = { navController.navigate("library") }
            )
          }

          composable("library") {
            com.example.ui.library.LibraryScreen(
              onBack = { navController.popBackStack() }
            )
          }

          composable("create") {
            val viewModel: ChatViewModel = hiltViewModel()
            CreateStoryScreen(
              onBack = { navController.popBackStack() },
              onGenerate = { idea, genre, voice ->
                viewModel.newChat()
                viewModel.sendMessage("أريد قصة عن $idea من نوع $genre بصوت $voice")
                navController.navigate("chat") {
                  popUpTo("home")
                }
              }
            )
          }

          composable("chat") {
            val viewModel: ChatViewModel = hiltViewModel()
            val messages by viewModel.messages.collectAsStateWithLifecycle()
            val blueprint by viewModel.blueprint.collectAsStateWithLifecycle()
            val characterReference by viewModel.characterReference.collectAsStateWithLifecycle()
            val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
            val generationProgress by viewModel.progress.collectAsStateWithLifecycle()

            HakayatChatScreen(
              messages = messages,
              blueprint = blueprint,
              characterReference = characterReference,
              isGenerating = isGenerating,
              generationProgress = generationProgress,
              onSend = { viewModel.sendMessage(it) },
              onGenerate = { viewModel.startGeneration() },
              onEditBlueprint = { navController.navigate("editor") },
              onStop = { viewModel.stopGeneration() },
              onRetry = { viewModel.retry() },
              onNewChat = { viewModel.newChat() },
              onBack = { navController.popBackStack() }
            )
          }

          composable("editor") {
            com.example.ui.editor.SceneEditorScreen(
              onBack = { navController.popBackStack() }
            )
          }

          composable("player") {
            StoryPlayerScreen(
              videoUri = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
              onBack = { navController.popBackStack() }
            )
          }
        }
      }
    }
  }
}
