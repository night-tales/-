package com.example.studio.generator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryPromptScreen(
    onBack: () -> Unit,
    onStoryGenerated: (String) -> Unit,
    onStoryClicked: (String) -> Unit,
    viewModel: GeneratedStoryViewModel = hiltViewModel()
) {
    var prompt by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val categories = listOf("مغامرة", "خيال علمي", "خيال", "رعب", "رومانسية", "غموض", "عام")
    var selectedCategory by remember { mutableStateOf(categories.last()) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generatedStoryId by viewModel.generatedStoryId.collectAsStateWithLifecycle()
    
    val allStories by viewModel.stories.collectAsStateWithLifecycle()
    val favoriteStories by viewModel.favoriteStories.collectAsStateWithLifecycle()
    
    val displayedStories = if (showFavoritesOnly) favoriteStories else allStories

    LaunchedEffect(generatedStoryId) {
        generatedStoryId?.let {
            onStoryGenerated(it)
            viewModel.resetNavigation()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0B0B14),
        topBar = {
            TopAppBar(
                title = { Text("AI Story Generator", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Story Title (Optional)", color = Color.Gray) },
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("What is the story about?", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B263B),
                    unfocusedContainerColor = Color(0xFF1B263B),
                    focusedBorderColor = Color(0xFF64D8FF),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = (selectedCategory == cat),
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1B263B),
                            labelColor = Color.White,
                            selectedContainerColor = Color(0xFF64D8FF),
                            selectedLabelColor = Color(0xFF0B0B14)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.generateStory(prompt, title, selectedCategory) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = prompt.isNotBlank() && !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF64D8FF),
                    contentColor = Color(0xFF0B0B14)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color(0xFF0B0B14), modifier = Modifier.size(24.dp))
                } else {
                    Text("Generate Story ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Past Stories",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Favorites Only", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = showFavoritesOnly,
                        onCheckedChange = { showFavoritesOnly = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0B0B14),
                            checkedTrackColor = Color(0xFF64D8FF)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(displayedStories) { story ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStoryClicked(story.id) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = story.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFF23324C),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = story.category,
                                                color = Color(0xFF64D8FF),
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        
                                        val wordCount = story.content.split("\\s+".toRegex()).count { it.isNotBlank() }
                                        val readTime = maxOf(1, wordCount / 200)
                                        Text(
                                            text = "⏱️ $readTime min read",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                
                                IconButton(
                                    onClick = { viewModel.toggleFavorite(story) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (story.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (story.isFavorite) Color.Red else Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = story.content,
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
