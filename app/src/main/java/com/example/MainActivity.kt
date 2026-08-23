package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.studio.dashboard.ProjectDashboardScreen
import com.example.studio.story.StoryStudioScreen
import com.example.studio.character.CharacterStudioScreen
import com.example.studio.scene.SceneStudioScreen
import com.example.studio.image.ImageStudioScreen
import com.example.studio.voice.VoiceStudioScreen
import com.example.studio.timeline.TimelineScreen
import com.example.studio.export.ExportScreen
import com.example.ui.theme.HakayatTheme

import com.example.studio.generator.CreateStoryPromptScreen
import com.example.studio.generator.StoryReaderScreen

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HakayatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Mock Project Data
                    val mockProject = Project(
                        id = "proj_1",
                        title = "مدينة ما وراء الباب",
                        genre = "مغامرات / فانتازيا",
                        durationMinutes = 5,
                        progress = 0.78f,
                        coverImageUrl = null,
                        status = ProjectStatus(
                            storyReady = true,
                            charactersReady = true,
                            scenesReady = true,
                            imagesReady = true,
                            voiceReady = false,
                            musicReady = false,
                            subtitlesReady = false,
                            timelineReady = false
                        )
                    )

                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            ProjectDashboardScreen(
                                project = mockProject,
                                onBack = { /* Exit App */ },
                                onPreview = { /* TODO: Implement Preview Screen */ },
                                onStoryClick = { navController.navigate("story_generator") },
                                onCharactersClick = { navController.navigate("character_studio") },
                                onScenesClick = { navController.navigate("scene_studio") },
                                onImagesClick = { navController.navigate("image_studio") },
                                onVoiceClick = { navController.navigate("voice_studio") },
                                onTimelineClick = { navController.navigate("timeline_studio") },
                                onExportClick = { navController.navigate("export_studio") }
                            )
                        }
                        composable("story_generator") {
                            CreateStoryPromptScreen(
                                onBack = { navController.popBackStack() },
                                onStoryGenerated = { storyId ->
                                    navController.navigate("story_reader/$storyId")
                                },
                                onStoryClicked = { storyId ->
                                    navController.navigate("story_reader/$storyId")
                                }
                            )
                        }
                        composable("story_reader/{storyId}") { backStackEntry ->
                            val storyId = backStackEntry.arguments?.getString("storyId") ?: return@composable
                            StoryReaderScreen(
                                storyId = storyId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("story_studio") {
                            StoryStudioScreen(onBack = { navController.popBackStack() })
                        }
                        composable("character_studio") {
                            CharacterStudioScreen(onBack = { navController.popBackStack() })
                        }
                        composable("scene_studio") {
                            SceneStudioScreen(onBack = { navController.popBackStack() })
                        }
                        composable("image_studio") {
                            ImageStudioScreen(onBack = { navController.popBackStack() })
                        }
                        composable("voice_studio") {
                            VoiceStudioScreen(onBack = { navController.popBackStack() })
                        }
                        composable("timeline_studio") {
                            TimelineScreen(onBack = { navController.popBackStack() })
                        }
                        composable("export_studio") {
                            ExportScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
