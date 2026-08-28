package com.example.studio.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.SceneDao
import com.example.data.local.entity.SceneEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val sceneDao: SceneDao
) : ViewModel() {

    private val currentProjectId = "proj_1"

    val scenes: StateFlow<List<SceneEntity>> = sceneDao.getScenesForProject(currentProjectId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
