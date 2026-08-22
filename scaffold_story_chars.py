import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + '\n')

# 1. Story Studio ViewModel
write_file('app/src/main/java/com/example/studio/story/StoryViewModel.kt', """
package com.example.studio.story

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor() : ViewModel() {
    private val _title = MutableStateFlow("مدينة ما وراء الباب")
    val title = _title.asStateFlow()

    private val _storyText = MutableStateFlow("كان آدم يجلس في غرفته عندما لاحظ ضوءًا غريبًا يتسرب من أسفل خزانته. اقترب بحذر، وبمجرد أن فتح الباب، وجد نفسه أمام بوابة متلألئة تقوده إلى عالم آخر تمامًا، عالم تحلق فيه المدن فوق الغيوم.")
    val storyText = _storyText.asStateFlow()

    fun updateTitle(newTitle: String) { _title.value = newTitle }
    fun updateStory(newStory: String) { _storyText.value = newStory }

    fun getWordCount(): Int {
        return _storyText.value.split(Regex("\\\\s+")).count { it.isNotBlank() }
    }

    fun getDurationString(): String {
        val words = getWordCount()
        val totalSeconds = (words * 60) / 130 // Approx 130 WPM
        val mins = totalSeconds / 60
        val secs = totalSeconds % 60
        return String.format("%d:%02d", mins, secs)
    }
}
""")

# 2. Story Studio Screen
write_file('app/src/main/java/com/example/studio/story/StoryStudioScreen.kt', """
package com.example.studio.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryStudioScreen(
    onBack: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val storyText by viewModel.storyText.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("Story Studio ✨ AI", color = Color.White, fontWeight = FontWeight.Bold) },
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
        ) {
            Text("العنوان", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::updateTitle,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B263B),
                    unfocusedContainerColor = Color(0xFF1B263B),
                    focusedBorderColor = Color(0xFF64D8FF),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("القصة", color = Color(0xFFA6A6B3), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = storyText,
                onValueChange = viewModel::updateStory,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B263B),
                    unfocusedContainerColor = Color(0xFF1B263B),
                    focusedBorderColor = Color(0xFF64D8FF),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("الكلمات: ${viewModel.getWordCount()}", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                Text("المدة التقريبية: ${viewModel.getDurationString()}", color = Color(0xFFA6A6B3), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiActionButton("تحسين", Modifier.weight(1f))
                AiActionButton("اختصار", Modifier.weight(1f))
                AiActionButton("توسيع", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiActionButton("إعادة كتابة", Modifier.weight(1f))
                AiActionButton("✨ AI", Modifier.weight(1f), isPrimary = true)
            }
        }
    }
}

@Composable
fun AiActionButton(text: String, modifier: Modifier = Modifier, isPrimary: Boolean = false) {
    Button(
        onClick = { /* TODO: Trigger AI Action on selected text */ },
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color(0xFF64D8FF) else Color(0xFF23324C),
            contentColor = if (isPrimary) Color(0xFF0B0B14) else Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
""")

# 3. Domain Model for Character
write_file('app/src/main/java/com/example/domain/model/CharacterInfo.kt', """
package com.example.domain.model

data class CharacterInfo(
    val id: String,
    val name: String,
    val role: String,
    val age: Int,
    val description: String,
    val imageUrl: String? = null
)
""")

# 4. Character Studio ViewModel
write_file('app/src/main/java/com/example/studio/character/CharacterViewModel.kt', """
package com.example.studio.character

import androidx.lifecycle.ViewModel
import com.example.domain.model.CharacterInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor() : ViewModel() {
    private val _characters = MutableStateFlow<List<CharacterInfo>>(
        listOf(
            CharacterInfo("1", "آدم", "البطل", 11, "فضولي وشجاع، يرتدي سترة حمراء ويحمل دائماً حقيبة ظهر صغيرة."),
            CharacterInfo("2", "لينا", "شخصية مساعدة", 12, "ذكية وهادئة، لديها نظارات دائرية وتحب قراءة الكتب القديمة.")
        )
    )
    val characters = _characters.asStateFlow()

    fun addCharacter() {
        // Placeholder for adding new
    }
}
""")

# 5. Character Studio Screen
write_file('app/src/main/java/com/example/studio/character/CharacterStudioScreen.kt', """
package com.example.studio.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
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
fun CharacterStudioScreen(
    onBack: () -> Unit,
    viewModel: CharacterViewModel = hiltViewModel()
) {
    val characters by viewModel.characters.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("الشخصيات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::addCharacter) {
                        Icon(Icons.Default.Add, contentDescription = "Add Character", tint = Color(0xFF64D8FF))
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(characters) { char ->
                CharacterCard(char)
            }
        }
    }
}

@Composable
fun CharacterCard(char: com.example.domain.model.CharacterInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF23324C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFA6A6B3), modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(char.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(char.role, color = Color(0xFF64D8FF), fontSize = 12.sp)
                }
                Text("${char.age} سنة", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(char.description, color = Color(0xFFE2E2E2), fontSize = 14.sp, maxLines = 2)
            }
        }
    }
}
""")
