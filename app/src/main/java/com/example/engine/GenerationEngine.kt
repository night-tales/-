package com.example.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class GenerationJob {
    data class StoryJob(val prompt: String) : GenerationJob()
    data class CharacterJob(val projectId: String) : GenerationJob()
    data class SceneJob(val projectId: String) : GenerationJob()
    data class ImageJob(val sceneId: String) : GenerationJob()
    data class VoiceJob(val text: String) : GenerationJob()
    data class RenderJob(val projectId: String) : GenerationJob()
}

enum class JobStatus { PENDING, RUNNING, SUCCESS, ERROR }

data class JobState(val job: GenerationJob, val status: JobStatus, val progress: Float = 0f)

@Singleton
class GenerationEngine @Inject constructor() {
    private val _jobQueue = MutableStateFlow<List<JobState>>(emptyList())
    val jobQueue: StateFlow<List<JobState>> = _jobQueue.asStateFlow()

    fun submitJob(job: GenerationJob) {
        val currentState = JobState(job, JobStatus.PENDING)
        _jobQueue.value = _jobQueue.value + currentState
        // Job processing logic will be implemented here
    }
}
