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
        return _storyText.value.split(Regex("\\s+")).count { it.isNotBlank() }
    }

    fun getDurationString(): String {
        val words = getWordCount()
        val totalSeconds = (words * 60) / 130 // Approx 130 WPM
        val mins = totalSeconds / 60
        val secs = totalSeconds % 60
        return String.format("%d:%02d", mins, secs)
    }
}
