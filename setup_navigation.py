import os

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Replace the navigation logic inside MainActivity to use the new dashboard and studios
new_nav = """
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
import com.example.ui.theme.NightTalesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NightTalesTheme {
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
                                onPreview = { navController.navigate("preview") },
                                onStoryClick = { navController.navigate("story_studio") },
                                onCharactersClick = { navController.navigate("character_studio") },
                                onScenesClick = { navController.navigate("scene_studio") },
                                onImagesClick = { navController.navigate("image_studio") },
                                onVoiceClick = { navController.navigate("voice_studio") },
                                onTimelineClick = { navController.navigate("timeline_studio") },
                                onExportClick = { navController.navigate("export_studio") }
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
"""

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(new_nav.strip() + '\n')

# Add missing click parameters to ProjectDashboardScreen
with open('app/src/main/java/com/example/studio/dashboard/ProjectDashboardScreen.kt', 'r') as f:
    dash_content = f.read()

dash_content = dash_content.replace(
    'onPreview: () -> Unit',
    'onPreview: () -> Unit,\n    onStoryClick: () -> Unit,\n    onCharactersClick: () -> Unit,\n    onScenesClick: () -> Unit,\n    onImagesClick: () -> Unit,\n    onVoiceClick: () -> Unit,\n    onTimelineClick: () -> Unit,\n    onExportClick: () -> Unit'
)

dash_content = dash_content.replace(
    'StatusItem("القصة", project.status.storyReady, "مكتمل")',
    'StatusItem("القصة", project.status.storyReady, "مكتمل", onClick = onStoryClick)'
)
dash_content = dash_content.replace(
    'StatusItem("الشخصيات", project.status.charactersReady, "مكتمل")',
    'StatusItem("الشخصيات", project.status.charactersReady, "مكتمل", onClick = onCharactersClick)'
)
dash_content = dash_content.replace(
    'StatusItem("المشاهد", project.status.scenesReady, "مكتمل")',
    'StatusItem("المشاهد", project.status.scenesReady, "مكتمل", onClick = onScenesClick)'
)
dash_content = dash_content.replace(
    'StatusItem("الصور", project.status.imagesReady, "مكتمل")',
    'StatusItem("الصور", project.status.imagesReady, "مكتمل", onClick = onImagesClick)'
)
dash_content = dash_content.replace(
    'StatusItem("الصوت", project.status.voiceReady, "يعمل...", isWorking = true)',
    'StatusItem("الصوت", project.status.voiceReady, "يعمل...", isWorking = true, onClick = onVoiceClick)'
)
dash_content = dash_content.replace(
    'StatusItem("Timeline", project.status.timelineReady, "")',
    'StatusItem("Timeline", project.status.timelineReady, "", onClick = onTimelineClick)'
)

dash_content = dash_content.replace(
    'fun StatusItem(title: String, isReady: Boolean, statusText: String, isWorking: Boolean = false)',
    'fun StatusItem(title: String, isReady: Boolean, statusText: String, isWorking: Boolean = false, onClick: () -> Unit = {})'
)

dash_content = dash_content.replace(
    'Row(\n        modifier = Modifier\n            .fillMaxWidth()\n            .padding(vertical = 8.dp)',
    'import androidx.compose.foundation.clickable\n\nRow(\n        modifier = Modifier\n            .fillMaxWidth()\n            .clickable(onClick = onClick)\n            .padding(vertical = 8.dp)'
)

with open('app/src/main/java/com/example/studio/dashboard/ProjectDashboardScreen.kt', 'w') as f:
    f.write(dash_content)

