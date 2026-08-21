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
                // Projects remain in the editor until the real generation pipeline
                // persists a playable video URL. Demo media must never be shown.
                navController.navigate("editor/${project.id}")
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
              onEditBlueprint = {
                val pid = viewModel.createdProjectId.value
                if (pid != null) {
                  navController.navigate("editor/$pid")
                }
              },
              onStop = { viewModel.stopGeneration() },
              onRetry = { viewModel.retry() },
              onNewChat = { viewModel.newChat() },
              onBack = { navController.popBackStack() }
            )
          }

          composable(
            route = "editor/{projectId}",
            arguments = listOf(
              androidx.navigation.navArgument("projectId") {
                type = androidx.navigation.NavType.StringType
              }
            )
          ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            com.example.ui.editor.SceneEditorScreen(
              projectId = projectId,
              onBack = { navController.popBackStack() }
            )
          }
        }
      }
    }
  }
}
