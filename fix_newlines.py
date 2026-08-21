import re

with open('app/src/main/java/com/example/ui/editor/SceneEditorScreen.kt', 'r') as f:
    content = f.read()

# Replace the whole IconButton block for sharing
start_str = "                        IconButton(onClick = {\n                            val fullStory = scenes.joinToString(\""
end_str = "                            Icon(Icons.Default.Share, contentDescription = \"Share\", tint = Color.White)\n                        }"

start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx) + len(end_str)

if start_idx != -1 and end_idx != -1:
    replacement = """                        IconButton(onClick = {
                            val fullStory = scenes.joinToString("\\n\\n") { "## ${it.title}\\n\\n${it.narration}" }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, fullStory)
                            }
                            context.startActivity(Intent.createChooser(intent, "مشاركة القصة"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }"""
    new_content = content[:start_idx] + replacement + content[end_idx:]
    with open('app/src/main/java/com/example/ui/editor/SceneEditorScreen.kt', 'w') as f:
        f.write(new_content)
