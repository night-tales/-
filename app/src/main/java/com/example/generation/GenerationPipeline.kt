package com.example.generation

import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
import com.example.data.local.entity.GenerationJobEntity
import com.example.data.local.entity.GenerationStatus
import com.example.data.repository.GenerationRepository
import com.example.domain.ai.AiDirector
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import javax.inject.Inject

class GenerationPipeline @Inject constructor(
    private val aiDirector: AiDirector,
    private val sceneDao: SceneDao,
    private val projectDao: ProjectDao,
    private val generationRepository: GenerationRepository,
    private val imageProvider: ImageProvider,
    private val audioProvider: AudioProvider,
    private val videoAssembler: VideoAssembler
) {
    suspend fun run(job: GenerationJobEntity) {
        try {
            update(job, 5, "AI_SCENES")
            val project = projectDao.getProjectById(job.projectId) ?: error("Project not found: ${job.projectId}")
            val existingScenes = sceneDao.getScenesForProjectOnce(job.projectId)
            val scenePlans = aiDirector.generateScenes(project.title, existingScenes.size.coerceAtLeast(1))
            val scenes = existingScenes.mapIndexed { index, scene ->
                val plan = scenePlans[index]
                scene.copy(title = plan.title, narration = plan.narration, imagePrompt = plan.imagePrompt, durationMs = plan.durationMs)
            }
            sceneDao.insertScenes(scenes)
            update(job, 20, "IMAGES")

            val media = mutableListOf<SceneMedia>()
            scenes.forEachIndexed { index, scene ->
                coroutineContext.ensureActive()
                val image = imageProvider.generate(scene.imagePrompt.orEmpty())
                sceneDao.updateScene(scene.copy(imageUrl = image.toString()))
                update(job, 20 + ((index + 1) * 40 / scenes.size), "IMAGES")
                media += SceneMedia(image, null, scene.durationMs)
            }

            update(job, 65, "AUDIO")
            val withAudio = media.mapIndexed { index, item ->
                coroutineContext.ensureActive()
                val scene = scenes[index]
                val audio = audioProvider.synthesize(scene.narration)
                sceneDao.updateScene(scene.copy(imageUrl = item.imageUri.toString(), audioUrl = audio.toString()))
                update(job, 65 + ((index + 1) * 20 / scenes.size), "AUDIO")
                item.copy(audioUri = audio)
            }

            update(job, 88, "VIDEO_ASSEMBLY")
            val video = videoAssembler.assemble(withAudio)
            projectDao.updateStatus(job.projectId, GenerationStatus.COMPLETED.name, System.currentTimeMillis())
            generationRepository.updateState(job.id, GenerationStatus.COMPLETED, 100, "COMPLETED")
            // Final video URI is persisted as the project status pipeline reaches completion in the next schema step.
            video.toString()
        } catch (t: Throwable) {
            if (t is kotlinx.coroutines.CancellationException) throw t
            generationRepository.updateState(job.id, GenerationStatus.FAILED, 0, "FAILED", t.message?.take(500))
            projectDao.updateStatus(job.projectId, GenerationStatus.FAILED.name, System.currentTimeMillis())
            throw t
        }
    }

    private suspend fun update(job: GenerationJobEntity, progress: Int, step: String) {
        generationRepository.updateState(job.id, GenerationStatus.GENERATING, progress, step)
        projectDao.updateStatus(job.projectId, GenerationStatus.GENERATING.name, System.currentTimeMillis())
    }
}
