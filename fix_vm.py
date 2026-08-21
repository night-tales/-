import re

with open('app/src/main/java/com/example/ui/editor/SceneEditorViewModel.kt', 'r') as f:
    content = f.read()

replacement = """    fun updateSceneImagePrompt(sceneId: String, newPrompt: String) {
        val updatedScenes = _scenes.value.map { 
            if (it.id == sceneId) it.copy(imagePrompt = newPrompt) else it 
        }
        _scenes.value = updatedScenes
        autoSave(updatedScenes)
    }

    fun generateImageForScene(sceneId: String, prompt: String) {
        viewModelScope.launch {
            try {
                val url = aiDirector.generateImageForScene(prompt)
                val updatedScenes = _scenes.value.map {
                    if (it.id == sceneId) it.copy(imageUrl = url) else it
                }
                _scenes.value = updatedScenes
                autoSave(updatedScenes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }"""

start_str = "    fun updateSceneImagePrompt(sceneId: String, newPrompt: String) {"
end_str = "        autoSave(updatedScenes)\n    }"

start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx) + len(end_str)

if start_idx != -1 and end_idx != -1:
    new_content = content[:start_idx] + replacement + content[end_idx:]
    with open('app/src/main/java/com/example/ui/editor/SceneEditorViewModel.kt', 'w') as f:
        f.write(new_content)
