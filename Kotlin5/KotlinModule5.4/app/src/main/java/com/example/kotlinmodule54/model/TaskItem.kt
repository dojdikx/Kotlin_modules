package com.example.kotlinmodule54.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val done: Boolean = false
)