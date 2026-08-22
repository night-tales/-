package com.example.studio.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
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
    onPreview: () -> Unit,
    onStoryClick: () -> Unit,
    onCharactersClick: () -> Unit,
    onScenesClick: () -> Unit,
    onImagesClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onTimelineClick: () -> Unit,
    onExportClick: () -> Unit
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
            
            StatusItem("القصة", project.status.storyReady, "مكتمل", onClick = onStoryClick)
            StatusItem("الشخصيات", project.status.charactersReady, "مكتمل", onClick = onCharactersClick)
            StatusItem("المشاهد", project.status.scenesReady, "مكتمل", onClick = onScenesClick)
            StatusItem("الصور", project.status.imagesReady, "مكتمل", onClick = onImagesClick)
            StatusItem("الصوت", project.status.voiceReady, "يعمل...", isWorking = true, onClick = onVoiceClick)
            StatusItem("الموسيقى", project.status.musicReady, "")
            StatusItem("الترجمة", project.status.subtitlesReady, "")
            StatusItem("Timeline", project.status.timelineReady, "", onClick = onTimelineClick)
        }
    }
}

@Composable
fun StatusItem(title: String, isReady: Boolean, statusText: String, isWorking: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
