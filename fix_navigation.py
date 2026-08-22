import os

# 1. Fix MainActivity Theme Import
with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.ui.theme.NightTalesTheme', 'import com.example.ui.theme.HakayatTheme')
content = content.replace('NightTalesTheme {', 'HakayatTheme {')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)


# 2. Fix ProjectDashboardScreen Syntax Error
with open('app/src/main/java/com/example/studio/dashboard/ProjectDashboardScreen.kt', 'r') as f:
    dash = f.read()

dash = dash.replace('import androidx.compose.foundation.clickable\n\nRow(', 'Row(')

# Add import at the top
if 'import androidx.compose.foundation.clickable' not in dash:
    dash = dash.replace('import androidx.compose.foundation.layout.*', 'import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.clickable')

with open('app/src/main/java/com/example/studio/dashboard/ProjectDashboardScreen.kt', 'w') as f:
    f.write(dash)

