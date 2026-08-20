package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Project(
    val id: String,
    val title: String,
    val genre: String,
    val isCompleted: Boolean
)

val mockProjects = listOf(
    Project("1", "حارسة بوابة الزمن", "خيال علمي", true),
    Project("2", "المدينة تحت البحر", "مغامرة", false),
    Project("3", "شبح الغابة القديمة", "رعب خفيف", true),
    Project("4", "رحلة إلى المريخ", "خيال علمي", false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onProjectClick: (Project) -> Unit,
    onLibraryClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        topBar = {
            TopAppBar(
                title = { Text("Night Tales Studio", color = Color(0xFFF9C74F), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1B263B),
                contentColor = Color(0xFFFFFFEF)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Home") },
                    label = { Text("الرئيسية") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0D1B2A),
                        selectedTextColor = Color(0xFFF9C74F),
                        indicatorColor = Color(0xFFF9C74F),
                        unselectedIconColor = Color(0xFFFFFFEF),
                        unselectedTextColor = Color(0xFFFFFFEF)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLibraryClick,
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Library") },
                    label = { Text("المكتبة") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0D1B2A),
                        selectedTextColor = Color(0xFFF9C74F),
                        indicatorColor = Color(0xFFF9C74F),
                        unselectedIconColor = Color(0xFFFFFFEF),
                        unselectedTextColor = Color(0xFFFFFFEF)
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = Color(0xFFF9C74F),
                contentColor = Color(0xFF0D1B2A)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Story")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "مشاريعي",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(mockProjects) { project ->
                    ProjectCard(project = project, onClick = { onProjectClick(project) })
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121222))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Thumbnail Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFF1A1A2E), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (project.isCompleted) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x800B0B14),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    Text("Draft", color = Color(0xFFA6A6B3), fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = project.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = project.genre,
                color = Color(0xFF64D8FF),
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF233B5E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (project.isCompleted) "تصدير" else "متابعة",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
