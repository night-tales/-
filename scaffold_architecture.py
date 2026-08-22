import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + '\n')

write_file('app/src/main/java/com/example/domain/model/ProjectModels.kt', """
package com.example.domain.model

data class Project(
    val id: String,
    val title: String,
    val genre: String,
    val durationMinutes: Int,
    val progress: Float,
    val coverImageUrl: String?,
    val status: ProjectStatus
)

data class ProjectStatus(
    val storyReady: Boolean = false,
    val charactersReady: Boolean = false,
    val scenesReady: Boolean = false,
    val imagesReady: Boolean = false,
    val voiceReady: Boolean = false,
    val musicReady: Boolean = false,
    val subtitlesReady: Boolean = false,
    val timelineReady: Boolean = false
)
""")

write_file('app/src/main/java/com/example/engine/GenerationEngine.kt', """
package com.example.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class GenerationJob {
    data class StoryJob(val prompt: String) : GenerationJob()
    data class CharacterJob(val projectId: String) : GenerationJob()
    data class SceneJob(val projectId: String) : GenerationJob()
    data class ImageJob(val sceneId: String) : GenerationJob()
    data class VoiceJob(val text: String) : GenerationJob()
    data class RenderJob(val projectId: String) : GenerationJob()
}

enum class JobStatus { PENDING, RUNNING, SUCCESS, ERROR }

data class JobState(val job: GenerationJob, val status: JobStatus, val progress: Float = 0f)

@Singleton
class GenerationEngine @Inject constructor() {
    private val _jobQueue = MutableStateFlow<List<JobState>>(emptyList())
    val jobQueue: StateFlow<List<JobState>> = _jobQueue.asStateFlow()

    fun submitJob(job: GenerationJob) {
        val currentState = JobState(job, JobStatus.PENDING)
        _jobQueue.value = _jobQueue.value + currentState
        // Job processing logic will be implemented here
    }
}
""")

write_file('app/src/main/java/com/example/studio/dashboard/ProjectDashboardScreen.kt', """
package com.example.studio.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDashboardScreen(
    project: Project,
    onBack: () -> Unit,
    onPreview: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(project.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${project.genre} • ${project.durationMinutes} دقائق", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B263B)),
                contentAlignment = Alignment.Center
            ) {
                Text("COVER IMAGE", color = Color(0xFFA6A6B3), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { project.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF64D8FF),
                    trackColor = Color(0xFF23324C)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("${(project.progress * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(if (project.progress >= 1f) "المشروع جاهز للمعاينة" else "جاري العمل على المشروع...", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onPreview,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF), contentColor = Color(0xFF0B0B14)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تشغيل الإنتاج الكامل", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            StatusItem("القصة", project.status.storyReady, "مكتمل")
            StatusItem("الشخصيات", project.status.charactersReady, "مكتمل")
            StatusItem("المشاهد", project.status.scenesReady, "مكتمل")
            StatusItem("الصور", project.status.imagesReady, "مكتمل")
            StatusItem("الصوت", project.status.voiceReady, "يعمل...", isWorking = true)
            StatusItem("الموسيقى", project.status.musicReady, "")
            StatusItem("الترجمة", project.status.subtitlesReady, "")
            StatusItem("Timeline", project.status.timelineReady, "")
        }
    }
}

@Composable
fun StatusItem(title: String, isReady: Boolean, statusText: String, isWorking: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isReady) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF64D8FF), modifier = Modifier.size(20.dp))
        } else if (isWorking) {
            Icon(Icons.Outlined.PlayCircle, contentDescription = null, tint = Color(0xFFF9C74F), modifier = Modifier.size(20.dp))
        } else {
            Icon(Icons.Outlined.Circle, contentDescription = null, tint = Color(0xFFA6A6B3), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = if (isReady || isWorking) Color.White else Color(0xFFA6A6B3), fontSize = 16.sp, modifier = Modifier.weight(1f))
        if (statusText.isNotEmpty()) {
            Text(statusText, color = if (isReady) Color(0xFF64D8FF) else if (isWorking) Color(0xFFF9C74F) else Color(0xFFA6A6B3), fontSize = 14.sp)
        }
    }
}
""")

import re
with open('app/build.gradle.kts', 'r') as f:
    build_gradle = f.read()

if 'media3' not in build_gradle:
    media3_deps = """
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-common:1.3.1")
"""
    build_gradle = build_gradle.replace('dependencies {', 'dependencies {' + media3_deps)
    with open('app/build.gradle.kts', 'w') as f:
        f.write(build_gradle)

