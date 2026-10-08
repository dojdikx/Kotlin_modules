package com.example.module6.task1.domain.repository

import com.example.module6.task1.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>
    suspend fun getPhotoById(id: String): Photo?
}