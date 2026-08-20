package com.example.ui.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen(
    onBack: () -> Unit,
    onGenerate: (String, String, String) -> Unit
) {
    var idea by remember { mutableStateOf("") }
    var expandedGenre by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("قصة خرافية (Fairy Tale)") }
    val genres = listOf("قصة خرافية (Fairy Tale)", "خيال علمي (Sci-Fi)", "إثارة وتشويق (Thriller)", "مغامرة (Adventure)")

    var expandedVoice by remember { mutableStateOf(false) }
    var selectedVoice by remember { mutableStateOf("راوي كلاسيكي") }
    val voices = listOf("راوي كلاسيكي", "صوت طفولي", "صوت عميق")

    var expandedStyle by remember { mutableStateOf(false) }
    var selectedStyle by remember { mutableStateOf("لوحة زيتية (Oil Painting)") }
    val styles = listOf("لوحة زيتية (Oil Painting)", "رسوم متحركة ثلاثية الأبعاد (3D Animation)", "قصاصات ورقية (Paper Cutout)", "أنمي (Anime)", "واقعي (Realistic)")

    val creativeSuggestions = when(selectedGenre) {
        "قصة خرافية (Fairy Tale)" -> listOf("يكتشف خريطة سحرية تظهر فقط في ضوء القمر", "حيوان ناطق يطلب المساعدة", "تعويذة تحول القرية إلى زجاج")
        "خيال علمي (Sci-Fi)" -> listOf("رسالة غامضة من الفضاء الخارجي", "روبوت يكتشف المشاعر", "بوابة زمنية تعيد البطل ليوم الأمس")
        "إثارة وتشويق (Thriller)" -> listOf("رسالة تحذيرية من شخص مجهول", "اختفاء غامض في قطار منتصف الليل", "باب مغلق لا يمكن فتحه من الداخل")
        "مغامرة (Adventure)" -> listOf("خريطة كنز قديمة ممزقة إلى نصفين", "عاصفة تجبرهم على الاحتماء بكهف مجهول", "سباق مع الزمن للوصول إلى المعبد")
        else -> emptyList()
    }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("إنشاء قصة جديدة", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            Text("فكرة القصة", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = idea,
                onValueChange = { idea = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("مثال: طفل يجد بوابة سحرية في غرفته...", color = Color(0xFFA6A6B3)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF121222),
                    unfocusedContainerColor = Color(0xFF121222),
                    focusedBorderColor = Color(0xFF64D8FF),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (creativeSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("محفز الإبداع ✨", color = Color(0xFF64D8FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(creativeSuggestions) { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF233B5E),
                            modifier = Modifier.clickable {
                                idea = if (idea.isBlank()) suggestion else "$idea، $suggestion"
                            }
                        ) {
                            Text(
                                text = suggestion,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("نوع القصة (Genre)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = selectedGenre,
                onValueChange = { },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth().clickable { expandedGenre = true },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledContainerColor = Color(0xFF121222),
                    disabledBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            DropdownMenu(
                expanded = expandedGenre,
                onDismissRequest = { expandedGenre = false }
            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(genre) },
                        onClick = {
                            selectedGenre = genre
                            expandedGenre = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("صوت الراوي (Voice Profile)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = selectedVoice,
                onValueChange = { },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth().clickable { expandedVoice = true },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledContainerColor = Color(0xFF121222),
                    disabledBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            DropdownMenu(
                expanded = expandedVoice,
                onDismissRequest = { expandedVoice = false }
            ) {
                voices.forEach { voice ->
                    DropdownMenuItem(
                        text = { Text(voice) },
                        onClick = {
                            selectedVoice = voice
                            expandedVoice = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("النمط البصري (Visual Style)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(styles) { style ->
                    val isSelected = style == selectedStyle
                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .height(120.dp)
                            .clickable { selectedStyle = style },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFF9C74F).copy(alpha = 0.2f) else Color(0xFF1B263B)
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF9C74F)) else null
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .background(Color(0xFF23324C)),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Image,
                                    contentDescription = "Style Thumbnail",
                                    tint = if (isSelected) Color(0xFFF9C74F) else Color(0xFFA6A6B3),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(4.dp),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text(
                                    text = style.split(" (").first(),
                                    color = if (isSelected) Color(0xFFF9C74F) else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (idea.isNotBlank()) {
                        onGenerate(idea, selectedGenre, selectedVoice)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = idea.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF64D8FF),
                    disabledContainerColor = Color(0xFF3C4A62),
                    contentColor = Color(0xFF0B0B14),
                    disabledContentColor = Color(0xFFA6A6B3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("توليد القصة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
