package com.example.kotlinmodule54.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmodule54.data.TaskDatabase
import com.example.kotlinmodule54.model.TaskItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray

private val Context.taskSettings by preferencesDataStore("task_settings")
private val completedColorKey = booleanPreferencesKey("completed_color")

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TaskDatabase.getDatabase(application).taskDao()
    val tasks = mutableStateListOf<TaskItem>()
    val colorCompleted = application.taskSettings.data
        .map { it[completedColorKey] == true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            importOnce()
            dao.getAllTasks().collectLatest { list ->
                tasks.clear()
                tasks.addAll(list)
            }
        }
    }

    private suspend fun importOnce() {
        val context = getApplication<Application>()
        val already = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
            .getBoolean("imported", false)
        if (already) return
        val json = context.assets.open("tasks.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            dao.insertTask(
                TaskItem(
                    title = item.getString("title"),
                    done = item.optBoolean("done")
                )
            )
        }
        context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("imported", true)
            .apply()
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { dao.insertTask(TaskItem(title = title.trim())) }
    }

    fun updateTask(task: TaskItem, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { dao.updateTask(task.copy(title = title.trim())) }
    }

    fun toggleDone(task: TaskItem) {
        viewModelScope.launch { dao.updateTask(task.copy(done = !task.done)) }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch { dao.deleteTask(task) }
    }

    fun setCompletedColor(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().taskSettings.edit { prefs ->
                prefs[completedColorKey] = enabled
            }
        }
    }
}
