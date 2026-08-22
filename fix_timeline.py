import os

with open('app/src/main/java/com/example/studio/timeline/TimelineScreen.kt', 'r') as f:
    content = f.read()

# Fix the RowScope issue by wrapping the TimelineSegment in a Row
content = content.replace('TimelineSegment("", 1f, Color(0xFF9013FE))', 'Row(modifier = Modifier.fillMaxWidth()) { TimelineSegment("", 1f, Color(0xFF9013FE)) }')
content = content.replace('TimelineSegment("", 1f, Color.White.copy(alpha = 0.5f))', 'Row(modifier = Modifier.fillMaxWidth()) { TimelineSegment("", 1f, Color.White.copy(alpha = 0.5f)) }')

with open('app/src/main/java/com/example/studio/timeline/TimelineScreen.kt', 'w') as f:
    f.write(content)

