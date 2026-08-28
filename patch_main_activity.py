with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'onPreview = { /* TODO: Implement Preview Screen */ },',
    'onPreview = { navController.navigate("timeline_studio") },'
)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
