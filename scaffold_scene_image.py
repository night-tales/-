import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + '\n')

# 1. Domain Model for Scene
write_file('app/src/main/java/com/example/domain/model/SceneInfo.kt', """
package com.example.domain.model

data class SceneInfo(
    val id: String,
    val order: Int,
    val title: String,
    val description: String,
    val durationSeconds: Int,
    val charactersIds: List<String>,
    val cameraDirection: String,
    val action: String,
    val soundEnv: String,
    val dialog: String?,
    val imageUrl: String? = null
)
""")

# 2. Scene Studio ViewModel
write_file('app/src/main/java/com/example/studio/scene/SceneViewModel.kt', """
package com.example.studio.scene

import androidx.lifecycle.ViewModel
import com.example.domain.model.SceneInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SceneViewModel @Inject constructor() : ViewModel() {
    private val _scenes = MutableStateFlow<List<SceneInfo>>(
        listOf(
            SceneInfo(
                id = "s1",
                order = 1,
                title = "الباب الغامض",
                description = "آدم يلاحظ ضوءًا أزرق خلف خزانته.",
                durationSeconds = 32,
                charactersIds = listOf("1"),
                cameraDirection = "Slow Push In",
                action = "Adam approaches the door",
                soundEnv = "Wind + room ambience",
                dialog = "ما هذا؟"
            )
        )
    )
    val scenes = _scenes.asStateFlow()
}
""")

# 3. Scene Studio Screen
write_file('app/src/main/java/com/example/studio/scene/SceneStudioScreen.kt', """
package com.example.studio.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneStudioScreen(
    onBack: () -> Unit,
    viewModel: SceneViewModel = hiltViewModel()
) {
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Storyboard", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0B14))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(scenes) { scene ->
                SceneCard(scene)
            }
        }
    }
}

@Composable
fun SceneCard(scene: com.example.domain.model.SceneInfo) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("SCENE ${String.format("%02d", scene.order)}", color = Color(0xFF64D8FF), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B263B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFA6A6B3), modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Details
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(scene.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                SceneProperty("الوصف:", scene.description)
                SceneProperty("المدة:", "00:${scene.durationSeconds}")
                SceneProperty("الكاميرا:", scene.cameraDirection)
                SceneProperty("الحركة:", scene.action)
                SceneProperty("الصوت:", scene.soundEnv)
                if (scene.dialog != null) {
                    SceneProperty("الحوار:", "\\\"${scene.dialog}\\\"")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C))) {
                Text("تغيير الكاميرا", color = Color.White, fontSize = 12.sp)
            }
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23324C))) {
                Text("إعادة الصورة", color = Color.White, fontSize = 12.sp)
            }
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF))) {
                Text("✨ تحسين", color = Color(0xFF0B0B14), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SceneProperty(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color(0xFFA6A6B3), fontSize = 14.sp, modifier = Modifier.width(80.dp))
        Text(value, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}
""")

# 4. Image Studio ViewModel
write_file('app/src/main/java/com/example/studio/image/ImageViewModel.kt', """
package com.example.studio.image

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel() {
    private val _prompt = MutableStateFlow("")
    val prompt = _prompt.asStateFlow()

    private val _negativePrompt = MutableStateFlow("ugly, blurry, low quality")
    val negativePrompt = _negativePrompt.asStateFlow()

    private val _style = MutableStateFlow("Cinematic Fantasy")
    val style = _style.asStateFlow()
    
    private val _aspectRatio = MutableStateFlow("16:9")
    val aspectRatio = _aspectRatio.asStateFlow()

    fun updatePrompt(value: String) { _prompt.value = value }
    fun updateNegativePrompt(value: String) { _negativePrompt.value = value }
    fun updateStyle(value: String) { _style.value = value }
    fun updateAspectRatio(value: String) { _aspectRatio.value = value }
}
""")

# 5. Image Studio Screen
write_file('app/src/main/java/com/example/studio/image/ImageStudioScreen.kt', """
package com.example.studio.image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStudioScreen(
    onBack: () -> Unit,
    viewModel: ImageViewModel = hiltViewModel()
) {
    val prompt by viewModel.prompt.collectAsStateWithLifecycle()
    val negativePrompt by viewModel.negativePrompt.collectAsStateWithLifecycle()
    val style by viewModel.style.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Image Generation", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .verticalScroll(rememberScrollState())
        ) {
            Text("Prompt", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = viewModel::updatePrompt,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = getStudioTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Negative Prompt", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = negativePrompt,
                onValueChange = viewModel::updateNegativePrompt,
                modifier = Modifier.fillMaxWidth(),
                colors = getStudioTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Style", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = style,
                        onValueChange = viewModel::updateStyle,
                        colors = getStudioTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Aspect Ratio", color = Color(0xFFA6A6B3), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = aspectRatio,
                        onValueChange = viewModel::updateAspectRatio,
                        colors = getStudioTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Trigger generation */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64D8FF), contentColor = Color(0xFF0B0B14)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Generate Image", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun getStudioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color(0xFF1B263B),
    unfocusedContainerColor = Color(0xFF1B263B),
    focusedBorderColor = Color(0xFF64D8FF),
    unfocusedBorderColor = Color.Transparent
)
""")
